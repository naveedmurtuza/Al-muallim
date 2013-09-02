package org.almuallim.lucene.search;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.almuallim.service.helpers.Application;
import org.almuallim.service.search.SearchDocument;
import org.almuallim.service.search.SearchIndexer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Naveed Quadri
 */
@ServiceProvider(service = SearchIndexer.class)
public class LuceneSearchIndexer implements SearchIndexer {


    @Override
    public void index(Collection<SearchDocument> sdocs) throws IOException {
        try (IndexWriter indexWriter = getWriter()) {
            for (SearchDocument searchDocument : sdocs) {
                addDoc(indexWriter, searchDocument);
            }
        }
    }

    private void addDoc(IndexWriter indexWriter, SearchDocument sdoc) throws IOException {
        Document doc = new Document();
        doc.add(new Field("text", sdoc.getText(), Field.Store.NO, Field.Index.ANALYZED));
        doc.add(new Field("abstract", sdoc.getAbstractText(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("moduleName", sdoc.getModuleName(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("title", sdoc.getTitle(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("url", sdoc.getUrl(), Field.Store.YES, Field.Index.NO));
        for (Map.Entry<String, Object> entry : sdoc.getParameters().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Integer) {
                Integer intValue = (Integer) value;
                doc.add(new NumericField(key, Field.Store.YES, false).setIntValue(intValue));
            } else {
                doc.add(new Field(key, value.toString(), Field.Store.YES, Field.Index.NO));

            }
        }
        indexWriter.addDocument(doc);
    }

    private IndexWriter getWriter() throws IOException {
        return new IndexWriter(FSDirectory.open(new File(Application.getHome() + File.separator + "index")), new IndexWriterConfig(Version.LUCENE_36, new StandardAnalyzer(Version.LUCENE_36)));
    }
}
