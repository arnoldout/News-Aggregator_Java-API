package main.java.types;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.poi.ddf.EscherColorRef.SysIndexSource;

public class FrequentlyUsedWords {
	private Set<String> freqWords = new HashSet<String>();

	public void generateWords() {
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("words.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				String lower = sCurrentLine.toLowerCase();
				lower = lower.trim();
				freqWords.add(lower);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public Set<String> getFreqWords() {
		return freqWords;
	}

	public void setFreqWords(Set<String> freqWords) {
		this.freqWords = freqWords;
	}
}
