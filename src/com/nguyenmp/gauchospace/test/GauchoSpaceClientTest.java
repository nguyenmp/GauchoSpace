/* Copyright (C) 2012 Mark
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.nguyenmp.gauchospace.test;

import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.nguyenmp.gauchospace.GauchoSpaceClient;
import com.nguyenmp.gauchospace.Credentials;

public class GauchoSpaceClientTest {

    @Test
    public void loginTest() throws IOException {
        String username = Credentials.Username();
        String password = Credentials.Password();
        assertNotEquals(GauchoSpaceClient.login(username, password), null);
    }

    @Test
    public void loginNullTest() throws IOException {
        String username = Credentials.Username();
        String password = Credentials.Password();
        assertEquals(GauchoSpaceClient.login("penis", "vagina"), null);
    }
}
