package org.crue.hercules.sgi.eti.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoriaInput {
  @NotNull
  Long comiteId;

  @NotNull
  Long peticionEvaluacionId;

  @NotNull
  Memoria.Tipo tipo;

  List<I18nFieldValueDto> titulo;

  String responsableRef;

}
