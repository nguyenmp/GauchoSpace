package com.nguyenmp.gauchospace;

import com.nguyenmp.gauchospace.common.Constants;
import com.nguyenmp.gauchospace.parser.CoursesParser;
import com.nguyenmp.gauchospace.parser.XMLException;
import com.nguyenmp.gauchospace.thing.Course;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookieStore;

public class Session {
    private final CookieStore cookies;

    public Session(CookieStore cookies) throws IOException {
        this.cookies = cookies;
        if (!isLoggedIn()) throw new IllegalStateException("The cookies provided are no longer valid.");
    }

    /**
     * Logs into GauchoSpace with the given credentials.
     * @param username the username of the user to log in.
     * @param password the password of the user to log in.
     * @throws IOException in case of a problem or the connection was aborted or if an I/O error occurs.
     * @throws java.lang.IllegalArgumentException if the credentials did not result in a successful login
     */
    public Session(String username, String password) throws IOException, IllegalArgumentException {
        OkHttpClient client = GauchoSpaceClient.getClient(null);

        Request get = new Request.Builder().url("https://gauchospace.ucsb.edu/courses/login/index.php").build();
        client.newCall(get).execute();

        //Create post
        Request post = new Request.Builder()
                .url("https://gauchospace.ucsb.edu/courses/login/index.php")
                .post(getBody(username, password))
                .build();
        String contentString = client.newCall(post).execute().body().string();

        if (contentString.contains(Constants.loggedInString)) {
            //Return logged in client's cookies
            //System.out.println("logged in string is contained! saving cookie...");
            cookies = ((CookieManager) client.getCookieHandler()).getCookieStore();
        } else {
            throw new IllegalArgumentException("Username and password did not result in a login");
        }
    }

    /**
     * Verifies whether or not the user is logged in at the moment.  This is compatible
     * with session time outs as well as regular log outs.
     * @return True of the cookies are valid, false if they are expired or invalid.
     * @throws java.io.IOException in case of a problem or the connection was aborted or if an I/O error occurs.
     */
    public boolean isLoggedIn() throws IOException {
        // Can't be logged in with no cookies!
        if (cookies == null) return false;

        OkHttpClient client = GauchoSpaceClient.getClient(this);
        Request get = new Request.Builder().url("https://gauchospace.ucsb.edu/courses/").build();
        String httpResponse = client.newCall(get).execute().body().string();

        return httpResponse.contains(Constants.loggedInString);
    }

    /**
     * Logs the owner of the cookies out.  If the cookies are invalid, nothing is done.
     * @return true if cookies are invalid now. false if they are still valid
     * @throws IOException in case of a problem or the connection was aborted or if an I/O error occurs
     */
    public boolean logout() throws IOException {
        if (!isLoggedIn()) return true;

        String sessionKey = getSessionKey();
        OkHttpClient client = GauchoSpaceClient.getClient(this);

        Request get = new Request.Builder().url("https://gauchospace.ucsb.edu/courses/login/logout.php?sesskey=" + sessionKey).build();
        client.newCall(get).execute();

        return !isLoggedIn();
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
        OkHttpClient client = GauchoSpaceClient.getClient(this);

        //Create GET
        Request get = new Request.Builder().url("https://gauchospace.ucsb.edu/courses/my/").build();
        String contentString = client.newCall(get).execute().body().string();

        //Compile and parse courses
        return CoursesParser.getCoursesFromHtml(contentString);
    }

    /**
     * @return the cookies that represent the current session of this user
     */
    public CookieStore getCookies() {
        return cookies;
    }

    /**
     * Returns the session key from the cookies.  The session key is used for
     * many data-altering actions with Moodle/GauchoSpace.
     * @return A string representation of the Session Key.  null if the session key could not be found.
     * @throws IOException in case of a problem or the connection was aborted or if an I/O error occurs.
     */
    private String getSessionKey() throws IOException {
        OkHttpClient client = GauchoSpaceClient.getClient(this);
        Request get = new Request.Builder().url("https://gauchospace.ucsb.edu/courses/").build();
        String htmlString = client.newCall(get).execute().body().string();
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
    private static RequestBody getBody(String username, String password) throws UnsupportedEncodingException {
        return new FormEncodingBuilder()
                .add("username", username)
                .add("password", password)
                .add("testcookies", "1")
                .build();
    }

}
