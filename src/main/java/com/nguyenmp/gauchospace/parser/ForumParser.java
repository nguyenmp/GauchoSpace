/* Copyright (C) 2012 Mark
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.nguyenmp.gauchospace.parser;

import com.nguyenmp.gauchospace.thing.Discussion;
import com.nguyenmp.gauchospace.thing.Post;
import com.nguyenmp.gauchospace.thing.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ForumParser {
	public static List<Discussion> getDiscussions(String html) throws IOException, XMLException {
		Document doc = XMLParser.getDocumentFromString(html);
		
		Element body = (Element) XMLParser.getChildFromName(doc.getDocumentElement(), "body");
		Element page = XMLParser.getChildFromAttribute(body, "id", "page");
		Element content = XMLParser.getChildFromAttribute(page, "id", "content");
		Element table = (Element) XMLParser.getChildFromName(content, "table");
		
		if (table == null) return new ArrayList<>();
		
		
		Element tbody = (Element) XMLParser.getChildFromName(table, "tbody");
		
		return getListFromTable(tbody);
	}

	private static List<Discussion> getListFromTable(Element table) throws XMLException {
		List<Discussion> discussions = new ArrayList<>();
		
		for (int i = 0; i < table.getChildNodes().getLength(); i++) {
			discussions.add(getDiscussionFromRow(table.getChildNodes().item(i)));
		}
		
		return discussions;
	}

	private static Discussion getDiscussionFromRow(Node item) throws XMLException {
		String string = XMLParser.nodeToString(item);
		
		int start, end;
		
		//Get the discussion id
		start = string.indexOf("?d=") + "?d=".length();
		end = string.indexOf("\"", start);
		int id = Integer.parseInt(string.substring(start, end));
		
		//Get the discussion title
		start = string.indexOf(">", end) + ">".length();
		end = string.indexOf("</a>", start);
		String title = string.substring(start, end);
		
		//Get the original poster (user)
		//Get the user's url
		start = string.indexOf("https://gauchospace.ucsb.edu/courses/user/view.php?", end);
		end = string.indexOf("\">", start);
		String userURL = string.substring(start, end);
		
		//Get the user's photo
		start = string.indexOf("https://gauchospace.ucsb.edu/courses/user/pix.php/", end);
		if (start == -1) start = string.indexOf("https://gauchospace.ucsb.edu/courses/theme/gaucho/pix/u/");
		end = string.indexOf("\"", start);
		String userPhoto = string.substring(start, end);
		
		//Get the user's name
		start = string.indexOf("Picture of ", end) + "Picture of ".length();
		end = string.indexOf("\"", start);
		String userName = string.substring(start, end);
		
		//Compile the data from the user into a user object
		User user = new User();
		user.setAvatarUrl(userPhoto);
		user.setAvatarDescription("Picture of " + userName);
		user.setName(userName);
		user.setUrl(userURL);
		
		//Get the number of replies
		start = string.indexOf("\"replies\"", end) + "\"replies\"".length();
		start = string.indexOf(">", start) + 1;
		start = string.indexOf(">", start) + 1;
		end = string.indexOf("</a>", start);
		int replies = Integer.parseInt(string.substring(start, end));
		
		//Get the most recent post
		//Get it's creator
		start = string.indexOf("https://gauchospace.ucsb.edu/courses/user/view.php", end);
		end = string.indexOf("\"", start);
		String lastPostUserURL = string.substring(start, end);
		
		//Get the username of the last poster
		start = string.indexOf(">", end) + 1;
		end = string.indexOf("</a>", start);
		String lastPostUserName = string.substring(start, end);
		
		//Compile the scraped data into a user object
		User lastPostUser = new User();
		lastPostUser.setUrl(lastPostUserURL);
		lastPostUser.setName(lastPostUserName);
		
		//Get the post's id
		int postID = 0;
		if (string.indexOf("parent=", end) != -1) {
			start = string.indexOf("parent=", end) + "parent=".length();
			end = string.indexOf("\"", start);
			postID = Integer.parseInt(string.substring(start, end));
		}
		
		//get the post's timestamp
		start = string.indexOf("\">", end) + 2;
		end = string.indexOf("</a>", start);
		String timestamp = string.substring(start, end);
		
		//
		Post lastPost = new Post(postID, timestamp, lastPostUser, null);
		
		//Compile the scraped data and return it
		return new Discussion(title, user, replies, lastPost, id);
	}
}