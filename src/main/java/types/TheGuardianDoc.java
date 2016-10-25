package main.java.types;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TheGuardianDoc extends XMLDoc {

	public final String url = "https://www.theguardian.com/world/rss";

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
			item.setImgUri((eElement.getElementsByTagName("media:content").item(0)).getAttributes().getNamedItem("url").getNodeValue());	
			//no categories, word cloud needed
			super.add(item);
		}	
	}
}