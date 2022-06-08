package org.crue.hercules.sgi.prc.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.prc.dto.ModuladorInput;
import org.crue.hercules.sgi.prc.dto.ModuladorOutput;
import org.crue.hercules.sgi.prc.model.Modulador;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ModuladorConverter {

  private final ModelMapper modelMapper;

  public Modulador convert(ModuladorInput input) {
    return convert(input.getId(), input);
  }

  public Modulador convert(Long id, ModuladorInput input) {
    Modulador grupo = modelMapper.map(input, Modulador.class);
    grupo.setId(id);
    return grupo;
  }

  public ModuladorOutput convert(Modulador entity) {
    return modelMapper.map(entity, ModuladorOutput.class);
  }

  public Page<ModuladorOutput> convert(Page<Modulador> page) {
    return page.map(this::convert);
  }

  public List<ModuladorOutput> convert(List<Modulador> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<ModuladorOutput> convertModuladors(
      List<Modulador> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<Modulador> convertModuladorInput(
      List<ModuladorInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
