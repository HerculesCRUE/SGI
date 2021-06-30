package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.eti.model.Evaluacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionWithNumComentario implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** id evaluación. */
  private Evaluacion evaluacion;

  /** Nº de comentarios asociados a la evaluacion */
  private Long numComentarios;

}
