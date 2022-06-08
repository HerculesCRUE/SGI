package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.GrupoResponsableEconomicoInput;
import org.crue.hercules.sgi.csp.dto.GrupoResponsableEconomicoOutput;
import org.crue.hercules.sgi.csp.model.GrupoResponsableEconomico;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoResponsableEconomicoConverter {

  private final ModelMapper modelMapper;

  public GrupoResponsableEconomico convert(GrupoResponsableEconomicoInput input) {
    return convert(input.getId(), input);
  }

  public GrupoResponsableEconomico convert(Long id, GrupoResponsableEconomicoInput input) {
    GrupoResponsableEconomico grupo = modelMapper.map(input, GrupoResponsableEconomico.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoResponsableEconomicoOutput convert(GrupoResponsableEconomico entity) {
    return modelMapper.map(entity, GrupoResponsableEconomicoOutput.class);
  }

  public Page<GrupoResponsableEconomicoOutput> convert(Page<GrupoResponsableEconomico> page) {
    return page.map(this::convert);
  }

  public List<GrupoResponsableEconomicoOutput> convert(List<GrupoResponsableEconomico> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<GrupoResponsableEconomicoOutput> convertGrupoResponsableEconomicos(
      List<GrupoResponsableEconomico> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<GrupoResponsableEconomico> convertGrupoResponsableEconomicoInput(
      List<GrupoResponsableEconomicoInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
