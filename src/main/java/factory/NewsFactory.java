package main.java.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.reflections.Reflections;

import main.java.services.MongoConnection;
import main.java.types.FrequentlyUsedWords;
import main.java.types.HTMLDoc;
import main.java.types.Story;
import main.java.types.XMLDoc;

public class NewsFactory {

	private List<XMLDoc> docs;
	private Queue<Story> stories;

	public NewsFactory() {
		setStories(new ConcurrentLinkedQueue<Story>());
		// scan package for instances of a type
		Reflections reflections = new Reflections("main.java.types");
		Set<Class<? extends XMLDoc>> classes = reflections.getSubTypesOf(XMLDoc.class);
		ArrayList<Class<? extends XMLDoc>> tmpList = new ArrayList<Class<? extends XMLDoc>>(classes);
		docs = new ArrayList<XMLDoc>();
		for (int i = 0; i < tmpList.size(); i++) {
			try {
				docs.add(tmpList.get(i).newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				System.out.println("Problem Creating new Instance of XMLDoc");
			}
		}

	}

	public void generateDocs(FrequentlyUsedWords fuw, Map<String, Queue<Story>> generatedTags, MongoConnection mc) {
		// docs size wont ever be massive, it will only ever be the amount of
		// supported sites
		ExecutorService executor = Executors.newFixedThreadPool(docs.size() * 4);

		// loop through all xml files, and parse on a thread
		for (XMLDoc docInstance : docs) {
			// parse the xml file in a thread
			executor.submit(() -> {
				try {
					docInstance.parseXml();
					stories.addAll(docInstance.getNewsItems());
					for (Story s : docInstance.getNewsItems()) {
						HTMLDoc doc = new HTMLDoc();
						doc.setUrl(s.getUri());
						executor.submit(() -> {
							s.setCategories(doc.parseText(fuw.getFreqWords()));
							new TaggingFactory(mc).generateTags(s, generatedTags);
						});
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			});
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.out.println("Thread safety Broken");
		}

	}

	public List<XMLDoc> getParsedDocs() {
		return docs;
	}

	public Queue<Story> getStories() {
		return stories;
	}

	public void setStories(Queue<Story> stories) {
		this.stories = stories;
	}
}
