package org.crue.hercules.sgi.eer.converter;

import org.crue.hercules.sgi.eer.dto.EmpresaInput;
import org.crue.hercules.sgi.eer.dto.EmpresaOutput;
import org.crue.hercules.sgi.eer.model.Empresa;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmpresaConverter {

  private final ModelMapper modelMapper;

  public Empresa convert(EmpresaInput input) {
    return convert(null, input);
  }

  public Empresa convert(Long id, EmpresaInput input) {
    Empresa empresa = modelMapper.map(input, Empresa.class);
    empresa.setId(id);
    return empresa;
  }

  public EmpresaOutput convert(Empresa entity) {
    EmpresaOutput empresaOutput = modelMapper.map(entity, EmpresaOutput.class);
    return empresaOutput;
  }

  public Page<EmpresaOutput> convert(Page<Empresa> page) {
    return page.map(this::convert);
  }

}
