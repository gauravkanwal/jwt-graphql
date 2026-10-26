package com.example.jwt_and_graphql.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTService {
    private final SecretKey signingKey;

    public JWTService(@Value("${jwt.secret}") String secret){
        this.signingKey= Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
    }

    public String generateToken(UserDetails userDetails){
        long expirationTime=15*60*1000L; //15 mins
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+expirationTime))
                .signWith(signingKey)
                .compact();
    }

    private Claims verifyTokenAndExtractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String verifyTokenAndExtractUsername(String token){
        return verifyTokenAndExtractAllClaims(token).getSubject();
    }

}
