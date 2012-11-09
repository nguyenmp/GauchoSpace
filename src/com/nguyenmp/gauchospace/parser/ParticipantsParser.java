package com.nguyenmp.gauchospace.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.nguyenmp.gauchospace.parser.WeeklyOutlineParser.UnparsableHtmlException;
import com.nguyenmp.gauchospace.thing.User;

public class ParticipantsParser {
	
	
	public static List<User> getParticipantsFromHtml(String htmlString) throws TransformerFactoryConfigurationError, SAXNotRecognizedException, SAXNotSupportedException, IOException, TransformerException, UnparsableHtmlException {
		Element tableElement = getTableFromHtml(htmlString);
		List<User> weeklyOutline = getParticipantsFromTable(tableElement);
//		List<User> weeklyOutline = null;
		return weeklyOutline;
	}
	
	private static Element getTableFromHtml(String htmlString) throws SAXNotRecognizedException, SAXNotSupportedException, IOException, TransformerFactoryConfigurationError, TransformerException {
		Document document = XMLParser.getDocumentFromString(htmlString);
		Element documentElement = document.getDocumentElement();
		Element bodyElement = (Element) XMLParser.getChildFromName(documentElement, "body");
		Element pageElement = XMLParser.getChildFromAttribute(bodyElement, "id", "page");
		Element contentElement = XMLParser.getChildFromAttribute(pageElement, "id", "content");
		
		Element participantElement = XMLParser.getChildFromAttribute(contentElement, "id", "participants");
		
		return participantElement;
	}
	
	private static List<User> getParticipantsFromTable(Element tableElement) throws TransformerException {
		NodeList children = tableElement.getChildNodes();
		List<User> participants = new ArrayList<User>();
		
		//Ignore the first row because it is just headers
		for (int index = 1; index < children.getLength(); index++) {
			participants.add(getUserFromTableRow((Element) children.item(index)));
		}
		
		return participants;
	}
	
	private static User getUserFromTableRow(Element tableRow) {
		//Get avatar details
		Element avatarCellElement = XMLParser.getChildFromAttribute(tableRow, "class", "cell c0");
		Element avatarAnchorElement = (Element) XMLParser.getChildFromName(avatarCellElement, "a");
		Element imageElement = (Element) XMLParser.getChildFromName(avatarAnchorElement, "img");

		String avatarDescription = imageElement.getAttribute("alt");
		String avatarUrl = imageElement.getAttribute("src");
		
		//Get name detail
		Element nameCellElement = XMLParser.getChildFromAttribute(tableRow, "class", "cell c1");
		Element strongNameElement = (Element) XMLParser.getChildFromName(nameCellElement, "strong");
		Element nameAnchorElement = (Element) XMLParser.getChildFromName(strongNameElement, "a");
		
		String profileUrl = nameAnchorElement.getAttribute("href");
		String username = nameAnchorElement.getTextContent();
		
		//Get city/town attribute
		Element cityTownElement = XMLParser.getChildFromAttribute(tableRow, "class", "cell c2");
		User.Attribute cityTownAttribute = new User.Attribute("City/town", cityTownElement.getTextContent());
		
		//get country attribute
		Element countryElement = XMLParser.getChildFromAttribute(tableRow, "class", "cell c3");
		User.Attribute countryAttribute = new User.Attribute("Country", countryElement.getTextContent());
		
		
		//Generate user object
		User user = new User();
		user.setAvatarDescription(avatarDescription);
		user.setAvatarUrl(avatarUrl);
		user.setName(username);
		user.setUrl(profileUrl);
		user.addAttribute(cityTownAttribute);
		user.addAttribute(countryAttribute);
		
		return user;
	}
}
