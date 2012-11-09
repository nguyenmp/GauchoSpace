package com.nguyenmp.gauchospace.thing;


public class Resource {
	private String mText = null, mImageUrl = null, mImageDescription, mUrl = null;
	
	public Resource(String text, String imageUrl, String imageDescription, String url) {
		mText = text;
		mImageUrl = imageUrl;
		mUrl = url;
		mImageDescription = imageDescription;
	}
	
	public String getText() {
		return mText;
	}
	
	public String getImageUrl() {
		return mImageUrl;
	}
	
	public String getImageDescription() {
		return mImageDescription;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public String toString() {
		return String.format("Text: %s; ImageUrl: %s; ImageDescription: %s; Url: %s", mText, mImageUrl, mImageDescription, mUrl);
	}
}
