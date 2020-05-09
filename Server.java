import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
/*
 * File: ChatServer.java
 * Author: Diana Choi, Sara Joshi
 * Class: CS 3800-02 Computer Networks
 * Professor: J. Korah
 * Assignment: Final
 * 
 * 
 * Purpose: This class is a server that listens for
 *          connections from chat users. It keeps track
 *          of all the users currently connected using a
 *          Set of usernames and a Set of their output streams.
 */
public class Server {

	private int portNumber;
	private Set<String> usernames = new HashSet<>();
	private Set<PrintWriter> clientOutputs = new HashSet<>();
	
	public Server(int port)
	{
		portNumber = port;
	}
	
	public static void main(String[] args) throws IOException
	{
		int port = 5000;
		Server server = new Server(port);
		server.beginLoop();
	}
	
	public void beginLoop()
	{
		try (ServerSocket server = new ServerSocket(portNumber))
		{
			System.out.println("Server: Listening on port " + portNumber);
			
			while (true)
			{
				Socket connection = server.accept();
				System.out.println("Server: Accepted new connection");
				
				UserThread newClient = new UserThread(connection);
				newClient.start();
			}
		}
		catch (IOException e)
		{
			System.out.println("Server: I/O Exception: " + e.getMessage());
		}
		
	}
	
	public void writeMessage(String message)
	{
		for (PrintWriter out : clientOutputs)
		{
			out.println("OUTSIDEMESSAGE " + message);
		}
	}
	
	private class UserThread extends Thread
	{
		private String username;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;
		
		public UserThread(Socket connection)
		{
			this.socket = connection;
		}
		
		public void run()
		{
			try
			{
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				registerNewUser();
				writeMessage(username + " has joined the chatroom!");
				clientOutputs.add(out);
				
				String message = in.readLine();
				while (!message.equals("."))
				{
					writeMessage(username + ": " + message);
					message = in.readLine();
				}
				removeMe();
			}
			catch (IOException e)
			{
				System.out.println("UserThread: I/O Exception: " + e.getMessage());
			}
		}
		
		public void registerNewUser() throws IOException
		{
			out.println("ASKFORNAME0");
			username = in.readLine();
			while (username != null && usernames.contains(username))
			{
				out.println("ASKFORNAME1");
				username = in.readLine();
			}
			usernames.add(username);
			out.println("WELCOMEMESSAGE " + username);
		}
		
		public void removeMe()
		{
			out.println("LOGOUT");
			usernames.remove(username);
			clientOutputs.remove(out);
			writeMessage(username + " has left the chatroom");
			try
			{
				socket.close();
			}
			catch (IOException e) {}
		}
	}
}

