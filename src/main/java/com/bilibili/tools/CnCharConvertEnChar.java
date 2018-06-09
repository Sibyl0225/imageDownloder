package com.bilibili.tools;

public class CnCharConvertEnChar {
	
	public static String ChineseToEnglish(String txt)   
    {   
		String txtAfter = txt;
        String[] ChineseInterpunction = { "/","\\\\","��", "��", "��", "��", "��", "��", "��", "��", "��", "��", "����", "��", "��", "��", "��", "��", "��" };   
        String[] EnglishInterpunction = { " "," ","\"", "\"", "'", "'", ".", ",", ";", ":", "?", "!", "��", "-", "~", "(", ")", "<", ">" };   
        for (int j = 0; j < ChineseInterpunction.length; j++)   
        {   
        	txtAfter = txtAfter.replaceAll(ChineseInterpunction[j], EnglishInterpunction[j]);  
        }  
        if(txt.equals(txtAfter)) {
        	return txt;
        }else {
        	System.out.println("ת��ǰΪ��"+txt);
        	System.out.println("ת����Ϊ��"+txtAfter);         	
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
        	System.out.println("ת��ǰΪ��"+txt);
        	System.out.println("ת����Ϊ��"+txtAfter);         	
        }
        return txtAfter; 
    }

}
