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
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class LuceneIndex_GermanAnalyzer {
    
    public static void main(String[] args) {
        
        try 
        {
            
             // docDir:     In diesem Ordner befinden sich die zu indizierenden Files
             // indexDir:   Lucene soll die Files für den Index in diesen Ordner speichern
             File docDir = new File("/Users/sandor/Documents/Schule/Data Mining/Information Retrieval/scrapy/irtest/ta");
             File indexDir = new File("/Users/sandor/Documents/Schule/Data Mining/Information Retrieval/scrapy/irtest/ta_doc_index2");
             
             Directory fsDir = FSDirectory.open(indexDir);
             
             GermanAnalyzer germanAnalyzer = new GermanAnalyzer(Version.LUCENE_36);
             LimitTokenCountAnalyzer ltcAn = new LimitTokenCountAnalyzer(germanAnalyzer, Integer.MAX_VALUE);
             
             IndexWriterConfig iwConf = new IndexWriterConfig(Version.LUCENE_36, ltcAn);
             iwConf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
             
             IndexWriter indexWriter = new IndexWriter(fsDir, iwConf);
             
             /*
              *     title - Ist der Titel des Tagesanzeiger-Artikels
              *     text  - Der Text des Tagesanzeiger-Artikel
              * 
              *     Argumente für das Field-Objekt sind -> Name, Wert, Index-Flags
              * 
              *     Für jeden Artikel im docDir wird ein eigenes Dokument erstellt und anschliessend dem IndexWriter hinzugefügt
              */
             for(File f : docDir.listFiles())
             {
                 String title = f.getName();
                 String text = readTextFromFile(f);
                 
                 Document d = new Document();
                 
                 // Im ersten Durchgang der Durchführung der Suchanfragen wurden die Felder wie folgt erstellt
//                 d.add(new Field("title",title,Store.YES,Index.ANALYZED));
//                 d.add(new Field("text",text,Store.YES,Index.ANALYZED));
                 
                 // Im zweiten Durchgang der Durchführung der Suchanfragen wurden die Felder wie folgt erstellt
                 d.add(new Field("title",title,Store.YES,Index.ANALYZED_NO_NORMS));
                 d.add(new Field("text",text,Store.YES,Index.ANALYZED_NO_NORMS));
                 
                 indexWriter.addDocument(d);
                 
             }
             
             int numDocs = indexWriter.numDocs();
             
             indexWriter.forceMerge(1);
             indexWriter.commit();
             indexWriter.close();
             
        } catch (IOException ex) {
            Logger.getLogger(LuceneIndex_GermanAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String readTextFromFile(File f) throws FileNotFoundException, IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(f));
             
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
     
        int count = 0;
             
        while(line != null)
        {
            sb.append(line);
            sb.append(" ");
            line = br.readLine();
            count++;
        }
             
        br.close();
        
        return sb.toString();
    }
    
}
