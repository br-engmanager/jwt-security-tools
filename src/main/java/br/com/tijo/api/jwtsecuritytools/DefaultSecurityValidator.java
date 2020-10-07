package br.com.tijo.api.jwtsecuritytools;

import javax.servlet.http.HttpServletRequest;

import br.com.tijo.api.jwttools.JwtTokenTools;

public class DefaultSecurityValidator implements ISecurity {

	@Override
	public ValidateResponse validate(HttpServletRequest request) {
		
		ValidateResponse response = new ValidateResponse();
		response.setToken(JwtTokenTools.getTokenFromRequest(request));

		boolean validated = JwtTokenTools.validateToken(response.getToken()) &&
				!Boolean.parseBoolean(JwtTokenTools.getClaimFromToken(request, "isChangeOnly"));
		
		response.setValidated(validated);
		
		return response;
	
	}
	
	
}
