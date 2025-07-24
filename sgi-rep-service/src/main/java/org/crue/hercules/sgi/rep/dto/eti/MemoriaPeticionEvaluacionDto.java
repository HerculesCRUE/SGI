package org.crue.hercules.sgi.rep.dto.eti;

import java.time.Instant;
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
public class MemoriaPeticionEvaluacionDto extends BaseRestDto {

  private String numReferencia;

  private List<I18nFieldValueDto> titulo;

  private ComiteDto comite;

  private TipoEstadoMemoriaDto estadoActual;

  private boolean requiereRetrospectiva;

  private RetrospectivaDto retrospectiva;

  private Instant fechaEvaluacion;

  private Instant fechaLimite;

  private boolean isResponsable;

  private boolean activo;

}