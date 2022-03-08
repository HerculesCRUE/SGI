package org.crue.hercules.sgi.com.model;

import java.io.Serializable;

import org.crue.hercules.sgi.framework.data.jpa.domain.Auditable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * BaseEntity
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class BaseEntity extends Auditable implements Serializable {
  public static final int TYPE_LENGTH = 10;
  public static final int RELATIVE_URL_LENGTH = 2048;
  public static final int NAME_LENGTH = 256;
  public static final int DESCRIPTION_LENGTH = 2048;
  public static final int EMAIL_LENGTH = 256;
  public static final int REF_LENGTH = 256;

  /** Serial version */
  private static final long serialVersionUID = 1L;

}
