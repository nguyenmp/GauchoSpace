package com.nguyenmp.gauchospace.thing;

import java.io.Serializable;


public class Discussion implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6778561939093804937L;
	public final String name;
	public final User startedBy;
	public final int replies;
	public final Post lastPost;
	public final int id;
	
	public Discussion(String name, User startedBy, int replies, Post lastPost, int id) {
		this.name = name;
		this.startedBy = startedBy;
		this.replies = replies;
		this.id = id;
		this.lastPost = lastPost;
	}
	
	@Override
	public String toString() {
		return String.format("Name: %s, Started By: %s, Replies: %d, Last Post: %s, ID: %d", name, startedBy, replies, lastPost, id);
	}

}
