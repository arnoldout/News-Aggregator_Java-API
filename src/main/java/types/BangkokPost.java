package main.java.types;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BangkokPost extends GenericRssDoc {

	public BangkokPost() {
		super("http://www.bangkokpost.com/rss/data/topstories.xml");
	}

	@Override
	public String findImage(String url) throws IOException {
		try {
			Document document = Jsoup.connect(url).get();
			Elements element = document.getElementsByTag("img");
			for (Element e : element) {
				try {
					if (new Integer(e.attr("height")) > 300) {
						return e.attr("src");
					}
				} catch (NumberFormatException ee) {
					// System.out.println("No Height Set");
				}
			}
		} catch (SocketTimeoutException e) {
		}
		return "";
	}
}
