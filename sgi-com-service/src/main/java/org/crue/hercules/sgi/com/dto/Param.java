package org.crue.hercules.sgi.com.dto;

import java.io.Serializable;

import org.crue.hercules.sgi.com.enums.ParamType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Param implements Serializable {
  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Name */
  private String name;
  /** Description */
  private String description;
  /** Type */
  ParamType type;
}
