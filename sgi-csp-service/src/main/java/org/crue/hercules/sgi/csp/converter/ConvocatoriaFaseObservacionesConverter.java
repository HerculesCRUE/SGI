package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.ConvocatoriaFaseObservaciones;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ConvocatoriaFaseObservacionesConverter {

  public ConvocatoriaFaseObservaciones convert(I18nFieldValueDto dto) {
    if (dto == null) {
      return null;
    }
    return new ConvocatoriaFaseObservaciones(dto.getLang(), dto.getValue());
  }

  public Set<ConvocatoriaFaseObservaciones> convertAll(List<I18nFieldValueDto> dtoList) {
    if (dtoList == null || dtoList.isEmpty()) {
      return Set.of();
    }
    return dtoList.stream()
        .map(this::convert)
        .collect(Collectors.toSet());
  }
}
