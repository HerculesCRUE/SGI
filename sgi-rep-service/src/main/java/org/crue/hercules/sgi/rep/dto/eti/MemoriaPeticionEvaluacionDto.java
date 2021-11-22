package org.crue.hercules.sgi.rep.dto.eti;

import java.time.Instant;

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

  private String titulo;

  private ComiteDto comite;

  private TipoEstadoMemoriaDto estadoActual;

  private boolean requiereRetrospectiva;

  private RetrospectivaDto retrospectiva;

  private Instant fechaEvaluacion;

  private Instant fechaLimite;

  private boolean isResponsable;

  private boolean activo;

}