package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.SolicitanteExternoInput;
import org.crue.hercules.sgi.csp.dto.SolicitanteExternoOutput;
import org.crue.hercules.sgi.csp.model.SolicitanteExterno;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SolicitanteExternoConverter {

  private final ModelMapper modelMapper;

  public SolicitanteExterno convert(SolicitanteExternoInput input) {
    return convert(null, input);
  }

  public SolicitanteExterno convert(Long id, SolicitanteExternoInput input) {
    SolicitanteExterno entity = modelMapper.map(input, SolicitanteExterno.class);
    entity.setId(id);
    return entity;
  }

  public SolicitanteExternoOutput convert(SolicitanteExterno entity) {
    return modelMapper.map(entity, SolicitanteExternoOutput.class);
  }

}
