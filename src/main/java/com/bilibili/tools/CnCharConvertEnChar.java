package com.bilibili.tools;

public class CnCharConvertEnChar {
	
	public static String ChineseToEnglish(String txt)   
    {   
		String txtAfter = txt;
        String[] ChineseInterpunction = { "/","\\\\","“", "”", "‘", "’", "。", "，", "；", "：", "？", "！", "……", "—", "～", "（", "）", "《", "》" };   
        String[] EnglishInterpunction = { " "," ","\"", "\"", "'", "'", ".", ",", ";", ":", "?", "!", "…", "-", "~", "(", ")", "<", ">" };   
        for (int j = 0; j < ChineseInterpunction.length; j++)   
        {   
        	txtAfter = txtAfter.replaceAll(ChineseInterpunction[j], EnglishInterpunction[j]);  
        }  
        if(txt.equals(txtAfter)) {
        	return txt;
        }else {
        	System.out.println("转换前为："+txt);
        	System.out.println("转换后为："+txtAfter);         	
        }
        return txtAfter; 
    } 
	
	public static String filePathCustomFilter(String txt)   
    {   
		String txtAfter = txt;
        String[] ChineseInterpunction = { "/"};   
        String[] EnglishInterpunction = { " "};   
        for (int j = 0; j < ChineseInterpunction.length; j++)   
        {   
        	txtAfter = txtAfter.replaceAll(ChineseInterpunction[j], EnglishInterpunction[j]);  
        }  
        if(txt.equals(txtAfter)) {
        	return txt;
        }else {
        	System.out.println("转换前为："+txt);
        	System.out.println("转换后为："+txtAfter);         	
        }
        return txtAfter; 
    }

}
