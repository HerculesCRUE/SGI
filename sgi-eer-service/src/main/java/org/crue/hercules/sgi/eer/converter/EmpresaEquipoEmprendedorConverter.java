package org.crue.hercules.sgi.eer.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eer.dto.EmpresaEquipoEmprendedorInput;
import org.crue.hercules.sgi.eer.dto.EmpresaEquipoEmprendedorOutput;
import org.crue.hercules.sgi.eer.model.EmpresaEquipoEmprendedor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmpresaEquipoEmprendedorConverter {

  private final ModelMapper modelMapper;

  public EmpresaEquipoEmprendedor convert(EmpresaEquipoEmprendedorInput input) {
    return convert(input.getId(), input);
  }

  public EmpresaEquipoEmprendedor convert(Long id, EmpresaEquipoEmprendedorInput input) {
    EmpresaEquipoEmprendedor grupo = modelMapper.map(input, EmpresaEquipoEmprendedor.class);
    grupo.setId(id);
    return grupo;
  }

  public EmpresaEquipoEmprendedorOutput convert(EmpresaEquipoEmprendedor entity) {
    return modelMapper.map(entity, EmpresaEquipoEmprendedorOutput.class);
  }

  public Page<EmpresaEquipoEmprendedorOutput> convert(Page<EmpresaEquipoEmprendedor> page) {
    return page.map(this::convert);
  }

  public List<EmpresaEquipoEmprendedorOutput> convert(List<EmpresaEquipoEmprendedor> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<EmpresaEquipoEmprendedorOutput> convertEmpresaEquipoEmprendedors(
      List<EmpresaEquipoEmprendedor> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<EmpresaEquipoEmprendedor> convertEmpresaEquipoEmprendedorInput(
      List<EmpresaEquipoEmprendedorInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
