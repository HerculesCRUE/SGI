package org.crue.hercules.sgi.rep.dto.eti;

import java.util.List;

import org.crue.hercules.sgi.framework.i18n.I18nFieldValueDto;
import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AsistentesDto extends BaseRestDto {

  private EvaluadorDto evaluador;
  private ConvocatoriaReunionDto convocatoriaReunion;
  private Boolean asistencia;
  private List<I18nFieldValueDto> motivo;

}