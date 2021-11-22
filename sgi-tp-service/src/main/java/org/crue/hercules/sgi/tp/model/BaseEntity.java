package org.crue.hercules.sgi.tp.model;

import java.io.Serializable;

import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base Entity.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class BaseEntity extends Auditable implements Serializable {

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
