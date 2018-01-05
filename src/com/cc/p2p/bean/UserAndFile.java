package com.cc.p2p.bean;

import java.io.Serializable;
/**
 * the class is used for storing user names and corresponding file paths 
 */
import java.util.ArrayList;
import java.util.List;

/**
 * this is a very important class used for staying real-time updated data in user list and file list
 * 
 * @author wangcongcong
 *
 */
public class UserAndFile implements Serializable {

	private static final long serialVersionUID = 1L;
	private String user;
	// one user maybe have muti-filepaths, so a collection is declared here
	private List<String> filepathes = new ArrayList<String>();

	/**
	 * a constructor two params needed
	 * 
	 * @param user
	 * @param filepaths
	 */
	public UserAndFile(String user, List<String> filepaths) {
		this.user = user;
		this.filepathes = filepaths;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public List<String> getFilepathes() {
		return filepathes;
	}

	public void setFilepathes(List<String> filepathes) {
		this.filepathes = filepathes;
	}

	@Override
	public String toString() {
		return "UserAndFile [user=" + user + ", filepathes=" + filepathes + "]";
	}

}
