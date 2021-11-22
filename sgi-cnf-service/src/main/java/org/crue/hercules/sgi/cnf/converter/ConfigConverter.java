package org.crue.hercules.sgi.cnf.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.cnf.dto.ConfigOutput;
import org.crue.hercules.sgi.cnf.dto.CreateConfigInput;
import org.crue.hercules.sgi.cnf.dto.UpdateConfigInput;
import org.crue.hercules.sgi.cnf.model.Config;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

/**
 * Utility class for {@link Config} conversion fron/to Input/Output DTOs.
 */
@Component
public class ConfigConverter {
  private ModelMapper modelMapper;

  /**
   * Constructor
   * 
   * @param modelMapper the model mapper used for the conversion
   */
  @Autowired
  private ConfigConverter(ModelMapper modelMapper) {
    this.modelMapper = modelMapper;
  }

  /**
   * Converts from {@link UpdateConfigInput} to {@link Config}.
   * 
   * @param name  the name of the configuration
   * @param input the {@link UpdateConfigInput} to be converted
   * @return the converted {@link Config}
   */
  public Config convert(String name, UpdateConfigInput input) {
    Config returnValue = modelMapper.map(input, Config.class);
    returnValue.setName(name);
    return returnValue;
  }

  /**
   * Converts from {@link CreateConfigInput} to {@link Config}.
   * 
   * @param input the {@link CreateConfigInput} to be converted
   * @return the converted {@link Config}
   */
  public Config convert(CreateConfigInput input) {
    return modelMapper.map(input, Config.class);
  }

  /**
   * Converts from {@link Config} to {@link ConfigOutput}.
   * 
   * @param config the {@link Config} to be converted
   * @return the converted {@link ConfigOutput}
   */
  public ConfigOutput convert(Config config) {
    return modelMapper.map(config, ConfigOutput.class);
  }

  /**
   * Converts a {@link Page} of {@link Config} to a {@link Page} of
   * {@link ConfigOutput}.
   * 
   * @param page the {@link Page} of {@link Config} to be converted
   * @return the converted {@link Page} of {@link ConfigOutput}
   */
  public Page<ConfigOutput> convert(Page<Config> page) {
    List<ConfigOutput> content = page.getContent().stream().map(this::convert).collect(Collectors.toList());
    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }
}
