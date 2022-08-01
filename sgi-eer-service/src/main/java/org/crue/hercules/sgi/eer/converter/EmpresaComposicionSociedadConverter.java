package org.crue.hercules.sgi.eer.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eer.dto.EmpresaComposicionSociedadInput;
import org.crue.hercules.sgi.eer.dto.EmpresaComposicionSociedadOutput;
import org.crue.hercules.sgi.eer.model.EmpresaComposicionSociedad;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmpresaComposicionSociedadConverter {

  private final ModelMapper modelMapper;

  public EmpresaComposicionSociedad convert(EmpresaComposicionSociedadInput input) {
    return convert(input.getId(), input);
  }

  public EmpresaComposicionSociedad convert(Long id, EmpresaComposicionSociedadInput input) {
    EmpresaComposicionSociedad grupo = modelMapper.map(input, EmpresaComposicionSociedad.class);
    grupo.setId(id);
    return grupo;
  }

  public EmpresaComposicionSociedadOutput convert(EmpresaComposicionSociedad entity) {
    return modelMapper.map(entity, EmpresaComposicionSociedadOutput.class);
  }

  public Page<EmpresaComposicionSociedadOutput> convert(Page<EmpresaComposicionSociedad> page) {
    return page.map(this::convert);
  }

  public List<EmpresaComposicionSociedadOutput> convert(List<EmpresaComposicionSociedad> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<EmpresaComposicionSociedadOutput> convertEmpresaComposicionSociedades(
      List<EmpresaComposicionSociedad> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<EmpresaComposicionSociedad> convertEmpresaComposicionSociedadInput(
      List<EmpresaComposicionSociedadInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
