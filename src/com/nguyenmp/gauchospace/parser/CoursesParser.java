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

        Element inst644 = doc.getElementById("inst644");
        Element content = XMLParser.getChildFromAttribute(inst644, "class", "content");
        Element classList = XMLParser.getChildFromAttribute(content, "class", "course_list");
        System.out.println(classList.getTextContent());

		return null;
	}
}