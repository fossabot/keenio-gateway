package de.noltarium.keenio.gateway.security;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * https://docs.travis-ci.com/user/notifications/#Verifying-Webhook-requests
 * 
 * @author nolte
 *
 */
@Service
@Slf4j
public class TravisCIAuthRequestFilter extends OncePerRequestFilter {

	@Autowired
	PublicKeyLoader loader;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.trace("validate incomming request");

		String signature = request.getHeader("Signature");
		String payload = request.getParameter("payload");
		if (StringUtils.isNotBlank(payload) && StringUtils.isNotBlank(signature)) {
			byte[] decoded = Base64.getDecoder().decode(signature);
			PublicKey key = loader.loadPublicKey();
			if (MessageVerifyer.verify(key, payload, decoded)) {
				ArrayList<GrantedAuthority> authorities = new ArrayList<>();
				authorities.add(new SimpleGrantedAuthority("ROLE_TRAVISCI"));
				Authentication auth = new UsernamePasswordAuthenticationToken("travisci", "none", authorities);
				SecurityContextHolder.getContext().setAuthentication(auth);
			} else {
				log.debug("MessageVerifyer faild");
			}
		}
		filterChain.doFilter(request, response);
	}

}
