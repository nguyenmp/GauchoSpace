package com.nguyenmp.gauchospace.thing;

public class Attachment {
	public final String title;
	public final String iconURL;
	public final String downloadURL;
	
	public Attachment(String title, String iconURL, String downloadURL) {
		this.title = title;
		this.iconURL = iconURL;
		this.downloadURL = downloadURL;
	}
	
	@Override
	public String toString() {
		return String.format("Title: %s, Icon URL: %s, Download URL: %s", title, iconURL, downloadURL);
	}
}
