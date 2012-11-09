package com.nguyenmp.gauchospace.thing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class User implements Comparable<User> {
	String mUrl = null;
	String mName = null;
	String mSummary = null;
	String mAvatarUrl = null;
	String mAvatarDescription = null;
	List<Attribute> mAttributes = null;
	
	public User() {
		mAttributes = new ArrayList<Attribute>();
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setAvatarUrl(String avatarUrl) {
		mAvatarUrl = avatarUrl;
	}
	
	public String getAvatarUrl() {
		return mAvatarUrl;
	}
	
	public void setSummary(String summary) {
		mSummary = summary;
	}
	
	public String getSummary() {
		return mSummary;
	}
	
	public void setAvatarDescription(String description) {
		mAvatarDescription = description;
	}
	
	public String getAvatarDescription() {
		return mAvatarDescription;
	}
	
	public void addAttributes(Collection<? extends Attribute> attributes) {
		mAttributes.addAll(attributes);
	}
	
	public void addAttribute(Attribute attribute) {
		mAttributes.add(attribute);
	}
	
	public List<Attribute> getAttributes() {
		return mAttributes;
	}
	
	public String toString() {
		return String.format("Name: %s; Summary: %s;", mName, mAttributes);
	}
	
	public void setUrl(String url) {
		mUrl = url;
	}
	
	public String getUrl() {
		return mUrl;
	}


	public static class Attribute {
		private String key = null;
		private String value = null;
		
		public Attribute(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public String getKey() {
			return key;
		}
		
		public String getValue() {
			return value;
		}
		
		public String toString() {
			return "[" + key + ":" + value +"]";
		}
	}
	
	@Override
	public int compareTo(User user) {
//		if (object instanceof User) 
			return getName().compareToIgnoreCase(user.getName());
//		
//		return 0;
	}
}