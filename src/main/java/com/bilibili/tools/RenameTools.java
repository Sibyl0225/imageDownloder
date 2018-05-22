package com.bilibili.tools;

import java.io.File;
import java.io.IOException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class RenameTools {

	public static void renameFilesWithJSONFilePath(String filePath) throws IOException {

		JSONArray readJsonArray = FileWriterTools.readFileToJSONArray(filePath + "\\fileInfo.json");
		if (readJsonArray.isEmpty()) {
			System.out.println("读取失败或者是空JSON!");
			return;
		}

		for (int i = 0; i < readJsonArray.size(); i++) {
			JSONObject obj = (JSONObject) readJsonArray.get(i);
			String oldName = obj.getString("oldName");
			String newName = obj.getString("newName");
			System.out.println("找到文件，将进行重命名……");
			renameFile(oldName, newName);
		}

	}

	public static void renameFile(String oldName, String newName) {
		if (!oldName.equals(newName)) {
			File oldFile = new File(oldName);
			File newFile = new File(newName);
			if (!oldFile.exists()) {
				System.out.println("重名名目标文件(old)" + oldName + "不存在！");
				return;
			}
			if (newFile.exists()) {
				System.out.println("重名名目标文件(new)" + newName + "已存在！");
			} else {

				if (oldFile.renameTo(newFile)) {
					System.out.println("文件(old)   " + oldName + " \n已重命名为\n文件(new)   " + newName + " !");
				} else {
					System.out.println("重命名失败………………");
					System.out.println("oldName:" + oldName);
					System.out.println("newName:" + newName);
				}
			}

		} else { // 新旧名相同
			System.out.println("新旧名相同……");
		}
	}

	public static String getLevelsPreFile(String filePathIn, int levels) {
		if (levels <= 0)
			return filePathIn;
		String directory = null;
		System.out.println(filePathIn);
		if (filePathIn.contains("\\")) {
			directory = filePathIn;
			while (levels > 0) {
				directory = directory.substring(0, directory.lastIndexOf("\\"));
				levels--;
				System.out.println(directory);
			}
			return directory;
		} else {
			System.out.println(filePathIn + "是一个文件，请输入文件夹！");
		}
		return filePathIn;
	}

}
