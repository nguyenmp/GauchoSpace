package com.nguyenmp.gauchospace;

import com.nguyenmp.gauchospace.thing.Course;
import org.apache.http.client.protocol.HttpClientContext;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SessionTest {

    @Test
    public void testLoginLogout() throws Exception {
        // Load credentials from stupid store
        String username = Credentials.Username();
        String password = Credentials.Password();

        Session session = new Session(username, password);
        assertTrue(session.isLoggedIn());
        assertTrue(session.isLoggedIn());
        assertTrue(session.logout());
        assertFalse(session.isLoggedIn());
        assertFalse(session.isLoggedIn());
        assertTrue(session.logout());
        assertFalse(session.isLoggedIn());
        assertFalse(session.isLoggedIn());

        // Try the same process again just to prevent any weird state issues.
        session = new Session(username, password);
        assertTrue(session.isLoggedIn());
        assertTrue(session.isLoggedIn());
        assertTrue(session.logout());
        assertFalse(session.isLoggedIn());
        assertFalse(session.isLoggedIn());
        assertTrue(session.logout());
        assertFalse(session.isLoggedIn());
        assertFalse(session.isLoggedIn());
    }

    @Test
    public void testAsContext() throws IOException {
        // Load credentials from stupid store
        String username = Credentials.Username();
        String password = Credentials.Password();

        Session session = new Session(username, password);

        HttpClientContext context = session.asContext();
        assertNotNull(context);
        assertTrue(context.getCookieStore().getCookies().size() > 0);
    }

    @Test
    public void testGetCourses() throws Exception {
        // Load credentials from stupid store
        String username = Credentials.Username();
        String password = Credentials.Password();

        Session session = new Session(username, password);
        Course[] courses = session.getCourses();
        assertNotNull(courses);

        assertTrue(courses.length > 0);
        for (Course course : courses) {
            assertNotNull(course.mName);
            assertNotEquals(course.mName.trim().length(), 0);
            assertNotNull(course.mTitle);
            assertNotEquals(course.mTitle.trim().length(), 0);
            assertNotNull(course.mQuarter);
            assertNotEquals(course.mQuarter.trim().length(), 0);
            assertNotNull(course.mUrl);
            assertNotEquals(course.mUrl.trim().length(), 0);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull1() throws Exception {
        new Session(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull2() throws Exception {
        new Session(null, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull3() throws Exception {
        new Session("", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull4() throws Exception {
        new Session("", "");
    }
}