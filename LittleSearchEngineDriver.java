package search;

import java.io.*;
import java.util.*;

import search.LittleSearchEngine;
import search.Occurrence;


public class LittleSearchEngineDriver 
{
public static void main(String[] args) throws FileNotFoundException{
		
		LittleSearchEngine engine = new LittleSearchEngine();
		HashMap<String, String> noiseWords;
		noiseWords = engine.noiseWords;

		Scanner textScan = new Scanner(new File("noisewords.txt"));
		while(textScan.hasNext()){//fills the noiseword hashmap
			String word = textScan.next();
			noiseWords.put(word, word);
			
			
		}
		
		textScan.close();
		
		
		HashMap<String, Occurrence> keyWords = engine.loadKeyWords("AliceCh1.txt");
	
		HashMap<String, Occurrence> keyWordsTwo = engine.loadKeyWords("WowCh1.txt");
	
		
		
		HashMap<String, ArrayList<Occurrence>> keyWordsIndex = engine.keywordsIndex;
		
		
		
		for(Map.Entry<String, Occurrence> entry : keyWords.entrySet()){//loads keywords and also checks getKeyWord method at the same time
			
			String currWord = entry.getKey();
			
			
			
			
		}
		
		engine.mergeKeyWords(keyWords);
		engine.mergeKeyWords(keyWordsTwo);
		
		ArrayList<String> topFive = engine.top5search("deep", "world");
		
		ArrayList<Occurrence> list1 = keyWordsIndex.get("deep");
		ArrayList<Occurrence> list2 = keyWordsIndex.get("world");
		
		System.out.print("Current list of item one: ");
		for(int i = 0; i <list1.size(); i++){
			
			System.out.print(list1.get(i) + " ");
			
			
		}
		System.out.println();
		System.out.println();

		System.out.print("Current list of item two: ");

	    for(int i = 0; i <list2.size(); i++){
			
			System.out.print(list2.get(i) + " ");
			
			
		}
		System.out.println();
		System.out.println();

		
		System.out.print("Top five of the two: ");

		for(int i = 0; i <topFive.size(); i++){
			
			System.out.print(topFive.get(i) + " ");
			
			
		}
	}
}