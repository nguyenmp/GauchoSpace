package com.nguyenmp.gauchospace.thing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {
	public static final long serialVersionUID = 6265010175935790471L;

	public String mName = null;
	public String mUrl = null;
	public String mTitle = null;
	public String mQuarter = null;

	public String toString() {
		return String.format("Name: %s; Url: %s; Title: %s; Quarter: %s", mName, mUrl, mTitle, mQuarter);
	}
}
