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
  private final GrupoEquipoInstrumentalNombreConverter nombreConverter;
  private final GrupoEquipoInstrumentalDescripcionConverter descripcionConverter;

  public GrupoEquipoInstrumental convert(GrupoEquipoInstrumentalInput input) {
    return convert(input.getId(), input);
  }

  public GrupoEquipoInstrumental convert(Long id, GrupoEquipoInstrumentalInput input) {
    GrupoEquipoInstrumental grupo = modelMapper.map(input, GrupoEquipoInstrumental.class);
    grupo.setId(id);
    if (input.getNombre() != null) {
      grupo.setNombre(nombreConverter.convertAll(input.getNombre()));
    }
    if (input.getDescripcion() != null) {
      grupo.setDescripcion(descripcionConverter.convertAll(input.getDescripcion()));
    }
    return grupo;
  }

  public GrupoEquipoInstrumentalOutput convert(GrupoEquipoInstrumental entity) {
    GrupoEquipoInstrumentalOutput output = modelMapper.map(entity, GrupoEquipoInstrumentalOutput.class);

    if (entity.getNombre() != null) {
      output.setNombre(nombreConverter.convertAll(entity.getNombre()));
    }
    if (entity.getDescripcion() != null) {
      output.setDescripcion(descripcionConverter.convertAll(entity.getDescripcion()));
    }

    return output;
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
