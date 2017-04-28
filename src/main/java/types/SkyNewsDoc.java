package main.java.types;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
* XML Document
* gets data from the Sky News Rss feed, 
* Converts info into story objects
* stores into list of stories
*/
public class SkyNewsDoc extends XMLDoc {

	//link to sky news' world news rss feed
	public final String url = "http://feeds.skynews.com/feeds/rss/world.xml";

	//parse rss feed
	@Override
	public void parseXml() {
		Document xmlReader = getXML(this.url);

		NodeList nList = xmlReader.getElementsByTagName("item");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;
			Story item = new Story(eElement.getElementsByTagName("guid").item(0).getTextContent());
			item.setTitle(eElement.getElementsByTagName("title").item(0).getTextContent());
			item.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());
			String img = ((eElement.getElementsByTagName("media:thumbnail").item(0)).getAttributes().getNamedItem("url").getNodeValue());			//no categories, word cloud needed
			img = img.replace("70x70", "1096x616");
			item.setImgUri(img);
			super.add(item);
		}
	}
}