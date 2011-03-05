package modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class TinyURL {

	private ArrayList<String> tmpLinks;
	private StringBuilder linkString;

	public TinyURL() {
		tmpLinks = new ArrayList<String>();
	}

	public String makeTiny(String message) {
		linkString = new StringBuilder();
		scanLinks(message);

		for (String link : tmpLinks) {
			linkString.append(getTiny(link) + " ");
		}

		tmpLinks.clear();
		return linkString.toString();
	}

	private String getTiny(String link) {
		URL tinyurl;
		String tinyLink = "none";
		try {
			tinyurl = new URL("http://tinyurl.com/api-create.php?url=" + link);
			URLConnection conn = tinyurl.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			tinyLink = reader.readLine();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tinyLink;
	}

	private void scanLinks(String message) {
		for (String word : message.split(" ")) {
			// Match http(s)://[..]. as a rough guess at a link
			if (word.matches("https?://" + ".*\\..*")) {
				if (word.length() >= 70) {
					tmpLinks.add(word);
				}
			}
		}
	}

	// public static void main(String[] args) {
	// TinyURL z = new TinyURL();
	// System.out.println(z.makeTiny("apple sauce "));
	// }
}
