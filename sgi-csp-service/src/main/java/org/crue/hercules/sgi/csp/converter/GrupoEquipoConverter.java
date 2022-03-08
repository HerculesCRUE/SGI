package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.GrupoEquipoInput;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoOutput;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoEquipoConverter {

  private final ModelMapper modelMapper;

  public GrupoEquipo convert(GrupoEquipoInput input) {
    return convert(null, input);
  }

  public GrupoEquipo convert(Long id, GrupoEquipoInput input) {
    GrupoEquipo grupo = modelMapper.map(input, GrupoEquipo.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoEquipoOutput convert(GrupoEquipo entity) {
    return modelMapper.map(entity, GrupoEquipoOutput.class);
  }

  public Page<GrupoEquipoOutput> convert(Page<GrupoEquipo> page) {
    return page.map(this::convert);
  }

}
