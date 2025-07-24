package org.crue.hercules.sgi.eti.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.dto.InformeDocumentoOutput;
import org.crue.hercules.sgi.eti.model.Informe;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InformeConverter {

  private final ModelMapper modelMapper;

  public InformeDocumentoOutput convert(Informe entity) {
    InformeDocumentoOutput informe = modelMapper.map(entity, InformeDocumentoOutput.class);
    return informe;
  }

  public Page<InformeDocumentoOutput> convertPage(Page<Informe> page) {
    return page.map(item -> convert(item));
  }

  public List<InformeDocumentoOutput> convertList(List<Informe> list) {
    return list.stream().map(item -> convert(item)).collect(Collectors.toList());

  }

}
