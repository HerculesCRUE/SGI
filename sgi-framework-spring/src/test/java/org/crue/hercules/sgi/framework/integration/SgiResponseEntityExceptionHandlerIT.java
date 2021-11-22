package org.crue.hercules.sgi.framework.integration;

import org.crue.hercules.sgi.framework.integration.SgiResponseEntityExceptionHandlerIT.TestWebConfig;
import org.crue.hercules.sgi.framework.problem.spring.web.ProblemExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@WebMvcTest(TestWebConfig.class)
class SgiResponseEntityExceptionHandlerIT {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser
  void requestWithPageableAnnotation_and_PagingHeaders_returnsPageable() throws Exception {
    // given: a controller method that throws an IlleagalArgumenException

    // when: access /test-illegal-argument-exception
    mockMvc.perform(MockMvcRequestBuilders.get("/test-illegal-argument-exception").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: bad request is returned
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
  }

  /**
   * A nested @Configuration class will be used instead of the application’s
   * primary configuration.
   * <p>
   * Unlike a nested @Configuration class, which would be used instead of your
   * application’s primary configuration, a nested @TestConfiguration class is
   * used in addition to your application’s primary configuration.
   */
  @Configuration
  static class TestWebConfig implements WebMvcConfigurer {
    @Bean
    public TestResponseEntityExceptionHandler testResponseEntityExceptionHandler() {
      return new TestResponseEntityExceptionHandler();
    }

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ControllerAdvice
    public static class TestResponseEntityExceptionHandler extends ProblemExceptionHandler {
    }

    @RestController
    public static class InnerWebConfigTestController {
      @GetMapping("/test-illegal-argument-exception")
      void testIllegalArgumentException() {
        throw new IllegalArgumentException();
      }
    }
  }

}