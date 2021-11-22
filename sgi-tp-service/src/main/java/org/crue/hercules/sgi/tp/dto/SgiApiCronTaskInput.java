package org.crue.hercules.sgi.tp.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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
public class SgiApiCronTaskInput extends SgiApiTaskInput {
  public static final int CRON_EXPRESSION_LENGTH = 256;

  @NotEmpty
  @Size(max = CRON_EXPRESSION_LENGTH)
  private String cronExpression;
}
