/* Copyright (C) 2012 Mark Nguyen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.nguyenmp.gauchospace;

import com.nguyenmp.gauchospace.parser.*;
import com.nguyenmp.gauchospace.thing.Discussion;
import com.nguyenmp.gauchospace.thing.Forum;
import com.nguyenmp.gauchospace.thing.User;
import com.nguyenmp.gauchospace.thing.grade.GradeFolder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A Java class that interfaces with the HTML frontend of GauchoSpace.
 * @author Mark Nguyen
 */
public class GauchoSpaceClient {
	
	/**
	 * Retrieves the details of a user
	 * @param url the url of the user page
	 * @param session the session of the logged in user to act as
	 * @return a User object containing the scraped data
	 * @throws ClientProtocolException in case of an http protocol error
	 * @throws IOException in case of a problem or the connection was aborted
     * @throws XMLException XML could not be parsed
	 */
	public static User getUserProfile(String url, Session session) throws IOException, XMLException {
		//Create http client and context from cookies
		HttpClient client = getClient();
		HttpContext context = session.asContext();
		
		//Create get request
		HttpGet get = new HttpGet(url);
		
		//Execute request
		HttpResponse response = client.execute(get, context);
		
		//Get the entity of the response
		HttpEntity entity = response.getEntity();
		
		//Get content of response
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		String line;
		StringBuilder coursesHtml = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			coursesHtml.append(line);
		}
		String contentString = coursesHtml.toString();
		
		//Close connection
		get.abort();
		
		//Process html string to User object
		return UserParser.getUserFromHtml(contentString);
    }

	public static List<User> getParticipantsFromCourse(int courseId, Session session) throws IOException, XMLException {
		//Create client and context
		HttpClient client = getClient();
		HttpContext context = session.asContext();
		
		//Create GET request
		//We need parameter mode to be 0 for "less detailed" in case default is more detailed
		//We need param perpage to be 50000 to get all participants (this is how the website does it)
		HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/user/index.php?id=" + courseId + "&mode=0&perpage=50000");
		
		//Do get and store response
		HttpResponse response = client.execute(get, context);
		
		//Get content of response
		HttpEntity entity = response.getEntity();
		
		//Read content
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		String line;
		StringBuilder participantsHtml = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			participantsHtml.append(line);
		}
		
		//Close connection
		get.abort();
		
		//Parse html and scrape participants
		String participantsHtmlString = participantsHtml.toString();
		return ParticipantsParser.getParticipantsFromHtml(participantsHtmlString);
	}
	
	public static GradeFolder getGrade(int courseId, Session session) throws IOException, XMLException {
		//Create client and context
		HttpClient client = getClient();
		HttpContext context = session.asContext();
		
		//Create GET
		HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/grade/report/user/index.php?id=" + courseId);
		
		//Do GET
		HttpResponse response = client.execute(get, context);
		
		//Get content of response
		HttpEntity entity = response.getEntity();
		
		//Read content
		String courseHtml = getStringFromEntity(entity);
		
		//Close connection
		get.abort();
		
		//Compile and parse courses
		return GradeParser.getGradeFromHtml(courseHtml);
	}
	
	/**
	 * Gets a list of forums that belong under the specified course.
	 * @param courseID The ID of the course under which these forums belong.
     * @param session the session of the logged in user to act as
	 * @return A list of Forums that belong to this Course.  Null if it 
	 * couldn't be parsed. 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static List<Forum> getForums(int courseID, Session session) throws IOException {
		//Create client and context
		HttpClient client = getClient();
		HttpContext context = session.asContext();
		
		//Create GET
		HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/mod/forum/index.php?id=" + courseID);
		
		//Do GET
		HttpResponse response = client.execute(get, context);
		
		//Get content of response
		HttpEntity entity = response.getEntity();
		
		//Read content
		String forumsHtml = getStringFromEntity(entity);
		
		//Close connection
		get.abort();
		
		List<Forum> forums = null;
		
		try {
			forums = ForumsParser.getForums(forumsHtml);
		} catch (XMLException e) {
			e.printStackTrace();
		}
		
		return forums;
	}
	
	/**
	 * Gets a list of forums that belong under the specified course.
	 * @param forumID The ID of the forum to fetch the discussions from
     * @param session the session of the logged in user to act as
	 * @return A list of Forums that belong to this Course.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static List<Discussion> getDiscussions(int forumID, Session session) throws IOException {
		//Create client and context
		HttpClient client = getClient();
		HttpContext context = session.asContext();
		
		//Create GET
		HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/mod/forum/view.php?f=" + forumID);
		
		//Do GET
		HttpResponse response = client.execute(get, context);
		
		//Get content of response
		HttpEntity entity = response.getEntity();
		
		//Read content
		String forumHtml = getStringFromEntity(entity);
		
		//Close connection
		get.abort();
		
		List<Discussion> discussions = null;
		
		try {
			discussions = ForumParser.getDiscussions(forumHtml);
		} catch (XMLException e) {
			e.printStackTrace();
		}
		
		return discussions;
	}

	/**
	 * Reads the content of an entity into a String and returns that String.  If the 
	 * stream is currently blocked, this function will also block until the end of 
	 * the stream is encountered or an exception is encountered.
	 * @param entity The entity where the content will be read from.
	 * @return The String representation of the content of the entity.
	 * @throws IOException  If an I/O error occurs
	 */
	public static String getStringFromEntity(HttpEntity entity) throws IOException {
		//Read content
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		String line;
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * Creates an HttpClient that with a UserAgent equal to "GauchoSpaceClient by Mark Nguyen @ mpnguyen@umail.ucsb.edu".
	 * @return An HttpClient with the preset user-agent.
	 */
	public static CloseableHttpClient getClient() {
		//Create client
        return HttpClients.custom()
                .setUserAgent("GauchoSpaceClient by Mark Nguyen @ mpnguyen@umail.ucsb.edu")
                .build();
	}
	
}
