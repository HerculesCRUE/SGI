package org.crue.hercules.sgi.csp.converter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.csp.model.SolicitudProyectoObjetivos;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class SolicitudProyectoObjetivosConverter {

  public SolicitudProyectoObjetivos convert(I18nFieldValueDto dto) {
    if (dto == null) {
      return null;
    }
    return new SolicitudProyectoObjetivos(dto.getLang(), dto.getValue());
  }

  public Set<SolicitudProyectoObjetivos> convertAll(List<I18nFieldValueDto> dtoList) {
    if (dtoList == null || dtoList.isEmpty()) {
      return Set.of();
    }
    return dtoList.stream().map(this::convert).collect(Collectors.toSet());
  }

  public List<I18nFieldValueDto> convertAll(Set<SolicitudProyectoObjetivos> observacionesSet) {
    if (observacionesSet == null || observacionesSet.isEmpty()) {
      return List.of();
    }
    return observacionesSet.stream().map(this::convertToDto).collect(Collectors.toList());
  }

  public I18nFieldValueDto convertToDto(SolicitudProyectoObjetivos observacion) {
    if (observacion == null) {
      return null;
    }
    I18nFieldValueDto dto = new I18nFieldValueDto();
    dto.setLang(observacion.getLang());
    dto.setValue(observacion.getValue());
    return dto;
  }

}
