package com.yss.common.bean;


import com.alibaba.fastjson.JSONObject;
import com.yss.common.bean.eum.ResultCode;
import com.yss.common.bean.eum.ResultMessage;

/**
 * @Description: api 统一返回参数
 * @author shuoshi.yan
 * @date 2020/08/03
 */
public class ApiResult {

	/** Result status */
	private Integer code;
	/** Result message */
	private String message;
	/** Result data */
	private Object data;
	/** Total item size */
	private Integer total;

	public ApiResult() {
		this(ResultCode.DEF_ERROR.getCode(), ResultMessage.DEF_ERROR.getMessage(), null, null);
	}

	public ApiResult(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public ApiResult(Integer code, String message, Object data, Integer total) {
		this.code = code;
		this.message = message;
		this.data = data;
		this.total=total;
		
	}
	public ApiResult(ResultCode code, ResultMessage message, Object data, Integer total) {
		this(code.getCode(), message.getMessage(), data,total);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(ResultCode code) {
		this.code = code.getCode();
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(ResultMessage message) {
		this.message = message.getMessage();
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public void setSuccess() {
		this.setCode(ResultCode.SUCCESS);
		this.setMessage(ResultMessage.DEF_SUCCESS.getMessage());
	}

	public String toJSON() {
		return JSONObject.toJSONString(this);
	}
}
