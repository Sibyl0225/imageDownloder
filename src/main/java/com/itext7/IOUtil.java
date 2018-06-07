package com.itext7;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class IOUtil {

	public static void copyCompletely(InputStream input, OutputStream output) throws IOException {
	    if(output instanceof FileOutputStream && input instanceof FileInputStream) {
	        try {
	            FileChannel buf1 = ((FileOutputStream)output).getChannel();
	            FileChannel ignore1 = ((FileInputStream)input).getChannel();
	            ignore1.transferTo(0L, 2147483647L, buf1);
	            ignore1.close();
	            buf1.close();
	            return;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    byte[] buf = new byte[8192];

	    while(true) {
	        int ignore = input.read(buf);
	        if(ignore < 0) {
	            try {
	                input.close();
	            } catch (IOException var5) {
	                ;
	            }

	            try {
	                output.close();
	            } catch (IOException var4) {
	                ;
	            }

	            return;
	        }

	        output.write(buf, 0, ignore);
	    }
	} 
}
