package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.GrupoEquipoInstrumentalInput;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoInstrumentalOutput;
import org.crue.hercules.sgi.csp.model.GrupoEquipoInstrumental;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoEquipoInstrumentalConverter {

  private final ModelMapper modelMapper;

  public GrupoEquipoInstrumental convert(GrupoEquipoInstrumentalInput input) {
    return convert(input.getId(), input);
  }

  public GrupoEquipoInstrumental convert(Long id, GrupoEquipoInstrumentalInput input) {
    GrupoEquipoInstrumental grupo = modelMapper.map(input, GrupoEquipoInstrumental.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoEquipoInstrumentalOutput convert(GrupoEquipoInstrumental entity) {
    return modelMapper.map(entity, GrupoEquipoInstrumentalOutput.class);
  }

  public Page<GrupoEquipoInstrumentalOutput> convert(Page<GrupoEquipoInstrumental> page) {
    return page.map(this::convert);
  }

  public List<GrupoEquipoInstrumentalOutput> convert(List<GrupoEquipoInstrumental> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<GrupoEquipoInstrumentalOutput> convertGrupoEquipoInstrumentals(List<GrupoEquipoInstrumental> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<GrupoEquipoInstrumental> convertGrupoEquipoInstrumentalInput(
      List<GrupoEquipoInstrumentalInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
