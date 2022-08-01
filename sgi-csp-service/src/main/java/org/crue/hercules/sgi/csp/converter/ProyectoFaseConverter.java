package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.ProyectoFaseInput;
import org.crue.hercules.sgi.csp.dto.ProyectoFaseOutput;
import org.crue.hercules.sgi.csp.model.ProyectoFase;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProyectoFaseConverter {
  private final ModelMapper modelMapper;

  public ProyectoFaseOutput convert(ProyectoFase entity) {
    return modelMapper.map(entity, ProyectoFaseOutput.class);
  }

  public ProyectoFase convert(ProyectoFaseInput input, Long id) {
    ProyectoFase convocatoriaFase = modelMapper.map(input, ProyectoFase.class);
    if (id != null) {
      convocatoriaFase.setId(id);
    }
    return convocatoriaFase;
  }

  public Page<ProyectoFaseOutput> convert(Page<ProyectoFase> page) {
    return page.map(this::convert);
  }
}
