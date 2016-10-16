package main.java.types;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public abstract class XMLDoc {
	private List<Story> newsItems = new ArrayList<Story>();

	public abstract void parseXml();

	public Document getXML(String url) {
		try{
			//URL webUrl = new URL(url); // Some instantiated URL object
			//URI uri = webUrl.toURI();
			//File f = new File(uri);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(url);
			doc.getDocumentElement().normalize();
			return doc;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public List<Story> getNewsItems() {
		return newsItems;
	}
	public void setNewsItems(List<Story> newsItems) {
		this.newsItems = newsItems;
	}

	public boolean add(Story e) {
		return newsItems.add(e);
	}

}
