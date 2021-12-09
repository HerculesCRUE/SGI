package org.crue.hercules.sgi.rep.controller;

import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ConfigControllerTest
 */
@WebMvcTest(value = ConfigController.class, properties = "sgi.time-zone=" + ConfigControllerTest.TIMEZONE_LONDON)
@EnableConfigurationProperties(value = SgiConfigProperties.class)
class ConfigControllerTest extends BaseControllerTest {
  static final String TIMEZONE_LONDON = "Europe/London";

  @Test
  @WithMockUser(username = "user", authorities = {})
  void getTimeZone_ReturnsTimeZone() throws Exception {
    // given: configured time
    // "Europe/London"
    // when: get time zone
    mockMvc
        .perform(MockMvcRequestBuilders.get(ConfigController.MAPPING + ConfigController.PATH_TIMEZONE)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns the configured time zone
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(TIMEZONE_LONDON));
  }

}
