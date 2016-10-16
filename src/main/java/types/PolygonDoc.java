package main.java.types;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PolygonDoc extends XMLDoc {

	public final String url = "http://www.polygon.com/rss/index.xml";

	@Override
	public void parseXml() {
		Document xmlReader = getXML(this.url);

		NodeList nList = xmlReader.getElementsByTagName("entry");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;
			Story item = new Story(eElement.getElementsByTagName("id").item(0).getTextContent());
			item.setTitle(eElement.getElementsByTagName("title").item(0).getTextContent());
			//no categories, word cloud needed
			super.add(item);
		}
	}
}