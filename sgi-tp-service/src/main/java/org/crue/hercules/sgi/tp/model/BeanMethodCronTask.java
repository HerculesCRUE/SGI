package org.crue.hercules.sgi.tp.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "bean_method_cron_tasks")
@DiscriminatorValue("CRON")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BeanMethodCronTask extends BeanMethodTask {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public static final int CRON_EXPRESSION_LENGTH = 256;

  /** Cron expression */
  @Column(name = "cron_expression", length = CRON_EXPRESSION_LENGTH, nullable = false)
  private String cronExpression;
}