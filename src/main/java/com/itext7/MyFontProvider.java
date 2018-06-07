package com.itext7;

import com.itextpdf.text.Font;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;

public class MyFontProvider extends XMLWorkerFontProvider {

	public MyFontProvider() {
		super(null, null);
	}

	@Override
	public Font getFont(final String fontname, String encoding, float size, final int style) {

		String fntname = fontname;
		if (fntname == null) {
			fntname = "ËÎÌו";
		}
		return super.getFont(fntname, encoding, size, style);
	}
	
	
}
