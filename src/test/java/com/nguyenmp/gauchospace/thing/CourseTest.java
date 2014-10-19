package com.nguyenmp.gauchospace.thing;

import com.nguyenmp.gauchospace.Credentials;
import com.nguyenmp.gauchospace.Session;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CourseTest {

    @Test
    public void testGetCourseID() throws Exception {
        Course course = new Course();
        course.mUrl = "https://gauchospace.ucsb.edu/courses/course/view.php?id=5193";
        assertEquals(course.getCourseID(), 5193);
    }

    @Test(expected = NullPointerException.class)
    public void testGetNullCourseID() throws Exception {
        Course course = new Course();
        course.getCourseID();
    }

    @Test(expected = NullPointerException.class)
    public void testGetNullCourseID2() throws Exception {
        Course course = new Course();
        course.mUrl = "";
        course.getCourseID();
    }

    @Test(expected = URISyntaxException.class)
    public void testGetNullCourseID3() throws Exception {
        Course course = new Course();
        course.mUrl = "foo bar";
        course.getCourseID();
    }

    @Test
    public void testGetWeeklyOutline() throws Exception {
        String username = Credentials.Username();
        String password = Credentials.Password();

        Session session = new Session(username, password);
        Course[] courses = session.getCourses();
        for (Course course : courses) {
            Week[] weeks = course.getWeeklyOutline(session);
            assertNotNull(weeks);
            assertTrue(weeks.length > 9); // TODO: This isn't actually what I want to test for
        }
    }

    @Test
    public void testGetWeeklyOutline1() throws Exception {
        String username = Credentials.Username();
        String password = Credentials.Password();

        Session session = new Session(username, password);
        Course[] courses = session.getCourses();
        for (Course course : courses) {
            Week[] weeks = Course.getWeeklyOutline(course.getCourseID(), session);
            assertNotNull(weeks);
            assertTrue(weeks.length > 9); // TODO: This isn't actually what I want to test for
        }
    }

    @Test
    public void testToString() throws Exception {
        Course course = new Course();
        course.mQuarter = "asdfsdfsdfasdfsadf";
        course.mUrl = "asdf";
        course.mName = "fdsa";
        course.mTitle = "titile";
        assertEquals(course.toString(), "Name: fdsa; Url: asdf; Title: titile; Quarter: asdfsdfsdfasdfsadf");
    }

    @Test
    public void testNullToString() throws Exception {
        Course course = new Course();
        assertEquals(course.toString(), "Name: null; Url: null; Title: null; Quarter: null");
    }
}