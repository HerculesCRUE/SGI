package org.crue.hercules.sgi.pii.dto;

import java.io.Serializable;
import java.time.Instant;

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
public class PaisValidadoOutput implements Serializable {

  private Long id;
  private Long solicitudProteccionId;
  private String paisRef;
  private String codigoInvencion;
  private Instant fechaValidacion;

}
