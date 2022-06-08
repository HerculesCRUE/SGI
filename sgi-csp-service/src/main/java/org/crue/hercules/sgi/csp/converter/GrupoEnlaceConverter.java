package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.GrupoEnlaceInput;
import org.crue.hercules.sgi.csp.dto.GrupoEnlaceOutput;
import org.crue.hercules.sgi.csp.model.GrupoEnlace;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoEnlaceConverter {

  private final ModelMapper modelMapper;

  public GrupoEnlace convert(GrupoEnlaceInput input) {
    return convert(input.getId(), input);
  }

  public GrupoEnlace convert(Long id, GrupoEnlaceInput input) {
    GrupoEnlace grupo = modelMapper.map(input, GrupoEnlace.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoEnlaceOutput convert(GrupoEnlace entity) {
    return modelMapper.map(entity, GrupoEnlaceOutput.class);
  }

  public Page<GrupoEnlaceOutput> convert(Page<GrupoEnlace> page) {
    return page.map(this::convert);
  }

  public List<GrupoEnlaceOutput> convert(List<GrupoEnlace> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<GrupoEnlaceOutput> convertGrupoEnlaces(List<GrupoEnlace> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<GrupoEnlace> convertGrupoEnlaceInput(
      List<GrupoEnlaceInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
