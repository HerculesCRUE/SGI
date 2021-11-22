package org.crue.hercules.sgi.tp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SgiApiInstantTaskOutput extends SgiApiInstantTaskInput {
  private Long id;
  private boolean disabled;
}
