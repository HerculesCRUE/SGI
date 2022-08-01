package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.RequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.RequerimientoJustificacion;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequerimientoJustificacionConverter {
  private final ModelMapper modelMapper;

  public RequerimientoJustificacionOutput convert(RequerimientoJustificacion entity) {
    return modelMapper.map(entity, RequerimientoJustificacionOutput.class);
  }

  public Page<RequerimientoJustificacionOutput> convert(Page<RequerimientoJustificacion> page) {
    return page.map(this::convert);
  }
}
