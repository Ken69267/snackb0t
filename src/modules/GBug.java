/**
 * Copyright (C) 2009-2011 Kenneth Prugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package modules;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class GBug {

    /* Ivars */
    private String bugID;
    private String reporter;
    private String s_desc;
    private String status;
    private String resolution;
    private String assigned_to;
    private String creation_ts;

    private String parseBugXML() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            Document dom;
            try {
                dom = db
                    .parse("http://bugs.gentoo.org/show_bug.cgi?ctype=xml&id="
                            + bugID);
            } catch (SAXParseException e) {
                return "Invalid Bug";
            }

            Element docEle = dom.getDocumentElement();

            NodeList bugNodes;

            // get the area (2 levels down)
            bugNodes = docEle.getElementsByTagName("bug");
            Element bugNEL = (Element) bugNodes.item(0);

            reporter = getValueForTag(bugNEL, "reporter");
            s_desc = getValueForTag(bugNEL, "short_desc");
            status = getValueForTag(bugNEL, "bug_status");
            resolution = getValueForTag(bugNEL, "resolution");
            assigned_to = getValueForTag(bugNEL, "assigned_to");
            creation_ts = getValueForTag(bugNEL, "creation_ts");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException ne) {
            return "Invalid Bug";
        }

        return formatBugText();
    }

    private String formatBugText() {
        StringBuilder sb = new StringBuilder();

        sb.append("Bug #");
        sb.append(bugID);
        sb.append(": [ ");

        sb.append(s_desc);
        sb.append(" ] [ ");
        sb.append(status);
        sb.append(";");
        sb.append(resolution);
        sb.append(" ] ");

        sb.append("Assigned to [ ");
        sb.append(assigned_to);
        sb.append(" ] ");

        sb.append("Reporter [ ");
        sb.append(reporter);
        sb.append(" ] at [ ");
        sb.append(creation_ts);
        sb.append(" ] ");

        sb.append("https://bugs.gentoo.org/");
        sb.append(bugID);

        return sb.toString();
    }

    public String getBug(String bugID) {
        this.bugID = bugID;
        return parseBugXML();
    }

    /**
     * @param docEle
     * @param tag
     */
    private static String getValueForTag(Element docEle, String tag) {
        String tagValue;
        try {
            NodeList nl = docEle.getElementsByTagName(tag);

            Element el = (Element) nl.item(0);
            tagValue = el.getFirstChild().getNodeValue();
        } catch (Exception e) {
            tagValue = "unknown";
        }
        return tagValue;
    }

    ///**
    // * @param args
    // */
    //public static void main(String[] args) {
    //    GBug gb = new GBug();
    //    String foo = gb.getBug("314501");
    //    System.out.println(foo);
    //}
}
