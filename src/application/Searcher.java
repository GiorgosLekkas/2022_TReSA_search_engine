package application;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;

public class Searcher {
	
	IndexSearcher indexSearcher;
	Directory indexDirectory;
	IndexReader indexReader;
	QueryParser queryParser;
	Query query;
    String fields = "";
    String fname = "";
    String[] f;
	
	public Searcher(String indexDirectoryPath, String fields) throws IOException {
		this.fields = fields;
        f =  fields.split(";");
		Path indexPath = Paths.get(indexDirectoryPath);
		indexDirectory = FSDirectory.open(indexPath);
		indexReader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(indexReader);
		queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
	}
	
	public TopDocs search(String searchQuery) throws IOException, ParseException {
		String searchQuery_tmp = "";
		for(String str : f) {
	        if(str.compareTo("title")==0 || str.compareTo("body")==0) {
	        	Porter p = new Porter();
	        	String[] t = searchQuery.split(" ");
	            for(String s : t) {
	            	searchQuery_tmp = searchQuery_tmp.concat(p.stemWord(s) + " ");
	            }
	            searchQuery = searchQuery_tmp;
	        }
		}
		query = queryParser.parse(searchQuery);
		System.out.println("query: "+ query.toString());
		return indexSearcher.search(query, LuceneConstants.MAX_SEARCH,Sort.RELEVANCE);
	}
	
	public Document getDocument(ScoreDoc scoreDoc) throws CorruptIndexException, IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}
	
	public void close() throws IOException {
		indexReader.close();
		indexDirectory.close();
	}
}