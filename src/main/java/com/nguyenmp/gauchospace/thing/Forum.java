package com.nguyenmp.gauchospace.thing;

import java.io.Serializable;


public class Forum implements Serializable {
	private String mName = null,
			mDescriptionHtml = null;
	private boolean mSubscribed = false;
	private int mNumDiscussions = 0;
	private int mID = 0;
	
	/**
	 * Sets the name of this forum.
	 * @param name the name of this forum in plaintext
	 */
	public void setName(String name) {
		mName = name;
	}
	
	/**
	 * Returns the name of this forum.
	 * @return the name of this forum in plaintext.
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * Sets the description of this forum.
	 * @param description The description as HTML.
	 */
	public void setDescription(String description) {
		mDescriptionHtml = description;
	}
	
	/**
	 * Gets the description of this forum
	 * @return The description as HTML.
	 */
	public String getDescription() {
		return mDescriptionHtml;
	}
	
	/**
	 * Sets the forum ID of this forum.
	 * @param id the forum ID as an int.
	 */
	public void setID(int id) {
		mID = id;
	}
	
	/**
	 * Gets the forum ID of this forum.
	 * @return the id of the forum as an int.
	 */
	public int getID() {
		return mID;
	}
	
	/**
	 * Sets whether the user is subscribed to this forum.
	 * @param subscribed true if subscribed, false if otherwise
	 */
	public void setSubscribed(boolean subscribed) {
		mSubscribed = subscribed;
	}
	
	/**
	 * returns whether or not the user is subscribed to this forum.
	 * @return true if subscribed, false if otherwise
	 */
	public boolean getSubscribed() {
		return mSubscribed;
}
	
	/**
	 * Sets the number of discussions in this forum.
	 * @param discussions the number of discusssions.
	 */
	public void setNumberOfDiscussions(int discussions) {
		mNumDiscussions = discussions;
	}
	
	/**
	 * Returns the number of disucssions in this forum.
	 * @return the number of discussions in this forum as an integer.
	 */
	public int getNumberOfDiscussions() {
		return mNumDiscussions;
	}
	
	@Override
	public String toString() {
		return String.format("Name:%s, Description: %s, Subscribed: %s, Discussions: %d, ID: %d", mName, mDescriptionHtml, mSubscribed, mNumDiscussions, mID);
	}
}
