/* Copyright (C) 2012 Mark Nguyen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.nguyenmp.gauchospace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.nguyenmp.gauchospace.common.Constants;
import com.nguyenmp.gauchospace.parser.CoursesParser;
import com.nguyenmp.gauchospace.parser.ForumParser;
import com.nguyenmp.gauchospace.parser.ForumsParser;
import com.nguyenmp.gauchospace.parser.GradeParser;
import com.nguyenmp.gauchospace.parser.ParticipantsParser;
import com.nguyenmp.gauchospace.parser.UserParser;
import com.nguyenmp.gauchospace.parser.WeeklyOutlineParser;
import com.nguyenmp.gauchospace.parser.WeeklyOutlineParser.UnparsableHtmlException;
import com.nguyenmp.gauchospace.thing.Course;
import com.nguyenmp.gauchospace.thing.Discussion;
import com.nguyenmp.gauchospace.thing.Forum;
import com.nguyenmp.gauchospace.thing.User;
import com.nguyenmp.gauchospace.thing.Week;
import com.nguyenmp.gauchospace.thing.grade.GradeFolder;

/**
 * A Java class that interfaces with the HTML frontend of GauchoSpace.
 * @author Mark Nguyen
 */
public class GauchoSpaceClient {
	/**
	 * Verifies whether or not the user is logged in at the moment.  This is compatible 
	 * with session time outs as well as regular log outs.
	 * @param cookie The cookie of the specified user.
	 * @return True of the cookies are valid, false if they are expired or invalid.
	 * @throws ClientProtocolException in case of an http protocol error
	 * @throws IOException in case of a problem or the connection was aborted or if an I/O error occurs.
	 */
	public static boolean isLoggedIn(CookieStore cookie) throws ClientProtocolException, IOException {
		HttpClient client = getClient();
		HttpContext context = getContext(cookie);

		HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/");
		
		HttpResponse response = client.execute(get, context);
		
		String httpResponse = getStringFromEntity(response.getEntity());
		
		//Clean up
		get.abort();
		client.getConnectionManager().shutdown();
		
		boolean isLoggedIn = httpResponse.contains(Constants.loggedInString);
		
		return isLoggedIn;
	}
	
	/**
	 * Logs the owner of the cookies out.  If the cookies are invalid, nothing is done. 
	 * @param cookies The cookies representing the user's current session.  must not be null.
	 * @return true if cookies are invalid now. false if they are still valid
	 * @throws ClientProtocolException in case of an http protocol error
	 * @throws IOException in case of a problem or the connection was aborted or if an I/O error occurs
	 */
	public static boolean logout(CookieStore cookies) throws ClientProtocolException, IOException {
		if (isLoggedIn(cookies) == false) return true;
		
		String sessionKey = getSessionKey(cookies);
		HttpClient client = getClient();
		HttpContext context = getContext(cookies);
		
		HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/login/logout.php?sesskey=" + sessionKey);
		
		client.execute(get, context);
		
		//Cleanup
		get.abort();
		client.getConnectionManager().shutdown();
		
		return !isLoggedIn(cookies);
	}
	
	/**
	 * Returns the session key from the cookies.  The session key is used for 
	 * many data-altering actions with Moodle/GauchoSpace.
	 * @param cookies must be valid cookies that represent the currently logged in user.
	 * @return A string representation of the Session Key.  null if the session key could not be found.
	 * @throws ClientProtocolException in case of an http protocol error
	 * @throws IOException in case of a problem or the connection was aborted or if an I/O error occurs.
	 */
	private static String getSessionKey(CookieStore cookies) throws ClientProtocolException, IOException {
		HttpClient client = getClient();
		HttpContext context = getContext(cookies);
		
		HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/");
		
		HttpResponse response = client.execute(get, context);
		
		String htmlString = getStringFromEntity(response.getEntity());
		get.abort();
		client.getConnectionManager().shutdown();
		int start = htmlString.indexOf("href=\"https://gauchospace.ucsb.edu/courses/login/logout.php?sesskey=") + "href=\"https://gauchospace.ucsb.edu/courses/login/logout.php?sesskey=".length();
		int end = htmlString.indexOf("\">Logout</a>");
		
		String sessionKey = null;
		if (start >=0 && end <= htmlString.length() && start <= end) sessionKey = htmlString.substring(start, end);
		return sessionKey;
	}
	
	/**
	 * Logs into GauchoSpace with the given credentials.
	 * @param username the username of the user to log in.
	 * @param password the password of the user to log in.
	 * @return The CookieStore that represents the logged-in session of the user or null if login failed
	 * @throws ClientProtocolException in case of an http protocol error
	 * @throws IOException in case of a problem or the connection was aborted or if an I/O error occurs.
	 * @throws UnsupportedEncodingException if the default encoding isn't supported
	 */
	public static CookieStore login(String username, String password) throws ClientProtocolException, IOException, UnsupportedEncodingException {
		HttpClient client = getClient();
		HttpContext context = getContext(null);
		
		//Get pre-cookies
		HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/login/index.php");
		client.execute(get, context);
		get.abort();
		
		//Create post
		HttpPost post = new HttpPost("https://gauchospace.ucsb.edu/courses/login/index.php");
		
		//Set post
		post.setEntity(getBody(username, password));
		
		//Get response
		HttpResponse response = client.execute(post, context);

		//Get the entity of the response
		HttpEntity entity = response.getEntity();
		
		//Get content of response
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		String line = null;
		StringBuilder coursesHtml = new StringBuilder();
		while ((line = reader.readLine()) != null) {
            //System.out.println(line);
			coursesHtml.append(line);
		}
		String contentString = coursesHtml.toString();

		post.abort();
		
		if (contentString.contains(Constants.loggedInString)) {
			//Return logged in client's cookies
            //System.out.println("logged in string is contained! saving cookie...");
			CookieStore cookieStore = getCookies(context);
			return cookieStore;
		} else {
			return null;
		}
		
	}
	
	/**
	 * Retrieves the details of a user
	 * @param url the url of the user page
	 * @param cookies the cookies of the client viewing
	 * @return a User object containing the scraped data
	 * @throws ClientProtocolException in case of an http protocol error
	 * @throws IOException in case of a problem or the connection was aborted
	 * @throws SAXNotRecognizedException If the feature value can't be assigned or retrieved.
	 * @throws SAXNotSupportedException When the XMLReader recognizes the feature name but cannot set the requested value.
	 * @throws TransformerFactoryConfigurationError Thrown if the implementation is not available or cannot be instantiated.
	 * @throws TransformerException When it is not possible to create a Transformer instance or if an unrecoverable error occurs during the course of the transformation.
	 */
	public static User getUserProfile(String url, CookieStore cookies) throws ClientProtocolException, IOException, SAXNotRecognizedException, SAXNotSupportedException, TransformerFactoryConfigurationError, TransformerException {
		//Create httpclient and context from cookies
		HttpClient client = getClient();
		HttpContext context = getContext(cookies);
		
		//Create get request
		HttpGet get = new HttpGet(url);
		
		//Execute request
		HttpResponse response = client.execute(get, context);
		
		//Get the entity of the response
		HttpEntity entity = response.getEntity();
		
		//Get content of response
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		String line = null;
		StringBuilder coursesHtml = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			coursesHtml.append(line);
		}
		String contentString = coursesHtml.toString();
		
		//Close connection
		get.abort();
		
		//Process html string to User object
		User user = UserParser.getUserFromHtml(contentString);
		
		return user;
	}
	
	/**
	 * Gets the Courses of the user who owns the cookies.
	 * @param cookies The cookies of a logged in session.
	 * @return The list of courses 
	 * @throws IOException in case of a problem or the connection was aborted, 
	 * if the stream could not be created, or if an I/O error occured
	 * @throws SAXNotRecognizedException If the feature value can't be 
	 * assigned or retrieved.
	 * @throws SAXNotSupportedException When the XMLReader recognizes 
	 * the feature name but cannot set the requested value.
	 * @throws TransformerFactoryConfigurationError Thrown if the 
	 * implementation is not available or cannot be instantiated.
	 * @throws TransformerException When it is not possible to create 
	 * a Transformer instance or if an unrecoverable error occurs 
	 * during the course of the transformation.
	 */
	public static List<Course> getCourses(CookieStore cookies) throws ClientProtocolException, IOException, SAXNotRecognizedException, SAXNotSupportedException, TransformerFactoryConfigurationError, TransformerException {
		//Create client and context
		HttpClient client = getClient();
		HttpContext context = getContext(cookies);
		
		//Create GET
		HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/my/");
		
		//Do GET
		HttpResponse response = client.execute(get, context);
		
		//Get content of response
		HttpEntity entity = response.getEntity();
		
		//Read content
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		String line = null;
		StringBuilder coursesHtml = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			coursesHtml.append(line);
		}
		String contentString = coursesHtml.toString();
		
		//Close connection
		get.abort();
		
		//Compile and parse courses
		List<Course> courses = CoursesParser.getCoursesFromHtml(contentString);
		System.out.println(courses);
		//Return the parsed content
		return courses;
	}
	public static List<User> getParticipantsFromCourse(int courseId, CookieStore cookies) throws ClientProtocolException, IOException, SAXNotRecognizedException, SAXNotSupportedException, TransformerFactoryConfigurationError, TransformerException, UnparsableHtmlException {
		//Create client and context
		HttpClient client = getClient();
		HttpContext context = getContext(cookies);
		
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
		String line = null;
		StringBuilder participantsHtml = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			participantsHtml.append(line);
		}
		
		//Close connection
		get.abort();
		
		//Parse html and scrape participants
		String participantsHtmlString = participantsHtml.toString();
		List<User> participants = ParticipantsParser.getParticipantsFromHtml(participantsHtmlString);
		
		return participants;
	}
	
	public static List<Week> getWeeklyOutlineFromCourse(int courseId, CookieStore cookies) throws TransformerFactoryConfigurationError, IOException, SAXNotRecognizedException, SAXNotSupportedException, TransformerException, com.nguyenmp.gauchospace.parser.WeeklyOutlineParser.UnparsableHtmlException {
		//Create client and context
		HttpClient client = getClient();
		HttpContext context = getContext(cookies);
		
		
		//Create GET
		HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/course/view.php?id=" + courseId);

		//Do GET
		HttpResponse response = client.execute(get, context);
		
		//Get content of response
		HttpEntity entity = response.getEntity();
		
		//Read content
		String courseHtml = getStringFromEntity(entity);
		
		//Close connection
		get.abort();
		
		//Compile and parse courses
		List<Week> calendar = WeeklyOutlineParser.getWeeklyOutlineFromHtml(courseHtml);
		
		//Return the parsed content
		return calendar;
	}
	
	public static GradeFolder getGrade(int courseId, CookieStore cookies) throws IOException, SAXNotRecognizedException, SAXNotSupportedException, TransformerFactoryConfigurationError, TransformerException {
		//Create client and context
		HttpClient client = getClient();
		HttpContext context = getContext(cookies);
		
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
		GradeFolder grade = GradeParser.getGradeFromHtml(courseHtml);
		
		//Return the parsed content
		return grade;
	}
	
	/**
	 * Gets a list of forums that belong under the specified course.
	 * @param courseID The ID of the course under which these forums belong.
	 * @param cookies The cookies of the user.
	 * @return A list of Forums that belong to this Course.  Null if it 
	 * couldn't be parsed. 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static List<Forum> getForums(int courseID, CookieStore cookies) throws ClientProtocolException, IOException {
		//Create client and context
		HttpClient client = getClient();
		HttpContext context = getContext(cookies);
		
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
		} catch (SAXNotRecognizedException e) {
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		return forums;
	}
	
	/**
	 * Gets a list of forums that belong under the specified course.
	 * @param courseID The ID of the course under which these forums belong.
	 * @param cookies The cookies of the user.
	 * @return A list of Forums that belong to this Course.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static List<Discussion> getForum(int forumID, CookieStore cookies) throws ClientProtocolException, IOException {
		//Create client and context
		HttpClient client = getClient();
		HttpContext context = getContext(cookies);
		
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
		} catch (SAXNotRecognizedException e) {
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
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
	private static String getStringFromEntity(HttpEntity entity) throws IOException {
		//Read content
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
		}
		
		return stringBuilder.toString();
	}
	
	/**
	 * Creates and initializes a UrlEncodedFormEntity containing the login 
	 * information.
	 * @param username the username to put in the entity.
	 * @param password the password to put into the entity.
	 * @return the UrlEncodedFormEntity that will contain the username, password, and testcookie set to 1.
	 * @throws UnsupportedEncodingException if the default encoding isn't supported
	 */
	private static UrlEncodedFormEntity getBody(String username, String password) throws UnsupportedEncodingException {
		List<NameValuePair> args = new ArrayList<NameValuePair>();
		args.add(new BasicNameValuePair("username", username));
		args.add(new BasicNameValuePair("password", password));
		args.add(new BasicNameValuePair("testcookies", "1"));
		
		return new UrlEncodedFormEntity(args);
	}
	
	/**
	 * Creates an HttpClient that with a UserAgent equal to "GauchoSpaceClient by Mark Nguyen @ mpnguyen@umail.ucsb.edu".
	 * @return An HttpClient with the preset user-agent.
	 */
	private static HttpClient getClient() {
		//Create client
		HttpClient client = new DefaultHttpClient();
		
		//Set useragent
		HttpProtocolParams.setUserAgent(client.getParams(), "GauchoSpaceClient by Mark Nguyen @ mpnguyen@umail.ucsb.edu");
		
		return client;
	}
	
	/**
	 * Extracts the CookieStore from an HttpContext.
	 * @param context The HttpContext to extract the CookieStore from
	 * @return The CookieStore in the HttpContext.  null if there is no CookieStore or 
	 * the COOKIE_STORE attribute doesn't contain a compatable CookieStore object.
	 */
	private static CookieStore getCookies(HttpContext context) {
		Object attribute = context.getAttribute(ClientContext.COOKIE_STORE);
		
		if (attribute instanceof CookieStore) 
			return (CookieStore) attribute;
		
		else return null;
	}
	
	/**
	 * Creates an HttpContext that contains the cookies given.
	 * @param cookies The cookies that the context will contain
	 * @return The HttpContext containing the given CookeiStore
	 */
	private static HttpContext getContext(CookieStore cookies) {
		HttpContext context = new BasicHttpContext();;
		
		if (cookies == null){
            cookies = new BasicCookieStore();
            //System.out.println("COOKIE WAS NULL!");
        }
		context.setAttribute(ClientContext.COOKIE_STORE, cookies);
		
		return context;
	}
	
}
