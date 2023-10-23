package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoriaEvaluada implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private Long id;
  private Long evaluacionId;
  private String numReferencia;
  private String personaRef;
  private String dictamen;
  private Integer version;
  private String tipoEvaluacion;
  private String titulo;
}
