package com.nguyenmp.gauchospace;

import com.nguyenmp.gauchospace.common.Constants;
import com.nguyenmp.gauchospace.parser.CoursesParser;
import com.nguyenmp.gauchospace.parser.XMLException;
import com.nguyenmp.gauchospace.thing.Course;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private final CookieStore cookies;

    /**
     * Logs into GauchoSpace with the given credentials.
     * @param username the username of the user to log in.
     * @param password the password of the user to log in.
     * @throws ClientProtocolException in case of an http protocol error
     * @throws IOException in case of a problem or the connection was aborted or if an I/O error occurs.
     * @throws UnsupportedEncodingException if the default encoding isn't supported
     * @throws java.lang.IllegalArgumentException if the credentials did not result in a successful login
     */
    public Session(String username, String password) throws IOException, IllegalArgumentException {
        CloseableHttpClient client = GauchoSpaceClient.getClient();
        HttpClientContext context = new HttpClientContext();

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
        String line;
        StringBuilder coursesHtml = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            //System.out.println(line);
            coursesHtml.append(line);
        }
        String contentString = coursesHtml.toString();

        post.abort();
        client.close();

        if (contentString.contains(Constants.loggedInString)) {
            //Return logged in client's cookies
            //System.out.println("logged in string is contained! saving cookie...");
            cookies = context.getCookieStore();
        } else {
            throw new IllegalArgumentException("Username and password did not result in a login");
        }
    }

    /**
     * Verifies whether or not the user is logged in at the moment.  This is compatible
     * with session time outs as well as regular log outs.
     * @return True of the cookies are valid, false if they are expired or invalid.
     * @throws org.apache.http.client.ClientProtocolException in case of an http protocol error
     * @throws java.io.IOException in case of a problem or the connection was aborted or if an I/O error occurs.
     */
    public boolean isLoggedIn() throws IOException {
        // Can't be logged in with no cookies!
        if (cookies == null) return true;

        CloseableHttpClient client = GauchoSpaceClient.getClient();
        HttpContext context = this.asContext();

        HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/");

        HttpResponse response = client.execute(get, context);

        String httpResponse = GauchoSpaceClient.getStringFromEntity(response.getEntity());

        //Clean up
        get.abort();
        client.close();

        return httpResponse.contains(Constants.loggedInString);
    }

    /**
     * Logs the owner of the cookies out.  If the cookies are invalid, nothing is done.
     * @return true if cookies are invalid now. false if they are still valid
     * @throws ClientProtocolException in case of an http protocol error
     * @throws IOException in case of a problem or the connection was aborted or if an I/O error occurs
     */
    public boolean logout() throws IOException {
        if (!isLoggedIn()) return true;

        String sessionKey = getSessionKey();
        CloseableHttpClient client = GauchoSpaceClient.getClient();
        HttpContext context = this.asContext();

        HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/login/logout.php?sesskey=" + sessionKey);

        client.execute(get, context);

        //Cleanup
        get.abort();
        client.close();

        return !isLoggedIn();
    }

    /**
     * Creates an HttpClientContext that contains the cookies given from the session.
     * @return The HttpContext containing the given CookieStore
     */
    public HttpClientContext asContext() {

        HttpClientContext context = new HttpClientContext();
        context.setCookieStore(cookies);

        return context;
    }

    /**
     * Gets the Courses of the user who owns the cookies.
     * @return The list of courses in order of the server
     * @throws IOException in case of a problem or the connection was aborted,
     * if the stream could not be created, or if an I/O error occured
     * @throws XMLException if the XML could not be parsed
     */
    public Course[] getCourses() throws IOException, XMLException {
        //Create client and context
        CloseableHttpClient client = GauchoSpaceClient.getClient();
        HttpContext context = this.asContext();

        //Create GET
        HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/my/");

        //Do GET
        HttpResponse response = client.execute(get, context);

        //Get content of response
        HttpEntity entity = response.getEntity();

        //Read content
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
        String line;
        StringBuilder coursesHtml = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            coursesHtml.append(line);
        }
        String contentString = coursesHtml.toString();

        //Close connection
        get.abort();
        client.close();

        //Compile and parse courses
        return CoursesParser.getCoursesFromHtml(contentString);
    }

    /**
     * Returns the session key from the cookies.  The session key is used for
     * many data-altering actions with Moodle/GauchoSpace.
     * @return A string representation of the Session Key.  null if the session key could not be found.
     * @throws ClientProtocolException in case of an http protocol error
     * @throws IOException in case of a problem or the connection was aborted or if an I/O error occurs.
     */
    private String getSessionKey() throws IOException {
        CloseableHttpClient client = GauchoSpaceClient.getClient();
        HttpContext context = this.asContext();

        HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/");

        HttpResponse response = client.execute(get, context);

        String htmlString = GauchoSpaceClient.getStringFromEntity(response.getEntity());
        get.abort();
        client.close();
        int start = htmlString.indexOf("href=\"https://gauchospace.ucsb.edu/courses/login/logout.php?sesskey=") + "href=\"https://gauchospace.ucsb.edu/courses/login/logout.php?sesskey=".length();
        int end = htmlString.indexOf("\">Logout</a>");

        String sessionKey = null;
        if (start >=0 && end <= htmlString.length() && start <= end) sessionKey = htmlString.substring(start, end);
        return sessionKey;
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
        List<NameValuePair> args = new ArrayList<>();
        args.add(new BasicNameValuePair("username", username));
        args.add(new BasicNameValuePair("password", password));
        args.add(new BasicNameValuePair("testcookies", "1"));

        return new UrlEncodedFormEntity(args);
    }
}
