package org.crue.hercules.sgi.csp.converter;

import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoCategoriaInput;
import org.crue.hercules.sgi.csp.dto.SolicitudRrhhRequisitoCategoriaOutput;
import org.crue.hercules.sgi.csp.model.SolicitudRrhhRequisitoCategoria;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SolicitudRrhhRequisitoCategoriaConverter {

  private final ModelMapper modelMapper;

  public SolicitudRrhhRequisitoCategoria convert(SolicitudRrhhRequisitoCategoriaInput input) {
    return convert(null, input);
  }

  public SolicitudRrhhRequisitoCategoria convert(Long id, SolicitudRrhhRequisitoCategoriaInput input) {
    SolicitudRrhhRequisitoCategoria entity = modelMapper.map(input, SolicitudRrhhRequisitoCategoria.class);
    entity.setId(id);
    return entity;
  }

  public SolicitudRrhhRequisitoCategoriaOutput convert(SolicitudRrhhRequisitoCategoria entity) {
    return modelMapper.map(entity, SolicitudRrhhRequisitoCategoriaOutput.class);
  }

  public Page<SolicitudRrhhRequisitoCategoriaOutput> convert(Page<SolicitudRrhhRequisitoCategoria> page) {
    return page.map(this::convert);
  }

}
