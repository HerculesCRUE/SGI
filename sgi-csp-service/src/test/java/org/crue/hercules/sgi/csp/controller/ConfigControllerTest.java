package org.crue.hercules.sgi.csp.controller;

import org.crue.hercules.sgi.csp.config.SgiConfigProperties;
import org.crue.hercules.sgi.csp.dto.ConfigParamOutput;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.service.ConfiguracionService;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ConfigControllerTest
 */
@WebMvcTest(ConfigController.class)
class ConfigControllerTest extends BaseControllerTest {

  @MockBean
  private ConfiguracionService service;

  @MockBean
  private SgiConfigProperties sgiConfigProperties;

  private static final String PATH_PARAMETER_NAME = "/{name}";
  private static final String CONTROLLER_BASE_PATH = "/config";

  @Test
  @WithMockUser(username = "user", authorities = { "ADM-CNF-E" })
  void update_WithExistingName_ReturnsConfigParamOutput() throws Exception {
    // given: existing Config
    ConfigParamOutput config = ConfigParamOutput.builder().name("name").value("value").build();

    BDDMockito.given(service.updateValue(ArgumentMatchers.<String>any(), ArgumentMatchers.<String>any()))
        .willReturn(config);

    // when: update Config
    mockMvc
        .perform(MockMvcRequestBuilders
            .patch(CONTROLLER_BASE_PATH + PATH_PARAMETER_NAME, config.getName())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(config.getValue())))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: Config is updated
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("name").value(config.getName()))
        .andExpect(
            MockMvcResultMatchers.jsonPath("value").value(config.getValue()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ADM-CNF-E" })
  void get_WithExistingName_ReturnsConfigParamOutput() throws Exception {
    // given: existing config
    ConfigParamOutput config = ConfigParamOutput.builder().name("name").value("value").build();
    BDDMockito.given(service.get(ArgumentMatchers.<String>any())).willReturn(config);

    // when: find by existing name
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_NAME, 1L)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: response is OK
        .andExpect(MockMvcResultMatchers.status().isOk())
        // and the requested config is resturned as JSON object
        .andExpect(MockMvcResultMatchers.jsonPath("name").value("name"))
        .andExpect(MockMvcResultMatchers.jsonPath("value").value("value"));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ADM-CNF-E" })
  void get_WithNoExistingName_Returns404() throws Exception {
    // given: no existing config name
    String name = "no";
    BDDMockito.given(service.get(ArgumentMatchers.anyString())).will((InvocationOnMock invocation) -> {
      throw new ConvocatoriaNotFoundException(null);
    });

    // when: find by non existing convocatoriaId
    mockMvc
        .perform(MockMvcRequestBuilders.get(CONTROLLER_BASE_PATH + PATH_PARAMETER_NAME, name)
            .with(SecurityMockMvcRequestPostProcessors.csrf()).accept(MediaType.APPLICATION_JSON))
        .andDo(SgiMockMvcResultHandlers.printOnError()).
        // then: HTTP code 404 NotFound pressent
        andExpect(MockMvcResultMatchers.status().isNotFound());
  }

}
