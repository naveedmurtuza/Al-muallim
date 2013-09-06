package org.almuallim.lucene.search;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.almuallim.service.search.SearchDocument;
import org.almuallim.service.search.SearchIndexer;
import org.apache.lucene.IndexAccess;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Naveed Quadri
 */
@ServiceProvider(service = SearchIndexer.class)
public class LuceneSearchIndexer implements SearchIndexer {

    @Override
    public void index(Collection<SearchDocument> sdocs) throws IOException {
        IndexWriter indexWriter = IndexAccess.getWriter();
        for (SearchDocument searchDocument : sdocs) {
            addDoc(indexWriter, searchDocument);
        }
    }

    @Override
    public void deleteDocument(String key, String value) throws IOException {
        Term term = new Term(key, value);
        IndexWriter writer = IndexAccess.getWriter();
        writer.deleteDocuments(term);
        writer.commit();
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
}
