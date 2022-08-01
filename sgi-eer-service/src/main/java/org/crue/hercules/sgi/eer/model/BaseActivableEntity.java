package org.crue.hercules.sgi.eer.model;

import java.io.Serializable;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base Activable Entity.
 */
@NoArgsConstructor
@SuperBuilder
public abstract class BaseActivableEntity extends Activable implements Identifiable, Serializable {
  public static final int REF_LENGTH = 50;
  public static final int SHORT_TEXT_LENGTH = 250;
  public static final int LONG_TEXT_LENGTH = 2000;

  /**
   * Interfaz para marcar validaciones en las activaciones de la entidad.
   */
  public interface OnActivar {
  }

}
