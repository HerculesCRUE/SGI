package org.crue.hercules.sgi.prc.dto.csp;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProyectoProyectoSgeDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;
  private Long proyectoId;
  private String proyectoSgeRef;
}