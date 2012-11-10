package com.nguyenmp.gauchospace.thing;

import java.io.Serializable;

public class Post implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8571847493223738070L;
	
	public final int postID;
	public final String timestamp;
	public final User poster;
	public final String title;
	public final Post parent;
	public final Attachment attachment;
	
	public Post(int postID, String timestamp, User poster, String title, Post parent, Attachment attachment) {
		this.postID = postID;
		this.timestamp = timestamp;
		this.poster = poster;
		this.title = title;
		this.parent = parent;
		this.attachment = attachment;
	}
	
	public Post(int postID, String timestamp, User poster, String title, Post parent) {
		this(postID, timestamp, poster, title, parent, null);
	}
	
	public Post(int postID, String timestamp, User poster, String title) {
		this(postID, timestamp, poster, title, null, null);
	}
	
	public Post(int postID, String timestamp, User poster, String title, Attachment attachment) {
		this(postID, timestamp, poster, title, null, attachment);
	}
	
	@Override
	public String toString() {
		return String.format("ID: %s, Timestamp: %s, Started By: %s, Title: %s, Parent: " +
				"%s, Attachment: %s", postID, timestamp, poster, title, parent, attachment);
	}
}
