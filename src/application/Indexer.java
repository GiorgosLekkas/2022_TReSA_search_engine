package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.BooleanQuery;

public class Indexer {
    
    private IndexWriter writer;
    int i = 0;
    String fields = "";
    String fname = "";
    String[] f;
    
    public Indexer(String indexDirectoryPath, String fields) throws IOException {
    	this.fields = fields;
        f =  fields.split(";");
		for(String str : f)
			fname = fname.concat(str);
		
		
        Path indexPath = Paths.get(indexDirectoryPath);
        if(!Files.exists(indexPath)) {
            Files.createDirectory(indexPath);
        }
        Directory indexDirectory = FSDirectory.open(indexPath);
        IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
        config.setOpenMode(OpenMode.CREATE);
        writer = new IndexWriter(indexDirectory, config);
        
    }
    
    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }
    
    private Document getDocument(File file) throws IOException {
        Document document = new Document();
        BufferedReader br = new BufferedReader(new FileReader(file));
        //String currentLine = br.readLine().toString();
        List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\giorg\\eclipse-workspace\\TReSA\\Data\\" + file.getName()), StandardCharsets.UTF_8);
        String places = lines.get(0), people = lines.get(1), title = lines.get(2), body = "", content = "";
        
        for(int i=3;i<lines.size();i++){
            body = body.concat(lines.get(i) + "\n");
        }
        Field placesField = new Field(LuceneConstants.PLACES, places, TextField.TYPE_STORED);
        Field peopleField = new Field(LuceneConstants.PEOPLE, people, TextField.TYPE_STORED);
        Field titleField = new Field(LuceneConstants.TITLE, title, TextField.TYPE_STORED);
        Field bodyField = new Field(LuceneConstants.BODY, body, TextField.TYPE_STORED);
        body = body.replace("\n"," ");
        
        RemoveTags rt = new RemoveTags();
        places = rt.removePlaces(places);
        people = rt.removePeople(people);
        title = rt.removeTitle(title);
        body = rt.removeBody(body);
        
        String[] t = title.split(" |,|\\.");
        String title_tmp = "";
        Porter p = new Porter();
        for(String str : t) {
        	title_tmp = title_tmp.concat(p.stemWord(str) + " ");
        }
        title = title_tmp;
        
        String[] b = body.split(" |,|\\.");
        String body_tmp = "";
        for(String str : b) {
        	body_tmp = body_tmp.concat(p.stemWord(str) + " ");
        }
        body = body_tmp;
        
        Boolean body_srch = false;
        Boolean title_srch = false;
        for(String s : f) {
        	if(fname.compareTo("title")!=0)
        		title_srch = true;
        	if(fname.compareTo("body")!=0)
        		body_srch = true;
        }
        
        if(title_srch||body_srch) {
        	String[] pl = places.split(" ");
            String places_tmp = "";
            for(String str : pl) {
            	places_tmp = places_tmp.concat(p.stemWord(str) + " ");
            }
            places = places_tmp;
            String[] ppl = places.split(" ");
            String people_tmp = "";
            for(String str : pl) {
            	people_tmp = people_tmp.concat(p.stemWord(str) + " ");
            }
            people = people_tmp;
        }
        System.out.println(people + "---" + places);
        
        if(fname.compareTo("places")!=0 && fname.compareTo("people")!=0 && fname.compareTo("title")!=0 && fname.compareTo("body")!=0) {
        	for(String str : f) {
    			if(str.compareTo("places")==0) {
    				content = content.concat(places + "");
    			}
    			if(str.compareTo("people")==0) {
    				content = content.concat(people + "");
    			}
    			if(str.compareTo("title")==0) {
    				content = content.concat(title + "");
    			}
    			if(str.compareTo("body")==0) {
    				content = content.concat(body + "");
    			}
        	}
        }else {
        	if(fname.compareTo("places")==0) {
        		content = places;
        	}else if(fname.compareTo("people")==0) {
        		content = people;
        	}else if(fname.compareTo("title")==0) {
        		content = title;
        	}else if(fname.compareTo("body")==0) {
        		content = body;
        	}
        }
        
        Field contentField = new Field(LuceneConstants.CONTENTS, content, TextField.TYPE_STORED);
        Field fileNameField = new Field(LuceneConstants.FILE_NAME, file.getName(), StringField.TYPE_STORED);
        Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), StringField.TYPE_STORED);
        
        document.add(placesField);
        document.add(peopleField);
        document.add(titleField);
        document.add(bodyField);
        document.add(contentField);
        document.add(fileNameField);
        document.add(filePathField);
        br.close();
        return document;
    }
    private void indexFile(File file) throws IOException {
        Document document = getDocument(file);
        writer.addDocument(document);
    }
    public int createIndex(String dataDirPath, FileFilter filter) throws IOException {
        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();
        for (File file : files) {
            if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file) ){
                indexFile(file);
            }
        }
        return writer.numRamDocs();
    }
}