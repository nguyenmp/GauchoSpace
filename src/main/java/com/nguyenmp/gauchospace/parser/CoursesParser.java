package com.nguyenmp.gauchospace.parser;

import com.nguyenmp.gauchospace.thing.Course;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class CoursesParser {
	
	/**
	 * 
	 * @param htmlString the Html as a String that contains Courses
	 * @return a List of Courses from htmlString
	 * @throws XMLException the XML could not be parsed
	 */
	public static Course[] getCoursesFromHtml(String htmlString) throws XMLException {
        List<Course> courses = new ArrayList<>();

		Document doc = XMLParser.getDocumentFromString(htmlString);

        Element inst644 = doc.getElementById("inst644");
        Element content = XMLParser.getChildFromAttribute(inst644, "class", "content");
        Element classList = XMLParser.getChildFromAttribute(content, "class", "course_list");
        NodeList childNodes = classList.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Element item = (Element) childNodes.item(i);
            Element titleWrapper = XMLParser.getChildFromAttribute(item, "class", "course_title");
            Element title = XMLParser.getChildFromAttribute(titleWrapper, "class", "title");
            Element link = (Element) XMLParser.getChildFromName(title, "a");
            String url = link.getAttribute("href");
            String[] titleText = link.getTextContent().split("\\s+-\\s+");

            Course course = new Course();
            course.mName = titleText[0];
            course.mTitle = titleText[1];
            course.mQuarter = titleText[2];
            course.mUrl = url;

            courses.add(course);
        }

        return courses.toArray(new Course[courses.size()]);
	}
}