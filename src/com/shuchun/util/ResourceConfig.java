package com.shuchun.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * �ļ�����
 * @author shuchun
 *
 */
public class ResourceConfig {

	//Ĭ�Ͻ���ƥ�����
	private String regEx = "[\"|']?([\u4e00-\u9fa5|\\w]+)[\"|']?=[\"|']?(.+)[\"|']?";
	private boolean defSep = true;//�Ƿ�ʹ��Ĭ�Ͻ�����ʽ
	private String basePath;//��Ŀ����Ŀ¼
	private Map<String, String> config;//�������key-value��

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public ResourceConfig() {
		try {
			basePath = new File("").getCanonicalPath() + File.separator;
		} catch (IOException e) {

			e.printStackTrace();
		}
		config = new HashMap<String, String>();
	}
	
	public ResourceConfig(String config){
		this();
		try {
			this.load(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param config
	 * @throws IOException
	 */
	public void load(String config) throws IOException {
		File f = new File(basePath + config);

		if (!f.exists() || !f.isFile()) {
			throw new FileNotFoundException(basePath + config);
		}

		String extension = f.getName().substring(f.getName().lastIndexOf("."))
				.toLowerCase().trim();

		if(extension.contains("xml")){
			//TODO ����xml�ļ�δʵ��
		}else if (extension.contains("csv")) {
			if (defSep) {
				regEx = "[\"|']?([\u4e00-\u9fa5|\\w]+)[\"|']?,[\"|']?(.+)[\"|']?";
			}
			resolveProperties(f);
		} else if (extension.contains("json")) {
			if (defSep) {
				regEx = "^[\\[|\\{]{1,2}([\"|']{1}(.+)[\"|']{1}:[\"|']{1}(.+)[\"|']{1},?)*[\\}|\\]]{1,2}$";
			}
			resolveJson(f);
		} else {
			resolveProperties(f);
		}
	}

	/**
	 * �����Զ���ָ����ļ�
	 * 
	 * @param config
	 *            �����ļ����·��
	 * @param separator
	 *            �Զ���ָ���
	 * @throws IOException
	 */
	public void load(String config, String separator) throws IOException {
		defSep = false;
		String key = "[\"|']?([\u4e00-\u9fa5|\\w]+)[\"|']?";
		String value = "[\"|']?(.+)[\"|']?";

		String escape = "\\\"\'[](){}?+.*|^$";

		if (escape.contains(separator)) {
			regEx = key + "\\" + separator + value;
		} else {
			regEx = key + separator + value;
		}

		this.load(config);
	}

	/**
	 * ��ȡ����ֵ
	 * 
	 * @param key
	 *            ��
	 * @return ֵ
	 */
	public String get(String key) {
		return this.config.get(key);
	}

	/**
	 * ��ȡ����������в���
	 * 
	 * @return Map<key,value>
	 */
	public Map<String, String> getAll() {
		return this.config;
	}

	// TODO ����xml

	/**
	 * ����json
	 * 
	 * @param file
	 *            json�ļ�
	 * @throws IOException
	 */
	private void resolveJson(File file) {
		StringBuffer text = new StringBuffer();
		FileReader reader = null;
		BufferedReader br = null;

		try {
			reader = new FileReader(file);
			br = new BufferedReader(reader);
			Pattern pattern = Pattern.compile(regEx);
			Matcher matcher = null;
			String tmp = "";

			if (br.ready()) {

				while ((tmp = br.readLine()) != null) {
					text.append(tmp);
				}
			}
			matcher = pattern.matcher(text.toString().trim());
			if (matcher.find() && matcher.groupCount() > 1) {
				tmp = matcher.group(1);
				// this.config.put(matcher.group(2), matcher.group(3));
				tmp = tmp.replace(":", ",");
				tmp = tmp.replace("\"", "");
				tmp = tmp.replace("'", "");
				String[] list = tmp.split(",");
				for (int i = 0; i < list.length; i = i + 2) {
					this.config.put(list[i], list[i + 1]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (br != null) {
					br.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		// echo();
	}

	/**
	 * �����ļ�
	 * 
	 * @param file
	 *            �����ļ�
	 * @throws IOException
	 */
	private void resolveProperties(File file) {

		String tmp = "";
		FileReader reader = null;
		BufferedReader br = null;

		try {
			reader = new FileReader(file);
			br = new BufferedReader(reader);
			Pattern pattern = Pattern.compile(regEx);
			Matcher matcher = null;

			if (br.ready()) {
				while ((tmp = br.readLine()) != null) {
					matcher = pattern.matcher(tmp);
					if (matcher.find() && matcher.groupCount() > 1) {
						this.config.put(matcher.group(1), matcher.group(2));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// echo();
	}

	/**
	 * �������key-value������̨
	 */
	private void echo() {
		String[] ite = this.config.keySet().toArray(
				new String[this.config.keySet().size()]);

		for (String key : ite) {
			System.out.println(key + "," + this.config.get(key));
		}
	}
	
}
