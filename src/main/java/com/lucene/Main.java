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

	/** 打开索引的存放目录 */
	public static Directory openDirectory() {
		try {
			System.out.println(new File(INDEX_DIR) + "-------打开索引--------------");
			return FSDirectory.open(new File(INDEX_DIR).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/** 打开索引的存放目录 */
	public static Directory openDirectory(String filePath) {
		try {
			System.out.println(new File(filePath.replace("C", "D")) + "-------打开索引--------------");
			return FSDirectory.open(new File(filePath.replace("C", "D")).toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 对文件的指定属性映射成域,返回文件文档对象 */
	public static Document createForumuploadDocument(Map<String, String> item) {
		Document doc = new Document(); // 创建一个文档对象
		// id 域
		//doc.Add(new Field("Id", "-1000", Field.Store.YES, Field.Index.NOT_ANALYZED));//存储不分词且索引 
		
//		DOCS_AND_FREQS
//		只有文档和术语频率被索引:位置被省略。
//		DOCS_AND_FREQS_AND_POSITIONS
//		索引文档、频率和位置。
//		DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
//		索引文档、频率、位置和偏移量。
//		DOCS_ONLY
//		只有文档被索引:术语频率和位置被省略。
		
		FieldType fieldType = new FieldType();  
        fieldType.setIndexOptions(IndexOptions.DOCS);//set 是否索引  
        fieldType.setStored(true);//set 是否存储  
//        fieldType.setTokenized(true);//set 是否分类  
		Field field = new Field(ID, String.valueOf(item.get(ID)), fieldType);
		doc.add(field);
		// title域
		Field field1 = new Field(TYPE, String.valueOf(item.get(TYPE)), TextField.TYPE_STORED);
		doc.add(field1);
		// content域
		Field field2 = new Field(FILEPATH, String.valueOf(item.get(FILEPATH)), TextField.TYPE_STORED);
		doc.add(field2);
		return doc;
	}

	public static void doSeacher(String keyword) {

		IndexSearcher searcher = null;
		try {
			// 创建一个索引搜索器
			IndexReader ireader = DirectoryReader.open(openDirectory("D:\\yande"));
			searcher = new IndexSearcher(ireader);
			// 用多域查询解析器来创建一个查询器,
			Analyzer analyzer = new StandardAnalyzer();

//			QueryParser parser = new QueryParser(CONTENTS, analyzer);
//			Query query = parser.parse(keyword);
			
			MultiFieldQueryParser multQuery = new MultiFieldQueryParser(new String[]{FILEPATH}, analyzer);
			Query query = multQuery.parse(keyword);
			// 查询结集信息类
			TopDocs ts = searcher.search(query, 100000);
			// 获取匹配到的结果集
			ScoreDoc[] hits = ts.scoreDocs;

			for (int i = 0; i < hits.length; i++) { // 循环获取分页数据
				// 通过内部编号从搜索器中得到对应的文档
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
		// 创建索引写入器
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
					indexWriter.close(); // 关闭IndexWriter,把内存中的数据写到文件
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void doIndexSingle(Map<String, String> item) {
		// 创建索引写入器
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
					indexWriter.close(); // 关闭IndexWriter,把内存中的数据写到文件
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
			iw = new IndexWriter(openDirectory(), config); // 打开指定目录下索引文件的索引读取器
			iw.deleteDocuments(new Term(ID, id)); // 删除符合条件的Document
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
        	System.err.println("文件集合是空的！");
        	return;
        }
        for (File file : files) {
	       	 if(file.isDirectory()){	      
	       		 System.out.println("++++++++++++++++++++++文件夹分割线+++++++++++++++++++");  
	       		 try {  
	       			 System.out.println("文件夹："+fileIn.getCanonicalPath());  //打印输入文件(夹)的名称	       		        
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
        	System.err.println("文件集合是空的！");
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
//				put(TITLE, "200年后 习近平这样讲述马克思的传奇一生");
//				put(CONTENTS, "'马克思是全世界无产阶级和劳动人民的革命导师，是马克思主义的主要创始人，是马克思主义政党的缔造者和国际共产主义的开创者，是近代以来最伟大的思想家。'5月4日，在纪念马克思诞辰200周年大会上，习近平总书记缅怀并高度评价了马克思的一生。\n5月5日，是马克思诞辰200周年纪念日。让我们跟随习近平总书记的讲述，走进欧洲，追寻马克思的足迹，探寻这位'千年第一思想家'的一生。");
//			}
//
//		};
//
//		doIndexSingle(map);
//		System.err.println("已存入");
//		//deleteIndex("0056");
		doSeacher("yande");

		// Analyzer analyzer = new StandardAnalyzer();
		//
		// // 二、创建索引存储目录
		// // Store the index in memory:
		// Directory directory = new RAMDirectory();
		//
		// // 三、创建索引写入器
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

		// // ============ 创建索引 =============
		// // 一、创建词法分析器
		// Analyzer analyzer = new StandardAnalyzer();
		//
		// // 二、创建索引存储目录
		// // Store the index in memory:
		// Directory directory = new RAMDirectory();
		// // To store an index on disk, use this instead:
		// // Directory directory = FSDirectory.open("/tmp/testindex");
		//
		// // 三、创建索引写入器
		// IndexWriterConfig config = new IndexWriterConfig(analyzer);
		// IndexWriter iwriter = null;
		// try {
		// iwriter = new IndexWriter(directory, config);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// // 四、将内容存储到索引
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
		// // ============ 关键字查询 =============
		// // 一、创建索引存储目录读取器
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
		// // 二、创建索引搜索器
		// IndexSearcher isearcher = new IndexSearcher(ireader);
		//
		// // 三、解析查询
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
		// // 四、获取结果
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
