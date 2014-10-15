package com.nguyenmp.gauchospace.thing;

import java.io.Serializable;

/**
 * Represents an Instructor account on GauchoSpace
 * @author Mark Nguyen
 * 
 */
public class Instructor extends User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8860943529219188498L;

	@Override
	public String toString() {
		return String.format("Name: %s; Url: %s", mName, mUrl);
	}
}
