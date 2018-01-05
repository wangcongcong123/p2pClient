package com.cc.p2p.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * this is a thread class to listen if any peer requests a file downloading or not
 * 
 * @author wangcongcong
 *
 */
public class PeerServerThread implements Runnable {

	private ServerSocket peerServerSocket;
	private Socket peerSocket;

	public PeerServerThread(ServerSocket peerServerSocket) {
		this.peerServerSocket = peerServerSocket;
	}

	@Override
	public void run() {
		while (true) {
			try {
				// if there is a peer asking for downloading file and then distribute a Thread to it to handle this task individually
				peerSocket = peerServerSocket.accept();
				new Thread(new PeerHandleThread(peerSocket)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
