package org.crue.hercules.sgi.csp.dto.tp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class SgiApiInstantTaskOutput extends SgiApiInstantTaskInput {
  private Long id;
  private boolean disabled;
}
