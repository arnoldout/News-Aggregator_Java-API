package main.java.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import main.java.types.BBCDoc;
import main.java.types.BangkokPost;
import main.java.types.BloombergNews;
import main.java.types.DailyTelegraph;
import main.java.types.GetWLondonDoc;
import main.java.types.LaTimes;
import main.java.types.NPRDoc;
import main.java.types.NYTDoc;
import main.java.types.PolygonDoc;
import main.java.types.SkyNewsDoc;
import main.java.types.TheGuardianDoc;
import main.java.types.XMLDoc;

public class NewsFactory {
	private List<XMLDoc> docs = new ArrayList<XMLDoc>(
			Arrays.asList(new NPRDoc(),new PolygonDoc(),new NYTDoc(), new BBCDoc(), new GetWLondonDoc(), new SkyNewsDoc(),
					new TheGuardianDoc(), new BloombergNews(), new DailyTelegraph(),
					new BangkokPost(), new LaTimes()));

	public void getDocs() {
		// docs size wont ever be massive, it will only ever be the amount of
		// supported sites
		ExecutorService executor = Executors.newFixedThreadPool(docs.size());

		// loop through all xml files, and parse on a thread
		for (XMLDoc d : docs) {
			// parse the xml file in a thread
			//executor.submit(() -> {
			try{
				d.parseXml();
			}
			catch(NullPointerException e)
			{
				e.printStackTrace();
			}
			catch(IndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}
			//});
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.out.println("Thread Broken");
		}

	}
	public List<XMLDoc> getParsedDocs()
	{
		return docs;
	}
}
