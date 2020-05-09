import java.awt.*;
import javax.swing.*;

public class UI {

	private JFrame window;
	private JTextField typeYourMessageHere;
	private JTextArea seeAllMessagesHere;
	
	public UI()
	{
		window = new JFrame("Chat");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initializeMessageArea();
		window.setVisible(false);
	}
	
	public void initializeMessageArea()
	{
		typeYourMessageHere = new JTextField(50);
		seeAllMessagesHere = new JTextArea(16, 50);
		typeYourMessageHere.setEditable(true);
		seeAllMessagesHere.setEditable(false);
		window.getContentPane().add(new JScrollPane(seeAllMessagesHere), BorderLayout.CENTER);
		window.getContentPane().add(typeYourMessageHere, BorderLayout.SOUTH);
		window.pack();
	}
	
	public JTextField getTextField()
	{
		return typeYourMessageHere;
	}
	
	public void printMessageToScreen(String message)
	{
		seeAllMessagesHere.append(message + "\n");
	}
	
	public void show()
	{
		window.setVisible(true);
	}
	
	public String usernameSelection(boolean first)
	{
		String prompt = "Please enter a username:";
		if (!first)
		{
			return JOptionPane.showInputDialog(window, "Invalid username.\n" + prompt, "Username Selection", JOptionPane.PLAIN_MESSAGE);
		}
		return JOptionPane.showInputDialog(window, prompt, "Username Selection", JOptionPane.PLAIN_MESSAGE);
	}
	
	public void exit()
	{
		window.setVisible(false);
		window.dispose();
	}

}
