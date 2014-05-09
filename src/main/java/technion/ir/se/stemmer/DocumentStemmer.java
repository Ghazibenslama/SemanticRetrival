package technion.ir.se.stemmer;

public class DocumentStemmer {
	private static final int BEGINING_OF_WORD = 0;
	private PorterStemmer stemmer;
	
	public DocumentStemmer() {
	}


	public String steamWord (String wordToSteam) {
		String stemmedWord = wordToSteam;
		stemmer = new PorterStemmer();
		stemmer.add(wordToSteam.toCharArray(), wordToSteam.length());
		stemmer.stem();
		if (wasWordStemmed(wordToSteam)) {
			stemmedWord = createStemmedWord();
		}
		return stemmedWord;
	}


	private String createStemmedWord() {
		return new String(stemmer.getResultBuffer(), BEGINING_OF_WORD, stemmer.getResultLength());
	}


	private boolean wasWordStemmed(String wordToSteam) {
		return wordToSteam.length() != stemmer.getResultLength();
	}
}
