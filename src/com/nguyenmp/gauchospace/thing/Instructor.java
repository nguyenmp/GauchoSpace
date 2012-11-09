package com.nguyenmp.gauchospace.thing;

/**
 * Represents an Instructor account on GauchoSpace
 * @author Mark Nguyen
 * 
 */
public class Instructor extends User {
	@Override
	public String toString() {
		return String.format("Name: %s; Url: %s", mName, mUrl);
	}
}
