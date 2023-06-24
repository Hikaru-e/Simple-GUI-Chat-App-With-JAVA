import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatDarkLaf;

public class ServerChatGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	public ServerChatGUI() {
		initComponents();
		action();
	}

	private void action() {
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		jButtonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!jTextAreaSendMessage.getText().isEmpty()) {
					jTextAreaAllChat.append("Me : " + jTextAreaSendMessage.getText() + "\n\n");
					String msg = jTextAreaSendMessage.getText();
					writer.println(msg);
					jTextAreaSendMessage.setText("");
				}
			}
		});

		jButtonSend.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "clickButton");
		jButtonSend.getActionMap().put("clickButton", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!jTextAreaSendMessage.getText().isEmpty()) {
					jTextAreaAllChat.append("Me : " + jTextAreaSendMessage.getText() + "\n\n");
					writer.println(jTextAreaSendMessage.getText());
					jTextAreaSendMessage.setText("");
				}
			}
		});

	}

	private void initComponents() {

		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

		jScrollPane1 = new JScrollPane();
		jTextAreaAllChat = new JTextArea();
		jPanel1 = new JPanel();
		jButtonSend = new JButton();
		jTextAreaSendMessage = new JTextField();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Waiting to be connected");

		getContentPane().setLayout(new BorderLayout(15, 15));

		jTextAreaAllChat.setColumns(20);
		jTextAreaAllChat.setRows(5);
		jTextAreaAllChat.setEditable(false);
		jScrollPane1.setViewportView(jTextAreaAllChat);
		getContentPane().add(jScrollPane1, BorderLayout.CENTER);

		jPanel1.setLayout(new BorderLayout(15, 15));

		jButtonSend.setText("Send");

		jPanel1.add(jButtonSend, BorderLayout.LINE_END);

		jTextAreaSendMessage.setPreferredSize(new Dimension(20, 40));

		jPanel1.add(jTextAreaSendMessage, BorderLayout.CENTER);

		getContentPane().add(jPanel1, BorderLayout.PAGE_END);
		jPanel1.setBorder(new EmptyBorder(0, 10, 10, 10));
		setBounds(0, 0, 512, 512);
	}

	private void formWindowOpened(WindowEvent evt) {
		try {
			serverSocket = new ServerSocket(5678);
			socket = serverSocket.accept();
			setTitle("Connected to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
			writer = new PrintWriter(socket.getOutputStream(), true);
			scanner = new Scanner(socket.getInputStream());
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						String input = scanner.nextLine();
						jTextAreaAllChat.append("Client: " + input + "\n\n");
					}
				}
			});
			t.start();
		} catch (Exception e) {
		}
	}

	

	public static void main(String args[]) {

		try {
			// Set the selected theme using UIManager
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ServerChatGUI().setVisible(true);
			}
		});
	}
	
	// Variables declaration 
	
	private KeyStroke keyStroke;
	private JButton jButtonSend;
	private JPanel jPanel1;
	private JScrollPane jScrollPane1;
	private JTextArea jTextAreaAllChat;
	private JTextField jTextAreaSendMessage;

	// End of variables declaration
	
	private ServerSocket serverSocket;
	private Socket socket;
	private PrintWriter writer;
	private Scanner scanner;
}
