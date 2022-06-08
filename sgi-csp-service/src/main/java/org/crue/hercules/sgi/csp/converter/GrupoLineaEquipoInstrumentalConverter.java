package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.GrupoLineaEquipoInstrumentalInput;
import org.crue.hercules.sgi.csp.dto.GrupoLineaEquipoInstrumentalOutput;
import org.crue.hercules.sgi.csp.model.GrupoLineaEquipoInstrumental;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoLineaEquipoInstrumentalConverter {

  private final ModelMapper modelMapper;

  public GrupoLineaEquipoInstrumental convert(GrupoLineaEquipoInstrumentalInput input) {
    return convert(null, input);
  }

  public GrupoLineaEquipoInstrumental convert(Long id, GrupoLineaEquipoInstrumentalInput input) {
    GrupoLineaEquipoInstrumental grupo = modelMapper.map(input, GrupoLineaEquipoInstrumental.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoLineaEquipoInstrumentalOutput convert(GrupoLineaEquipoInstrumental entity) {
    return modelMapper.map(entity, GrupoLineaEquipoInstrumentalOutput.class);
  }

  public Page<GrupoLineaEquipoInstrumentalOutput> convert(Page<GrupoLineaEquipoInstrumental> page) {
    return page.map(this::convert);
  }

  public List<GrupoLineaEquipoInstrumentalOutput> convert(List<GrupoLineaEquipoInstrumental> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<GrupoLineaEquipoInstrumentalOutput> convertGrupoLineaEquipoInstrumentals(
      List<GrupoLineaEquipoInstrumental> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<GrupoLineaEquipoInstrumental> convertGrupoLineaEquipoInstrumentalInput(
      List<GrupoLineaEquipoInstrumentalInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
