package technion.ir.se.cleaning;

import java.util.List;

public class DocumentStemmer {
	private static final int BEGINING_OF_WORD = 0;
	private PorterStemmer stemmer;
	
	public DocumentStemmer() {
	}


	public String stemWord (String wordToStem) {
		String stemmedWord = wordToStem;
		stemmer = new PorterStemmer();
		stemmer.add(wordToStem.toCharArray(), wordToStem.length());
		stemmer.stem();
		if (wasWordStemmed(wordToStem)) {
			stemmedWord = createStemmedWord();
		}
		return stemmedWord;
	}
	
	public void steamWords(List<String> wordsToStem) {
		for (int i = 0; i < wordsToStem.size() ; i++) {
			String wordToStem = wordsToStem.get(i);
			String stemmedWord = stemWord(wordToStem);
			wordsToStem.set(i, stemmedWord);
		}
	}


	private String createStemmedWord() {
		return new String(stemmer.getResultBuffer(), BEGINING_OF_WORD, stemmer.getResultLength());
	}


	private boolean wasWordStemmed(String wordToSteam) {
		return wordToSteam.length() != stemmer.getResultLength();
	}
}
