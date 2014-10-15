/* Copyright (C) 2012 Mark
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.nguyenmp.gauchospace.thing.grade;

import java.io.Serializable;

public class GradeItem implements Serializable {
	private String mName = null;
	private String mGrade = null;
	private String mRange = null;
	private String mPercentage = null;
	private String mFeedback = null;
	private String mType = null;
	
	public GradeItem() {
	}
	
	public GradeItem(String name) {
		setName(name);
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setGrade(String grade) {
		mGrade = grade;
	}
	
	public String getGrade() {
		return mGrade;
	}
	
	public void setRange(String range) {
		mRange = range;
	}
	
	public String getRange() {
		return mRange;
	}
	
	public void setPercentage(String percentage) {
		mPercentage = percentage;
	}
	
	public String getPercentage() {
		return mPercentage;
	}
	
	public void setFeedback(String feedback) {
		mFeedback = feedback;
	}
	
	public String getFeedback() {
		return mFeedback;
	}
	
	public void setType(String type) {
		mType = type;
	}
	
	public String getType() {
		return mType;
	}
	
	@Override
	public String toString() {
		return String.format("%s\t%s\t%s\t%s\t%s", getName(), getGrade(), getRange(), getPercentage(), getFeedback());
	}
}
