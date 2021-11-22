package org.crue.hercules.sgi.framework.http.converter.json;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.framework.web.config.SgiWebConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class PageMappingJackson2HttpMessageConverterIT {

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void responsePaginatedData_returnsDataAndPagingHeaders() throws Exception {
    // given: a data array
    String[] data = new String[] { "one", "two", "tree", "four", "five", "six", "seven", "eight", "nine", "ten" };

    // when: call a controller method that returns the data as Page object
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get("/test-response-page").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).header(PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_HEADER, 0)
            .header(PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_SIZE_HEADER, 5)
            .content(mapper.writeValueAsString(data)))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: page info is returned as headers
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        // is requested page
        .andExpect(
            MockMvcResultMatchers.header().longValue(PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_HEADER, 0))
        // the page size is the requested page size
        .andExpect(MockMvcResultMatchers.header()
            .longValue(PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_SIZE_HEADER, 5))
        // the number of elements is this page is the size of the page
        .andExpect(MockMvcResultMatchers.header()
            .longValue(PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_TOTAL_COUNT_HEADER, 5))
        // the number of pages is two
        .andExpect(MockMvcResultMatchers.header()
            .longValue(PageMappingJackson2HttpMessageConverter.DEFAULT_PAGE_COUNT_HEADER, 2))
        // the total number of elements is the size of data
        .andExpect(MockMvcResultMatchers.header()
            .longValue(PageMappingJackson2HttpMessageConverter.DEFAULT_TOTAL_COUNT_HEADER, data.length))
        .andReturn();

    // this uses a TypeReference to inform Jackson about the List's generic type
    List<String> actual = mapper.readValue(requestResult.getResponse().getContentAsString(),
        new TypeReference<List<String>>() {
        });

    // and data as content
    Assertions.assertThat(actual.size()).isEqualTo(5);
    for (int i = 0; i < actual.size(); i++) {
      Assertions.assertThat(actual.get(i)).isEqualTo(data[i]);
    }
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
  static class TestWebConfig extends SgiWebConfig {
    @RestController
    static class InnerWebConfigTestController {
      @GetMapping("/test-response-page")
      Page<String> testPage(@RequestBody String[] data, @RequestPageable Pageable paging) {
        int pageSize = paging.getPageSize();
        long offset = paging.getOffset();
        Page<String> page = new PageImpl<String>(Arrays.asList(data).subList((int) offset, pageSize + (int) offset),
            paging, data.length);
        return page;
      }
    }
  }
}