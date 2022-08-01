package org.crue.hercules.sgi.eer.model;

import java.io.Serializable;

import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable;

/**
 * Base Entity.
 */
public class BaseEntity extends Auditable implements Serializable {
  public static final int REF_LENGTH = 50;
  public static final int SHORT_TEXT_LENGTH = 250;
  public static final int LONG_TEXT_LENGTH = 2000;

  /** Serial version. */
  private static final long serialVersionUID = 1L;

  /**
   * Interfaz para marcar validaciones en los create.
   */
  public interface Create {
  }

  /**
   * Interfaz para marcar validaciones en los update.
   */
  public interface Update {
  }

}
