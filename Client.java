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
	private BufferedReader inFromServer;
	private PrintWriter outToServer;
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
				outToServer.println(message);
				window.getTextField().setText("");	//reset text box
			}
		});
	}
	
	public static void main(String[] args) throws UnknownHostException
	{
		InetAddress hostname = InetAddress.getLocalHost();
		int port = 5000;
		Client client = new Client(hostname, port);
		client.start();
	}
	
	public void start()
	{
		try
		{
			Socket clientSocket = new Socket(host, portNumber);
			System.out.println("Connected to server");
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
			
			String promptFromServer = inFromServer.readLine();
			while (!promptFromServer.equals("LOGOUT"))
			{
				String snippedMessage = promptFromServer.substring(15);	//removes header
				if (promptFromServer.startsWith("ASKFORNAME"))
				{
					boolean isValidUsername = (promptFromServer.charAt(10) == '0');
					String username = window.usernameSelection(isValidUsername);
					outToServer.println(username);
				}
				else if (promptFromServer.startsWith("WELCOMEMESSAGE"))
				{
					username = snippedMessage;
					window.show();	//NTC FOR JAVAFX
					window.printMessageToScreen("Welcome " + username); //NTC FOR JAVAFX
				}
				else if (promptFromServer.startsWith("OUTSIDEMESSAGE"))
				{
					System.out.println(snippedMessage);
					window.printMessageToScreen(snippedMessage);	//NTC FOR JAVAFX
				}
				else
				{
					outToServer.println(promptFromServer);
				}
				promptFromServer = inFromServer.readLine();
			}
			clientSocket.close();
			window.exit(); //NTC FOR JAVAFX (just delete)
		}
		catch (Exception e)
		{
			System.out.println("Client Error:");
			e.printStackTrace();
		}
	}

}
