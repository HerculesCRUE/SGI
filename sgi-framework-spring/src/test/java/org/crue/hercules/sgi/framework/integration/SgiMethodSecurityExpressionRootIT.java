package org.crue.hercules.sgi.framework.integration;

import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.framework.web.config.SgiSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest
class SgiMethodSecurityExpressionRootIT {

  @Autowired
  private MockMvc mockMvc;

  @Configuration // A nested @Configuration class wild be used instead of the
                 // application’s primary configuration.
  // Since WebMvcTest is only sliced controller layer for the testing, it would
  // not take the security configurations.
  @Import({ SgiSecurityConfig.class })
  public static class TestWebConfig {
  }

  @TestConfiguration // Unlike a nested @Configuration class, which would be used instead of your
                     // application’s primary configuration, a nested @TestConfiguration class is
                     // used in addition to your application’s primary configuration.
  @RestController
  public static class InnerWebConfigTestController {
    @PreAuthorize("hasAuthority('AUTH_UO1')")
    @GetMapping("/test-auth")
    void testAuth() {
    }

    @PreAuthorize("hasAuthorityForAnyUO('AUTH')")
    @GetMapping("/test-auth-for-any-uo")
    void testAuthForAnyUO() {
    }
  }

  /**
   * @throws Exception
   */
  @Test
  @WithMockUser(username = "user", authorities = { "AUTH_UO1" })
  void requestTestAuth_WithAuthForUO1_returnsOk() throws Exception {
    // given:

    // when:
    mockMvc.perform(MockMvcRequestBuilders.get("/test-auth")).andDo(SgiMockMvcResultHandlers.printOnError())
        // then:
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  /**
   * @throws Exception
   */
  @Test
  @WithMockUser(username = "user", authorities = { "AUTH_UO2" })
  void requestTestAuth_WithAuthForUO2_returnsForbidden() throws Exception {
    // given:

    // when:
    mockMvc.perform(MockMvcRequestBuilders.get("/test-auth")).andDo(SgiMockMvcResultHandlers.printOnError())
        // then:
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  /**
   * @throws Exception
   */
  @Test
  @WithMockUser(username = "user", authorities = { "AUTH_UO1" })
  void requestTestAuthForAnyUO_WithAuthForUO1_returnsOk() throws Exception {
    // given:

    // when:
    mockMvc.perform(MockMvcRequestBuilders.get("/test-auth-for-any-uo")).andDo(SgiMockMvcResultHandlers.printOnError())
        // then:
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  /**
   * @throws Exception
   */
  @Test
  @WithMockUser(username = "user", authorities = { "AUTH_UO2" })
  void requestTestAuthForAnyUO_WithAuthForUO2_returnsOk() throws Exception {
    // given:

    // when:
    mockMvc.perform(MockMvcRequestBuilders.get("/test-auth-for-any-uo")).andDo(SgiMockMvcResultHandlers.printOnError())
        // then:
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

}