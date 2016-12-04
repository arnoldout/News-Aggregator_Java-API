package main.java.types;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * XML Document
 * gets data from the getWestLondon Sports Rss feed, 
 * Converts info into story objects
 * stores into list of stories
 */
public class GetWLondonDoc extends XMLDoc{
	//link to GetWestLondon Sports Rss feed
	public final String url = "http://www.getwestlondon.co.uk/sport/?service=rss";
	//parse rss feed
	@Override
	public void parseXml() {
		Document xmlReader = getXML(this.url);

		NodeList nList = xmlReader.getElementsByTagName("item");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;
			Story item = new Story(eElement.getElementsByTagName("link").item(0).getTextContent());
			item.setTitle(eElement.getElementsByTagName("title").item(0).getTextContent());
			item.setDescription(eElement.getElementsByTagName("description").item(0).getTextContent());
			item.setImgUri((eElement.getElementsByTagName("media:thumbnail").item(0)).getAttributes().getNamedItem("url").getNodeValue());
			super.add(item);
		}
	}
}
