package org.crue.hercules.sgi.eti.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.dto.RespuestaOutput;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RespuestaConverter {

  private final ModelMapper modelMapper;

  public RespuestaOutput convert(Respuesta entity) {
    return modelMapper.map(entity, RespuestaOutput.class);
  }

  public List<RespuestaOutput> convert(List<Respuesta> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());

  }

}
