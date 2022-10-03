package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.AlegacionRequerimientoInput;
import org.crue.hercules.sgi.csp.dto.AlegacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.model.AlegacionRequerimiento;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlegacionRequerimientoConverter {
  private final ModelMapper modelMapper;

  public AlegacionRequerimientoOutput convert(AlegacionRequerimiento entity) {
    return modelMapper.map(entity, AlegacionRequerimientoOutput.class);
  }

  public AlegacionRequerimiento convert(AlegacionRequerimientoInput inputDto) {
    return convert(inputDto, null);
  }

  public AlegacionRequerimiento convert(AlegacionRequerimientoInput inputDto,
      Long id) {
    AlegacionRequerimiento entity = modelMapper.map(inputDto, AlegacionRequerimiento.class);
    entity.setId(id);
    return entity;
  }
}
