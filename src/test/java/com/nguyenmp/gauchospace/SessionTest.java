package com.nguyenmp.gauchospace;

import org.junit.Test;

import static org.junit.Assert.*;

public class SessionTest {

    @Test
    public void test() throws Exception {
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
}