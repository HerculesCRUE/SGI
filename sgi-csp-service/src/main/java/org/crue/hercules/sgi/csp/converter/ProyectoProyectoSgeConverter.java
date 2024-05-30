package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.ProyectoProyectoSgeInput;
import org.crue.hercules.sgi.csp.dto.ProyectoProyectoSgeOutput;
import org.crue.hercules.sgi.csp.model.ProyectoProyectoSge;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProyectoProyectoSgeConverter {
  private final ModelMapper modelMapper;

  public ProyectoProyectoSgeOutput convert(ProyectoProyectoSge entity) {
    return modelMapper.map(entity, ProyectoProyectoSgeOutput.class);
  }

  public ProyectoProyectoSge convert(ProyectoProyectoSgeInput input, Long id) {
    ProyectoProyectoSge proyectoProyectoSge = modelMapper.map(input, ProyectoProyectoSge.class);
    if (proyectoProyectoSge != null) {
      proyectoProyectoSge.setId(id);
    }
    return proyectoProyectoSge;
  }

  public ProyectoProyectoSge convert(ProyectoProyectoSgeInput input) {
    return convert(input, null);
  }

  public Page<ProyectoProyectoSgeOutput> convert(Page<ProyectoProyectoSge> page) {
    return page.map(this::convert);
  }
}
