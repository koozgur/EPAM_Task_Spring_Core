package com.gymcrm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that performs stateless JWT-based authentication.
 *<p>
 * For each request, it extracts a Bearer token from the Authorization header,
 * validates it, loads the associated user, and populates the
 * {@link SecurityContextHolder} with an authenticated principal.
 *<p>
 * This enables downstream Spring Security authorization mechanisms to
 * enforce access control based on the resolved user identity.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Autowired(required = false)
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractBearerToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {

            // check token blacklist (TokenBlacklistService injected when available)
            if (tokenBlacklistService != null) {
                String jti = jwtTokenProvider.getJtiFromToken(token);
                if (tokenBlacklistService.isBlacklisted(jti)) {
                    logger.debug("Rejected blacklisted JWT jti={}", jti);
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            String username = jwtTokenProvider.getUsernameFromToken(token);

            // Only set authentication if not already set (e.g. by a previous filter)
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authenticated user '{}' via JWT", username);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the raw JWT string from {@code Authorization: Bearer <token>}.
     * Returns null if the header is absent or malformed.
     */
    public static String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            return token.isEmpty() ? null : token;
        }
        return null;
    }
}
