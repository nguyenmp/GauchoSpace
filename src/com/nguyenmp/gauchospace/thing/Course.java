package com.nguyenmp.gauchospace.thing;

import java.util.ArrayList;
import java.util.List;

public class Course {
	private String mName = null;
	private String mUrl = null;
	private String mSummary = null;
	private String mTitle = null;
	private String mQuarter = null;
	private List<Instructor> mInstructors = null;
	
	public Course() {
		mInstructors = new ArrayList<Instructor>();
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public void setQuarter(String quarter) {
		mQuarter = quarter;
	}
	
	public String getQuarter() {
		return mQuarter;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setUrl(String url) {
		mUrl = url;
	}
	
	public String getUrl() {
		return mUrl;
	}
	
	public String getSummary() {
		return mSummary;
	}

	public void setSummary(String summary) {
		mSummary = summary;
	}

	public List<Instructor> getInstructors() {
		return mInstructors;
	}
	
	public String toString() {
		return String.format("Name: %s; Url: %s; Summary: %s; Instructors: %s", mName, mUrl, mSummary, mInstructors);
	}
}
