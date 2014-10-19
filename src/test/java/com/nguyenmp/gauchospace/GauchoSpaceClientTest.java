package com.nguyenmp.gauchospace;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GauchoSpaceClientTest {

    @Test
    public void testGetClient() throws Exception {
        CloseableHttpClient client = GauchoSpaceClient.getClient();
        assertNotNull(client);
        client.close();
        // TODO: In the future, it may be wise to test that the useragent was set
    }
}