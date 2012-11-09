/* Copyright (C) 2012 Mark
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.nguyenmp.gauchospace.thing.grade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GradeFolder extends GradeItem {
	private List<GradeItem> mItems = null;
	private String mGradeName = null;
	private GradeFolder mParent = null;
	private String mTitle = null;
	
	public GradeFolder(String name) {
		super(name);
		mTitle = name;
		mItems = new ArrayList<GradeItem>();
	}
	
	public void setParent(GradeFolder parent) {
		mParent = parent;
	}
	
	public GradeFolder getParent() {
		return mParent;
	}
	
	public void addItem(GradeItem item) {
		mItems.add(item);
	}
	
	public void addAllItems(Collection<? extends GradeItem> item) {
		mItems.addAll(item);
	}
	
	public List<GradeItem> getItems() {
		return mItems;
	}
	
	public String getGradeName() {
		return mGradeName;
	}
	
	public void setGradeName(String gradeName) {
		mGradeName = gradeName;
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		
//		//Print name
//		builder.append(getName() + "\n");
//		
//		//Print each child
//		for (GradeItem item : mItems) {
//			if (item instanceof GradeFolder) 
//				builder.append("" + ((GradeFolder) item).toString() + "\n");
//			else builder.append("" + item.toString() + "\n");
//		}
//		
////		Print summary
////		builder.append(getGradeName() + " " + getGrade() + " " + getRange() + " " + getPercentage() + " " + getFeedback());
//		
//		//Return generated string
//		return builder.toString();
//	}
}
