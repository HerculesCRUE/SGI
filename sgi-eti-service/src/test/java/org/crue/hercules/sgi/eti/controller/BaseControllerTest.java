package org.crue.hercules.sgi.eti.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.crue.hercules.sgi.framework.test.context.support.SgiTestProfileResolver;
import org.crue.hercules.sgi.eti.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// Since WebMvcTest is only sliced controller layer for the testing, it would
// not take the security configurations.
@Import(SecurityConfig.class)
@ActiveProfiles(resolver = SgiTestProfileResolver.class)
public class BaseControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper mapper;

  @MockBean
  protected JwtDecoder jwtDecoder;
}
