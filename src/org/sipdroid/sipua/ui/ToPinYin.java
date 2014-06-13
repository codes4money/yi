package org.sipdroid.sipua.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ToPinYin {
	
	/**
	 * 将传递的汉字list转换成拼音List
	 * @param list
	 */
	public static List<String> getPinyinList(List<String> list){
		List<String> pinyinList = new ArrayList<String>();
		for(Iterator<String> i=list.iterator(); i.hasNext();) {
			String str = (String)i.next();
			try {
				String pinyin = getPinYin(str);
				pinyinList.add(pinyin);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		}
		return pinyinList;
	}
	
    /**
     * 将中文转换成拼音
     * @param 拼音-汉字
     * @return
     */
    public static String getPinYin(String zhongwen)   
            throws BadHanyuPinyinOutputFormatCombination {   
  
        String zhongWenPinYin = "";   
        char[] chars = zhongwen.toCharArray();   
  
        for (int i = 0; i < chars.length; i++) {   
            String[] pinYin = PinyinHelper.toHanyuPinyinStringArray(chars[i], getDefaultOutputFormat());   
            // 当转换不是中文字符时,返回null   
            if (pinYin != null) {   
            	zhongWenPinYin += pinYin[0];   
            } else {   
                zhongWenPinYin += chars[i];   
            }   
        }   
        return zhongWenPinYin;   
    }   
  
    /**  
     * 输出格式  
     *   
     * @return  
     */  
    private static HanyuPinyinOutputFormat getDefaultOutputFormat() {   
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();   
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);// 大写   
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 没有音调数字   
        format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);// u显示   
        return format;   
    }   
    /**
	    * 将字符串中的中文转化为拼音,其他字符不变
	    *
	    * @param inputString
	    * @return
	    */
	    public static String getPingYin(String inputString) {
	        HanyuPinyinOutputFormat format = new
	        HanyuPinyinOutputFormat();
	        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	        format.setVCharType(HanyuPinyinVCharType.WITH_V);

	        char[] input = inputString.trim().toCharArray();
	        String output = "";

	        try {
	            for (int i = 0; i < input.length; i++) {
	                if (java.lang.Character.toString(input[i]).
	                matches("[\\u4E00-\\u9FA5]+")) {
	                    String[] temp = PinyinHelper.
	                    toHanyuPinyinStringArray(input[i],
	                    format);
	                    output += temp[0];
	                } else
	                    output += java.lang.Character.toString(
	                    input[i]);
	            }
	        } catch (BadHanyuPinyinOutputFormatCombination e) {
	            e.printStackTrace();
	        }
	        return output;
	    }
}
