package com.shenghuoli.library.utils;

import java.text.DecimalFormat;

/**
 * 金额转换工具类
 * @author vendor
 */
public class MoneyUtil {
	
	/**
	 * 显示  切换  数值
	 */
	public static String changeMoney(boolean isDecimal, boolean isMillion, String money){
		
		if(money == null){
			money = "0";
		}
		
		if(isMillion){  //如果是万元
			return MoneyUtil.convertMillion(money); //是万元就不要管是否显示小数了
		}
		
		if(isDecimal){
			return MoneyUtil.convertDecimal(money);
		}else{
			return MoneyUtil.convertInteger(money);
		}
	}
	
	/**
	 * 转换成整数的千分位显示
	 */
	public static String convertInteger(String money){
		String pattern="###,##0";
		
		return convert(pattern, money);
	}
	
	/**
	 * 转换成带小数的千分位显示
	 */
	public static String convertDecimal(String money){
		String pattern="###,##0.00";

		return convert(pattern, money);
	}
	
	/**
	 * 转换成万元带小数的千分位显示
	 */
	public static String convertMillion(String money){
		
		double m = 0.00;
		
		try {
			m = Double.valueOf(money);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return convertDecimal(String.valueOf(m/10000));
	}
	
	private static String convert(String pattern, String money){
		DecimalFormat df = new DecimalFormat(pattern);
		
	    try {
			return df.format(Double.valueOf(money));
		} catch (Exception e) {
			e.printStackTrace();
			return money == null ? "" : money;
		}
	}
}
