package com.shuchun.util;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @see(功能描述):通用验证类
 * @author(作者): shuchun
 * @version(版本):v1.0
 * @date(创建日期):2014-3-12
 */
public class ValidateUtil {

	/**
	 * 为空判断
	 * 
	 * @param obj
	 *            判定对象
	 * @return 判断结果
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		String tmp = obj.toString();
		if (tmp.trim().length() < 1) {
			return true;
		}

		return false;
	}

	/**
	 * 邮箱格式判断
	 * 
	 * @param email
	 *            邮箱地址
	 * @return 是否符合
	 */
	public static boolean isEmail(String email) {

		if (ValidateUtil.isEmpty(email)) {
			return false;
		}

		String regEx = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"; // Email格式
		Pattern pattern = Pattern.compile(regEx);
		Matcher mat = pattern.matcher(email);
		return mat.find();
	}

	/**
	 * 日期判定
	 * 
	 * @param date
	 *            日期字串
	 * @return 是否是日期
	 */
	public static boolean isDate(String date) {
		String pattern = null;
		if (date.contains("-")) {
			pattern = "yyyy-mm-dd";
		} else if (date.contains("/")) {
			pattern = "yyyy/mm/dd";
		}
		return ValidateUtil.isDate(date, pattern);
	}

	/**
	 * 日期判定
	 * 
	 * @param date
	 *            日期字串
	 * @param pattern
	 *            日期格式
	 * @return 是否是日期
	 */
	public static boolean isDate(String date, String pattern) {
		if (ValidateUtil.isEmpty(date) || ValidateUtil.isEmpty(pattern)) {
			return false;
		}
		//用正则匹配常用日期格式，然后截取年月日。
		/**
		 * 添加此验证的原因是Date.parse方法自动将月份、日期升降
		 * 如：2014/13/20将自动转换为1月，但与需求不符。
		 */
		
		String regEx = "(\\d{1,4})[-|\\/](\\d{1,2})[-|\\/](\\d{1,2})";
		Pattern pat = Pattern.compile(regEx);
		Matcher matcher = pat.matcher(date);

		List matches=null;

		if (matcher.find() && matcher.groupCount() >= 1) {
			matches = new ArrayList();
			for (int i = 1; i <= matcher.groupCount(); i++) {
				String temp = matcher.group(i);
				matches.add(temp);
			}
		}
		
		int year=-1;
		int month=-1;
		int day=-1;
		
		if(matches!=null&&matches.size()>0){
			year=Integer.valueOf(matches.get(0).toString());
			month=Integer.valueOf(matches.get(1).toString());
			day=Integer.valueOf(matches.get(2).toString());
		}

		if (month > 12) {
			return false;
		}

		Date tmp = null;
		try {
			tmp = new java.text.SimpleDateFormat(pattern).parse(date);
			if (tmp != null) {

				switch (month) {
				case 1:
				case 3:
				case 5:
				case 7:
				case 8:
				case 10:
				case 12:
					return day <= 31;
				case 2:
					if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
						return day <= 29;
					} else {
						return day <= 28;
					}

				default:
					return day <= 30;
				}

			}
			return false;
		} catch (ParseException e) {
			return false;
		}
	}

	/**
	 * date是否在指定日期之后
	 * 
	 * @param date
	 *            日期
	 * @param when
	 *            指定日期
	 * @return 判断结果
	 */
	public static boolean isAfter(Date date, Date when) {
		return date.after(when);
	}

	/**
	 * date是否在指定日期之前
	 * 
	 * @param date
	 *            日期
	 * @param when
	 *            指定日期
	 * @return 判断结果
	 */
	public static boolean isBefore(Date date, Date when) {
		return date.before(when);
	}

	/**
	 * 日期是否相等
	 * 
	 * @param date
	 *            日期
	 * @param when
	 *            指定日期
	 * @return 判断结果
	 */
	public static boolean isEqu(Date date, Date when) {
		return date.equals(when);
	}
	

	/**
	 * 判断日期是否在指定时段[after,before]之间
	 * 
	 * @param date
	 *            日期
	 * @param after
	 *            开始
	 * @param before
	 *            结束
	 * @return 是否在时段之间
	 */
	public static boolean dateCom(Date date, Date after, Date before) {
		return date.after(after) ? date.before(before) : false;
	}

	/**
	 * 长度是否小于指定长度
	 * 
	 * @param obj
	 *            对象
	 * @param max
	 *            最大长度
	 * @return 是否符合长度标准
	 */
	public static boolean lengthLow(String obj, int max) {
		if (max <= 0) {
			throw new IllegalArgumentException("最大长度应大于0");
		}
		return ValidateUtil.lengthCom(obj, 0, max - 1);
	}

	/**
	 * 长度是否大于指定长度
	 * 
	 * @param obj
	 *            对象
	 * @param min
	 *            最小长度
	 * @return 是否符合长度标准
	 */
	public static boolean lengthUp(String obj, int min) {
		min = Math.max(0, min);

		return ValidateUtil.lengthCom(obj, min + 1, Integer.MAX_VALUE);
	}

	/**
	 * 长度是否等于指定长度
	 * 
	 * @param obj
	 *            对象
	 * @param length
	 *            长度
	 * @return 是否符合长度标准
	 */
	public static boolean lengthEqu(String obj, int length) {
		return ValidateUtil.lengthCom(obj, length, length);
	}

	/**
	 * 判断长度是否介于[min,max]
	 * 
	 * @param obj
	 *            对象
	 * @param min
	 *            最小长度
	 * @param max
	 *            最大长度
	 * @return 长度是否符合
	 */
	public static boolean lengthCom(String obj, int min, int max) {
		if (ValidateUtil.isEmpty(obj)) {
			return false;
		}
		if (min > max) {
			throw new IllegalArgumentException("最大长度小于最小长度");
		}
		if (min == max) {
			return obj.length() == min;
		}
		return obj.length() > min ? obj.length() <= max : false;
	}

	/**
	 * 号码判断
	 * 
	 * @param num
	 *            号码
	 * @return 是否都是数字
	 */
	public static boolean isNumber(Object num) {
		if (ValidateUtil.isEmpty(num)) {
			return false;
		}
		String regEx = "^[0-9]*$";
		String tmp = num.toString();
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(tmp);

		return matcher.find();

	}

	/**
	 * 手机号码判断
	 * 
	 * @param number
	 *            号码
	 * @return 是否是手机号
	 */
	public static boolean isPhoneNumber(String number) {
		if (ValidateUtil.isNumber(number)) {
			return ValidateUtil.lengthEqu(number, 11);
		}
		return false;
	}

	/**
	 * 字符是否由数字、字母、下划线组成
	 * 
	 * @param input
	 *            输入
	 * @return 是否符合
	 */
	public static boolean isSafeChar(String input) {
		if (ValidateUtil.isEmpty(input)) {
			return false;
		}
		String regEx = "^\\w+$";// 安全字符格式
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(input);

		return matcher.find();
	}

	/**
	 * 身份证验证
	 * 
	 * @param number
	 *            身份证号
	 * @return 是否有效
	 */
	public static boolean isID(String number) {
		if (ValidateUtil.isEmpty(number)) {
			return false;
		}
		String regEx = "^(\\d{14}|\\d{17})(\\d|[xX])$";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(number);

		return matcher.find();
	}

	/**
	 * url验证
	 * 
	 * @param url
	 *            url
	 * @return 是否是url
	 */
	public static boolean isUrl(String url) {
		if (ValidateUtil.isEmpty(url)) {
			return false;
		}

		// url格式
		String regEx = "^http://[^\\.]([(w{3}\\.)|(\\w+)\\.])(\\w+)\\.(\\w{2,3})([\\w\\-\\./?%&=#]*)$";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(url);

		return matcher.find();
	}

}
