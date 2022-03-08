package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.GrupoEspecialInvestigacionOutput;
import org.crue.hercules.sgi.csp.model.GrupoEspecialInvestigacion;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoEspecialInvestigacionConverter {

  private final ModelMapper modelMapper;

  public GrupoEspecialInvestigacionOutput convert(GrupoEspecialInvestigacion entity) {
    return modelMapper.map(entity, GrupoEspecialInvestigacionOutput.class);
  }

  public Page<GrupoEspecialInvestigacionOutput> convert(Page<GrupoEspecialInvestigacion> page) {
    return page.map(this::convert);
  }

}
