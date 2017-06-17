package main.java.types;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;

public class LaTimes extends XMLDoc{

	private String url = "http://www.latimes.com/world/europe/rss2.0.xml";
	
	@Override
	public void parseXml() {
		try {
			URL feedSource = new URL(url);

			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new InputStreamReader(feedSource.openStream()));

			List<SyndEntry> items = feed.getEntries();
			for (SyndEntry entry : items) {
				try{
					NeverNullString uri = new NeverNullString(entry.getUri());
					NeverNullString desc = new NeverNullString(entry.getDescription().getValue());
					NeverNullString title = new NeverNullString(entry.getTitle());
					NeverNullString img;
					if(!entry.getForeignMarkup().isEmpty()){
						img = new NeverNullString(entry.getForeignMarkup().get(0).getAttribute("url").getValue());
					}
					else{
						img = new NeverNullString("https://pbs.twimg.com/profile_images/546329819919560704/XMWy2Z50.jpeg");
					}
					Story item = new Story(uri, title, desc, img);
					super.add(item);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
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
