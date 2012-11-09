package com.nguyenmp.gauchospace.parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.nguyenmp.gauchospace.thing.Course;
import com.nguyenmp.gauchospace.thing.Instructor;

public class CoursesParser {
	
	/**
	 * 
	 * @param htmlString the Html as a String that contains Courses
	 * @return a List of Courses from htmlString
	 * @throws SAXNotRecognizedException If the feature value can't be 
	 * assigned or retrieved.
	 * @throws SAXNotSupportedException When the XMLReader recognizes 
	 * the feature name but cannot set the requested value.
	 * @throws TransformerFactoryConfigurationError Thrown if the 
	 * implementation is not available or cannot be instantiated.
	 * @throws TransformerException When it is not possible to create 
	 * a Transformer instance or if an unrecoverable error occurs 
	 * during the course of the transformation.
	 */
	public static List<Course> getCoursesFromHtml(String htmlString) throws SAXNotRecognizedException, SAXNotSupportedException, TransformerFactoryConfigurationError, TransformerException {
		Document doc = XMLParser.getDocumentFromString(htmlString);
		
		Element body = (Element) XMLParser.getChildFromName(doc.getDocumentElement(), "body");
		Element page = XMLParser.getChildFromAttribute(body, "id", "page");
		Element content = XMLParser.getChildFromAttribute(page, "id", "content");
		Element table = (Element) XMLParser.getChildFromName(content, "table");
		Element tr = (Element) XMLParser.getChildFromName(table, "tr");
		Element middleColumn = XMLParser.getChildFromAttribute(tr, "id", "middle-column");
		Element div = (Element) XMLParser.getChildFromName(middleColumn, "div");
		Element unorderedList = (Element) XMLParser.getChildFromName(div, "ul");
		
		List<Course> courses = getCoursesFromUnorderedList(unorderedList);
		
		return courses;
	}
	
	/**
	 * Extracts and parses a list of courses from an UnorderedList
	 * @param unorderedList an Element representing an UnorderedList of Courses
	 * @return a List of Courses
	 */
	private static List<Course> getCoursesFromUnorderedList(Element unorderedList) {
		ArrayList<Course> list = new ArrayList<Course>();
		
		NodeList children = unorderedList.getChildNodes();
		
		for (int index = 0; index < children.getLength(); index++) {
			list.add(getCourseFromListItem(children.item(index)));
		}
		
		return list;
	}
	
	/**
	 * Extracts and parses the course from a list item node.
	 * @param listItem the list item node
	 * @return the Course with as much informaiton filled in as possible
	 */
	private static Course getCourseFromListItem(Node listItem) {
		Element courseboxClearfix = (Element) XMLParser.getChildFromName((Element) listItem, "div");
		Element infoElement = XMLParser.getChildFromAttribute(courseboxClearfix, "class", "info");
		Element nameElement= XMLParser.getChildFromAttribute(infoElement, "class", "name");
		Element teachersElement = XMLParser.getChildFromAttribute(infoElement, "class", "teachers");
		Element heading = (Element) XMLParser.getChildFromName(nameElement, "a");
		
		Element summaryElement = (Element) XMLParser.getChildFromAttribute(courseboxClearfix, "class", "summary");
		
		String url = heading.getAttribute("href");
		String summary = summaryElement.getTextContent();
		String headingText = heading.getTextContent();
		String[] headingSegments = headingText.split(" - ");
		String name = null;
		String title = null;
		String quarter = null;
		if (headingSegments.length > 0) {
			name = headingSegments[0];
			int lastIndex = name.lastIndexOf("(");
			if (lastIndex != -1 && lastIndex > 0) {
				name = name.substring(0, lastIndex - 1);
			}
		}
		if (headingSegments.length > 1) title = headingSegments[1].trim();
		if (headingSegments.length > 2) quarter = headingSegments[2].trim();
		
		Course course = new Course();
		course.setName(name);
		course.setTitle(title);
		course.setQuarter(quarter);
		course.getInstructors().addAll(getInstructorsFromUnorderedList(teachersElement));
		course.setUrl(url);
		course.setSummary(summary);
		
		return course;
	}
	
	/**
	 * Extracts and parses an Element into a list of Instructors
	 * @param unorderedList An Element that contains an UnorderedList of Instructors
	 * @return A List of Instructors
	 */
	private static List<Instructor> getInstructorsFromUnorderedList(Element unorderedList) {
		List<Instructor> instructors = new ArrayList<Instructor>();
		
		NodeList children = unorderedList.getChildNodes();
		
		for (int index = 0; index < children.getLength(); index++) {
			Node listItem = children.item(index);

			if (listItem.getNodeType() == Node.ELEMENT_NODE) 
				instructors.add(getInstructorFromListItem(listItem));
			
		}
		
		return instructors;
	}
	
	/**
	 * Returns the instructor from a Node that is a XHTML ListItem
	 * @param listItem The Node that represents the Instructor
	 * @return The Instructor being represented
	 */
	private static Instructor getInstructorFromListItem(Node listItem) {
		Instructor instructor = new Instructor();
		
		Element linkElement = (Element) XMLParser.getChildFromName((Element) listItem, "a");
		
		instructor.setName(linkElement.getTextContent());
		instructor.setUrl(linkElement.getAttribute("href"));
		
		return instructor;
	}
}