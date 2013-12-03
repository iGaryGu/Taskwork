import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class NLParser {

	/**
	 * @param args
	 */
	private static final String documentPath = "/home/damon/ETop/Terms/";
	private static final String stopWordPath = "/home/damon/ETop/stopwords.csv";
	private static String[] stopWord;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		readStopWords(stopWordPath);
		
	}
	
	//return the TF-IDF of all word
	public static HashMap<String, Double> getTFIDF(String inputStr) throws IOException{
		HashMap<String, Double> result = new HashMap<String, Double>();
		HashMap<String, Integer> termsMap = new HashMap<String, Integer>();		
		ArrayList<String> inputTerms = getTerms(inputStr);
		
		for(String s : inputTerms){
			if(termsMap.containsKey(s) != true){
				termsMap.put(s, 1);
			}else{
				termsMap.put(s, termsMap.get(s)+1);
			}
		}
		for(String term : termsMap.keySet()){
			double TF;
			//System.out.println(term + " " + termsMap.get(term));				
			TF = (double)termsMap.get(term)/inputTerms.size();
			result.put(term, TF*cal_IDF(term));
		}
		
		return result;
	}
	
	private static void readStopWords(String path) throws IOException{
		stopWord = readFileToString(path).split(",");
	}
	
	//read file from the filePath
	private static String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return  fileData.toString();	
	}
	
	//calculate the TD-IDF of target terms 
	private static double cal_IDF(String tarTerm) throws IOException{
		File file = new File(documentPath);
		File[] files = file.listFiles();
		int D_count = 0;
		
		for(File f : files){
			String document = readFileToString(f.getAbsolutePath());
			String[] terms = document.split(",");
			boolean match = false;
			for(String term : terms){
				if(term.equals(tarTerm)){
					match = true;
					D_count++;
					break;
				}
			}
			if(match) continue;
		}
		return Math.log10((double)files.length/D_count);
	}

	//get the terms from the sentence entered by user
	public static ArrayList<String> getTerms(String comment){

		String[] terms = comment.split("[^a-zA-Z]+");
		//if(terms.length == 0) System.out.println("error: " + comment);
		ArrayList<String> termsarray = new ArrayList<String>();
		for(int i=0;i<terms.length;i++){
			
			String iterm = terms[i].toLowerCase();
			boolean isStopWord = false;
			for(String s : stopWord){
				if(iterm.equals(s)){
					isStopWord = true;
					break;
				}
			}
			if(isStopWord == true)
				continue;
			
			Stemmer stemmer = new Stemmer();
			stemmer.add(iterm.toCharArray(), iterm.length());
			stemmer.stem();
			iterm = stemmer.toString();
			
			//System.out.println(iterm);
			if(iterm.length() >=2 && !termsarray.contains(iterm))termsarray.add(iterm);
		}
	
		return termsarray;
	}
}
