package org.crue.hercules.sgi.eer.converter;

import org.crue.hercules.sgi.eer.dto.TipoDocumentoOutput;
import org.crue.hercules.sgi.eer.model.TipoDocumento;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TipoDocumentoConverter {

  private final ModelMapper modelMapper;

  public Page<TipoDocumentoOutput> convert(Page<TipoDocumento> page) {
    return page.map(this::convert);
  }

  public TipoDocumentoOutput convert(TipoDocumento source) {
    return modelMapper.map(source, TipoDocumentoOutput.class);
  }
}
