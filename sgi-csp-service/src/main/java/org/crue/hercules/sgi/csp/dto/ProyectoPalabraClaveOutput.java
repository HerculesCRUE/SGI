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
public class ProyectoPalabraClaveOutput implements Serializable {

  private Long id;
  private Long proyectoId;
  private String palabraClaveRef;
}
