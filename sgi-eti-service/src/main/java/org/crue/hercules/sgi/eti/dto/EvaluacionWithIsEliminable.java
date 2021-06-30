package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.time.Instant;

import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionWithIsEliminable implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  private Long id;

  /** Memoria */
  private Memoria memoria;

  /** Convocatoria reunión */
  private ConvocatoriaReunion convocatoriaReunion;

  /** Tipo evaluacion */
  private TipoEvaluacion tipoEvaluacion;

  /** Dictamen */
  private Dictamen dictamen;

  /** Evaluador 1 */
  private Evaluador evaluador1;

  /** Evaluador 2 */
  private Evaluador evaluador2;

  /** Fecha Dictamen */
  private Instant fechaDictamen;

  /** Version */
  private Integer version;

  /** Es revisión mínima */
  private Boolean esRevMinima;

  /** Activo */
  private Boolean activo;

  /** Indica si se puede eliminar */
  private boolean eliminable;

}
