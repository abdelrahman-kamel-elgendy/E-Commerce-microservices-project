package com.e_commerce.auth_service.config;

import com.e_commerce.auth_service.utils.JwtTokenProvider;
import com.e_commerce.auth_service.repositories.UserSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import com.e_commerce.auth_service.exceptions.InvalidTokenException;
import com.e_commerce.auth_service.exceptions.TokenExpiredException;
import com.e_commerce.auth_service.models.UserSession;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserSessionRepository userSessionRepository;

    // constructor for explicit wiring from SecurityConfig
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, UserSessionRepository userSessionRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userSessionRepository = userSessionRepository;
    }

    // default constructor kept for frameworks that might need it
    public JwtTokenFilter() {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtTokenProvider.resolveToken(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                // Some tokens (refresh, password_reset, account_unlock, email_verification)
                // are not tied to a session. Those tokens carry a "purpose" claim.
                // If the token's purpose is "refresh" we should NOT enforce a session
                // check here, otherwise refresh tokens presented to the /refresh-token
                // endpoint will be rejected by the filter.
                String purpose = jwtTokenProvider.getTokenPurpose(token);

                if (purpose == null || !"refresh".equals(purpose)) {
                    // normal access token flow: require a sessionId and active session
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    Long sessionId = jwtTokenProvider.getSessionIdFromToken(token);

                    // Check that the session is active (token not blacklisted)
                    var sessionOpt = (sessionId == null)
                            ? java.util.Optional.<UserSession>empty()
                            : userSessionRepository.findBySessionIdAndActiveTrue(sessionId);

                    if (sessionId == null || sessionOpt.isEmpty())
                        throw new InvalidTokenException("Session inactive or token revoked");

                    // additionally, check whether the session has a loggedOutAt timestamp
                    // and deny access to any token issued before or at that time
                    var session = sessionOpt.get();
                    if (session.getLoggedOutAt() != null) {
                        java.util.Date loggedOutAt = java.util.Date.from(session.getLoggedOutAt());
                        if (jwtTokenProvider.isIssuedBefore(token, loggedOutAt)
                                || jwtTokenProvider.getIssuedAtFromToken(token) == null)
                            throw new InvalidTokenException("Token was issued before logout");

                    }

                    if (username != null) {
                        var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                username, null, java.util.Collections.emptyList());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } else {
                    // purpose == "refresh" (or any other non-session purpose) ->
                    // allow the request to proceed without setting authentication.
                    // The controller/service handling the endpoint will validate the token
                    // again and issue new tokens as needed.
                }
            }
        } catch (InvalidTokenException | TokenExpiredException ex) {
            SecurityContextHolder.clearContext();
            throw ex;
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            throw new InvalidTokenException(ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}