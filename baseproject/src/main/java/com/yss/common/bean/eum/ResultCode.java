package com.yss.common.bean.eum;

/**
 * @Description: 返回标志
 * @author shuoshi.yan
 * @date 2020/08/03
 */
public enum ResultCode {
	
	/**
	 * 成功
	 */
	SUCCESS(0), 
	
	/**
	 * 请求失败
	 */
	DEF_ERROR(-1),
	
	/**
	 * 无数据
	 */

	NO_DATA(10),
	
	/**
	 * url不存在
	 */
	NO_URL(404),

	/**
	 * 请求方式不知道
	 */
	METHOD_NOT_ALLOWED(405),

	/**
	 * 参数异常
	 */
	PARAM_ERROR(2),

	/**
	 * 服务器异常
	 */
	SERVER_ERROR(500);
	
	private Integer code;

	private ResultCode(Integer code) {
		this.code = code;
	}

	public Integer getCode() {
		return code;
	}
}
