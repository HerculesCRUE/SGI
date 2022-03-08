package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;
import java.time.Instant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AutorOutput implements Serializable {
  private Long id;
  private String firma;
  private String personaRef;
  private String nombre;
  private String apellidos;
  private Integer orden;
  private String orcidId;
  private Instant fechaInicio;
  private Instant fechaFin;
  private Boolean ip;
  private Long produccionCientificaId;
}
