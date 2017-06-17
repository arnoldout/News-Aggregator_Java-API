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
			
			String img = ((eElement.getElementsByTagName("media:thumbnail").item(0)).getAttributes().getNamedItem("url").getNodeValue());			//no categories, word cloud needed
			img = img.replace("70x70", "1096x616");
			NeverNullString uri = new NeverNullString(eElement.getElementsByTagName("guid").item(0).getTextContent());
			NeverNullString title = new NeverNullString(eElement.getElementsByTagName("title").item(0).getTextContent());
			NeverNullString desc = new NeverNullString(eElement.getElementsByTagName("description").item(0).getTextContent(), "No Description Provided");
			NeverNullString imgUri = new NeverNullString(img, "https://pbs.twimg.com/profile_images/596404466200489984/cZjPh8eP_400x400.png");
			Story item = new Story(uri, title, desc, imgUri);
			super.add(item);
		}
	}
}