package org.crue.hercules.sgi.com.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * EmailParamPK
 * <p>
 * The {@link EmailParam} has a compounded primary key.
 */
@Embeddable
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailParamPK implements Serializable {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Email Id */
  @Column(name = "email_id", nullable = false)
  private Long emailId;
  /** Param Id for the given Email Id */
  @Column(name = "param_id", nullable = false)
  private Long paramId;
}
