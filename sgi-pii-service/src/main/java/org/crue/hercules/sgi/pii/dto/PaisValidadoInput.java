package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.time.Instant;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.crue.hercules.sgi.pii.model.PaisValidado;

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
public class PaisValidadoInput implements Serializable {

  @NotNull
  private Long solicitudProteccionId;

  @NotEmpty
  @Size(max = PaisValidado.REF_LENGTH)
  private String paisRef;

  @NotEmpty
  @Size(max = PaisValidado.REF_LENGTH)
  private String codigoInvencion;

  @NotNull
  private Instant fechaValidacion;

}
