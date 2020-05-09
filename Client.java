import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.PrintWriter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
	private InetAddress host;
	private int portNumber;
	private String username;
	private BufferedReader in;
	private PrintWriter out;
	private UI window;
	
	public Client(InetAddress hostname, int port)
	{
		host = hostname;
		portNumber = port;
		window = new UI();
		
		//listen for changes to the GUI's text box. if changes, then 
		window.getTextField().addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent a) {
				String message = window.getTextField().getText();	//
				writeMessage(message);
				window.getTextField().setText("");	//reset text box
			}
		});
	}
	
	public static void main(String[] args) throws UnknownHostException
	{
		InetAddress hostname = InetAddress.getLocalHost();
		int port = 5000;
		Client client = new Client(hostname, port);
		client.beginLoop();
	}
	
	public void beginLoop()
	{
		try
		{
			Socket connection = new Socket(host, portNumber);
			System.out.println("Client: Connected to server");
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			out = new PrintWriter(connection.getOutputStream(), true);
			
			String message = in.readLine();
			while (!message.equals("LOGOUT"))
			{
				if (message.startsWith("ASKFORNAME"))
				{
					boolean first = (message.charAt(10) == '0');
					String name = window.usernameSelection(first);
					out.println(name);
				}
				else if (message.startsWith("WELCOMEMESSAGE"))
				{
					username = message.substring(15);
					window.show();
					window.printMessageToScreen("Welcome " + username);
				}
				else if (message.startsWith("OUTSIDEMESSAGE"))
				{
					displayMessage(message.substring(15));
				}
				else
				{
					writeMessage(message);
				}
				message = in.readLine();
			}
			connection.close();
			window.exit();
		}
		catch (UnknownHostException e)
		{
			System.out.println("Client: Unknown Host Exception: " + e.getMessage());
		}
		catch (IOException e)
		{
			System.out.println("Client: I/O Exception: " + e.getMessage());
		}
	}
	
	public void displayMessage(String message)
	{
		System.out.println(message);
		window.printMessageToScreen(message);
	}
	
	public void writeMessage(String message)
	{
		out.println(message);
	}
}
