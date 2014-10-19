package com.nguyenmp.gauchospace;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class GauchoSpaceClientTest {
    @Test
    public void loginTest() throws IOException {
        String username = Credentials.Username();
        String password = Credentials.Password();
        assertNotEquals(GauchoSpaceClient.login(username, password), null);
    }

    @Test
    public void loginNullTest() throws IOException {
        assertEquals(GauchoSpaceClient.login("penis", "vagina"), null);
    }
}