package org.crue.hercules.sgi.rel.converter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.rel.model.RelacionObservaciones;

public class RelacionConverter {

  public static RelacionObservaciones convertir(I18nFieldValueDto dto) {
    return new RelacionObservaciones(dto.getLang(), dto.getValue());
  }

  public static I18nFieldValueDto convertir(RelacionObservaciones observacion) {
    return new I18nFieldValueDto(observacion.getLang(), observacion.getValue());
  }

  public static Set<RelacionObservaciones> convertirObservaciones(List<I18nFieldValueDto> dtos) {
    return dtos == null ? Set.of()
        : dtos.stream()
            .map(RelacionConverter::convertir)
            .collect(Collectors.toSet());
  }

  public static List<I18nFieldValueDto> convertirObservaciones(Set<RelacionObservaciones> observaciones) {
    return observaciones == null ? List.of()
        : observaciones.stream()
            .map(RelacionConverter::convertir)
            .collect(Collectors.toList());
  }

}
