package com.nguyenmp.gauchospace.parser;

import com.nguyenmp.gauchospace.thing.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParticipantsParser {
	
	
	public static List<User> getParticipantsFromHtml(String htmlString) throws IOException, XMLException {
		Element tableElement = getTableFromHtml(htmlString);
		return getParticipantsFromTable(tableElement);
	}
	
	private static Element getTableFromHtml(String htmlString) throws IOException, XMLException {
		Document document = XMLParser.getDocumentFromString(htmlString);
		Element documentElement = document.getDocumentElement();
		Element bodyElement = (Element) XMLParser.getChildFromName(documentElement, "body");
		Element pageElement = XMLParser.getChildFromAttribute(bodyElement, "id", "page");
		Element contentElement = XMLParser.getChildFromAttribute(pageElement, "id", "content");
		
		return XMLParser.getChildFromAttribute(contentElement, "id", "participants");
	}
	
	private static List<User> getParticipantsFromTable(Element tableElement) throws XMLException {
		NodeList children = tableElement.getChildNodes();
		List<User> participants = new ArrayList<>();
		
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
		avatarUrl = avatarUrl.replace("f2.jpg", "f1.jpg");
		
		//Get name detail
		Element nameCellElement = XMLParser.getChildFromAttribute(tableRow, "class", "cell c1");
		Element strongNameElement = (Element) XMLParser.getChildFromName(nameCellElement, "strong");
		Element nameAnchorElement = (Element) XMLParser.getChildFromName(strongNameElement, "a");
		
		String profileUrl = nameAnchorElement.getAttribute("href");
		String username = nameAnchorElement.getTextContent();
		
		//Get city/town attribute
		Element cityTownElement = XMLParser.getChildFromAttribute(tableRow, "class", "cell c2");
		User.Attribute cityTownAttribute = new User.Attribute(User.Attribute.KEY_CITY, cityTownElement.getTextContent());
		
		//get country attribute
		Element countryElement = XMLParser.getChildFromAttribute(tableRow, "class", "cell c3");
		User.Attribute countryAttribute = new User.Attribute(User.Attribute.KEY_COUNTRY, countryElement.getTextContent());
		
		
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
