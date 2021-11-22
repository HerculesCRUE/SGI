package org.crue.hercules.sgi.tp.dto;

import java.time.Instant;

import javax.validation.constraints.NotEmpty;

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
public class SgiApiInstantTaskInput extends SgiApiTaskInput {
  @NotEmpty
  private Instant instant;
}
