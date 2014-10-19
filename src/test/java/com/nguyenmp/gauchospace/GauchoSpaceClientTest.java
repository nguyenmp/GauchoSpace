package com.nguyenmp.gauchospace;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class GauchoSpaceClientTest {

    @Test
    public void testGetClient() throws Exception {
        CloseableHttpClient client = GauchoSpaceClient.getClient();
        assertNotNull(client);
        client.close();
        // TODO: In the future, it may be wise to test that the useragent was set
    }

    @Test
    public void testGetContext() throws Exception {
        HttpClientContext context;

        context = GauchoSpaceClient.getContext(null);
        assertNotNull(context);
        assertEquals(context.getCookieStore().getCookies().size(), 0);

        CookieStore cookies = new BasicCookieStore();
        context = GauchoSpaceClient.getContext(cookies);
        assertNotNull(context);
        assertNotNull(context.getCookieStore());
        assertEquals(cookies, context.getCookieStore());
    }
}