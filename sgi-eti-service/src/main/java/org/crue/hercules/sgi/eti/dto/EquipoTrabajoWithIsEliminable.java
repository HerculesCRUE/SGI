package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoTrabajoWithIsEliminable implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  private Long id;
  private String personaRef;
  private PeticionEvaluacion peticionEvaluacion;
  private boolean eliminable;

}
