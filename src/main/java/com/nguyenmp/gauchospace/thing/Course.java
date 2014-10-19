package com.nguyenmp.gauchospace.thing;

import com.nguyenmp.gauchospace.GauchoSpaceClient;
import com.nguyenmp.gauchospace.Session;
import com.nguyenmp.gauchospace.parser.WeeklyOutlineParser;
import com.nguyenmp.gauchospace.parser.XMLException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;

public class Course implements Serializable {
	public static final long serialVersionUID = 6265010175935790471L;

	public String mName = null;
	public String mUrl = null;
	public String mTitle = null;
	public String mQuarter = null;

    public int getCourseID() throws URISyntaxException {
        if (mUrl == null) throw new NullPointerException("Course URL was set to null, and the course ID could not be inferred");

        URIBuilder uriBuilder = new URIBuilder(mUrl);
        for (NameValuePair nameValuePair : uriBuilder.getQueryParams()) {
            if (nameValuePair.getName().equalsIgnoreCase("id")) {
                return Integer.parseInt(nameValuePair.getValue());
            }
        }

        throw new NullPointerException("Course URL does not contain a course ID");
    }

    public Week[] getWeeklyOutline(Session session) throws URISyntaxException, IOException, XMLException {
        return Course.getWeeklyOutline(getCourseID(), session);
    }

    public static Week[] getWeeklyOutline(int courseId, Session session) throws XMLException, IOException{
        //Create client and context
        HttpClient client = GauchoSpaceClient.getClient();
        HttpContext context = session.asContext();

        //Create GET
        HttpGet get = new HttpGet("https://gauchospace.ucsb.edu/courses/course/view.php?id=" + courseId);

        //Do GET
        HttpResponse response = client.execute(get, context);

        //Get content of response
        HttpEntity entity = response.getEntity();

        //Read content
        String courseHtml = GauchoSpaceClient.getStringFromEntity(entity);

        //Close connection
        get.abort();

        //Compile and parse courses
        return WeeklyOutlineParser.getWeeklyOutlineFromHtml(courseHtml);
    }

	public String toString() {
		return String.format("Name: %s; Url: %s; Title: %s; Quarter: %s", mName, mUrl, mTitle, mQuarter);
	}
}
