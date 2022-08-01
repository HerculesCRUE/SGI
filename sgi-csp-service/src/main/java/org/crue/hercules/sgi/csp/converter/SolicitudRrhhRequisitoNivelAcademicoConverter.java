package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoNivelAcademicoInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoNivelAcademicoOutput;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoNivelAcademico;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SolicitudRrhhRequisitoNivelAcademicoConverter {

  private final ModelMapper modelMapper;

  public SolicitudRrhhRequisitoNivelAcademico convert(SolicitudRrhhRequisitoNivelAcademicoInput input) {
    return convert(null, input);
  }

  public SolicitudRrhhRequisitoNivelAcademico convert(Long id, SolicitudRrhhRequisitoNivelAcademicoInput input) {
    SolicitudRrhhRequisitoNivelAcademico entity = modelMapper.map(input, SolicitudRrhhRequisitoNivelAcademico.class);
    entity.setId(id);
    return entity;
  }

  public SolicitudRrhhRequisitoNivelAcademicoOutput convert(SolicitudRrhhRequisitoNivelAcademico entity) {
    return modelMapper.map(entity, SolicitudRrhhRequisitoNivelAcademicoOutput.class);
  }

  public Page<SolicitudRrhhRequisitoNivelAcademicoOutput> convert(Page<SolicitudRrhhRequisitoNivelAcademico> page) {
    return page.map(this::convert);
  }

}
