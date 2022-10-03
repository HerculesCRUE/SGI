package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoAlegacionInput;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoInput;
import org.crue.hercules.sgi.csp.dto.IncidenciaDocumentacionRequerimientoOutput;
import org.crue.hercules.sgi.csp.model.IncidenciaDocumentacionRequerimiento;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IncidenciaDocumentacionRequerimientoConverter {
  private final ModelMapper modelMapper;

  public IncidenciaDocumentacionRequerimientoOutput convert(IncidenciaDocumentacionRequerimiento entity) {
    return modelMapper.map(entity, IncidenciaDocumentacionRequerimientoOutput.class);
  }

  public Page<IncidenciaDocumentacionRequerimientoOutput> convert(Page<IncidenciaDocumentacionRequerimiento> page) {
    return page.map(this::convert);
  }

  public IncidenciaDocumentacionRequerimiento convert(IncidenciaDocumentacionRequerimientoInput inputDto) {
    return convert(inputDto, null);
  }

  public IncidenciaDocumentacionRequerimiento convert(IncidenciaDocumentacionRequerimientoInput inputDto,
      Long id) {
    IncidenciaDocumentacionRequerimiento entity = modelMapper.map(inputDto, IncidenciaDocumentacionRequerimiento.class);
    entity.setId(id);
    return entity;
  }

  public IncidenciaDocumentacionRequerimiento convert(IncidenciaDocumentacionRequerimientoAlegacionInput inputDto,
      Long id) {
    IncidenciaDocumentacionRequerimiento entity = modelMapper.map(inputDto, IncidenciaDocumentacionRequerimiento.class);
    entity.setId(id);
    return entity;
  }
}
