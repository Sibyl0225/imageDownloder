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
	
	/** �������Ĵ��Ŀ¼ */
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
		
	/** �������Ĵ��Ŀ¼ */
	public static Directory openDirectory(String filePath) {
		try {
			System.out.println(new File(filePath.replace("D", "F")) + "-------������--------------");
			return FSDirectory.open(new File(filePath.replace("D", "F")).toPath());
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

	public static void doSeacher(String keyword,String directoryPath) {

		IndexSearcher searcher = null;
		try {
			// ����һ������������
			IndexReader ireader = DirectoryReader.open(openDirectory(directoryPath));
			searcher = new IndexSearcher(ireader);
			// �ö����ѯ������������һ����ѯ��,
			Analyzer analyzer = new StandardAnalyzer();

			QueryParser parser = new QueryParser(FILEPATH, analyzer);

			///sort.setSort(field);
			//Query query = parser.parse(FILEPATH+":"+keyword+" "+TYPE+":FILE");
			Query query = parser.parse(keyword);
			
			
//			MultiFieldQueryParser multQuery = new MultiFieldQueryParser(new String[]{FILEPATH}, analyzer);
//			Query query = multQuery.parse(keyword);
			// ��ѯ�Ἧ��Ϣ��
//			SortField pathSortField = new SortField(FILEPATH, Type.SCORE);			
//			Sort sort = new Sort();
//			sort.setSort(pathSortField); 
//			TopDocs ts = searcher.search(query, 100000,sort);
			TopDocs ts = searcher.search(query, 100000);
			// ��ȡƥ�䵽�Ľ����
			ScoreDoc[] hits = ts.scoreDocs;
            System.err.println("��������"+hits.length+"�����");
			for (int i = 0; i < hits.length; i++) { // ѭ����ȡ��ҳ����
				// ͨ���ڲ���Ŵ��������еõ���Ӧ���ĵ�
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
		// ��������д����



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
			    System.err.println("�ύ250����");
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
        	System.err.println("�ļ������ǿյģ�");
        	return;
        }
        for (File file : files) {
	       	 if(file.isDirectory()){	
	       		 String canonicalPath = null;
	       		 System.out.println("++++++++++++++++++++++�ļ��зָ���+++++++++++++++++++");  
	       		 try {  
	       			 canonicalPath = file.getCanonicalPath().replaceAll("\\\\", "/");
	       			 System.out.println("�ļ��У�"+canonicalPath);  //��ӡ�����ļ�(��)������	       		        
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
        	System.err.println("�ļ������ǿյģ�");
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
		            System.err.println("�ύ"+ramIWriter.numRamDocs()+"����");
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
//				put(TITLE, "200��� ϰ��ƽ�����������˼�Ĵ���һ��");
//				put(CONTENTS, "'���˼��ȫ�����޲��׼����Ͷ�����ĸ�����ʦ�������˼�������Ҫ��ʼ�ˣ������˼���������ĵ����ߺ͹��ʹ�������Ŀ����ߣ��ǽ���������ΰ���˼��ҡ�'5��4�գ��ڼ������˼����200�������ϣ�ϰ��ƽ������廳���߶����������˼��һ����\n5��5�գ������˼����200��������ա������Ǹ���ϰ��ƽ����ǵĽ������߽�ŷ�ޣ�׷Ѱ���˼���㼣��̽Ѱ��λ'ǧ���һ˼���'��һ����");
//			}
//
//		};
//
//		doIndexSingle(map);
//		System.err.println("�Ѵ���");
//		//deleteIndex("0056");
		
//		
      String topPath = "D:/apache-maven-3.5.2";  
	  doSeacher("linux64",topPath);

		System.exit(0);
	}
}
