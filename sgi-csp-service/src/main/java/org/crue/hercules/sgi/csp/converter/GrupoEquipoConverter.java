package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.GrupoEquipoUpdateInput;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoCreateInput;
import org.crue.hercules.sgi.csp.dto.GrupoEquipoOutput;
import org.crue.hercules.sgi.csp.model.GrupoEquipo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoEquipoConverter {

  private final ModelMapper modelMapper;

  public GrupoEquipo convert(GrupoEquipoUpdateInput input) {
    return convert(input.getId(), input);
  }

  public GrupoEquipo convert(GrupoEquipoCreateInput input) {
    return convert(null, input);
  }

  public GrupoEquipo convert(Long id, GrupoEquipoUpdateInput input) {
    GrupoEquipo grupo = modelMapper.map(input, GrupoEquipo.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoEquipo convert(Long id, GrupoEquipoCreateInput input) {
    GrupoEquipo grupo = modelMapper.map(input, GrupoEquipo.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoEquipoOutput convert(GrupoEquipo entity) {
    return modelMapper.map(entity, GrupoEquipoOutput.class);
  }

  public Page<GrupoEquipoOutput> convert(Page<GrupoEquipo> page) {
    return page.map(this::convert);
  }

  public List<GrupoEquipoOutput> convert(List<GrupoEquipo> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<GrupoEquipoOutput> convertGrupoEquipos(List<GrupoEquipo> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<GrupoEquipo> convertGrupoEquipoInput(List<GrupoEquipoUpdateInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
