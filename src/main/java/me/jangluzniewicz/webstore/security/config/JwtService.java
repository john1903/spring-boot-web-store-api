package me.jangluzniewicz.webstore.security.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import me.jangluzniewicz.webstore.exceptions.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private static final int EXP_TIME_SEC = 60 * 60 * 24;
  private final JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;
  private final JWSSigner jwsSigner;
  private final JWSVerifier jwsVerifier;

  public JwtService(@Value("${jwt.secret}") String secret) {
    try {
      jwsSigner = new MACSigner(secret);
      jwsVerifier = new MACVerifier(secret);
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }
  }

  public String createSignedJwt(@NotNull String username, @NotNull List<String> roles) {
    JWSHeader header = new JWSHeader(jwsAlgorithm);
    Date exp =
        Date.from(
            LocalDateTime.now()
                .plusSeconds(EXP_TIME_SEC)
                .atZone(ZoneId.systemDefault())
                .toInstant());
    JWTClaimsSet claims =
        new JWTClaimsSet.Builder()
            .subject(username)
            .claim("roles", roles)
            .expirationTime(exp)
            .build();
    SignedJWT signedJWT = new SignedJWT(header, claims);
    try {
      signedJWT.sign(jwsSigner);
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }
    return signedJWT.serialize();
  }

  private boolean isJwtExpired(@NotNull SignedJWT signedJWT) {
    try {
      JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
      LocalDateTime expirationTime =
          claims.getExpirationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
      if (expirationTime.isBefore(LocalDateTime.now())) {
        return true;
      }
    } catch (ParseException e) {
      throw new JwtException("JWT does not contain expiration date");
    }
    return false;
  }

  public void verifyJwt(@NotNull SignedJWT signedJWT) {
    try {
      if (!signedJWT.verify(jwsVerifier)) {
        throw new JwtException("JWT signature is not valid");
      }
      if (isJwtExpired(signedJWT)) {
        throw new JwtException("JWT is expired");
      }
    } catch (JOSEException e) {
      throw new JwtException("JWT is not valid");
    }
  }

  public Authentication getAuthentication(@NotNull SignedJWT signedJWT) {
    String username;
    List<String> roles;
    try {
      JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
      username = jwtClaimsSet.getSubject();
      roles = jwtClaimsSet.getStringListClaim("roles");
    } catch (ParseException e) {
      throw new JwtException("JWT is not valid");
    }
    return new UsernamePasswordAuthenticationToken(
        username, null, roles.stream().map(SimpleGrantedAuthority::new).toList());
  }
}
