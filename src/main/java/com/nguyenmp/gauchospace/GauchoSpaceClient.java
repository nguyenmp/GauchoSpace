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
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * @throws IOException in case of a problem or the connection was aborted
     * @throws XMLException XML could not be parsed
	 */
	public static User getUserProfile(String url, Session session) throws IOException, XMLException {
		//Create http client and context from cookies
		OkHttpClient client = getClient(session);
        Request get = new Request.Builder().url(url).build();

        Response response = client.newCall(get).execute();
		String contentString = response.body().string();
		
		//Process html string to User object
		return UserParser.getUserFromHtml(contentString);
    }

	public static List<User> getParticipantsFromCourse(int courseId, Session session) throws IOException, XMLException {
		//Create client and context
		OkHttpClient client = getClient(session);

        //Create GET request
        //We need parameter mode to be 0 for "less detailed" in case default is more detailed
        //We need param perpage to be 50000 to get all participants (this is how the website does it)
        Request get = new Request.Builder().url("https://gauchospace.ucsb.edu/courses/user/index.php?id=" + courseId + "&mode=0&perpage=50000").build();
        String participantsHtmlString = client.newCall(get).execute().body().string();

		return ParticipantsParser.getParticipantsFromHtml(participantsHtmlString);
	}
	
	public static GradeFolder getGrade(int courseId, Session session) throws IOException, XMLException {
		//Create client and context
		OkHttpClient client = getClient(session);

        Request get = new Request.Builder().url("https://gauchospace.ucsb.edu/courses/grade/report/user/index.php?id=" + courseId).build();
        String courseHtml = client.newCall(get).execute().body().string();

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
	 */
	public static List<Forum> getForums(int courseID, Session session) throws IOException, XMLException {
		//Create client and context
		OkHttpClient client = getClient(session);
        Request get = new Request.Builder().url("https://gauchospace.ucsb.edu/courses/mod/forum/index.php?id=" + courseID).build();

		String forumsHtml = client.newCall(get).execute().body().string();

		return ForumsParser.getForums(forumsHtml);
	}
	
	/**
	 * Gets a list of forums that belong under the specified course.
	 * @param forumID The ID of the forum to fetch the discussions from
     * @param session the session of the logged in user to act as
	 * @return A list of Forums that belong to this Course.
	 * @throws IOException
	 */
	public static List<Discussion> getDiscussions(int forumID, Session session) throws IOException, XMLException {
		//Create client and context
		OkHttpClient client = getClient(session);

        Request get = new Request.Builder().url("https://gauchospace.ucsb.edu/courses/mod/forum/view.php?f=" + forumID).build();
		String forumHtml = client.newCall(get).execute().body().string();

        return ForumParser.getDiscussions(forumHtml);
	}


	public static OkHttpClient getClient(Session session) {
        OkHttpClient client = new OkHttpClient();
        CookieManager cookieManager = new CookieManager(session != null ? session.getCookies() : null, CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
        return client;
	}

    public static Map<String, String[]> parseUrlQueryString (String s) {
        if (s == null) return new HashMap<>(0);
        // In map1 we use strings and ArrayLists to collect the parameter values.
        HashMap<String, Object> map1 = new HashMap<>();
        int p = 0;
        while (p < s.length()) {
            int p0 = p;
            while (p < s.length() && s.charAt(p) != '=' && s.charAt(p) != '&') p++;
            String name = urlDecode(s.substring(p0, p));
            if (p < s.length() && s.charAt(p) == '=') p++;
            p0 = p;
            while (p < s.length() && s.charAt(p) != '&') p++;
            String value = urlDecode(s.substring(p0, p));
            if (p < s.length() && s.charAt(p) == '&') p++;
            Object x = map1.get(name);
            if (x == null) {
                // The first value of each name is added directly as a string to the map.
                map1.put (name, value);
            }
            else if (x instanceof String) {
                // For multiple values, we use an ArrayList.
                ArrayList<String> a = new ArrayList<>();
                a.add ((String)x);
                a.add (value);
                map1.put (name, a);
            }
            else {
                @SuppressWarnings("unchecked")
                ArrayList<String> a = (ArrayList<String>)x;
                a.add (value);
            }}
        // Copy map1 to map2. Map2 uses string arrays to store the parameter values.
        HashMap<String, String[]> map2 = new HashMap<>(map1.size());
        for (Map.Entry<String, Object> e : map1.entrySet()) {
            String name = e.getKey();
            Object x = e.getValue();
            String[] v;
            if (x instanceof String) {
                v = new String[]{(String)x};
            }
            else {
                @SuppressWarnings("unchecked")
                ArrayList<String> a = (ArrayList<String>)x;
                v = new String[a.size()];
                v = a.toArray(v);
            }
            map2.put (name, v);
        }
        return map2;
    }

    private static String urlDecode (String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error in urlDecode.", e);
        }
    }


}
