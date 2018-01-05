package com.cc.p2p.thread;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * this is a class for handing file downloading requested by peers and this is a thread on the response peer side to handle file upload request by other peers
 * 
 * @author wangcongcong
 *
 */
public class PeerHandleThread implements Runnable {
	private Socket peerSocket;
	private BufferedReader br;
	private PrintStream ps;
	private DataInputStream dataInputStreamReceive;
	private DataOutputStream dataOutputStreamUploade;

	public PeerHandleThread(Socket peerSocket) {

		// to get the peerSocket passed from PeerServerThread
		this.peerSocket = peerSocket;
		try {
			// get input and output stream perspectively by peerSocket
			dataOutputStreamUploade = new DataOutputStream(peerSocket.getOutputStream());
			dataInputStreamReceive = new DataInputStream(peerSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {

			// to read the filepath sent from the request peer
			String filepath = dataInputStreamReceive.readUTF();

			// new a file by file name
			File file = new File(filepath);

			// get file input stream by encapsulation layer by layer
			DataInputStream fileInputStream = new DataInputStream(
					new BufferedInputStream(new FileInputStream(file)));
			// write a flag whose value is o which means successful connection
			dataOutputStreamUploade.writeInt(0);
			dataOutputStreamUploade.flush();
			// tell the requesting peer the length of the requested file so that it can record the progress of downloading
			dataOutputStreamUploade.writeLong(file.length());
			dataOutputStreamUploade.flush();
			int buffersize = 1024;
			byte[] buffer = new byte[buffersize];
			int read = 0;
			// from here, to upload a file to the requesting peer, there will include the following steps:
			// step 1: to read content from the destination file to buffer
			// step 2: transmit the content of buffer to the socket output steam and ultimately be sent to the requesting peer
			while ((read = fileInputStream.read(buffer)) != -1) {
				dataOutputStreamUploade.write(buffer, 0, read);
			}
			dataOutputStreamUploade.flush();

			// to close relevant stream and socket
			fileInputStream.close();
			dataOutputStreamUploade.close();
			dataInputStreamReceive.close();
			peerSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
