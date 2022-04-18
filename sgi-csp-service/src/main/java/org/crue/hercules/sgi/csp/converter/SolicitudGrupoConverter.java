package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.SolicitudGrupoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudGrupoOutput;
import org.crue.hercules.sgi.csp.model.SolicitudGrupo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SolicitudGrupoConverter {

  private final ModelMapper modelMapper;

  public SolicitudGrupo convert(SolicitudGrupoInput input) {
    return convert(null, input);
  }

  public SolicitudGrupo convert(Long id, SolicitudGrupoInput input) {
    SolicitudGrupo solicitudGrupo = modelMapper.map(input, SolicitudGrupo.class);
    solicitudGrupo.setId(id);
    return solicitudGrupo;
  }

  public SolicitudGrupoOutput convert(SolicitudGrupo entity) {
    return modelMapper.map(entity, SolicitudGrupoOutput.class);
  }

  public Page<SolicitudGrupoOutput> convert(Page<SolicitudGrupo> page) {
    return page.map(this::convert);
  }

}
