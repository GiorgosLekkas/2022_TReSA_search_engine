package application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import javafx.collections.ObservableList;

public class Lucene {
	
	
	public ArrayList<File> res = new ArrayList<>();
	public ArrayList<Float> score = new ArrayList<>();
	public ArrayList<Document> docum = new ArrayList<>();
	public TopDocs hits;
	String indexDir = "C:\\Users\\giorg\\eclipse-workspace\\TReSA\\Index";
	String dataDir = "C:\\Users\\giorg\\eclipse-workspace\\TReSA\\Data";
	Indexer indexer;
	public Searcher searcher;
	
	public Lucene() {
		
	}
	
	public Lucene(String str, String fields) {
        try {
        	this.createIndex(fields);
        	this.search(str,fields);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void createIndex(String fields) throws IOException {
        indexer = new Indexer(indexDir,fields);
        int numIndexed;
        long startTime = System.currentTimeMillis();
        numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed+" File(s) indexed, time taken: " + (endTime-startTime) + " ms");
    }

    private void search(String searchQuery, String fields) throws IOException, ParseException {
        searcher = new Searcher(indexDir,fields);
        long startTime = System.currentTimeMillis();

        hits = searcher.search(searchQuery);
        long endTime = System.currentTimeMillis();
        
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            docum.add(searcher.getDocument(scoreDoc));
        }

        System.out.println(hits.totalHits +" documents found. Time :" + (endTime - startTime));
        for(ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = searcher.getDocument(scoreDoc);
            File f = new File(doc.get(LuceneConstants.FILE_PATH).toString());
			res.add(f);
			score.add(scoreDoc.score);
            
            System.out.print("Score: "+ scoreDoc.score + " ");
            System.out.println("File: " + doc.get(LuceneConstants.FILE_PATH));
        }
        searcher.close();
    }
    
}