package org.almuallim.lucene.search;

import java.io.File;
import org.almuallim.service.helpers.Application;
import org.almuallim.service.helpers.EscapeUtils;
import org.almuallim.service.search.SearchCallback;
import org.almuallim.service.search.SearchProvider;
import org.almuallim.service.search.SearchResult;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Naveed Quadri
 */
public class BasicSearchProvider implements SearchProvider {

    @Override
    public void search(String args, SearchCallback callback,int pageNumber) throws Exception {
        QueryParser q = new QueryParser(Version.LUCENE_36, "text", new StandardAnalyzer(Version.LUCENE_36));
        int hitsPerPage = 10;
        IndexReader reader = IndexReader.open(FSDirectory.open(new File(Application.getHome() + File.separator + "index")));
        IndexSearcher searcher = new IndexSearcher(reader);
        
        TopDocs topdocs = searcher.search(q.parse(args),hitsPerPage * pageNumber);
        ScoreDoc[] hits = topdocs.scoreDocs;
        int from = (pageNumber - 1) * hitsPerPage;
        int to = Math.min(hitsPerPage * pageNumber, topdocs.totalHits);
        for (int i = from; i < to; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            SearchResult sr = new SearchResult();
            for (Fieldable field : d.getFields()) {
                String name = field.name();
                switch (name) {
                    case "abstract":
                        sr.setAbstractTxt(EscapeUtils.escapeJava(field.stringValue()));
                        break;
                    case "title":
                        sr.setTitle(field.stringValue());
                        break;
                    case "url":
                        sr.setUri(field.stringValue());
                        break;
                    case "moduleName":
                        sr.setModuleName(field.stringValue());
                        break;
                    default:
                        if (field.isStored() && !field.isBinary()) {
                            sr.addField(name, field.stringValue());
                        }
                }
            }
            sr.addField("Score", hits[i].score);
//            sr.setAbstractTxt(d.get("abstract"));
////                sr.setT(args);
//            sr.setTitle(d.get("title"));
//            sr.setUri(d.get("url"));
//            sr.setModuleName(d.get("moduleName"));
            callback.resultFound(args, hits[i].score, sr);
//                System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
        }



    }
}
