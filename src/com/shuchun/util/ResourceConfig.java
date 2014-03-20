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
 * 文件解析
 * @author shuchun
 *
 */
public class ResourceConfig {

	//默认解析匹配规则
	private String regEx = "[\"|']?([\u4e00-\u9fa5|\\w]+)[\"|']?=[\"|']?(.+)[\"|']?";
	private boolean defSep = true;//是否使用默认解析格式
	private String basePath;//项目启动目录
	private Map<String, String> config;//解析后的key-value对

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
			//TODO 解析xml文件未实现
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
	 * 加载自定义分隔符文件
	 * 
	 * @param config
	 *            配置文件相对路径
	 * @param separator
	 *            自定义分隔符
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
	 * 获取属性值
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public String get(String key) {
		return this.config.get(key);
	}

	/**
	 * 获取解析后的所有参数
	 * 
	 * @return Map<key,value>
	 */
	public Map<String, String> getAll() {
		return this.config;
	}

	// TODO 解析xml

	/**
	 * 解析json
	 * 
	 * @param file
	 *            json文件
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
	 * 解析文件
	 * 
	 * @param file
	 *            配置文件
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
	 * 输出解析key-value到控制台
	 */
	private void echo() {
		String[] ite = this.config.keySet().toArray(
				new String[this.config.keySet().size()]);

		for (String key : ite) {
			System.out.println(key + "," + this.config.get(key));
		}
	}
	
}
