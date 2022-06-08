package org.crue.hercules.sgi.prc.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.prc.dto.RangoInput;
import org.crue.hercules.sgi.prc.dto.RangoOutput;
import org.crue.hercules.sgi.prc.model.Rango;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RangoConverter {

  private final ModelMapper modelMapper;

  public Rango convert(RangoInput input) {
    return convert(input.getId(), input);
  }

  public Rango convert(Long id, RangoInput input) {
    Rango grupo = modelMapper.map(input, Rango.class);
    grupo.setId(id);
    return grupo;
  }

  public RangoOutput convert(Rango entity) {
    return modelMapper.map(entity, RangoOutput.class);
  }

  public Page<RangoOutput> convert(Page<Rango> page) {
    return page.map(this::convert);
  }

  public List<RangoOutput> convert(List<Rango> list) {
    return list.stream().map(this::convert).collect(Collectors.toList());
  }

  public List<RangoOutput> convertRangos(
      List<Rango> entityList) {
    return entityList.stream()
        .map(this::convert)
        .collect(Collectors.toList());
  }

  public List<Rango> convertRangoInput(
      List<RangoInput> inputList) {
    return inputList.stream().map(this::convert).collect(Collectors.toList());
  }
}
