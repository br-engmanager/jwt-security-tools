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
		this.token = token;
		try {
			this.setExpTime(JwtTokenTools.getClaimFromToken(this.getToken(), Claims::getExpiration));
			this.setUserId(JwtTokenTools.getClaimFromToken(this.getToken(), Claims::getSubject));
			this.setStatus(JwtTokenTools.getClaimFromToken(this.getToken(), "status"));
		}
		catch (Exception e) {}
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
	
	public void initializeFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain, List<ISecurity> validationList) throws IOException, ServletException {
		
		initializeVariables();
		
		if(request.getMethod().toLowerCase().equals("options")){
			chain.doFilter(request, response);
			return;
		}

		if(validationList == null || validationList.size()==0) {
			validated = JwtTokenTools.validateToken(request);
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
			
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
					this.getUserId(), null, null);

			usernamePasswordAuthenticationToken
			.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

		}
	}
	


	
}
