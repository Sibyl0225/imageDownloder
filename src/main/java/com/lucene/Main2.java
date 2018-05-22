package com.lucene;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
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

public class Main2 {

	private static final String ID = "id";
	
	private static final String TYPE = "type";
	private static final String FILEPATH = "filePath";
	
	private static JSONArray jsonAry = new JSONArray();
	
	private static RAMDirectory pubRAMDirectory = null;
	
	private static IndexWriter ramIndexWriter = null;

	public static Analyzer getAnalyzer() {
		return new StandardAnalyzer();
	}
	
	/** 打开索引的存放目录 */
	public static RAMDirectory getRamDirectory() {
		if(pubRAMDirectory == null){	
			pubRAMDirectory = new RAMDirectory();
		}
		return pubRAMDirectory;
	}
	public static IndexWriter getRamIndexWriter() throws IOException {	
		if(ramIndexWriter == null){		
			IndexWriterConfig ramConfig = new IndexWriterConfig(getAnalyzer());
			ramIndexWriter= new IndexWriter(pubRAMDirectory, ramConfig);
		}
		return ramIndexWriter;
	}
		
	/** 打开索引的存放目录 */
	public static Directory openDirectory(String filePath) {
		try {
			System.out.println(new File(filePath.replace("D", "F")) + "-------打开索引--------------");
			return FSDirectory.open(new File(filePath.replace("D", "F")).toPath());
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

	public static void doSeacher(String keyword,String directoryPath) {

		IndexSearcher searcher = null;
		try {
			// 创建一个索引搜索器
			IndexReader ireader = DirectoryReader.open(openDirectory(directoryPath));
			searcher = new IndexSearcher(ireader);
			// 用多域查询解析器来创建一个查询器,
			Analyzer analyzer = new StandardAnalyzer();

			QueryParser parser = new QueryParser(FILEPATH, analyzer);

			///sort.setSort(field);
			//Query query = parser.parse(FILEPATH+":"+keyword+" "+TYPE+":FILE");
			Query query = parser.parse(keyword);
			
			
//			MultiFieldQueryParser multQuery = new MultiFieldQueryParser(new String[]{FILEPATH}, analyzer);
//			Query query = multQuery.parse(keyword);
			// 查询结集信息类
//			SortField pathSortField = new SortField(FILEPATH, Type.SCORE);			
//			Sort sort = new Sort();
//			sort.setSort(pathSortField); 
//			TopDocs ts = searcher.search(query, 100000,sort);
			TopDocs ts = searcher.search(query, 100000);
			// 获取匹配到的结果集
			ScoreDoc[] hits = ts.scoreDocs;
            System.err.println("共搜索到"+hits.length+"结果！");
			for (int i = 0; i < hits.length; i++) { // 循环获取分页数据
				// 通过内部编号从搜索器中得到对应的文档
				Document doc = searcher.doc(hits[i].doc);
				System.err.println("ID: " + doc.get(ID) + " TITLE: " + doc.get(TYPE) + " CONTENTS: " + doc.get(FILEPATH));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public static void doIndexSingle(Map<String, String> item,String topFilePath) throws IOException {
		// 创建索引写入器



		RAMDirectory ramDir = getRamDirectory();
		IndexWriter ramIWriter = getRamIndexWriter();
		try {

			Document doc = createForumuploadDocument(item);
			ramIWriter.addDocument(doc);
			
			if(ramIWriter.numRamDocs() >=250){
				
			    IndexWriterConfig config = new IndexWriterConfig(getAnalyzer());
			    IndexWriter indexWriter = new IndexWriter(openDirectory(topFilePath), config);
			    final Directory[] dirs =  new Directory[] { getRamDirectory() };
			    ramIWriter.flush();
			    ramIWriter.close();
			    ramIndexWriter = null;
			    pubRAMDirectory = null;				
			    indexWriter.addIndexes(dirs);
			    indexWriter.flush();
			    indexWriter.close();
			    System.err.println("提交250条！");
			}
			
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    
	public static void listFile(File fileIn,JSONArray jsonAry ) throws IOException{
        File[] files = fileIn.listFiles();
        if(!(files !=null && files.length > 0)){
        	System.err.println("文件集合是空的！");
        	return;
        }
        for (File file : files) {
	       	 if(file.isDirectory()){	
	       		 String canonicalPath = null;
	       		 System.out.println("++++++++++++++++++++++文件夹分割线+++++++++++++++++++");  
	       		 try {  
	       			 canonicalPath = file.getCanonicalPath().replaceAll("\\\\", "/");
	       			 System.out.println("文件夹："+canonicalPath);  //打印输入文件(夹)的名称	       		        
				 } catch (IOException e) { e.printStackTrace(); } 
	       		 if(jsonAry.contains(canonicalPath)) continue;
	       		 jsonAry.add(canonicalPath);
	       		 
	       		 listFile(file,jsonAry);
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
			try {
				canonicalPath =   URLDecoder.decode(canonicalPath, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	String type = null;
	       	 if(file.isDirectory()){	       
	       		listFileAndAddDocument(file,topPath);
	       		 type = "Directory";
	       	 }else{
	       		type = "File";
	       	 }
	       	fileInfo.put(ID, System.currentTimeMillis()+"");
	       	fileInfo.put(TYPE, type);
	       	fileInfo.put(FILEPATH, canonicalPath.replaceAll("\\\\", "/"));
	       	doIndexSingle(fileInfo,topPath);
		}
	}

	public static void main(String[] args) throws IOException {
		
		 File fileIn = FileReaderTools.getFile("D:\\apache-maven-3.5.2");
		 long start = System.currentTimeMillis();  
		    try {
				listFile(fileIn,jsonAry);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
      			String canonicalPath = fileIn.getCanonicalPath().replaceAll("\\\\", "/");
      			jsonAry.add(canonicalPath);
				System.err.println("jsonAry.size: "+ jsonAry.size() +"   \nTime is:"+(System.currentTimeMillis()-start) + "ms");  
			}
		    
		    
		    for (int i = 0; i < jsonAry.size(); i++) {
		    	long forStart = System.currentTimeMillis();  
		    	String topPath = jsonAry.getString(i);
		    	File jsonAryFile = FileReaderTools.getFile(topPath);
		    	try {
					listFileAndAddDocument(jsonAryFile,topPath);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					IndexWriter ramIWriter = getRamIndexWriter();
		            System.err.println("提交"+ramIWriter.numRamDocs()+"条！");
				    IndexWriterConfig config = new IndexWriterConfig(getAnalyzer());
				    IndexWriter indexWriter = new IndexWriter(openDirectory(topPath), config);
				    final Directory[] dirs =  new Directory[] { getRamDirectory() };
				    ramIWriter.flush();
				    ramIWriter.close();
				    ramIndexWriter = null;
				    pubRAMDirectory = null;
					indexWriter.addIndexes(dirs);
					indexWriter.flush();
				    indexWriter.close();
					System.err.println("Time is:"+(System.currentTimeMillis()-forStart) + "ms");  
				}
			}
		    


//        long start = System.currentTimeMillis();  
//        String topPath = "D:\\apache-maven-3.5.2";  
//        File jsonAryFile = FileReaderTools.getFile(topPath);
//		try {
//			listFileAndAddDocument(jsonAryFile,topPath);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.err.println("Time is:"+(System.currentTimeMillis()-start) + "ms");  


		    
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
		
//		
      String topPath = "D:/apache-maven-3.5.2";  
	  doSeacher("linux64",topPath);

		System.exit(0);
	}
}
