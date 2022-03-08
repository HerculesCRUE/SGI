package org.crue.hercules.sgi.prc.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ProyectoOutput implements Serializable {
  private Long id;
  private Long proyectoRef;
  private Long produccionCientificaId;
}
