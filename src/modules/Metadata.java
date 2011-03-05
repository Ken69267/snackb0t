package modules;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kcp.KFile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Metadata {

	private final DocumentBuilderFactory dbf = DocumentBuilderFactory
			.newInstance();

	public Metadata() {
		/*
		 * Disable fetching http://www.gentoo.org/dtd/metadata.dtd
		 */
		dbf.setAttribute("http://xml.org/sax/features/validation", false);
		dbf.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		dbf.setAttribute("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		dbf.setValidating(false);
	}

	private String cPackage;

	public String getMetadata(String msg) {
		Pattern pattern = Pattern.compile("[\\w-]+/\\w+");

		Matcher matcher = pattern.matcher(msg);

		if (matcher.find()) {
			cPackage = matcher.group();
			String pkg = "/usr/portage/" + cPackage + "/metadata.xml";
			return readMetadata(pkg);
		} else {
			return "Invalid package format, try foo/bar";
		}
	}

	private String readMetadata(String pkg) {
		KFile file = new KFile(pkg);

		String response = null;

		try {
			String contents = file.read();

			Reader reader = new CharArrayReader(contents.toCharArray());

			DocumentBuilder db = dbf.newDocumentBuilder();

			Document dom = db.parse(new org.xml.sax.InputSource(reader));

			// The root element
			Element docEle = dom.getDocumentElement();

			// maintainer elements
			NodeList mNodes = docEle.getElementsByTagName("maintainer");

			StringBuilder email = new StringBuilder();

			// Loop over Maintainer elements
			for (int i = 0; i < mNodes.getLength(); i++) {
				System.out.println("maintainer node");

				// a maintainer element
				Element mEL = (Element) mNodes.item(i);

				email.append(getValueForTag(mEL, "email") + ", ");
			}

			String herd = getValueForTag(docEle, "herd");

			response = cPackage + ": herd: " + herd + ", maintainer: "
					+ email.toString();
		} catch (IOException e) {
			e.printStackTrace();
			response = "Package does not exist";
		} catch (ParserConfigurationException e) {
			response = "Houston, we accidentally.";
		} catch (SAXException e) {
			response = "Parsing error :(";
		}

		return response;
	}

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

	public static void main(String[] args) {
		Metadata m = new Metadata();
		System.out.println(m.getMetadata("wat app-shells/squirrelsh stuff"));
	}
}
