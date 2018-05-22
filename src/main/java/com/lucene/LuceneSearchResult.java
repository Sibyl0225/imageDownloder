package com.lucene;  
import java.util.List;  
public class LuceneSearchResult<T> {  
    private int pageNo = 1;    //当前页    
    private int pageSize = 5;  //每页显示记录数    
    private int recordCount;   //总记录数    
    private double time;       //耗时    
    private List<T> datas;     //当前页的数据    
    private int stratNo;       //开始记录数    
    private int endNo;         //结束记录数    
    private String keyword;    //关键字    
    public int getPageNo() {    
    return pageNo;    
    }    
    public void setPageNo(int pageNo) {    
    this.pageNo = pageNo;    
    }    
    public int getPageSize() {    
    return pageSize;    
    }    
    public void setPageSize(int pageSize) {    
    this.pageSize = pageSize;    
    }    
    public int getRecordCount() {    
    return recordCount;    
    }    
    public void setRecordCount(int recordCount) {    
    this.recordCount = recordCount;    
    }    
    public List<T> getDatas() {    
    return datas;    
    }    
    public void setDatas(List<T> datas) {    
    this.datas = datas;    
    }    
    public double getTime() {    
    return time;    
    }    
    public void setTime(double time) {    
    this.time = time;    
    }    
    public String getKeyword() {    
    return keyword;    
    }    
    public void setKeyword(String keyword) {    
    this.keyword = keyword;    
    }    
    public int getStratNo() {    
    return stratNo;    
    }    
    public void setStratNo(int stratNo) {    
    this.stratNo = stratNo;    
    }    
    public int getEndNo() {    
    return endNo;    
    }    
    public void setEndNo(int endNo) {    
    this.endNo = endNo;    
    }    
}
