package com.nguyenmp.gauchospace.parser;

import java.io.IOException;
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

import com.nguyenmp.gauchospace.thing.Resource;
import com.nguyenmp.gauchospace.thing.Week;

public class WeeklyOutlineParser {
	
	
	public static List<Week> getWeeklyOutlineFromHtml(String htmlString) throws TransformerFactoryConfigurationError, SAXNotRecognizedException, SAXNotSupportedException, IOException, TransformerException, UnparsableHtmlException {
		Element middleColumn = getColumnFromHtml(htmlString, WeeklyOutlineParser.COLUMN_MIDDLE);
		List<Week> weeklyOutline = getCalendarFromMiddleColumn(middleColumn);
		
		return weeklyOutline;
	}
	
	public static final int COLUMN_LEFT = 0;
	public static final int COLUMN_MIDDLE = 1;
	public static final int COLUMN_RIGHT = 2;
	private static Element getColumnFromHtml(String htmlString, int column_id) throws TransformerFactoryConfigurationError, SAXNotRecognizedException, SAXNotSupportedException, IOException, TransformerException, UnparsableHtmlException {
			Document doc = XMLParser.getDocumentFromString(htmlString);
			
			Element docElement = doc.getDocumentElement();
			

//			Element html =  XMLParser.getChildFromName(docElement, "html");
			Element body =  (Element) XMLParser.getChildFromName(docElement, "body");
			Element page =  XMLParser.getChildFromAttribute(body, "id", "page");
			Element content =  XMLParser.getChildFromAttribute(page, "id", "content");
			Element courseContent =  XMLParser.getChildFromAttribute(content, "class", "course-content");
			Element layoutTable =  XMLParser.getChildFromAttribute(courseContent, "id", "layout-table");
	//		Element tbody =  layoutTable.item(0);
	//				//XMLParser.getChildFromName(layoutTable, "tbody");
	//		System.out.println(tbody);
			Element tr =  (Element) XMLParser.getChildFromName(layoutTable, "tr");
			
			String columnId = null;
			
			switch(column_id) {
			case COLUMN_LEFT:
				columnId = "left-column";
				break;
			case COLUMN_MIDDLE:
				columnId = "middle-column";
				break;
			case COLUMN_RIGHT:
				columnId = "right-column";
				break;
			default:
				throw new UnparsableHtmlException("Must have proper column specified");
			}
			
			Element column =  XMLParser.getChildFromAttribute(tr, "id", columnId);
			return column;
		}

	private static List<Week> getCalendarFromMiddleColumn(Element middleColumn) throws TransformerException {
		List<Week> calendar = new ArrayList<Week>();

		Element div =  (Element) XMLParser.getChildFromName(middleColumn, "div");
		Element tableElement = (Element) XMLParser.getChildFromName(div, "table");
		NodeList table = tableElement.getChildNodes();
//		Element tbody =  XMLParser.getChildFromName(table, "tbody");
		
		for (int index = 0; index < table.getLength(); index++) {
			Node tr = table.item(index);
			
			if (tr.getNodeType() == Node.ELEMENT_NODE && ((Element) tr).getAttribute("class").contains("section main")) {
				calendar.add(getWeekFromTableRow((Element) tr));
			}
		}
		
		return calendar;
	}
	
	private static Week getWeekFromTableRow(Element tableRow) throws TransformerException {
		//These are the datas that are needed to initialize the Week object
		boolean current = false;
		String weekdates = null, summary = null;
		List<Resource> contentList = new ArrayList<Resource>();
		
		//Mine the node for data
		Element tableRowElement = (Element) tableRow;
		current = tableRowElement.getAttribute("class").contains("current");
		
		Element content =  XMLParser.getChildFromAttribute(tableRow, "class", "content");

		Element weekdatesElement = (Element) XMLParser.getChildFromAttribute(content, "class", "weekdates");
		//null check necessary because weekdates element does not appear for header weeks (null weeks)
		if (weekdatesElement != null) weekdates = weekdatesElement.getTextContent().trim();
		
		Element summaryElement = (Element) XMLParser.getChildFromAttribute(content, "class", "summary");
		if (summaryElement != null) summary = summaryElement.getTextContent();
		
		Element unorderedList =  (Element) XMLParser.getChildFromName(content, "ul");
		if (unorderedList != null) contentList.addAll(getResourcesFromUnorderedList(unorderedList));
		
		//Compile all processed data into a week object
		Week week = new Week();
		week.setCurrent(current);
		week.setTitle(weekdates);
		week.setSummary(summary);
		week.setHtml(XMLParser.nodeToString(content));
		week.getContent().addAll(contentList);
		
		//Return the compiled week object
		return week;
	}
	
	private static List<Resource> getResourcesFromUnorderedList(Element unorderedList) {
		List<Resource> resources = new ArrayList<Resource>();
		
		NodeList children = unorderedList.getChildNodes();
		
		for (int index = 0; index < children.getLength(); index++) {
			Node listItem =  children.item(index);
			if (listItem.getNodeType() == Node.ELEMENT_NODE) {
				Resource resource = getResourceFromListItem((Element) listItem);
				resources.add(resource);
			}
		}
		
		return resources;
	}
	
	private static Resource getResourceFromListItem(Element listItem) {
		String url = null, imageUrl = null, imageDescription = null, text = null;
		
		Element anchor =  (Element) XMLParser.getChildFromName(listItem, "a");
		if (anchor != null) {
			Element anchorElement = (Element) anchor;
			url = anchorElement.getAttribute("href");
			
			Element imageElement = (Element) XMLParser.getChildFromName(anchor, "img");
			imageUrl = imageElement.getAttribute("src");
			
			Element spanElement = (Element) XMLParser.getChildFromName(anchor, "span");
			text = XMLParser.getChildFromName(spanElement, "#text").getTextContent();
			
//			spanElement = XMLParser.getChildFromName(spanElement, "span");
			if (spanElement != null && XMLParser.getChildFromName(spanElement, "span") != null)
				imageDescription = XMLParser.getChildFromName(spanElement, "span").getTextContent();
			if (imageDescription != null) imageDescription = imageDescription.trim();
		} else {
			Element spanElement = (Element) XMLParser.getChildFromName(listItem, "span");
			text = spanElement.getTextContent();
		}
		
		return new Resource(text, imageUrl, imageDescription, url);
	}
	
	public static class UnparsableHtmlException extends Exception {
		public UnparsableHtmlException(String message) {
			super(message);
		}
		public UnparsableHtmlException() {
			super();
		}
		private static final long serialVersionUID = 5963949411088529856L;
	}
}
