package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.ProyectoPeriodoSeguimientoOutput;
import org.crue.hercules.sgi.csp.model.ProyectoPeriodoSeguimiento;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProyectoPeriodoSeguimientoConverter {
  private final ModelMapper modelMapper;

  public Page<ProyectoPeriodoSeguimientoOutput> convert(Page<ProyectoPeriodoSeguimiento> page) {
    List<ProyectoPeriodoSeguimientoOutput> content = page.getContent().stream()
        .map(this::convert).collect(Collectors.toList());

    return new PageImpl<>(content, page.getPageable(), page.getTotalElements());
  }

  public ProyectoPeriodoSeguimientoOutput convert(ProyectoPeriodoSeguimiento input) {
    return modelMapper.map(input, ProyectoPeriodoSeguimientoOutput.class);
  }
}
