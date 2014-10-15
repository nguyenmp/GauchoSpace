package com.nguyenmp.gauchospace.thing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Week implements Serializable {
	private String mTitle = null, mSummary = null, mHtml;
	private boolean mCurrent = false;
	private List<Resource> mContent = null;
	public Week() {
		mContent = new ArrayList<Resource>();
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public void setSummary(String summary) {
		mSummary = summary;
	}
	
	public String getSummary() {
		return mSummary;
	}
	
	public void setHtml(String html) {
		mHtml = html;
	}
	
	public String getHtml() {
		return mHtml;
	}
	
	public void setCurrent(boolean current) {
		mCurrent = current;
	}
	
	public boolean getCurrent() {
		return mCurrent;
	}
	
	public List<Resource> getContent() {
		return mContent;
	}
	
	public String toString() {
		return String.format("Title: %s; Summary: %s; Current: %s; Content: %s", mTitle, mSummary, mCurrent, mContent);
	}
}
