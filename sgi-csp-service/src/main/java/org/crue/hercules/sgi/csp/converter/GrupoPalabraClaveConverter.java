package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.GrupoPalabraClaveInput;
import org.crue.hercules.sgi.csp.dto.GrupoPalabraClaveOutput;
import org.crue.hercules.sgi.csp.model.GrupoPalabraClave;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoPalabraClaveConverter {

  private final ModelMapper modelMapper;

  public GrupoPalabraClave convert(GrupoPalabraClaveInput input) {
    GrupoPalabraClave entity = modelMapper.map(input, GrupoPalabraClave.class);
    entity.setId(null);
    return entity;
  }

  public List<GrupoPalabraClave> convertGrupoPalabrasClaveInput(List<GrupoPalabraClaveInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }

  public Page<GrupoPalabraClaveOutput> convert(Page<GrupoPalabraClave> page) {
    return page.map(this::convert);
  }

  public List<GrupoPalabraClaveOutput> convertGrupoPalabrasClave(List<GrupoPalabraClave> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public GrupoPalabraClaveOutput convert(GrupoPalabraClave entity) {
    return modelMapper.map(entity, GrupoPalabraClaveOutput.class);
  }

}
