package com.cc.p2p.bean;

import java.io.Serializable;

/**
 * this is a class used for the object transmitted between peers and center server
 * 
 * @author wangcongcong
 *
 */

public class InfoBean implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * declare basic variables which will be used in the transmission
	 */
	private int port;
	private String ip;
	private String account;
	private String password;
	private String message_content;
	private String filepath;

	/**
	 * this method is for testing
	 */
	@Override
	public String toString() {
		return "MessageBean [port=" + port + ", ip=" + ip + ", account=" + account + ", password=" + password
				+ ", message_content=" + message_content + ", filepath=" + filepath + "]";
	}

	/**
	 * a constructor with general info
	 * 
	 * @param message
	 */
	public InfoBean(String message) {
		this.message_content = message;
	}

	/**
	 * a constructor with four params needed and its usage is to new a object with specific details
	 * 
	 * @param port
	 * @param ip
	 * @param account
	 * @param password
	 * @param message_content
	 * @param filepath
	 */
	public InfoBean(int port, String ip, String account, String password, String message_content,
			String filepath) {
		super();
		this.port = port;
		this.ip = ip;
		this.account = account;
		this.password = password;
		this.message_content = message_content;
		this.filepath = filepath;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMessage_content() {
		return message_content;
	}

	public void setMessage_content(String message_content) {
		this.message_content = message_content;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

}
