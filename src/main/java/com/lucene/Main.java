package com.lucene;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bilibili.tools.FileReaderTools;
import com.bilibili.tools.FileWriterTools;

public class Main {

	private static final String ID = "id";
	private static final String TITLE = "title";
	private static final String CONTENTS = "contents";
	
	private static final String TYPE = "type";
	private static final String FILEPATH = "filePath";
	
	private static final String INDEX_DIR = "D:\\lcuene";
	
	private static final String filePath ="C:\\Program Files (x86)";
	
	private static JSONArray jsonAry = new JSONArray();

	public static Analyzer getAnalyzer() {
		return new StandardAnalyzer();
	}

	/** �������Ĵ��Ŀ¼ */
	public static Directory openDirectory() {
		try {
			System.out.println(new File(INDEX_DIR) + "-------������--------------");
			return FSDirectory.open(new File(INDEX_DIR).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/** �������Ĵ��Ŀ¼ */
	public static Directory openDirectory(String filePath) {
		try {
			System.out.println(new File(filePath.replace("C", "D")) + "-------������--------------");
			return FSDirectory.open(new File(filePath.replace("C", "D")).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** ���ļ���ָ������ӳ�����,�����ļ��ĵ����� */
	public static Document createForumuploadDocument(Map<String, String> item) {
		Document doc = new Document(); // ����һ���ĵ�����
		// id ��
		//doc.Add(new Field("Id", "-1000", Field.Store.YES, Field.Index.NOT_ANALYZED));//�洢���ִ������� 
		
//		DOCS_AND_FREQS
//		ֻ���ĵ�������Ƶ�ʱ�����:λ�ñ�ʡ�ԡ�
//		DOCS_AND_FREQS_AND_POSITIONS
//		�����ĵ���Ƶ�ʺ�λ�á�
//		DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
//		�����ĵ���Ƶ�ʡ�λ�ú�ƫ������
//		DOCS_ONLY
//		ֻ���ĵ�������:����Ƶ�ʺ�λ�ñ�ʡ�ԡ�
		
		FieldType fieldType = new FieldType();  
        fieldType.setIndexOptions(IndexOptions.DOCS);//set �Ƿ�����  
        fieldType.setStored(true);//set �Ƿ�洢  
//        fieldType.setTokenized(true);//set �Ƿ����  
		Field field = new Field(ID, String.valueOf(item.get(ID)), fieldType);
		doc.add(field);
		// title��
		Field field1 = new Field(TYPE, String.valueOf(item.get(TYPE)), TextField.TYPE_STORED);
		doc.add(field1);
		// content��
		Field field2 = new Field(FILEPATH, String.valueOf(item.get(FILEPATH)), TextField.TYPE_STORED);
		doc.add(field2);
		return doc;
	}

	public static void doSeacher(String keyword) {

		IndexSearcher searcher = null;
		try {
			// ����һ������������
			IndexReader ireader = DirectoryReader.open(openDirectory("D:\\yande"));
			searcher = new IndexSearcher(ireader);
			// �ö����ѯ������������һ����ѯ��,
			Analyzer analyzer = new StandardAnalyzer();

//			QueryParser parser = new QueryParser(CONTENTS, analyzer);
//			Query query = parser.parse(keyword);
			
			MultiFieldQueryParser multQuery = new MultiFieldQueryParser(new String[]{FILEPATH}, analyzer);
			Query query = multQuery.parse(keyword);
			// ��ѯ�Ἧ��Ϣ��
			TopDocs ts = searcher.search(query, 100000);
			// ��ȡƥ�䵽�Ľ����
			ScoreDoc[] hits = ts.scoreDocs;

			for (int i = 0; i < hits.length; i++) { // ѭ����ȡ��ҳ����
				// ͨ���ڲ���Ŵ��������еõ���Ӧ���ĵ�
				Document doc = searcher.doc(hits[i].doc);
				System.err
						.println("ID: " + doc.get(ID) + " TITLE: " + doc.get(TYPE) + " CONTENTS: " + doc.get(FILEPATH));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void doIndexSingle(Map<String, String> item,String topFilePath) {
		// ��������д����
		IndexWriter indexWriter = null;
		try {
			IndexWriterConfig config = new IndexWriterConfig(getAnalyzer());
			indexWriter = new IndexWriter(openDirectory(topFilePath), config);
			Document doc = createForumuploadDocument(item);
			indexWriter.addDocument(doc);

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (indexWriter != null) {
					indexWriter.close(); // �ر�IndexWriter,���ڴ��е�����д���ļ�
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void doIndexSingle(Map<String, String> item) {
		// ��������д����
		IndexWriter indexWriter = null;
		try {
			IndexWriterConfig config = new IndexWriterConfig(getAnalyzer());
			indexWriter = new IndexWriter(openDirectory(), config);
			Document doc = createForumuploadDocument(item);
			indexWriter.addDocument(doc);

		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (indexWriter != null) {
					indexWriter.close(); // �ر�IndexWriter,���ڴ��е�����д���ļ�
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void deleteIndex(String id) {
		IndexWriter iw = null;
		try {
			IndexWriterConfig config = new IndexWriterConfig(getAnalyzer());
			iw = new IndexWriter(openDirectory(), config); // ��ָ��Ŀ¼�������ļ���������ȡ��
			iw.deleteDocuments(new Term(ID, id)); // ɾ������������Document
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (iw != null) {
				try {
					iw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	  
    public void updateIndex(HashMap<String, String> item) {  
        deleteIndex(item.get(ID));    
        doIndexSingle(item);    
    } 
    
	public static void listFile(File fileIn,JSONArray jsonAry ) throws IOException{
        File[] files = fileIn.listFiles();
        if(!(files !=null && files.length > 0)){
        	System.err.println("�ļ������ǿյģ�");
        	return;
        }
        for (File file : files) {
	       	 if(file.isDirectory()){	      
	       		 System.out.println("++++++++++++++++++++++�ļ��зָ���+++++++++++++++++++");  
	       		 try {  
	       			 System.out.println("�ļ��У�"+fileIn.getCanonicalPath());  //��ӡ�����ļ�(��)������	       		        
				 } catch (IOException e) { e.printStackTrace(); } 
	       		 
	       		 jsonAry.add(fileIn.getCanonicalPath());
	       		 
	       		 FileReaderTools.listFile(file,jsonAry);
	       	 }else{
	       		//System.out.println(file.getName()); 
	       		 continue;
	       	 }
			
		}
	}
	
	
	public static void listFileAndAddDocument(File fileIn,String topPath) throws IOException{
        File[] files = fileIn.listFiles();
        if(!(files !=null && files.length > 0)){
        	System.err.println("�ļ������ǿյģ�");
        	return;
        }
        
        for (File file : files) {
        	HashMap<String, String> fileInfo = new HashMap<String, String>();
        	String canonicalPath = file.getCanonicalPath();
        	String type = null;
	       	 if(file.isDirectory()){	       
	       		 FileReaderTools.listFile(file,jsonAry);
	       		type = "Directory";
	       	 }else{
	       		type = "File";
	       	 }
	       	fileInfo.put(ID, System.currentTimeMillis()+"");
	       	fileInfo.put(TYPE, type);
	       	fileInfo.put(FILEPATH, canonicalPath);
	       	doIndexSingle(fileInfo,topPath);
		}
	}

	public static void main(String[] args) {
		
//		 File fileIn = FileReaderTools.getFile(filePath);
//		 long start = System.currentTimeMillis();  
//		    try {
//				listFile(fileIn,jsonAry);
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				System.err.println("jsonAry.size: "+ jsonAry.size() +"   \nTime is:"+(System.currentTimeMillis()-start) + "ms");  
//			}
//		    
//		    for (int i = 0; i < jsonAry.size(); i++) {
//		    	long forStart = System.currentTimeMillis();  
//		    	String topPath = jsonAry.getString(i);
//		    	File jsonAryFile = FileReaderTools.getFile(topPath);
//		    	try {
//					listFileAndAddDocument(jsonAryFile,topPath);
//				} catch (IOException e) {
//					e.printStackTrace();
//				} finally {
//					System.err.println("Time is:"+(System.currentTimeMillis()-forStart) + "ms");  
//				}
//			}
//		
//        String topPath = "D:\\yande";  
//        File jsonAryFile = FileReaderTools.getFile(topPath);
//		try {
//			listFileAndAddDocument(jsonAryFile,topPath);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		    
//		HashMap<String, String> map = new HashMap<String, String>() {
//			private static final long serialVersionUID = 1L;
//			{
//				put(ID, "0056");
//				put(TITLE, "200��� ϰ��ƽ�����������˼�Ĵ���һ��");
//				put(CONTENTS, "'���˼��ȫ�����޲��׼����Ͷ�����ĸ�����ʦ�������˼�������Ҫ��ʼ�ˣ������˼���������ĵ����ߺ͹��ʹ�������Ŀ����ߣ��ǽ���������ΰ���˼��ҡ�'5��4�գ��ڼ������˼����200�������ϣ�ϰ��ƽ������廳���߶����������˼��һ����\n5��5�գ������˼����200��������ա������Ǹ���ϰ��ƽ����ǵĽ������߽�ŷ�ޣ�׷Ѱ���˼���㼣��̽Ѱ��λ'ǧ���һ˼���'��һ����");
//			}
//
//		};
//
//		doIndexSingle(map);
//		System.err.println("�Ѵ���");
//		//deleteIndex("0056");
		doSeacher("yande");

		// Analyzer analyzer = new StandardAnalyzer();
		//
		// // �������������洢Ŀ¼
		// // Store the index in memory:
		// Directory directory = new RAMDirectory();
		//
		// // ������������д����
		// IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// IndexWriter iwriter = null;
		// try {
		// iwriter = new IndexWriter(directory, config);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// try {
		// iwriter.addDocument(doc);
		// iwriter.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// // ============ �������� =============
		// // һ�������ʷ�������
		// Analyzer analyzer = new StandardAnalyzer();
		//
		// // �������������洢Ŀ¼
		// // Store the index in memory:
		// Directory directory = new RAMDirectory();
		// // To store an index on disk, use this instead:
		// // Directory directory = FSDirectory.open("/tmp/testindex");
		//
		// // ������������д����
		// IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// IndexWriter iwriter = null;
		// try {
		// iwriter = new IndexWriter(directory, config);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// // �ġ������ݴ洢������
		// Document doc = new Document();
		// String text = "This is the text to be indexed.";
		// doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
		// try {
		// iwriter.addDocument(doc);
		// iwriter.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		//
		//
		// // ============ �ؼ��ֲ�ѯ =============
		// // һ�����������洢Ŀ¼��ȡ��
		// // Now search the index:
		// DirectoryReader ireader = null;
		// try {
		// ireader = DirectoryReader.open(directory);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// //System.err.println();
		//
		// // ������������������
		// IndexSearcher isearcher = new IndexSearcher(ireader);
		//
		// // ����������ѯ
		// // Parse a simple query that searches for "text":
		// QueryParser parser = new QueryParser("fieldname", analyzer);
		// Query query = null;
		// try {
		// query = parser.parse("text");
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// // �ġ���ȡ���
		// ScoreDoc[] hits = null;
		// try {
		// hits = isearcher.search(query, 1000).scoreDocs;
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// //search(query, null, 1000).scoreDocs;
		// assertEquals(1, hits.length);
		// // Iterate through the results:
		// for (int i = 0; i < hits.length; i++) {
		// Document hitDoc = null;
		// try {
		// hitDoc = isearcher.doc(hits[i].doc);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// assertEquals("This is the text to be indexed.",
		// hitDoc.get("fieldname"));
		// }
		// try {
		// ireader.close();
		// directory.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		System.exit(0);
	}
}
