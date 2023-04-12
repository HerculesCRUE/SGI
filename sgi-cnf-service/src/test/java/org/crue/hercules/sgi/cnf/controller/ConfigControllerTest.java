package org.crue.hercules.sgi.cnf.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import org.crue.hercules.sgi.cnf.config.SgiConfigProperties;
import org.crue.hercules.sgi.cnf.converter.ConfigConverter;
import org.crue.hercules.sgi.cnf.dto.CreateConfigInput;
import org.crue.hercules.sgi.cnf.model.Config;
import org.crue.hercules.sgi.cnf.model.Config_;
import org.crue.hercules.sgi.cnf.service.ConfigService;
import org.crue.hercules.sgi.framework.exception.NotFoundException;
import org.crue.hercules.sgi.framework.test.web.servlet.result.SgiMockMvcResultHandlers;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * ConfigControllerTest
 */
@WebMvcTest(value = ConfigController.class, properties = "sgi.time-zone=" + ConfigControllerTest.TIMEZONE_LONDON)
@EnableConfigurationProperties(value = SgiConfigProperties.class)
// Since WebMvcTest is only sliced controller layer for the testing, it would
// not take the ConfigConverter.
@Import(ConfigConverter.class)
class ConfigControllerTest extends BaseControllerTest {
  static final String TIMEZONE_LONDON = "Europe/London";

  @MockBean
  private ConfigService service;

  @Autowired
  private ModelMapper modelMapper;

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

  @Test
  @WithMockUser(username = "user", authorities = {})
  void getConfig_ReturnsConfig() throws Exception {
    // given: existing Config
    Config config = Config.builder().name("test-config").description("Test config description")
        .value("Test config value").build();
    BDDMockito.given(service.get(config.getName())).willReturn(config);
    // when: get existing Config
    mockMvc
        .perform(MockMvcRequestBuilders.get(ConfigController.MAPPING + ConfigController.PATH_NAME, config.getName())
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns the existing Config
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath(Config_.NAME).value(config.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath(Config_.DESCRIPTION).value(config.getDescription()))
        .andExpect(MockMvcResultMatchers.jsonPath(Config_.VALUE).value(config.getValue()));
  }

  @Test
  @WithMockUser(username = "user", authorities = {})
  void getConfig_NotExisting_ReturnsNotFound() throws Exception {
    // given: non existing Config
    String name = "test-config";
    BDDMockito.given(service.get(ArgumentMatchers.anyString())).will(invocation -> {
      throw new NotFoundException(invocation.getArgument(0));
    });
    // when: get non existing Config
    mockMvc
        .perform(MockMvcRequestBuilders.get(ConfigController.MAPPING + ConfigController.PATH_NAME, name)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 404-Not found
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ADM-CNF-E" })
  void creteConfig_ReturnsConfig() throws Exception {
    // given: new Config
    Config config = Config.builder().name("test-config").description("Test config description")
        .value("Test config value").build();
    BDDMockito.given(service.create(ArgumentMatchers.<Config>any())).will(invocation -> {
      return invocation.getArgument(0);
    });
    // when: post the Config
    mockMvc
        .perform(MockMvcRequestBuilders.post(ConfigController.MAPPING).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modelMapper.map(config, CreateConfigInput.class))))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns the new Config
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath(Config_.NAME).value(config.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath(Config_.DESCRIPTION).value(config.getDescription()))
        .andExpect(MockMvcResultMatchers.jsonPath(Config_.VALUE).value(config.getValue()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ADM-CNF-E" })
  void creteConfig_WithExistingName_ReturnsBadRequest() throws Exception {
    // given: existing Config name
    Config config = Config.builder().name("test-config").description("Test config description")
        .value("Test config value").build();
    BDDMockito.given(service.create(ArgumentMatchers.<Config>any())).will(invocation -> {
      String messageTemplate = "";
      Map<String, Object> messageParameters = null;
      Map<String, Object> expressionVariables = null;
      String interpolatedMessage = "";
      Class<Config> rootBeanClass = Config.class;
      Config rootBean = (Config) invocation.getArgument(0);
      Object leafBeanInstance = null;
      Object value = ((Config) invocation.getArgument(0)).getName();
      Path propertyPath = PathImpl.createPathFromString("name");
      ConstraintDescriptor<?> constraintDescriptor = null;
      Object dynamicPayload = null;

      ConstraintViolation<Config> constraintViolation = ConstraintViolationImpl.<Config>forBeanValidation(
          messageTemplate, messageParameters, expressionVariables, interpolatedMessage, rootBeanClass, rootBean,
          leafBeanInstance, value, propertyPath, constraintDescriptor, dynamicPayload);

      List<ConstraintViolation<Config>> cnfList = Arrays.asList(constraintViolation);
      Set<ConstraintViolation<Config>> cnfSet = new HashSet<>(cnfList);
      throw new ConstraintViolationException(cnfSet);
    });

    // when: post the Config
    mockMvc
        .perform(MockMvcRequestBuilders.post(ConfigController.MAPPING).with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modelMapper.map(config, CreateConfigInput.class))))
        .andDo(SgiMockMvcResultHandlers.printOnError())
        // then: returns 400-Bad Request
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ADM-CNF-E" })
  void updateConfig_ReturnsConfig() throws Exception {
    Config config = Config.builder().name("test-config").description("Test config description")
        .value("Test config value").build();
    BDDMockito.given(service.update(ArgumentMatchers.<Config>any())).will(invocation -> {
      return invocation.getArgument(0);
    });

    mockMvc
        .perform(MockMvcRequestBuilders.put(ConfigController.MAPPING + ConfigController.PATH_NAME, config.getName())
            .with(SecurityMockMvcRequestPostProcessors.csrf()).contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modelMapper.map(config, CreateConfigInput.class))))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath(Config_.NAME).value(config.getName()))
        .andExpect(MockMvcResultMatchers.jsonPath(Config_.DESCRIPTION).value(config.getDescription()))
        .andExpect(MockMvcResultMatchers.jsonPath(Config_.VALUE).value(config.getValue()));
  }

  @Test
  @WithMockUser(username = "user", authorities = { "ADM-CNF-E" })
  void deleteConfig_ReturnsNothing() throws Exception {
    String name = "test-config";

    mockMvc
        .perform(MockMvcRequestBuilders.delete(ConfigController.MAPPING + ConfigController.PATH_NAME, name)
            .with(SecurityMockMvcRequestPostProcessors.csrf()))
        .andDo(SgiMockMvcResultHandlers.printOnError()).andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
