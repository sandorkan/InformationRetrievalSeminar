package tagesanzeiger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneSearch {
    
    public static void main(String[] args) throws IOException, ParseException {
        
        File indexDir = new File("/Users/sandor/Documents/Schule/Data Mining/Information Retrieval/scrapy/irtest/lucene");
        String query = "+svp +schweiz";
        int maxHits = 1024;
        
        Directory fsDir = FSDirectory.open(indexDir);
        
        IndexReader reader = IndexReader.open(fsDir);
        IndexSearcher searcher = new IndexSearcher(reader);
           
        
        GermanAnalyzer germanAnalyzer = new GermanAnalyzer(Version.LUCENE_36);
        
        String defaultField = "text";
        QueryParser parser = new QueryParser(Version.LUCENE_36, defaultField, germanAnalyzer);
        
        Query q = parser.parse(query);
        
        System.out.println("Query: " + q.toString());
        
        TopDocs hits = searcher.search(q, maxHits);
        ScoreDoc[] scoreDocs = hits.scoreDocs;
        
        System.out.println("Total Docs: " + scoreDocs.length);
        
        for (int i = 0; i < scoreDocs.length; i++) {
            
            ScoreDoc doc = scoreDocs[i];
            float score = doc.score;
            int docId = doc.doc;
            
            Document d = searcher.doc(docId);
            String title = d.get("title");
            
            System.out.printf("%3d %4.2f  %s\n", docId, score, title);
        }
        
    }
    
}
