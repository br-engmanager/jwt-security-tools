package br.com.tijo.api.jwtsecuritytools;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.tijo.api.jwttools.JwtTokenTools;
import io.jsonwebtoken.Claims;

@Service
@Component
public class SecurityService {

	private String userId;
	private String token;
	private String status;
	private Date expTime;
	private boolean validated;

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		if(token != null && token != "") {
			this.token = token;
			try {
				this.setExpTime(JwtTokenTools.getClaimFromToken(this.getToken(), Claims::getExpiration));
				this.setUserId(JwtTokenTools.getClaimFromToken(this.getToken(), Claims::getSubject));
				this.setStatus(JwtTokenTools.getClaimFromToken(this.getToken(), "status"));
			}
			catch (Exception e) {}
		}
	}

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public Date getExpTime() {
		return expTime;
	}
	public void setExpTime(Date expTime) {
		this.expTime = expTime;
	}


	
	private void initializeVariables() {
		setUserId("");
		setToken("");
		setStatus("");
		setExpTime(null);
		validated = false;
	}
	
	public void initializeFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain, List<ISecurity> validationList, List<RouteException> exceptions) throws IOException, ServletException {
		
		initializeVariables();
		
		if(request.getMethod().toLowerCase().equals("options")){
			chain.doFilter(request, response);
			return;
		}

		notValidatedPath(request, exceptions);
		
		if(!validated) {
			validateTokenAuthenticated(request, validationList);
		}

	}

	private void notValidatedPath(HttpServletRequest request, List<RouteException> exceptions) {
		if(exceptions != null && exceptions.size()>0) {
			for (RouteException routeException : exceptions) {
				if(request.getMethod().equals(routeException.getMethod()) &&
						(routeException.getPath().equals("*") ||
								request.getRequestURI().endsWith(routeException.getPath())
								)) {
					this.validated = true;
					break;
				}
			}
		}	
	}
	
	private void validateTokenAuthenticated(HttpServletRequest request, List<ISecurity> validationList) {
		if(validationList == null || validationList.size()==0) {
			validated = JwtTokenTools.validateToken(request);
			setToken(JwtTokenTools.getTokenFromRequest(request));
		}
		else {
			for (ISecurity r : validationList) {
				if(r.validate(request).isValidated()) {
					validated = true;
					setToken(r.validate(request).getToken());
					break;
				}
			}
		}

		if (validated) {
			
			if(this.getToken() == null || this.getToken().isEmpty()) {
				this.setToken(JwtTokenTools.getTokenFromRequest(request));
			}
			
			validateSecurityOK(request, this.getUserId());
			
		}	
	}
	
	private void validateSecurityOK(HttpServletRequest request, String userName){
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				userName, null, null);

		usernamePasswordAuthenticationToken
		.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

	}

	
}
