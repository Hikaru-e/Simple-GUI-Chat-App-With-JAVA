
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatLightLaf;
public class ClientChatGUI {

	public ClientChatGUI() {
		jdialog();
	}

	private void jdialog() {
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		dialog = new JDialog(main, "Enter server IP");
		JButton openMain = new JButton("Run");
		serverIP = new JTextField("127.0.0.1", 20);
		JPanel panelDialogBottom = new JPanel();
		JPanel panelDialogUp = new JPanel();
		JLabel notice = new JLabel("Enter the IP address of the server ex: 192.168.1.101");
		
		panelDialogUp.setBorder(new EmptyBorder(10, 10, 0, 10));
		panelDialogUp.add(notice);
		
		panelDialogBottom.setBorder(new EmptyBorder(10, 10, 10, 10));
		panelDialogBottom.setLayout(new FlowLayout());
		panelDialogBottom.add(serverIP);
		panelDialogBottom.add(openMain);
		
		
		dialog.setLayout(new GridLayout(2,1));
		dialog.add(panelDialogUp);
		dialog.add(panelDialogBottom);
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(null);
		dialog.pack();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		openMain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, "clickButton");
		openMain.getActionMap().put("clickButton", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
            	if (!isValidIPAddress(serverIP.getText())) {
					JOptionPane.showMessageDialog(null, "Please enter a valid IP address !", "Error",
							JOptionPane.ERROR_MESSAGE);
            	}
            	else {
            		
            		dialog.dispose();
            		
            		initComponents();
            		action();
            	}
			}
		});
		
	    openMain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (!isValidIPAddress(serverIP.getText())) {
					JOptionPane.showMessageDialog(null, "Please enter a valid IP address !", "Error",
							JOptionPane.ERROR_MESSAGE);
            	}
            	else {
            		server = serverIP.getText();
            		dialog.dispose();
            		
            		initComponents();
            		action();
            	}

            }
        });
		
	}

	
	private void initComponents() {
		main = new JFrame();
		keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

		jScrollPane1 = new JScrollPane();
		jTextAreaAllChat = new JTextArea();
		jPanel1 = new JPanel();
		jTextAreaSendMessage = new JTextField();
		jButtonSend = new JButton();

		main.getContentPane().setLayout(new BorderLayout(10, 10));

		jTextAreaAllChat.setColumns(20);
		jTextAreaAllChat.setRows(5);
		jTextAreaAllChat.setEditable(false);
		jScrollPane1.setViewportView(jTextAreaAllChat);

		main.getContentPane().add(jScrollPane1, BorderLayout.CENTER);

		jPanel1.setLayout(new BorderLayout(10, 10));

		jTextAreaSendMessage.setPreferredSize(new Dimension(20, 40));

		jPanel1.add(jTextAreaSendMessage, BorderLayout.CENTER);

		jButtonSend.setText("Send");

		jPanel1.add(jButtonSend, BorderLayout.LINE_END);
		jPanel1.setBorder(new EmptyBorder(0, 10, 10, 10));

		main.getContentPane().add(jPanel1, BorderLayout.PAGE_END);
		main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		main.setTitle("Client");
		main.setVisible(true);
		main.setBounds(0, 0, 512, 512);
		main.setLocationRelativeTo(null);
	}
	
	private void action() {

		main.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent evt) {
				formWindowOpened(evt);
			}
		});

		jButtonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (!jTextAreaSendMessage.getText().isEmpty()) {
					jTextAreaAllChat.append("Me : " + jTextAreaSendMessage.getText() + "\n\n");
					writer.println(jTextAreaSendMessage.getText());
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


	
    private static boolean isValidIPAddress(String ipAddress) {
        String ipAddressPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                   "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                   "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                                   "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        return Pattern.matches(ipAddressPattern, ipAddress);
    }

	private void formWindowOpened(WindowEvent evt) {
		try {
			
			socket = new Socket(server , 5678);
			writer = new PrintWriter(socket.getOutputStream(), true);
			scanner = new Scanner(socket.getInputStream());
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						String input = scanner.nextLine();
						jTextAreaAllChat.append("Server: " + input + "\n\n");
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
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/* Create and display the form */
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ClientChatGUI();
			}
		});
	}

	// Variables declaration 
	
	private JFrame main;

	private JDialog dialog;
	private JTextField serverIP;

	private String server;
	
	private KeyStroke keyStroke;
	private JButton jButtonSend;
	private JPanel jPanel1;
	private JScrollPane jScrollPane1;
	private JTextArea jTextAreaAllChat;
	private JTextField jTextAreaSendMessage;
	
	// End of variables declaration
	private Socket socket;
	private PrintWriter writer;
	private Scanner scanner;

}
