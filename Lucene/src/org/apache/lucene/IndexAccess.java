package org.apache.lucene;

import java.io.File;
import java.io.IOException;
import org.almuallim.service.helpers.Application;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

/**
 *
 * @author Naveed Quadri
 */
public class IndexAccess {

    private static IndexWriter writer;
    private static IndexReader reader;

    public static IndexReader getReader() {
        if (reader == null) {
        }
        return reader;
    }

    public static IndexWriter getWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
        if (writer == null) {
            writer = new IndexWriter(FSDirectory.open(new File(Application.getHome() + File.separator + "index")), new IndexWriterConfig(Version.LUCENE_36, new StandardAnalyzer(Version.LUCENE_36)));
        }
        return writer;
    }

    public static void close() throws CorruptIndexException, IOException {
        if (writer != null) {
            writer.close();
        }
    }
}
