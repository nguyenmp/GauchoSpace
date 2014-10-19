package com.nguyenmp.gauchospace.thing;

import java.io.Serializable;


public class Resource implements Serializable {
	public String text = null, imageUrl = null, imageDescription, url = null;

	public String toString() {
		return String.format("Text: %s; ImageUrl: %s; ImageDescription: %s; Url: %s", text, imageUrl, imageDescription, url);
	}
}
