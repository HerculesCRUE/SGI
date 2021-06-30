package org.crue.hercules.sgi.eti.model;

import java.io.Serializable;

import javax.validation.groups.Default;

import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable;

/**
 * Base Entity.
 */
public class BaseEntity extends Auditable implements Serializable {

  /** Serial version. */
  private static final long serialVersionUID = 1L;

  /**
   * Interfaz para marcar validaciones en los create.
   */
  public interface Create extends Default {
  }

  /**
   * Interfaz para marcar validaciones en los update.
   */
  public interface Update extends Default {
  }

}
