package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConvocatoriaHitoInput implements Serializable {

  @NotNull
  private Long convocatoriaId;
  @NotNull
  private Long tipoHitoId;
  @NotNull
  private Instant fecha;

  private String comentario;
  @Valid
  private ConvocatoriaHitoAvisoInput aviso;
}
