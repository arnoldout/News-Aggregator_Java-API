package main.java.types;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/*
* XML Document
* gets data from the Polygon Rss feed, 
* Converts info into story objects
* stores into list of stories
*/
public class PolygonDoc extends XMLDoc {

	//link to polygon rss feed
	public final String url = "http://www.polygon.com/rss/index.xml";

	//parse rss feed
	@Override
	public void parseXml() {
		Document xmlReader = getXML(this.url);

		NodeList nList = xmlReader.getElementsByTagName("entry");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;
			Story item = new Story(eElement.getElementsByTagName("id").item(0).getTextContent());
			item.setTitle(eElement.getElementsByTagName("title").item(0).getTextContent());
			String img = (eElement.getElementsByTagName("content").item(0).getTextContent());
			String[] parts = img.split("\"");
			item.setImgUri(parts[3]);
			//no categories, word cloud needed
			super.add(item);
		}
	}
}