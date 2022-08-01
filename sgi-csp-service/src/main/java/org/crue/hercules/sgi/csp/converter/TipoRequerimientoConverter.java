package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.TipoRequerimientoOutput;
import org.crue.hercules.sgi.csp.model.TipoRequerimiento;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TipoRequerimientoConverter {

  private final ModelMapper modelMapper;

  public TipoRequerimientoOutput convert(TipoRequerimiento entity) {
    return modelMapper.map(entity, TipoRequerimientoOutput.class);
  }

  public Page<TipoRequerimientoOutput> convert(Page<TipoRequerimiento> page) {
    return page.map(this::convert);
  }
}
