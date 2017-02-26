package com.os.minirpc.common.bean;

/**
 * RPC 响应对象
 * 封装调用服务的方法执行结果
 */
public class RpcResponse {
	private String requestId;
	private Exception exception;
	private Object result;
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
}
