package com.cc.p2p.windows;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.cc.p2p.bean.InfoBean;
import com.cc.p2p.util.GUIUtil;

/**
 * this is a registration dialog for finishing registration task
 * 
 * @author wangcongcong
 *
 */
public class RegisterWindow extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	JLabel jl1 = new JLabel("Account:");
	JTextField jtf1 = new JTextField(10);
	JLabel jl2 = new JLabel("Password:");
	JPasswordField jtf2 = new JPasswordField(10);
	JLabel jl3 = new JLabel("PasswordAgain:");
	JPasswordField jtf3 = new JPasswordField(10);
	JButton back = new JButton("back");
	JButton confirm = new JButton("confirm");
	LoginWindow loginWindow;

	public RegisterWindow(JFrame loginWindow) {
		super(loginWindow, true);
		this.loginWindow = (LoginWindow) loginWindow;

		// initialize this window
		init();

	}

	private void init() {

		this.setTitle("Register");
		this.setLayout(new GridLayout(4, 2));
		this.add(jl1);
		this.add(jtf1);
		this.add(jl2);
		this.add(jtf2);
		this.add(jl3);
		this.add(jtf3);
		this.add(back);
		this.add(confirm);

		back.addActionListener(this);
		confirm.addActionListener(this);
		this.setSize(500, 400);
		GUIUtil.toCenter(this);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == confirm) {

			// check if all information is filled
			if ("".equals(jtf1.getText()) || "".equals(jtf2.getText()) || "".equals(jtf3.getText())) {
				JOptionPane.showMessageDialog(this, "fill all information", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;

			}

			// check two passwords are the same
			if (!jtf2.getText().equals(jtf3.getText())) {
				JOptionPane.showMessageDialog(this, "two passwords are inconsistent", "Warning",
						JOptionPane.WARNING_MESSAGE);
				return;

			}

			// after all check, send message to server and tell it i wanna register an account
			loginWindow.writeMessageToServer(
					new InfoBean(-1, null, jtf1.getText(), jtf2.getText(), "register", null));
			this.dispose();

			// if back button is clicked, dispose this window and go back to the login window
		} else if (e.getSource() == back) {
			this.dispose();
		}

	}

}
