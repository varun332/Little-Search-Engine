package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			mergeKeyWords(kws);
		}
		
	}
	
	private boolean isNoiseTrueOrFalse (String word) 
			throws FileNotFoundException {
				Scanner scanText = new Scanner(new File("noisewords.txt"));
				
				while (scanText.hasNext()) {
					String nextWord = scanText.next();
					
					if (nextWord.equals(word)) {
						return true;
					}
				}

				return false;
			}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		Scanner scanText = new Scanner(new File(docFile));
		HashMap<String, Occurrence> hashOfStrings = new HashMap<String, Occurrence>(1000,2.0f);
		
		while (scanText.hasNext()) {
		
			String full = scanText.next();
			String half = getKeyWord(full);
			
			if (half != null) {
				
				if (hashOfStrings.containsKey(half)) {
					++hashOfStrings.get(half).frequency;
				} 
				
				else {
					Occurrence timesItAppears = new Occurrence(docFile, 1);
					hashOfStrings.put(half, timesItAppears);
				}
			}
		}
		scanText.close();
		return hashOfStrings;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		for (String key: kws.keySet()) {
			
			if (keywordsIndex.containsKey(key)) {
				keywordsIndex.get(key).add(kws.get(key));
				insertLastOccurrence(keywordsIndex.get(key));
			} 
			
			else {
				ArrayList<Occurrence> arr = new ArrayList<Occurrence>();
				
				arr.add(kws.get(key));
				keywordsIndex.put(key, arr);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		word = word.toLowerCase();
		
		while (word.length() > 0) {
			boolean isTrue = false;
			
			if (word.endsWith("?") || word.endsWith(":") || word.endsWith(";") || word.endsWith(".") || word.endsWith(",") || word.endsWith("!") || word.endsWith(")") || word.endsWith("]")) {
				word = word.substring(0,word.length() - 1);
				isTrue = true;
			}
			
			if (word.startsWith("(") || word.startsWith("[")) {
				word = word.substring(1);
				isTrue = true;
			}
			
			if (!isTrue) {
				break;
			}
		}
		
		if (word.contains(":") || word.contains(";") || word.contains(".") || word.contains(",") || word.contains("?") || word.contains("!") || word.contains("(")|| word.contains(")")|| word.contains("[")|| word.contains("]") || word.contains("-") ||word.contains("'")) {
			return null;
		} 
		
		else 
			try {
				if (isNoiseTrueOrFalse(word)) {
					return null;
				}
			} 
			
			catch (FileNotFoundException error) {
				error.printStackTrace();
			}
		
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		
		if (occs.size() <= 1 || occs.isEmpty()) {
			return null;
		}
		
		if (occs.size() == 2) {
			arr.add(0);
			
			if (occs.get(0).frequency<occs.get(1).frequency) {
				Occurrence howManyTimes = occs.get(0);
				
				occs.set(0, occs.get(1));
				occs.set(1, howManyTimes);
			}
			
			return arr;
		}
		
		int lowest = occs.size() - 2;
		int highest = 0;
		int inBetween = 0;
		
		Occurrence searchFor = occs.get(occs.size()-1);
		
		while (lowest >= highest) {
			inBetween = (highest + lowest) / 2;
			arr.add(inBetween);
			
			if (occs.get(inBetween).frequency == searchFor.frequency) {
				break;
			} 
			
			else if (searchFor.frequency > occs.get(inBetween).frequency) {
				lowest = inBetween - 1;
			} 
			
			else {
				highest = inBetween + 1;
			}
		}
		
		if (searchFor.frequency < occs.get(occs.size() - 2).frequency) {
			arr.add(occs.size() - 1);
			return arr;
		}
		
		for (int i = occs.size() - 1; i > inBetween; i--) {
			occs.set(i, occs.get(i - 1));
		}
		
		occs.set(inBetween, searchFor);
		return arr;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		if (kw1 == null || kw2 == null) {
			return null;
		}
		
		ArrayList<String> searchInTop5 = new ArrayList<String>();
		ArrayList<Occurrence> firstWord = new ArrayList<Occurrence>();
		ArrayList<Occurrence> secondWord = new ArrayList<Occurrence>();
		
		if (keywordsIndex.get(kw1.toLowerCase()) != null) {
			firstWord = keywordsIndex.get(kw1.toLowerCase());
		}
		
		int count = 0;
		int count2 = 0;
		
		if (keywordsIndex.get(kw2.toLowerCase()) != null) {
			secondWord = keywordsIndex.get(kw2.toLowerCase());
		}
		
		if (firstWord == null && secondWord != null) {
			
			while (searchInTop5.size() <= 4 && count2 < secondWord.size()) {
				searchInTop5.add(secondWord.get(count2).document);
				count2++;
			}
		}
		
		else if (firstWord != null && secondWord == null) {
			
			while (searchInTop5.size()<=4 && count < firstWord.size()) {
				searchInTop5.add(firstWord.get(count).document);
				count++;
			}
		}
		
		else {
			
			while (count2 < secondWord.size() && count < firstWord.size() && searchInTop5.size() <= 4) {
				
				if (secondWord.get(count2).frequency < firstWord.get(count).frequency) {
					
					if (!searchInTop5.contains(firstWord.get(count).document)) {
						searchInTop5.add(firstWord.get(count).document);
					}
					
					count++;
				}
				
				else if (secondWord.get(count2).frequency > firstWord.get(count).frequency) {
					
					if (!searchInTop5.contains(secondWord.get(count2).document)){
						searchInTop5.add(secondWord.get(count2).document);
					}
					
					count2++;
				}
				
				else {
					
					if (!searchInTop5.contains(firstWord.get(count).document)) {
						searchInTop5.add(firstWord.get(count).document);
					}
					
					if (!searchInTop5.contains(secondWord.get(count2).document) && searchInTop5.size() < 5) {
						searchInTop5.add(secondWord.get(count2).document);
					}
					
					count++;
					count2++;
				}
			}
			
			if (count == firstWord.size()) {
				
				while (searchInTop5.size() <= 4 && count2 < secondWord.size()) {
					
					if (!searchInTop5.contains(secondWord.get(count2).document)) {
						searchInTop5.add(secondWord.get(count2).document);
					}
					
					count2++;
				}
			}
			
			if (count2 == secondWord.size()) {
				
				while (searchInTop5.size() <= 4 && count < firstWord.size()) {
					
					if (!searchInTop5.contains(firstWord.get(count).document)) {
						searchInTop5.add(firstWord.get(count).document);
					}
					
					count++;
				}
			}
		}
		
		if (searchInTop5.size() == 0) {
			return null;
		}
		
		return searchInTop5;
	}
}