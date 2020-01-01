package com.altimetrik.greetingservice.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtProvider implements InitializingBean {

    @Value("${altimetrik.jwt.secret}")
    private String secretKey;

    private static final String AUTHORITIES_KEY = "auth";
    private static final String USER_ID_KEY = "userId";

    private Key key;

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes;

        //log.debug("Using a Base64-encoded JWT secret key");
        keyBytes = Decoders.BASE64.decode(secretKey);

        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities;
        if (!StringUtils.isEmpty(claims.get(AUTHORITIES_KEY))) {
            authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

        } else {
            authorities = new ArrayList<>();
        }

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public String createToken(Authentication authentication, String userId) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date validity = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10);


        Map<String, Object> claimMap = new HashMap<>();
        claimMap.put(USER_ID_KEY, userId);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .addClaims(claimMap)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public boolean validateToken(String authToken) {
        Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
        return true;

    }
}