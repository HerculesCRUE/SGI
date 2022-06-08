package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.GrupoLineaClasificacionInput;
import org.crue.hercules.sgi.csp.dto.GrupoLineaClasificacionOutput;
import org.crue.hercules.sgi.csp.model.GrupoLineaClasificacion;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoLineaClasificacionConverter {

  private final ModelMapper modelMapper;

  public GrupoLineaClasificacion convert(GrupoLineaClasificacionInput input) {
    return convert(null, input);
  }

  public GrupoLineaClasificacion convert(Long id, GrupoLineaClasificacionInput input) {
    GrupoLineaClasificacion grupo = modelMapper.map(input, GrupoLineaClasificacion.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoLineaClasificacionOutput convert(GrupoLineaClasificacion entity) {
    return modelMapper.map(entity, GrupoLineaClasificacionOutput.class);
  }

  public Page<GrupoLineaClasificacionOutput> convert(Page<GrupoLineaClasificacion> page) {
    return page.map(this::convert);
  }

  public List<GrupoLineaClasificacionOutput> convert(List<GrupoLineaClasificacion> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<GrupoLineaClasificacionOutput> convertGrupoLineaClasificacions(List<GrupoLineaClasificacion> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<GrupoLineaClasificacion> convertGrupoLineaClasificacionInput(
      List<GrupoLineaClasificacionInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
