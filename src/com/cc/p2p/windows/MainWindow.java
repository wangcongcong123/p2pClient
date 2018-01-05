package com.cc.p2p.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cc.p2p.bean.InfoBean;
import com.cc.p2p.bean.UserAndFile;
import com.cc.p2p.thread.PeerServerThread;

/**
 * this is a class for handling file sharing between peers and interaction with the center server and almost all very important operations run in this class
 * 
 * @author wangcongcong
 *
 */
public class MainWindow extends JFrame implements ActionListener, ListSelectionListener, Runnable {

	private static final long serialVersionUID = 1L;
	Socket socket;
	InfoBean mb;
	private JPanel fileuploadpane;
	private JLabel fileflag;
	private JTextField filepath;
	private JButton choosebutton;
	private JButton uploadbutton;
	private JPanel listPanel;
	private JLabel userliststring;
	private JList<String> userlist;
	private JScrollPane userScollPane;
	private JLabel labelbetweenlist;
	private JLabel fileliststring;
	private JList<String> filelist;
	private JScrollPane fileScollPane;
	private JPanel controlpanel;
	private JButton logout;
	private JFileChooser fileChooser = new JFileChooser();
	private DefaultListModel<String> jList1Model, jList2Model;
	private JPopupMenu popupMenu;
	private JMenuItem downloadItem;
	private JMenuItem removeItem;
	private String userName;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	// this is a very useful variable and it functions as a bundle to store all info of current online clients and corresponding file paths
	private List<UserAndFile> userAndFilesbundle;
	private ServerSocket peerServerSocket;
	private JLabel statuslabel;

	// declare a progress bar to record the progress when file transfer is running between two peers
	private JProgressBar progressbar;

	public MainWindow(Socket socket, InfoBean mb, ObjectInputStream ois, ObjectOutputStream oos) {
		// to hold the required parameters passed from login window
		userName = mb.getAccount();
		this.socket = socket;
		this.mb = mb;

		// initialize this frame
		initFrame();
		this.ois = ois;
		this.oos = oos;

		// this thread is created for handling message which will be transmitted between the pivotal server and peers
		new Thread(this).start();

		// when a peer is started, we need to send a message to server to refresh the user list
		writeMessageToServer(new InfoBean("refresh"));
		try {

			// create a server thread to listen listen peers' status when file transmission is required by other peers
			peerServerSocket = new ServerSocket(socket.getLocalPort());
			// start a thread to listen peers' status
			new Thread(new PeerServerThread(peerServerSocket)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * initialize this frame
	 */
	private void initFrame() {
		this.setTitle("p2p file sharing system(Napster style) and " + userName + " is online");
		fileuploadpane = new JPanel();
		fileflag = new JLabel("file:");
		filepath = new JTextField(20);
		filepath.setEditable(false);
		choosebutton = new JButton("choose");
		uploadbutton = new JButton("upload");
		fileuploadpane.setLayout(new FlowLayout());
		fileuploadpane.add(fileflag);
		fileuploadpane.add(filepath);
		fileuploadpane.add(choosebutton);
		fileuploadpane.add(uploadbutton);
		this.add(fileuploadpane, BorderLayout.NORTH);

		listPanel = new JPanel();

		userliststring = new JLabel("Userlist");
		userliststring.setFont(new Font("TimesRoman", Font.BOLD, 10));
		userliststring.setBounds(50, 10, 50, 20);
		listPanel.add(userliststring);

		listPanel.setLayout(null);
		userlist = new JList<String>();
		// declare a list model to deal with user list status
		jList1Model = new DefaultListModel<String>();
		userlist.setModel(jList1Model);
		userlist.setSelectedIndex(0);
		userScollPane = new JScrollPane(userlist);
		userScollPane.setBounds(50, 30, 100, 300);
		listPanel.add(userScollPane);

		// the following label will be shown a right-pointed arrow
		labelbetweenlist = new JLabel("\u2192");
		labelbetweenlist.setFont(new Font("TimesRoman", Font.BOLD, 50));
		labelbetweenlist.setBounds(150, 175, 80, 20);
		listPanel.add(labelbetweenlist);

		fileliststring = new JLabel("Filelist");
		fileliststring.setFont(new Font("TimesRoman", Font.BOLD, 10));
		fileliststring.setBounds(200, 10, 50, 20);
		listPanel.add(fileliststring);

		filelist = new JList<String>();

		// declare a list model to deal with file list status
		jList2Model = new DefaultListModel<String>();
		filelist.setModel(jList2Model);
		fileScollPane = new JScrollPane(filelist);
		fileScollPane.setBounds(200, 30, 550, 300);
		listPanel.add(fileScollPane);
		this.add(listPanel, BorderLayout.CENTER);
		controlpanel = new JPanel();
		controlpanel.setLayout(new GridLayout(3, 1));

		logout = new JButton("logout");
		controlpanel.add(logout);
		statuslabel = new JLabel(userName);
		controlpanel.add(statuslabel);

		// initialize the progress bar
		progressbar = new JProgressBar();
		progressbar.setOrientation(JProgressBar.HORIZONTAL);
		progressbar.setMinimum(0);
		progressbar.setMaximum(100);
		progressbar.setValue(0);
		progressbar.setStringPainted(true);
		progressbar.setBorderPainted(true);
		progressbar.setBackground(Color.pink);
		controlpanel.add(progressbar);
		this.add(controlpanel, BorderLayout.SOUTH);

		userlist.addListSelectionListener(this);

		popupMenu = new JPopupMenu();
		downloadItem = new JMenuItem("download");
		removeItem = new JMenuItem("remove");
		popupMenu.add(downloadItem);
		popupMenu.addSeparator();
		popupMenu.add(removeItem);

		filelist.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				filelist.setSelectedIndex(filelist.locationToIndex(e.getPoint()));
				if ((e.isPopupTrigger() && filelist.getSelectedIndex() != -1) || e.getClickCount() == 2) {
					Object selected = filelist.getModel().getElementAt(filelist.getSelectedIndex());
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		choosebutton.addActionListener(this);
		uploadbutton.addActionListener(this);
		logout.addActionListener(this);
		downloadItem.addActionListener(this);
		removeItem.addActionListener(this);
		this.setVisible(true);
		this.setSize(800, 500);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// add a window listener to this window in order to handling exit operation
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int check = JOptionPane.showConfirmDialog((MainWindow) e.getSource(), "Are you sure to exit?", "Confimation to exit",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (check == JOptionPane.NO_OPTION) {
					return;
				}
				if (check == JOptionPane.YES_OPTION) {
					if (oos != null) {
						writeMessageToServer(new InfoBean(1, null, userName, null, "logout", null));
					}
					try {
						ois.close();
						oos.close();
						socket.close();
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
		if (e.getSource() == choosebutton) {
			int i = fileChooser.showOpenDialog(this);
			if (i == JFileChooser.APPROVE_OPTION) {
				filepath.setText(fileChooser.getSelectedFile().getPath());
			}
		} else if (e.getSource() == uploadbutton) {
			if (!filepath.getText().equals("")) {
				writeMessageToServer(new InfoBean(-1, null, userName, null, "uploadfile", filepath.getText()));
			} else {
				JOptionPane.showMessageDialog(this, "choose a file please");
			}

		} else if (e.getSource() == logout) {
			int check = JOptionPane.showConfirmDialog(this, "Are you sure to logout?", "Confimation to exit", JOptionPane.YES_NO_CANCEL_OPTION);
			if (check == JOptionPane.YES_OPTION) {
				if (oos != null) {
					writeMessageToServer(new InfoBean(1, null, userName, null, "logout", null));
				}
				try {
					ois.close();
					oos.close();
					socket.close();
					ois = null;
					oos = null;
					socket = null;
					System.exit(0);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

		else if (e.getSource() == downloadItem) {
			if (userlist.isSelectionEmpty()) {
				JOptionPane.showMessageDialog(this, "chooose a user, please!");
				return;
			}
			if (userlist.getSelectedValue().equals(userName)) {
				String[] filepath = filelist.getSelectedValue().split("/");
				String filename = filepath[filepath.length - 1];
				fileChooser.setCurrentDirectory(new File("/Users/wangcongcong/Desktop"));
				fileChooser.setSelectedFile(new File(filename));
				fileChooser.setDialogTitle("download to");
				int i = fileChooser.showSaveDialog(this);
				if (i == JOptionPane.NO_OPTION) {
					return;
				}
				if (i == JFileChooser.APPROVE_OPTION) {
					File toFile = fileChooser.getSelectedFile();
					File fromFile = new File(filelist.getSelectedValue());
					try {

						FileInputStream in = new FileInputStream(fromFile);
						FileOutputStream out = new FileOutputStream(toFile);
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = in.read(buffer)) != -1) {
							out.write(buffer, 0, len);
						}

						JOptionPane.showMessageDialog(this, "downloaded!");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} else {
				// when not downloading directly from own file list, send a message to server to get info (like id and port) of desination peer
				// send a request for the center server for obtaining the port and host of destination peer
				writeMessageToServer(
						new InfoBean(-1, null, userlist.getSelectedValue(), null, "getPeerInfoForDownloading", filelist.getSelectedValue()));

			}
		} else if (e.getSource() == removeItem) {
			if (!userlist.isSelectionEmpty()) {
				if (!userlist.getSelectedValue().equals(userName)) {
					JOptionPane.showMessageDialog(this, "you cannot remove other users' files");
					return;
				}
			}
			writeMessageToServer(new InfoBean(-1, null, userName, null, "deletefile", filelist.getSelectedValue()));
		}
	}

	/**
	 * this below is going to refresh showing different lists of files when user clicks one certain user name in use list
	 * 
	 * all content of online user and corresponding files is maintained in userAndFileBundle variable
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == userlist) {
			String selectedAccount = userlist.getSelectedValue();
			for (UserAndFile userAndFile : userAndFilesbundle) {
				if (userAndFile.getUser().equals(selectedAccount)) {
					jList2Model.removeAllElements();
					List<String> files = userAndFile.getFilepathes();
					for (String string : files) {
						jList2Model.addElement(string);
					}
				}
			}
		}
	}

	/**
	 * this method is the same as one in login window, it is for writing message to server
	 * 
	 * @param messageBean
	 */
	private void writeMessageToServer(InfoBean messageBean) {
		try {
			oos.writeObject(messageBean);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		boolean isRun = true;
		while (isRun) {
			try {

				// read object from pivotal server
				InfoBean check = (InfoBean) ois.readObject();
				String content = check.getMessage_content();

				// do different operations by different read-message
				if (content.equals("StartRefresh")) {
					refreshList();
				} else if (content.equals("uploaded")) {
					if (check.getAccount().equals(userName)) {
						JOptionPane.showMessageDialog(this, "uploaded");
						filepath.setText("");
						userlist.setSelectedIndex(0);
						jList2Model.addElement(check.getFilepath());
					}
					addFilepathToBundle(check.getAccount(), check.getFilepath());
				} else if (content.equals("upload_error")) {
					if (check.getAccount().equals(userName)) {
						JOptionPane.showMessageDialog(this, "uploade error");
					}
				} else if (content.equals("userexit")) {
					jList1Model.removeElement(check.getAccount());
					removeClientFromBundle(check.getAccount());
				} else if (content.equals("removed")) {
					if (check.getAccount().equals(userName)) {
						jList2Model.removeElement(filelist.getSelectedValue());
					}
					removeFilePathFromBundle(check.getAccount(), check.getFilepath());
				} else if (content.equals("remove_error")) {
					JOptionPane.showMessageDialog(this, "remove error");
				} else if (content.equals("peerInfoReturn")) {
					startDownload(check.getPort(), check.getIp(), check.getFilepath());
				}
			} catch (Exception e) {
				socket = null;
				isRun = false;
				JOptionPane.showMessageDialog(this, "server is disconnected, client stoped");
				System.exit(0);
			}
		}
	}

	/**
	 * this following method is to handle file transmission between peers
	 * 
	 * @param port
	 * @param ip
	 * @param filepath
	 */
	private void startDownload(int port, String ip, String filepath) {

		try {
			@SuppressWarnings("resource")

			// by the got info of destination peer, start a socket to connect it
			Socket requestPeerSocket = new Socket(ip, port);

			// get input and output stream
			DataInputStream dataInputStreamReceive = new DataInputStream(new BufferedInputStream(requestPeerSocket.getInputStream()));
			DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(requestPeerSocket.getOutputStream()));

			// start a thread to receive message from the destination peer
			new Thread(new Runnable() {
				DataOutputStream filedos;
				boolean isRun = true;

				@Override
				public void run() {
					while (isRun) {
						try {
							int flag = dataInputStreamReceive.readInt();
							// 0 stands for start downloading
							if (flag == 0) {

								// read a length of supposed-file which will be used to record downloading progress
								long length = dataInputStreamReceive.readLong();
								String[] savepath = filepath.split("/");
								String savename = savepath[savepath.length - 1];
								// set the default directory that is desktop
								fileChooser.setCurrentDirectory(new File("/Users/wangcongcong/Desktop"));
								fileChooser.setSelectedFile(new File(savename));
								fileChooser.setDialogTitle("download to");
								int i = fileChooser.showSaveDialog(MainWindow.this);
								if (i == JOptionPane.NO_OPTION) {
									try {
										if (filedos != null) {
											filedos.close();
										}
										if (dataInputStreamReceive != null) {
											dataInputStreamReceive.close();
										}
										if (requestPeerSocket != null) {
											requestPeerSocket.close();
										}
										isRun = false;
										return;
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								if (i == JFileChooser.APPROVE_OPTION) {
									progressbar.setValue(0);
									File tofile = fileChooser.getSelectedFile();
									try {
										filedos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tofile)));
										byte[] buffer = new byte[2014];
										int len = 0;
										int passedlen = 0;
										while ((len = dataInputStreamReceive.read(buffer)) != -1) {
											filedos.write(buffer, 0, len);
											passedlen += len;
											statuslabel.setText("file[" + savename + "] have received already: " + passedlen * 100L / length + "%");
											int progressVaule = (int) (passedlen * 100L / length);
											progressbar.setValue(progressVaule);
										}
										JOptionPane.showMessageDialog(MainWindow.this, "uploaded from remote peer");
									} catch (IOException e) {
										e.printStackTrace();
									} finally {
										try {
											if (filedos != null) {
												filedos.close();
											}
											if (dataInputStreamReceive != null) {
												dataInputStreamReceive.close();
											}
											if (requestPeerSocket != null) {
												requestPeerSocket.close();
											}
											isRun = false;
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}

			}).start();

			// write the file path to destination peer(which could be regarded as a server over here )
			dataOutputStream.writeUTF(filepath);
			dataOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * when a user logout, we need call this method to maintain info of users and their files in bundle
	 * 
	 * @param account
	 */
	private void removeClientFromBundle(String account) {
		int index = 0;
		for (int i = 0; i < userAndFilesbundle.size(); i++) {
			if (userAndFilesbundle.get(i).getUser().equals(account)) {
				break;
			}
			index++;
		}
		userAndFilesbundle.remove(index);
	}

	/**
	 * when one peer removed a file, we need call this method to maintain info of users and their files in bundle
	 * 
	 * @param account
	 * @param filepath
	 */
	private void removeFilePathFromBundle(String account, String filepath) {
		for (int i = 0; i < userAndFilesbundle.size(); i++) {
			if (userAndFilesbundle.get(i).getUser().equals(account)) {
				List<String> filepaths = userAndFilesbundle.get(i).getFilepathes();
				for (String string : filepaths) {
					if (string.equals(filepath)) {
						userAndFilesbundle.get(i).getFilepathes().remove(filepath);
						break;
					}
				}
			}
		}

	}

	/**
	 * when one peer uploaded a file, we need call this method to maintain user and file bundle
	 * 
	 * @param account
	 * @param filepath
	 */
	private void addFilepathToBundle(String account, String filepath) {
		for (int i = 0; i < userAndFilesbundle.size(); i++) {
			if (userAndFilesbundle.get(i).getUser().equals(account)) {
				userAndFilesbundle.get(i).getFilepathes().add(filepath);
				break;
			}
		}

	}

	@SuppressWarnings("unchecked")
	/**
	 * this method is to refresh list
	 */
	private void refreshList() {
		try {
			userAndFilesbundle = (ArrayList<UserAndFile>) ois.readObject();
			refresh();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	public void refresh() {
		// before refresh lists, we need to remove all already-existed entries
		jList1Model.removeAllElements();
		jList2Model.removeAllElements();
		jList1Model.addElement(userName);

		// iterate this bundle
		for (UserAndFile userAndFile : userAndFilesbundle) {
			// if detecting the current user, execute the following codes
			if (userAndFile.getUser().equals(userName)) {
				// get a list of all file paths affiliated to this current user
				// the list below encapsulate file paths info
				List<String> filepaths = userAndFile.getFilepathes();
				// iterate file_paths list
				for (String string : filepaths) {
					jList2Model.addElement(string);
				}
			}
			// if not the current user, execute the following codes
			else {
				jList1Model.addElement(userAndFile.getUser());
			}
		}

	}

}
