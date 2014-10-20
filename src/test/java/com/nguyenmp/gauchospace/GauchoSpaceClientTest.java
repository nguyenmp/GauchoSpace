package com.nguyenmp.gauchospace;

import com.squareup.okhttp.OkHttpClient;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GauchoSpaceClientTest {

    @Test
    public void testGetClient() throws Exception {
        String username = Credentials.Username();
        String password = Credentials.Password();

        Session session = new Session(username, password);
        OkHttpClient client = GauchoSpaceClient.getClient(session);
        assertNotNull(client);
    }

    @Test(expected = IllegalStateException.class)
    public void testNullGetClient1() throws Exception {
        OkHttpClient client = GauchoSpaceClient.getClient(new Session(null));
        assertNotNull(client);
    }

    @Test
    public void testNullGetClient2() throws Exception {
        OkHttpClient client = GauchoSpaceClient.getClient(null);
        assertNotNull(client);
    }
}