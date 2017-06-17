package main.java.types;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

/*
* XML Document
* gets data from the New York Times Rss feed, 
* Converts info into story objects
* stores into list of stories
*/
public class NYTDoc extends XMLDoc {

	// link to nyt rss most viewed stories rss feed
	public final String url = "http://rss.nytimes.com/services/xml/rss/nyt/MostViewed.xml";

	public String findImage(String url) throws IOException {
		try {
			Document document = Jsoup.connect(url).get();
			Elements element = document.getElementsByTag("img");
			for (Element e : element) {
				if (e.attr("src").startsWith("https://static01.nyt.com/images/")) {
					return e.attr("src");
				}
			}
		} catch (SocketTimeoutException e) {
			//no images found
		}
		return "http://1000logos.net/wp-content/uploads/2017/04/Symbol-New-York-Times.png";
	}

	// parse the rss feed
	@Override
	public void parseXml() {
		try {
			URL feedSource = new URL(url);

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new InputStreamReader(feedSource.openStream()));

			List<SyndEntry> items = feed.getEntries();
			for (SyndEntry entry : items) {
				
				NeverNullString uri = new NeverNullString(entry.getUri() + "?smid=fb-nytimes&smtyp=cur");
				uri.getString().replace("?partner=rss&amp;emc=rss", "");
				NeverNullString desc = new NeverNullString(entry.getDescription().getValue());
				NeverNullString title = new NeverNullString(entry.getTitle());
				NeverNullString imgUri = new NeverNullString(findImage(uri.getString()));
				Story item = new Story(uri, title, desc, imgUri);
				super.add(item);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}