package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.dto.GrupoLineaInvestigadorInput;
import org.crue.hercules.sgi.csp.dto.GrupoLineaInvestigadorOutput;
import org.crue.hercules.sgi.csp.model.GrupoLineaInvestigador;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GrupoLineaInvestigadorConverter {

  private final ModelMapper modelMapper;

  public GrupoLineaInvestigador convert(GrupoLineaInvestigadorInput input) {
    return convert(null, input);
  }

  public GrupoLineaInvestigador convert(Long id, GrupoLineaInvestigadorInput input) {
    GrupoLineaInvestigador grupo = modelMapper.map(input, GrupoLineaInvestigador.class);
    grupo.setId(id);
    return grupo;
  }

  public GrupoLineaInvestigadorOutput convert(GrupoLineaInvestigador entity) {
    return modelMapper.map(entity, GrupoLineaInvestigadorOutput.class);
  }

  public Page<GrupoLineaInvestigadorOutput> convert(Page<GrupoLineaInvestigador> page) {
    return page.map(this::convert);
  }

  public List<GrupoLineaInvestigadorOutput> convert(List<GrupoLineaInvestigador> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<GrupoLineaInvestigadorOutput> convertGrupoLineaInvestigadors(
      List<GrupoLineaInvestigador> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<GrupoLineaInvestigador> convertGrupoLineaInvestigadorInput(
      List<GrupoLineaInvestigadorInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
