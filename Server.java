import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class Server {

	private int portNumber = 5000;
	private Set<String> usernameList = new HashSet<>();
	private Set<PrintWriter> chatList = new HashSet<>();
	
	public void start()
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
		for (PrintWriter out : chatList)
		{
			out.println("OUTSIDEMESSAGE " + message);
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		Server server = new Server();
		server.start();
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
		
		public synchronized void run() //synchronized ensures usernameList and chatList is up-to-date on all threads
		{
			try
			{
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				registerNewUser();
				writeMessage(username + " signed in to chat.");
				chatList.add(out);
				
				String message = in.readLine();
				while (!message.equals("."))
				{
					writeMessage(username + ": " + message);
					message = in.readLine();
				}
				out.println("LOGOUT");
				usernameList.remove(username);
				chatList.remove(out);
				writeMessage(username + " logged out.");
				try
				{
					socket.close();
				}
				catch (IOException e) {}
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
			while (username != null && usernameList.contains(username))
			{
				out.println("ASKFORNAME1");
				username = in.readLine();
			}
			usernameList.add(username);
			out.println("WELCOMEMESSAGE " + username);
		}
	}
}

