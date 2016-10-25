package main.java.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import main.java.types.BBCDoc;
import main.java.types.GetWLondonDoc;
import main.java.types.NYTDoc;
import main.java.types.PolygonDoc;
import main.java.types.SkyNewsDoc;
import main.java.types.XMLDoc;

public class NewsFactory {
	public List<XMLDoc> docs = new ArrayList<XMLDoc>(Arrays.asList(new NYTDoc(), new PolygonDoc(), new BBCDoc(), new GetWLondonDoc(), new SkyNewsDoc()));
	
	public void getDocs()
    {
		//docs size wont ever be massive, it will only ever be the amount of supported sites
		ExecutorService executor = Executors.newFixedThreadPool(docs.size());
        
		for(XMLDoc d : docs)
        {
            executor.submit(() -> {d.parseXml();});
        }
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.out.println("Thread Broken");
		}
        	
    }
}
