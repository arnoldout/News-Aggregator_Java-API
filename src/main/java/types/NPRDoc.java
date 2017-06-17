package main.java.types;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jdom2.Element;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

public class NPRDoc extends XMLDoc{
	public final String url = "http://www.npr.org/rss/rss.php?id=103943429";
	//parse rss feed
	@Override
	public void parseXml() {
		try {
			URL feedSource = new URL(url);

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new InputStreamReader(feedSource.openStream()));

			List<SyndEntry> items = feed.getEntries();
			for (SyndEntry entry : items) {
				NeverNullString uri = new NeverNullString(entry.getLink());
				NeverNullString title = new NeverNullString(entry.getTitle());
				NeverNullString desc = new NeverNullString(entry.getDescription().getValue(), "No Description Provided");
				NeverNullString imgUri = new NeverNullString(entry.getContents().get(0).getValue().split("'")[1], "http://www.thedailysheeple.com/wp-content/uploads/2012/09/npr.jpeg");
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
