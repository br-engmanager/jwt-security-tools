package br.com.tijo.api.jwtsecuritytools;

public class ValidateResponse {

	private boolean validated = false;
	private String token;

	
	public ValidateResponse(boolean validated, String token) {
		this.setValidated(validated);
		this.setToken(token);
	}


	public ValidateResponse() {}


	public boolean isValidated() {
		return validated;
	}
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
