package com.nguyenmp.gauchospace.thing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {
	public static final long serialVersionUID = 6265010175935790470L;

	public String mName = null;
	public String mUrl = null;
	public String mSummary = null;
	public String mTitle = null;
	public String mQuarter = null;
	public List<Instructor> mInstructors = new ArrayList<Instructor>();
	
	public String toString() {
		return String.format("Name: %s; Url: %s; Summary: %s; Instructors: %s", mName, mUrl, mSummary, mInstructors);
	}
}
