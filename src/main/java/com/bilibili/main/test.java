package com.bilibili.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

	public static void main(String[] args) {
		String imgUrl = "https://files.yande.re/image/de2e503250b1418b16919de80836b1ec/yande.re%20442592%20megane%20pantyhose%20tagme%20weapon%20zero_no_tsukaima.jpg";
		 Matcher matcher = Pattern.compile("yande.re%20\\d+").matcher(imgUrl);
		 while (matcher.find()){
			 System.out.println(matcher.group());
		 }
	}

}
