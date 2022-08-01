package org.crue.hercules.sgi.eer.converter;

import org.crue.hercules.sgi.eer.dto.EmpresaDocumentoInput;
import org.crue.hercules.sgi.eer.dto.EmpresaDocumentoOutput;
import org.crue.hercules.sgi.eer.model.EmpresaDocumento;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmpresaDocumentoConverter {

  private final ModelMapper modelMapper;

  public EmpresaDocumento convert(EmpresaDocumentoInput source) {
    return convert(null, source);
  }

  public EmpresaDocumento convert(Long id, EmpresaDocumentoInput source) {
    EmpresaDocumento target = modelMapper.map(source, EmpresaDocumento.class);
    target.setId(id);
    return target;
  }

  public EmpresaDocumentoOutput convert(EmpresaDocumento source) {
    EmpresaDocumentoOutput target = modelMapper.map(source, EmpresaDocumentoOutput.class);
    return target;
  }

  public Page<EmpresaDocumentoOutput> convert(Page<EmpresaDocumento> page) {
    return page.map(this::convert);
  }
}
