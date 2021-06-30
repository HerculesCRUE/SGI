package org.crue.hercules.sgi.framework.integration;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.framework.web.config.SgiWebConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class PageMappingJackson2HttpMessageConverterIT {

  @Autowired
  private ObjectMapper mapper;

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
    @GetMapping("/test-response-page")
    Page<String> testPage(@RequestBody String[] data) {
      Page<String> page = new PageImpl<String>(Arrays.asList(data));
      return page;
    }
  }

  /**
   * @throws Exception
   */
  @Test
  public void responsePaginatedData_returnsDataAndPagingHeaders() throws Exception {
    // given: a data array
    String[] data = new String[] { "one", "two", "tree" };

    // when: call a controller method that returns the data as Page object
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get("/test-response-page").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: page info is returned as headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        // is page 0
        .andExpect(MockMvcResultMatchers.header().string("X-Page", "0"))
        // the page size is the size of data
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Size", String.valueOf(data.length)))
        // the number of elements is ths page is the size of data
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Total-Count", String.valueOf(data.length)))
        // the number of pages is one
        .andExpect(MockMvcResultMatchers.header().string("X-Page-Count", "1"))
        // the total number of elements is the size of data
        .andExpect(MockMvcResultMatchers.header().string("X-Total-Count", String.valueOf(data.length))).andReturn();

    // this uses a TypeReference to inform Jackson about the List's generic type
    List<String> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<String>>() {
        });

    // and data as content
    Assertions.assertThat(actual.size()).isEqualTo(data.length);
    for (int i = 0; i < actual.size(); i++) {
      Assertions.assertThat(actual.get(i)).isEqualTo(data[i]);
    }
  }
}