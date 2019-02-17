
package com.jbo.demo.configuration;

import org.apache.lucene.index.*;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @Author jiangbo
 * @Date 2019/2/16 14:14
 * @Version 1.0
 * @Description
 */
class EmptyReader extends LeafReader {
    
    public EmptyReader() {
        super();
        tryIncRef();
    }

    @Override
    public void addCoreClosedListener(final CoreClosedListener listener) {
    }

    @Override
    public void removeCoreClosedListener(final CoreClosedListener listener) {
    }

    @Override
    public Fields fields() throws IOException {
        return new Fields() {
            @Override
            public Iterator<String> iterator() {
                return Collections.<String> emptyList().iterator();
            }

            @Override
            public Terms terms(final String field) throws IOException {
                return new Terms() {
                    
                    @Override
                    public long size() throws IOException {
                        // TODO Auto-generated method stub
                        return 0;
                    }
                    
                    @Override
                    public TermsEnum iterator() throws IOException {
                        // TODO Auto-generated method stub
                        return new TermsEnum() {
                            
                            @Override
                            public BytesRef next() throws IOException {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public long totalTermFreq() throws IOException {
                                // TODO Auto-generated method stub
                                return 0;
                            }
                            
                            @Override
                            public BytesRef term() throws IOException {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public void seekExact(long ord) throws IOException {
                                // TODO Auto-generated method stub
                                
                            }
                            
                            @Override
                            public SeekStatus seekCeil(BytesRef text) throws IOException {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public PostingsEnum postings(PostingsEnum reuse, int flags) throws IOException {
                                // TODO Auto-generated method stub
                                return null;
                            }
                            
                            @Override
                            public long ord() throws IOException {
                                // TODO Auto-generated method stub
                                return 0;
                            }
                            
                            @Override
                            public int docFreq() throws IOException {
                                // TODO Auto-generated method stub
                                return 0;
                            }
                        };
                    }
                    
                    @Override
                    public boolean hasPositions() {
                        // TODO Auto-generated method stub
                        return false;
                    }
                    
                    @Override
                    public boolean hasPayloads() {
                        // TODO Auto-generated method stub
                        return false;
                    }
                    
                    @Override
                    public boolean hasOffsets() {
                        // TODO Auto-generated method stub
                        return false;
                    }
                    
                    @Override
                    public boolean hasFreqs() {
                        // TODO Auto-generated method stub
                        return false;
                    }
                    
                    @Override
                    public long getSumTotalTermFreq() throws IOException {
                        // TODO Auto-generated method stub
                        return 0;
                    }
                    
                    @Override
                    public long getSumDocFreq() throws IOException {
                        // TODO Auto-generated method stub
                        return 0;
                    }
                    
                    @Override
                    public int getDocCount() throws IOException {
                        // TODO Auto-generated method stub
                        return 0;
                    }
                };
            }

            @Override
            public int size() {
                return 0;
            }
        };
    }

    @Override
    public NumericDocValues getNumericDocValues(final String field) throws IOException {
        return null;
    }

    @Override
    public BinaryDocValues getBinaryDocValues(final String field) throws IOException {
        return null;
    }

    @Override
    public SortedDocValues getSortedDocValues(final String field) throws IOException {
        return null;
    }

    @Override
    public SortedNumericDocValues getSortedNumericDocValues(final String field) throws IOException {
        return null;
    }

    @Override
    public SortedSetDocValues getSortedSetDocValues(final String field) throws IOException {
        return null;
    }

    @Override
    public Bits getDocsWithField(final String field) throws IOException {
        return null;
    }

    @Override
    public NumericDocValues getNormValues(final String field) throws IOException {
        return null;
    }

    @Override
    public FieldInfos getFieldInfos() {
        return new FieldInfos(new FieldInfo[0]);
    }

    final Bits liveDocs = new Bits.MatchNoBits(0);

    @Override
    public Bits getLiveDocs() {
        return liveDocs;
    }

    @Override
    public void checkIntegrity() throws IOException {
    }

    @Override
    public Fields getTermVectors(final int docID) throws IOException {
        return null;
    }

    @Override
    public int numDocs() {
        return 0;
    }

    @Override
    public int maxDoc() {
        return 0;
    }

    @Override
    public void document(final int docID, final StoredFieldVisitor visitor) throws IOException {
    }

    @Override
    protected void doClose() throws IOException {
    }

    @Override
    public boolean hasDeletions() {
        return false;
    }

    @Override
    public Object getCoreCacheKey() {
        return new Object();
    }

    @Override
    public Object getCombinedCoreAndDeletesKey() {
        return new Object();
    }
}
