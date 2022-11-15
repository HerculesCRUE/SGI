package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionSeguimientoInput;
import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoJustificacionSeguimientoOutput;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacionSeguimiento;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProyectoPeriodoJustificacionSeguimientoConverter {
  private final ModelMapper modelMapper;

  public ProyectoPeriodoJustificacionSeguimientoOutput convert(ProyectoPeriodoJustificacionSeguimiento entity) {
    return modelMapper.map(entity, ProyectoPeriodoJustificacionSeguimientoOutput.class);
  }

  public ProyectoPeriodoJustificacionSeguimiento convert(ProyectoPeriodoJustificacionSeguimientoInput entity) {
    return convert(entity, null);
  }

  public ProyectoPeriodoJustificacionSeguimiento convert(ProyectoPeriodoJustificacionSeguimientoInput entity, Long id) {
    ProyectoPeriodoJustificacionSeguimiento requerimientoJustificacion = modelMapper.map(
        entity, ProyectoPeriodoJustificacionSeguimiento.class);
    requerimientoJustificacion.setId(id);
    return requerimientoJustificacion;
  }
}
