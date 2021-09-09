package br.com.tijo.api.jwtsecuritytools;

public class RouteException {
	
	private String path;
	private String method;
	
	public RouteException() {}
	public RouteException(String _path, String _method) {
		this.path = _path;
		this.method = _method;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	

}
