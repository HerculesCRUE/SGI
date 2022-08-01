package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseInput;
import org.crue.hercules.sgi.csp.dto.ConvocatoriaFaseOutput;
import org.crue.hercules.sgi.csp.model.ConvocatoriaFase;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConvocatoriaFaseConverter {
  private final ModelMapper modelMapper;

  public ConvocatoriaFaseOutput convert(ConvocatoriaFase entity) {
    return modelMapper.map(entity, ConvocatoriaFaseOutput.class);
  }

  public ConvocatoriaFase convert(ConvocatoriaFaseInput input, Long id) {
    ConvocatoriaFase convocatoriaFase = modelMapper.map(input, ConvocatoriaFase.class);
    if (id != null) {
      convocatoriaFase.setId(id);
    }
    return convocatoriaFase;
  }

  public Page<ConvocatoriaFaseOutput> convert(Page<ConvocatoriaFase> page) {
    return page.map(this::convert);
  }
}
