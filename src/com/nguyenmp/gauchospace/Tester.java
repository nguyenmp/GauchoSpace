/* Copyright (C) 2012 Mark
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.nguyenmp.gauchospace;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.nguyenmp.gauchospace.thing.Course;
import org.apache.http.client.CookieStore;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.nguyenmp.gauchospace.parser.WeeklyOutlineParser.UnparsableHtmlException;
import com.nguyenmp.gauchospace.thing.Forum;
import com.nguyenmp.gauchospace.thing.grade.GradeFolder;
import com.nguyenmp.gauchospace.thing.grade.GradeItem;

public class Tester {
	public static void main(String[] args) throws SAXNotRecognizedException, SAXNotSupportedException, IOException, TransformerFactoryConfigurationError, TransformerException, UnparsableHtmlException{
		//CookieStore cookies = GauchoSpaceClient.login("username", "password");
        CookieStore cookies = GauchoSpaceClient.login("un", "pass");
        List<Course> Cor = GauchoSpaceClient.getCourses(cookies);
	}
}
