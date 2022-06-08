package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.GrupoLineaInvestigacionInput;
import org.crue.hercules.sgi.csp.dto.GrupoLineaInvestigacionOutput;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigacion;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoLineaInvestigacionConverter {

  private final ModelMapper modelMapper;

  public GrupoLineaInvestigacion convert(GrupoLineaInvestigacionInput input) {
    return convert(null, input);
  }

  public GrupoLineaInvestigacion convert(Long id, GrupoLineaInvestigacionInput input) {
    GrupoLineaInvestigacion grupo = modelMapper.map(input, GrupoLineaInvestigacion.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoLineaInvestigacionOutput convert(GrupoLineaInvestigacion entity) {
    return modelMapper.map(entity, GrupoLineaInvestigacionOutput.class);
  }

  public Page<GrupoLineaInvestigacionOutput> convert(Page<GrupoLineaInvestigacion> page) {
    return page.map(this::convert);
  }

  public List<GrupoLineaInvestigacionOutput> convert(List<GrupoLineaInvestigacion> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<GrupoLineaInvestigacionOutput> convertGrupoLineaInvestigacions(
      List<GrupoLineaInvestigacion> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<GrupoLineaInvestigacion> convertGrupoLineaInvestigacionInput(
      List<GrupoLineaInvestigacionInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
