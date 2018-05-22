package com.bilibili.tools;

import java.io.File;
import java.io.IOException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class RenameTools {

	public static void renameFilesWithJSONFilePath(String filePath) throws IOException {

		JSONArray readJsonArray = FileWriterTools.readFileToJSONArray(filePath + "\\fileInfo.json");
		if (readJsonArray.isEmpty()) {
			System.out.println("��ȡʧ�ܻ����ǿ�JSON!");
			return;
		}

		for (int i = 0; i < readJsonArray.size(); i++) {
			JSONObject obj = (JSONObject) readJsonArray.get(i);
			String oldName = obj.getString("oldName");
			String newName = obj.getString("newName");
			System.out.println("�ҵ��ļ�������������������");
			renameFile(oldName, newName);
		}

	}

	public static void renameFile(String oldName, String newName) {
		if (!oldName.equals(newName)) {
			File oldFile = new File(oldName);
			File newFile = new File(newName);
			if (!oldFile.exists()) {
				System.out.println("������Ŀ���ļ�(old)" + oldName + "�����ڣ�");
				return;
			}
			if (newFile.exists()) {
				System.out.println("������Ŀ���ļ�(new)" + newName + "�Ѵ��ڣ�");
			} else {

				if (oldFile.renameTo(newFile)) {
					System.out.println("�ļ�(old)   " + oldName + " \n��������Ϊ\n�ļ�(new)   " + newName + " !");
				} else {
					System.out.println("������ʧ�ܡ�����������");
					System.out.println("oldName:" + oldName);
					System.out.println("newName:" + newName);
				}
			}

		} else { // �¾�����ͬ
			System.out.println("�¾�����ͬ����");
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
			System.out.println(filePathIn + "��һ���ļ����������ļ��У�");
		}
		return filePathIn;
	}

}
