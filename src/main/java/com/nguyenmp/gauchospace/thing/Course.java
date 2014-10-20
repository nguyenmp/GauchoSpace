package com.nguyenmp.gauchospace.thing;

import com.nguyenmp.gauchospace.GauchoSpaceClient;
import com.nguyenmp.gauchospace.Session;
import com.nguyenmp.gauchospace.parser.WeeklyOutlineParser;
import com.nguyenmp.gauchospace.parser.XMLException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class Course implements Serializable {
	public static final long serialVersionUID = 6265010175935790471L;

	public String mName = null;
	public String mUrl = null;
	public String mTitle = null;
	public String mQuarter = null;

    public int getCourseID() throws URISyntaxException, MalformedURLException {
        if (mUrl == null) throw new NullPointerException("Course URL was set to null, and the course ID could not be inferred");

        try {
            return Integer.parseInt(GauchoSpaceClient.parseUrlQueryString(new URL(mUrl).getQuery()).get("id")[0]);
        } catch (NumberFormatException e) {
            throw new NullPointerException("Course URL does not contain a course ID");
        }
    }

    public Week[] getWeeklyOutline(Session session) throws URISyntaxException, IOException, XMLException {
        return Course.getWeeklyOutline(getCourseID(), session);
    }

    public static Week[] getWeeklyOutline(int courseId, Session session) throws XMLException, IOException{
        //Create client and context
        OkHttpClient client = GauchoSpaceClient.getClient(session);
        Request get = new Request.Builder().url("https://gauchospace.ucsb.edu/courses/course/view.php?id=" + courseId).build();
        String courseHtml = client.newCall(get).execute().body().string();

        //Compile and parse courses
        return WeeklyOutlineParser.getWeeklyOutlineFromHtml(courseHtml);
    }

	public String toString() {
		return String.format("Name: %s; Url: %s; Title: %s; Quarter: %s", mName, mUrl, mTitle, mQuarter);
	}
}
