package com.nguyenmp.gauchospace.parser;

import com.nguyenmp.gauchospace.thing.Week;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class WeeklyOutlineParser {

	public static Week[] getWeeklyOutlineFromHtml(String htmlString) throws XMLException {
        Document doc = XMLParser.getDocumentFromString(htmlString);

        List<Week> weeks = new ArrayList<>();

        int i = 0;
        Node weekNode;
        while ((weekNode = doc.getElementById("section-" + i)) != null) {
            weeks.add(parseWeek(weekNode));
            i += 1;
        }

        return weeks.toArray(new Week[weeks.size()]);
    }

    private static Week parseWeek(Node item) throws XMLException {
        Element content = XMLParser.getChildFromAttribute((Element) item, "class", "content");
        Node titleElement = XMLParser.getChildFromName(content, "h3");
        String title = titleElement == null ? null : titleElement.getTextContent();
        String summary = XMLParser.nodeToString(XMLParser.getChildFromAttribute(content, "class", "summary"));
        boolean current = ((Element) item).getAttribute("class").contains("current");

        Week week = new Week();
        week.title = title;
        week.summary = summary;
        week.current = current;
        // TODO: parse resources from weekly posting
        return week;
    }
}