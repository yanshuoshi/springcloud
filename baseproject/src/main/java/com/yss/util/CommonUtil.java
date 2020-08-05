package com.yss.util;

import java.util.*;


/**
 * 基本类型判断工具
 * @author start
 *
 */
public class CommonUtil {

	public static Boolean isNotIntegerNull(Integer parameter){
		Boolean flag = false;
		if(parameter!=null && parameter>0){
			flag = true;
		}
		return flag;
	}
	public static Boolean isNotLongNull(Long parameter){
		Boolean flag = false;
		if(parameter!=null && parameter>0){
			flag = true;
		}
		return flag;
	}
	public static Boolean isNotDoubleNull(Double parameter){
		Boolean flag = false;
		if(parameter!=null && parameter>0){
			flag = true;
		}
		return flag;
	}

	public static Boolean isNotMapNull(Map map){
		Boolean flag = false;
		if(map!=null &&!map.isEmpty()){
			flag = true;
		}
		return flag;
	}

	public static Boolean isNotListNull(List list){
		Boolean flag = false;
		if(list!=null &&list.size()>0){
			flag = true;
		}
		return flag;
	}
	
}
