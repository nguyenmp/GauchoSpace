/* Copyright (C) 2012 Mark
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.nguyenmp.gauchospace.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.nguyenmp.gauchospace.thing.grade.GradeFolder;
import com.nguyenmp.gauchospace.thing.grade.GradeItem;

public class GradeParser {
	public static GradeFolder getGradeFromHtml(String gradeHtml) throws SAXNotRecognizedException, SAXNotSupportedException, IOException, TransformerFactoryConfigurationError, TransformerException {
		Element tBodyElement = getTableBodyElementFromHtmlString(gradeHtml);
		GradeFolder grades = getGradesFromTBodyElement(tBodyElement);
		return grades;
	}
	
	private static GradeFolder getGradesFromTBodyElement(Element tBodyElement) {
		NodeList children = tBodyElement.getChildNodes();
		
		GradeFolder masterFolder = null;
		GradeFolder currentFolder = null;
		int currentLevel = 0;
		
		for (int index = 0; index < children.getLength(); index++) {
			Element tableRowElement = (Element) children.item(index);
			
			//The content is always on the second node
			Element contentRowElement = (Element) tableRowElement.getChildNodes().item(1);
//			Element itemContentElement = (Element) tableRowElement.getChildNodes().item(0);
			if (contentRowElement == null) continue;
			String tableRowClass = contentRowElement.getAttribute("class");
			if (tableRowClass.startsWith("oddd") || tableRowClass.startsWith("evend")) {
				
				Scanner scanner = new Scanner(new ByteArrayInputStream(tableRowClass.getBytes()));
				int level = scanner.skip("[^0-9]*").nextInt();
				
				if (level > currentLevel) {
					//New Sub-Folder
					currentLevel++;
					GradeFolder folder = new GradeFolder(tableRowElement.getTextContent());
					if (currentFolder == null) {
						currentFolder = folder;
						masterFolder = folder;
					} else {
						folder.setParent(currentFolder);
						currentFolder.addItem(folder);
						currentFolder = folder;
//						System.out.println("Adding " + folder.getName() + " to " + folder.getParent().getName());
					}
				} else {
					//Close Old Folder and go to parent (folder closed)
					getItemFromRow(currentFolder, tableRowElement);
					currentFolder = currentFolder.getParent();
					currentLevel--;
				}
				scanner.close();
				
			} else if (tableRowClass.startsWith("item")) {
				//add item to current folder
//				GradeItem item = new GradeItem(itemContentElement.getTextContent());
				GradeItem item = getItemFromRow(null, tableRowElement);
				currentFolder.addItem(item);
//				System.out.println("Adding " + item.getName() + " to " + currentFolder.getName());
			}
		}
		
		return masterFolder;
	}
	
	private static GradeItem getItemFromRow(GradeItem item, Element element) {
		NodeList children = element.getChildNodes();
		Node titleNode = children.item(0);
		if (item == null) 
			item = new GradeItem(titleNode.getTextContent());
		else
			item.setName(titleNode.getTextContent());
		
		Node gradeNode = children.item(1);
		item.setGrade(gradeNode.getTextContent());
		
		Node rangeNode = children.item(2);
		item.setRange(rangeNode.getTextContent());
		
		Node percentageNode = children.item(3);
		item.setPercentage(percentageNode.getTextContent());
		
		Node feedbackNode = children.item(4);
		item.setFeedback(feedbackNode.getTextContent());
		
		return item;
	}

	private static Element getTableBodyElementFromHtmlString(String gradeHtml) throws SAXNotRecognizedException, SAXNotSupportedException, IOException, TransformerFactoryConfigurationError, TransformerException {
		Document doc = XMLParser.getDocumentFromString(gradeHtml);
		Element docElement = doc.getDocumentElement();
		Element bodyElement = (Element) XMLParser.getChildFromName(docElement, "body");
		Element pageElement = XMLParser.getChildFromAttribute(bodyElement, "id", "page");
		Element contentElement = XMLParser.getChildFromAttribute(pageElement, "id", "content");
		Element tableElement = (Element) XMLParser.getChildFromName(contentElement, "table");
		Element tBodyElement = (Element) XMLParser.getChildFromName(tableElement, "tbody");
		
		return tBodyElement;
	}
	
//	private class GradeWrapper {
//		private int index = 0;
//		Element getNext() {
//			
//		}
//	}
}
