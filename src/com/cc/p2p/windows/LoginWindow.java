package com.cc.p2p.windows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.cc.p2p.bean.InfoBean;
import com.cc.p2p.util.GUIUtil;

/**
 * this is a login class mainly responsible for user login and registration
 * 
 * @author wangcongcong
 *
 */

public class LoginWindow extends JFrame implements ActionListener, Runnable {

	private static final long serialVersionUID = -7321772680725912127L;

	private JPanel menuPanel = new JPanel();
	private JLabel ipLabel = new JLabel("IP:");

	private JTextField ipTextField = new JTextField(10);
	private JLabel portLabel = new JLabel("Port:");
	private JTextField portTextField = new JTextField(10);
	private JButton connectButton = new JButton("connectServer");
	private JButton disconnectButton = new JButton("disconnectServer");

	JPanel jp = new JPanel();
	JLabel jl1 = new JLabel("Account:");
	JTextField jtf1 = new JTextField(10);
	JLabel jl2 = new JLabel("Password:");
	JTextField jtf2 = new JPasswordField(10);
	JButton login = new JButton("login");
	JButton register = new JButton("register");
	InfoBean messageBean;
	Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private boolean isRun = true;

	private int port;

	public LoginWindow() {
		// Initialize the login window
		init();

	}

	private void init() {
		this.setTitle("p2pClient_Login");
		setLayout(new BorderLayout());
		menuPanel.setLayout(new FlowLayout());
		menuPanel.add(ipLabel);
		menuPanel.add(ipTextField);
		menuPanel.add(portLabel);
		menuPanel.add(portTextField);
		menuPanel.add(connectButton);
		menuPanel.add(disconnectButton);
		ipTextField.setText("127.0.0.1");
		portTextField.setText("9999");
		this.add(menuPanel, BorderLayout.NORTH);
		jp.setLayout(new GridLayout(3, 2));
		jp.add(jl1);
		jp.add(jtf1);
		jp.add(jl2);
		jp.add(jtf2);
		jp.add(login);
		jp.add(register);
		this.add(jp, BorderLayout.CENTER);
		this.setResizable(false);
		this.setSize(700, 400);
		this.setVisible(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		GUIUtil.toCenter(this);
		disconnectButton.setEnabled(false);
		connectButton.addActionListener(this);
		disconnectButton.addActionListener(this);
		login.addActionListener(this);

		// this button is for user registration
		register.addActionListener(this);

		this.addWindowListener(new WindowAdapter() {

			// disconnection handling
			@Override
			public void windowClosing(WindowEvent e) {

				int check = JOptionPane.showConfirmDialog((LoginWindow) e.getSource(), "Are you sure to exit?", "Confimation to exit",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (check == JOptionPane.YES_OPTION) {
					if (oos != null) {
						writeMessageToServer(new InfoBean("exit"));
					}
					try {
						ois = null;
						oos = null;
						socket = null;
						System.exit(0);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				super.windowClosed(e);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// if connect button is clicked, the following codes will be executed
		if (e.getSource() == connectButton) {
			try {

				// convert port into integer type
				port = Integer.parseInt(portTextField.getText());
				socket = new Socket(ipTextField.getText(), port);
				// get input and output stream

				ois = new ObjectInputStream(socket.getInputStream());

				oos = new ObjectOutputStream(socket.getOutputStream());
				// start a thread to handle message transmission between this client and center server
				new Thread(this).start();
				// the following codes are to deal with different types of exceptions
			} catch (UnknownHostException e1) {
				JOptionPane.showMessageDialog(this, "failed, check if your port or ip is correct", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "error, check if server was started and port or ip is correct", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(this, "please a digit port", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			JOptionPane.showMessageDialog(this, "connected successfully!", "Warning", JOptionPane.WARNING_MESSAGE);

			// button enabled handling
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(true);
			portTextField.setEditable(false);
			ipTextField.setEditable(false);
		} else if (e.getSource() == disconnectButton) {
			try {

				// if disconnect button is clicked, close all supposed-closed object
				writeMessageToServer(new InfoBean("disconnect"));
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(this, "disconnection failed !", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			disconnectButton.setEnabled(false);
			connectButton.setEnabled(true);
			portTextField.setEditable(true);
			ipTextField.setEditable(true);
			JOptionPane.showMessageDialog(this, "disconnected successfully !", "Warning", JOptionPane.WARNING_MESSAGE);
		} else if (e.getSource() == login) {
			if (socket == null) {
				// if not connection, go to send a warning to this window
				JOptionPane.showMessageDialog(this, "connect server first, please", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			// if connection is set, send a message to center server
			writeMessageToServer(
					new InfoBean(socket.getLocalPort(), socket.getLocalAddress().getHostAddress(), jtf1.getText(), jtf2.getText(), "login", null));

		}
		// to handle with the case of register button clicked
		else if (e.getSource() == register) {
			if (socket == null) {
				JOptionPane.showMessageDialog(this, "connect server first, please", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			new RegisterWindow(this);
		}
	}

	/**
	 * a method used to send message to the center server
	 * 
	 * @param messageBean
	 */
	public void writeMessageToServer(InfoBean messageBean) {

		try {
			oos.writeObject(messageBean);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {

		// this thread is to deal with message transmission between peer and centre server
		while (isRun) {
			try {

				// read a message from center server
				InfoBean mb = (InfoBean) ois.readObject();
				// following different content of message, do different operations
				if (mb.getMessage_content().equals("registered")) {
					JOptionPane.showConfirmDialog(this, "registered successfully!");
				} else if (mb.getMessage_content().equals("register_error")) {
					JOptionPane.showConfirmDialog(this, "error, perhaps your account was alreday registered");
				} else if (mb.getMessage_content().equals("login_success")) {
					JOptionPane.showMessageDialog(this, "successfully");
					isRun = false;
					this.dispose();
					new MainWindow(socket, new InfoBean(port, ipTextField.getText(), jtf1.getText(), jtf2.getText(), null, null), ois, oos);
				} else if (mb.getMessage_content().equals("login_error")) {
					JOptionPane.showMessageDialog(this, "either account or password is wrong", "Warning", JOptionPane.WARNING_MESSAGE);
				} else if (mb.getMessage_content().equals("loginedalready")) {
					JOptionPane.showMessageDialog(this, "you can not login an online account", "Warning", JOptionPane.WARNING_MESSAGE);
				}

			} catch (Exception e) {
				socket = null;
				isRun = false;
			}
		}

	}
}
