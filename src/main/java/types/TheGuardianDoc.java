package main.java.types;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
* XML Document
* gets data from the The Guardian Rss feed, 
* Converts info into story objects
* stores into list of stories
*/
public class TheGuardianDoc extends XMLDoc {

	//link to world news rss feed from the guardian
	public final String url = "https://www.theguardian.com/world/rss";

	//parse rss feed
	@Override
	public void parseXml() {
		Document xmlReader = getXML(this.url);

		NodeList nList = xmlReader.getElementsByTagName("item");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			Element eElement = (Element) nNode;
			//no categories, word cloud needed
			NeverNullString uri = new NeverNullString(eElement.getElementsByTagName("guid").item(0).getTextContent());
			NeverNullString title = new NeverNullString(eElement.getElementsByTagName("title").item(0).getTextContent());
			NeverNullString desc = new NeverNullString(eElement.getElementsByTagName("description").item(0).getTextContent().replaceAll("\\<.*?>",""), "No Description Provided");
			NeverNullString imgUri = new NeverNullString((eElement.getElementsByTagName("media:content").item(1)).getAttributes().getNamedItem("url").getNodeValue(),"http://icons.iconarchive.com/icons/martz90/circle/512/guardian-icon.png");
			Story item = new Story(uri, title, desc, imgUri);
			super.add(item);
		}	
	}
}