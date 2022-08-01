package org.crue.hercules.sgi.eer.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eer.dto.EmpresaAdministracionSociedadInput;
import org.crue.hercules.sgi.eer.dto.EmpresaAdministracionSociedadOutput;
import org.crue.hercules.sgi.eer.model.EmpresaAdministracionSociedad;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmpresaAdministracionSociedadConverter {

  private final ModelMapper modelMapper;

  public EmpresaAdministracionSociedad convert(EmpresaAdministracionSociedadInput input) {
    return convert(input.getId(), input);
  }

  public EmpresaAdministracionSociedad convert(Long id, EmpresaAdministracionSociedadInput input) {
    EmpresaAdministracionSociedad grupo = modelMapper.map(input, EmpresaAdministracionSociedad.class);
    grupo.setId(id);
    return grupo;
  }

  public EmpresaAdministracionSociedadOutput convert(EmpresaAdministracionSociedad entity) {
    return modelMapper.map(entity, EmpresaAdministracionSociedadOutput.class);
  }

  public Page<EmpresaAdministracionSociedadOutput> convert(Page<EmpresaAdministracionSociedad> page) {
    return page.map(this::convert);
  }

  public List<EmpresaAdministracionSociedadOutput> convert(List<EmpresaAdministracionSociedad> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<EmpresaAdministracionSociedadOutput> convertEmpresaAdministracionSociedades(
      List<EmpresaAdministracionSociedad> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<EmpresaAdministracionSociedad> convertEmpresaAdministracionSociedadInput(
      List<EmpresaAdministracionSociedadInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
