package com.nguyenmp.gauchospace.parser;

import com.nguyenmp.gauchospace.thing.Course;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class CoursesParser {
	
	/**
	 * 
	 * @param htmlString the Html as a String that contains Courses
	 * @return a List of Courses from htmlString
	 * @throws XMLException the XML could not be parsed
	 */
	public static List<Course> getCoursesFromHtml(String htmlString) throws XMLException {
		Document doc = XMLParser.getDocumentFromString(htmlString);

        Element inst644 = doc.getElementById("inst644");
        Element content = XMLParser.getChildFromAttribute(inst644, "class", "content");
        Element classList = XMLParser.getChildFromAttribute(content, "class", "course_list");
        System.out.println(classList.getTextContent());

		return null;
	}
}