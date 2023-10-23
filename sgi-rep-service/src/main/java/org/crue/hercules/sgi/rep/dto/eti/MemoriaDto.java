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
public class MemoriaDto extends BaseRestDto {

  private String numReferencia;
  private PeticionEvaluacionDto peticionEvaluacion;
  private ComiteDto comite;
  private String titulo;
  private String personaRef;
  private TipoMemoriaDto tipoMemoria;
  private TipoEstadoMemoriaDto estadoActual;
  private Instant fechaEnvioSecretaria;
  private Boolean requiereRetrospectiva;
  private RetrospectivaDto retrospectiva;
  private Integer version;
  private Boolean activo;
  private MemoriaDto memoriaOriginal;

}