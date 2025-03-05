package me.jangluzniewicz.webstore.security.units.services;

import static org.junit.jupiter.api.Assertions.*;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import me.jangluzniewicz.webstore.security.models.CustomUser;
import me.jangluzniewicz.webstore.security.services.JwtService;
import me.jangluzniewicz.webstore.users.models.User;
import me.jangluzniewicz.webstore.utils.testdata.users.UserTestDataBuilder;
import me.jangluzniewicz.webstore.utils.units.config.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class JwtServiceTest extends UnitTest {
  private static final String JWT_SECRET =
      "c467be2fdfa1dfcebbc490bc57b5e21f52a33a14106ccf0b78b0ad2dd3d244f7";
  private JwtService jwtService;
  private User user;
  private List<String> roles;

  @BeforeEach
  void setUp() {
    user = UserTestDataBuilder.builder().build().buildUser();
    roles = List.of(user.getRole().getName());
    jwtService = new JwtService(JWT_SECRET);
  }

  @Test
  void createSignedJwt_whenAllParametersGiven_thenReturnJwtSerialized()
      throws ParseException, JOSEException {
    String jwt = jwtService.createSignedJwt(user.getId(), user.getEmail(), roles);

    SignedJWT signedJWT = SignedJWT.parse(jwt);
    MACVerifier verifier = new MACVerifier(JWT_SECRET);
    assertTrue(signedJWT.verify(verifier));
    String subject = signedJWT.getJWTClaimsSet().getSubject();
    assertEquals(user.getEmail(), subject);
    Long id = signedJWT.getJWTClaimsSet().getLongClaim("id");
    assertEquals(user.getId(), id);
    List<String> jwtRoles = signedJWT.getJWTClaimsSet().getStringListClaim("roles");
    assertEquals(roles, jwtRoles);
    assertTrue(signedJWT.getJWTClaimsSet().getExpirationTime().after(new Date()));
  }

  @Test
  void verifyJwt_whenValidJwtGiven_thenReturnTrue() {
    String jwt = jwtService.createSignedJwt(user.getId(), user.getEmail(), roles);

    assertDoesNotThrow(() -> jwtService.verifyJwt(SignedJWT.parse(jwt)));
  }

  @Test
  void getAuthentication_whenValidJwtGiven_thenReturnAuthentication() throws ParseException {
    String jwt = jwtService.createSignedJwt(user.getId(), user.getEmail(), roles);
    Authentication authentication = jwtService.getAuthentication(SignedJWT.parse(jwt));

    assertEquals(user.getEmail(), authentication.getName());
    if (authentication.getPrincipal() instanceof CustomUser customUser) {
      assertEquals(user.getId(), customUser.getId());
    } else {
      fail("Authentication principal is not CustomUser");
    }
  }
}
