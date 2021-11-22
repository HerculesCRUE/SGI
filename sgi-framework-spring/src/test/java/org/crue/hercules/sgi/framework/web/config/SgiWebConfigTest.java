package org.crue.hercules.sgi.framework.web.config;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.web.bind.annotation.RequestPageable;
import org.crue.hercules.sgi.framework.web.config.SgiWebConfigTest.WebConfigTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(WebConfigTest.class)
class SgiWebConfigTest {

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @WithMockUser
  void requestWithPageableAnnotation_and_PagingHeaders_returnsPageable() throws Exception {
    // given: some Paging info
    int pageNumber = 3;
    int pageSize = 10;

    // when: test-request-pageable controller with @RequestPageable annotations
    mockMvc
        .perform(MockMvcRequestBuilders.get("/test-request-pageable").header("X-Page", pageNumber)
            .header("X-Page-Size", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: sent paging header data is returned as Pageable object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("pageNumber").value(pageNumber))
        .andExpect(MockMvcResultMatchers.jsonPath("pageSize").value(pageSize));
  }

  @Test
  @WithMockUser
  void requestWithPageableAnnotation_and_No_PagingHeaders_returnsUnpagedPageable() throws Exception {
    // given: no Paging info

    // when: test-request-pageable controller with @RequestPageable annotations
    mockMvc.perform(MockMvcRequestBuilders.get("/test-request-pageable").accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: sent paging header data is returned as no paged object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("unpaged").value(true));
  }

  @Test
  @WithMockUser
  void requestWithPageableAnnotation_and_CustomPagingHeaders_returnsPageable() throws Exception {
    // given: some Paging info
    int pageNumber = 3;
    int pageSize = 10;

    // when: test-request-pageable controller with @RequestPageable annotations with
    // customized header names
    mockMvc
        .perform(MockMvcRequestBuilders.get("/test-request-pageable-custom-headers").header("XX-P", pageNumber)
            .header("XX-S", pageSize).accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // then: sent paging header data is returned as Pageable object
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("pageNumber").value(pageNumber))
        .andExpect(MockMvcResultMatchers.jsonPath("pageSize").value(pageSize));
  }

  @Test
  @WithMockUser
  void responsePaginatedData_returnsDataAndPagingHeaders() throws Exception {
    // given: a data array
    String[] data = new String[] { "one", "two", "tree" };

    // when: call a controller method that returns the data as Page object
    MvcResult requestResult = mockMvc
        .perform(MockMvcRequestBuilders.get("/test-response-page").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(data)))
        .andDo(MockMvcResultHandlers.print())
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

  /**
   * A nested @Configuration class will be used instead of the application’s
   * primary configuration.
   * <p>
   * Unlike a nested @Configuration class, which would be used instead of your
   * application’s primary configuration, a nested @TestConfiguration class is
   * used in addition to your application’s primary configuration.
   */
  @Configuration
  public static class WebConfigTest extends SgiWebConfig {
    @RestController
    public static class InnerWebConfigTestController {
      @GetMapping("/test-request-pageable")
      Pageable testPageable(@RequestPageable Pageable paging) {
        return paging;
      }

      @GetMapping("/test-request-pageable-custom-headers")
      Pageable testPageableCustomHeaders(
          @RequestPageable(pageHeader = "XX-P", pageSizeHeader = "XX-S") Pageable paging) {
        return paging;
      }

      @GetMapping("/test-response-page")
      Page<String> testPage(@RequestBody String[] data) {
        Page<String> page = new PageImpl<String>(Arrays.asList(data));
        return page;
      }
    }
  }

}