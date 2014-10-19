package com.nguyenmp.gauchospace.thing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single week in the weekly outline when viewing a course's summary.
 */
public class Week implements Serializable {
    /** The heading for the week.  This is pulled out of the <h3> tag.
     * Will be null for the first entry and then the week range for the remainder.*/
	public String title = null;

    /** The HTML content of the week.  This is written by the professor in WYSIWYG.  IN HTML! */
    public String summary = null;

    /** True if GauchoSpace is highlighting this week in light blue to denote it as the current week */
	public boolean current = false;

    /** The list of links below the week.  Could be forums, links, files, etc. */
	public List<Resource> content = new ArrayList<>();
	
	public String toString() {
		return String.format("Title: %s; Summary: %s; Current: %s; Content: %s", title, summary, current, content);
	}
}
