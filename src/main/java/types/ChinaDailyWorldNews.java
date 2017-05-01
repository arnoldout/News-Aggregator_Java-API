package main.java.types;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

public class ChinaDailyWorldNews extends XMLDoc {
	// link to bbc rss feed
	public final String url = "	http://www.chinadaily.com.cn/rss/world_rss.xml";

	// parse rss feed for stories
	@Override
	public void parseXml() {
		
		URL feedSource;
		try {
			feedSource = new URL(url);
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new InputStreamReader(feedSource.openStream()));
			feed.getDescription();
			
		} catch (IllegalArgumentException | FeedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		Document xmlReader = getXML(this.url);

		NodeList nList = xmlReader.getElementsByTagName("table");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;
			Story item = new Story(eElement.getElementsByTagName("guid").item(0).getTextContent());
			item.setTitle(eElement.getElementsByTagName("title").item(0).getTextContent());

			item.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());
			item.setImgUri((eElement.getElementsByTagName("img").item(0)).getAttributes().getNamedItem("src").getNodeValue());
			super.add(item);
		}
	}
}
