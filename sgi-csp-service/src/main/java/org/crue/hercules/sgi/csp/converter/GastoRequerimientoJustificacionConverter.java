package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.GastoRequerimientoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.GastoRequerimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.GastoRequerimientoJustificacion;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GastoRequerimientoJustificacionConverter {
  private final ModelMapper modelMapper;

  public GastoRequerimientoJustificacionOutput convert(GastoRequerimientoJustificacion entity) {
    return modelMapper.map(entity, GastoRequerimientoJustificacionOutput.class);
  }

  public Page<GastoRequerimientoJustificacionOutput> convert(Page<GastoRequerimientoJustificacion> page) {
    return page.map(this::convert);
  }

  public GastoRequerimientoJustificacion convert(GastoRequerimientoJustificacionInput entity) {
    return convert(entity, null);
  }

  public GastoRequerimientoJustificacion convert(GastoRequerimientoJustificacionInput entity, Long id) {
    GastoRequerimientoJustificacion requerimientoJustificacion = modelMapper.map(
        entity, GastoRequerimientoJustificacion.class);
    requerimientoJustificacion.setId(id);
    return requerimientoJustificacion;
  }
}
