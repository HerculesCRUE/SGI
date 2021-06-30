package org.crue.hercules.sgi.csp.dto;

import java.io.Serializable;

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
public class SolicitudProyectoResponsableEconomicoOutput implements Serializable {
  private Long id;
  private Long solicitudProyectoId;
  private String personaRef;
  private Integer mesInicio;
  private Integer mesFin;
}
