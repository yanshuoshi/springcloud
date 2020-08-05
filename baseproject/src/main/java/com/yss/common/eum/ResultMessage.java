package com.yss.common.eum;

/**
 * @date 2020/08/03
 */
public enum ResultMessage {

	DEF_SUCCESS("请求成功！"),
	DEF_ERROR("请求失败！"),
	NO_DATA("没有匹配数据！"),
	NO_URL("访问地址不存在！"),
	METHOD_NOT_ALLOWED("请求方式不被支持！"),
	PARAM_ERROR("参数异常！"),
	SERVER_ERROR("服务器异常！");

	private String message;

	private ResultMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
