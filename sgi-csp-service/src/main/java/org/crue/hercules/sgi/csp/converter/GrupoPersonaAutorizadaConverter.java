package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.GrupoPersonaAutorizadaInput;
import org.crue.hercules.sgi.csp.dto.GrupoPersonaAutorizadaOutput;
import org.crue.hercules.sgi.csp.model.GrupoPersonaAutorizada;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoPersonaAutorizadaConverter {

  private final ModelMapper modelMapper;

  public GrupoPersonaAutorizada convert(GrupoPersonaAutorizadaInput input) {
    return convert(input.getId(), input);
  }

  public GrupoPersonaAutorizada convert(Long id, GrupoPersonaAutorizadaInput input) {
    GrupoPersonaAutorizada grupo = modelMapper.map(input, GrupoPersonaAutorizada.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoPersonaAutorizadaOutput convert(GrupoPersonaAutorizada entity) {
    return modelMapper.map(entity, GrupoPersonaAutorizadaOutput.class);
  }

  public Page<GrupoPersonaAutorizadaOutput> convert(Page<GrupoPersonaAutorizada> page) {
    return page.map(this::convert);
  }

  public List<GrupoPersonaAutorizadaOutput> convert(List<GrupoPersonaAutorizada> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<GrupoPersonaAutorizadaOutput> convertGrupoPersonaAutorizadas(
      List<GrupoPersonaAutorizada> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<GrupoPersonaAutorizada> convertGrupoPersonaAutorizadaInput(
      List<GrupoPersonaAutorizadaInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
