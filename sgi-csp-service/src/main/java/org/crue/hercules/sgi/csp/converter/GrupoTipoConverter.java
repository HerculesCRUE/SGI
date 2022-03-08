package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.GrupoTipoOutput;
import org.crue.hercules.sgi.csp.model.GrupoTipo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoTipoConverter {

  private final ModelMapper modelMapper;

  public GrupoTipoOutput convert(GrupoTipo entity) {
    return modelMapper.map(entity, GrupoTipoOutput.class);
  }

  public Page<GrupoTipoOutput> convert(Page<GrupoTipo> page) {
    return page.map(this::convert);
  }

}
