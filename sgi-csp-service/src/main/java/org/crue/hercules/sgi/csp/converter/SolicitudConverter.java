package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.SolicitudResumenOutput;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SolicitudConverter {

  private final ModelMapper modelMapper;

  public SolicitudResumenOutput convertResumenOutput(Solicitud entity) {
    return modelMapper.map(entity, SolicitudResumenOutput.class);
  }

}
