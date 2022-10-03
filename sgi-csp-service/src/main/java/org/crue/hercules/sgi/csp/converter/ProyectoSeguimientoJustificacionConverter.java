package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionInput;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoJustificacionOutput;
import org.crue.hercules.sgi.csp.model.ProyectoSeguimientoJustificacion;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProyectoSeguimientoJustificacionConverter {
  private final ModelMapper modelMapper;

  public ProyectoSeguimientoJustificacionOutput convert(ProyectoSeguimientoJustificacion entity) {
    return modelMapper.map(entity, ProyectoSeguimientoJustificacionOutput.class);
  }

  public Page<ProyectoSeguimientoJustificacionOutput> convert(Page<ProyectoSeguimientoJustificacion> page) {
    return page.map(this::convert);
  }

  public ProyectoSeguimientoJustificacion convert(ProyectoSeguimientoJustificacionInput entity) {
    return convert(entity, null);
  }

  public ProyectoSeguimientoJustificacion convert(ProyectoSeguimientoJustificacionInput entity, Long id) {
    ProyectoSeguimientoJustificacion requerimientoJustificacion = modelMapper.map(
        entity, ProyectoSeguimientoJustificacion.class);
    requerimientoJustificacion.setId(id);
    return requerimientoJustificacion;
  }
}
