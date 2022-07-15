package br.com.engmanager.api.jwtsecuritytools;

import javax.servlet.http.HttpServletRequest;

public interface ISecurity {
	
	ValidateResponse validate(HttpServletRequest request);

}
