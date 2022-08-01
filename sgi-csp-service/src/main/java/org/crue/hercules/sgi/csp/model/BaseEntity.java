package org.crue.hercules.sgi.csp.model;

import java.io.Serializable;

import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable;

/**
 * Base Entity.
 */
public abstract class BaseEntity extends Auditable implements Identifiable, Serializable {
  /**
   * Todas las entidades del SGI tienen como identificador un Long.
   * <p>
   * El rango de long en Java es de -9,223,372,036,854,775,808 a
   * +9,223,372,036,854,775,807.
   * <p>
   * En total 20 dígitos con el signo incluido.
   */
  public static final int SGI_REF_LENGTH = 20;
  /**
   * Es el tamaño que, por defecto, da Hiberante a un campo de tipo String.
   */
  public static final int EXTERNAL_REF_LENGTH = 255;
  /**
   * Longitud maxima de un texto largo
   */
  public static final int DEFAULT_LONG_TEXT_LENGTH = 2000;

  /**
   * Longitud maxima de un texto
   */
  public static final int DEFAULT_TEXT_LENGTH = 250;

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
