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

import com.nguyenmp.gauchospace.thing.User;
import com.nguyenmp.gauchospace.thing.User.Attribute;

public class UserParser {
	
	/**
	 * 
	 * @param htmlString
	 * @return Scrapes user data from the html of a user's profile page
	 */
	public static User getUserFromHtml(String htmlString) throws XMLException {
		Document doc = XMLParser.getDocumentFromString(htmlString);
		
		Element docElement = doc.getDocumentElement();
		
		//Get name
		Element bodyElement = (Element) XMLParser.getChildFromName(docElement, "body");
		Element pageElement = XMLParser.getChildFromAttribute(bodyElement, "id", "page");
		Element contentElement = XMLParser.getChildFromAttribute(pageElement, "id", "content");
		Element nameElement = (Element) XMLParser.getChildFromName(contentElement, "h2");
		
		String name = nameElement.getTextContent();
		
		
		//Get avatar
		Element userinfoboxElement = (Element) XMLParser.getChildFromAttribute(contentElement, "class", "userinfobox");
//		Element tbodyElement = (Element) XMLParser.getChildFromName(userinfoboxElement, "tbody");
//		System.out.println(tbodyElement);
		Element trElement = (Element) XMLParser.getChildFromName(userinfoboxElement, "tr");
		Element tdSideElement = (Element) XMLParser.getChildFromAttribute(trElement, "class", "side");
		Element imgElement = (Element) XMLParser.getChildFromName(tdSideElement, "img");
		
		String avatarUrl = imgElement.getAttribute("src");
		String avatarDescription = imgElement.getAttribute("alt");
		
		
		//Get summary and attributes table
		Element tdContentElement = XMLParser.getChildFromAttribute(trElement, "class", "content");
		
		Node attributesTableNode = XMLParser.getChildFromAttribute(tdContentElement, "class", "list");
		Node hrNode = XMLParser.getChildFromName(tdContentElement, "hr");

		Element summaryElement = tdContentElement;
		if (attributesTableNode != null) summaryElement.removeChild(attributesTableNode);
		if (hrNode != null) summaryElement.removeChild(hrNode);
		
		String summary = XMLParser.nodeToString(summaryElement);
		List<Attribute> attributes = getMapFromTable((Element) attributesTableNode);
		
		
		//Generate User object form scraped data
		User user = new User();
		user.setName(name);
		user.setSummary(summary);
		user.setAvatarUrl(avatarUrl);
		user.setAvatarDescription(avatarDescription);
		user.addAttributes(attributes);
		
		//Return generated user
		return user;
	}
	
	private static List<Attribute> getMapFromTable(Element tableNode) {
		List<Attribute> attributes = new ArrayList<>();
		NodeList children = tableNode.getChildNodes();
		
		for (int index = 0; index < children.getLength(); index++) {
			Attribute attribute = getAttributeFromTableRow((Element) children.item(index));
			attributes.add(attribute);
		}
		
		return attributes;
	}
	
	private static Attribute getAttributeFromTableRow(Element tableRowElement) {
		//Get the key and value elements
		Element labelElement = XMLParser.getChildFromAttribute(tableRowElement, "class", "label c0");
		Element valueElement = XMLParser.getChildFromAttribute(tableRowElement, "class", "info c1");
		
		//Parse the key and value from elements
		String key = labelElement.getTextContent();
		key = key.trim();
		if (key.endsWith(":")) key = key.substring(0, key.length() - 1); //Remove prepending colon
		String value = valueElement.getTextContent();
		
		//Create the attribute object from the key and value
		return new Attribute(key, value);
	}
}