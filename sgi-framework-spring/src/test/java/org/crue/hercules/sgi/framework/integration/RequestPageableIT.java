package org.crue.hercules.sgi.framework.integration;

import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.framework.web.config.SgiWebConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class RequestPageableIT {

  @Autowired
  private MockMvc mockMvc;

  @Configuration // A nested @Configuration class wild be used instead of the
                 // application’s primary configuration.
  @Import(SgiWebConfig.class)
  public static class TestWebConfig {
  }

  @TestConfiguration // Unlike a nested @Configuration class, which would be used instead of your
                     // application’s primary configuration, a nested @TestConfiguration class is
                     // used in addition to your application’s primary configuration.
  @RestController
  public static class InnerWebConfigTestController {
    @GetMapping("/test-request-pageable")
    Pageable testPageable(@RequestPageable Pageable paging) {
      return paging;
    }

    @GetMapping("/test-request-pageable-custom-headers")
    Pageable testPageableCustomHeaders(@RequestPageable(pageHeader = "XX-P", pageSizeHeader = "XX-S") Pageable paging) {
      return paging;
    }
  }

  /**
   * @throws Exception
   */
  @Test
  void requestWithPageableAnnotation_and_PagingHeaders_returnsPageable() throws Exception {
    // given: some Paging info
    int pageNumber = 3;
    int pageSize = 10;

    // when: test-request-pageable controller with @RequestPageable annotations
    mockMvc
        .perform(MockMvcRequestBuilders.get("/test-request-pageable").header("X-Page", pageNumber)
            .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: sent paging header data is returned as Pageable object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("pageNumber").value(pageNumber))
        .andExpect(MockMvcResultMatchers.jsonPath("pageSize").value(pageSize));
  }

  /**
   * @throws Exception
   */
  @Test
  void requestWithPageableAnnotation_and_No_PagingHeaders_returnsUnpagedPageable() throws Exception {
    // given: no Paging info

    // when: test-request-pageable controller with @RequestPageable annotations
    mockMvc.perform(MockMvcRequestBuilders.get("/test-request-pageable").accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: sent paging header data is returned as no paged object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("unpaged").value(true));
  }

  /**
   * @throws Exception
   */
  @Test
  void requestWithPageableAnnotation_and_CustomPagingHeaders_returnsPageable() throws Exception {
    // given: some Paging info
    int pageNumber = 3;
    int pageSize = 10;

    // when: test-request-pageable controller with @RequestPageable annotations with
    // customized header names
    mockMvc
        .perform(MockMvcRequestBuilders.get("/test-request-pageable-custom-headers").header("XX-P", pageNumber)
            .header("XX-S", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: sent paging header data is returned as Pageable object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("pageNumber").value(pageNumber))
        .andExpect(MockMvcResultMatchers.jsonPath("pageSize").value(pageSize));
  }

}