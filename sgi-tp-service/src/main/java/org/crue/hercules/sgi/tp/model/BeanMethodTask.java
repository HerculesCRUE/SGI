package org.crue.hercules.sgi.tp.model;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "bean_method_tasks")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "schedule_type")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BeanMethodTask extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public static final int BEAN_LENGTH = 256;
  public static final int METHOD_LENGHT = 256;
  public static final int DESCRIPTION_LENGTH = 1024;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bean_method_tasks_seq")
  @SequenceGenerator(name = "bean_method_tasks_seq", sequenceName = "bean_method_tasks_seq", allocationSize = 1)
  private Long id;

  /** Description */
  @Column(name = "description", length = DESCRIPTION_LENGTH, nullable = true)
  private String description;

  /** Bean name */
  @Column(name = "bean", length = BEAN_LENGTH, nullable = false)
  private String bean;

  /** Method name */
  @Column(name = "method", length = METHOD_LENGHT, nullable = false)
  private String method;

  /** Method parameters */
  @ElementCollection(fetch = FetchType.EAGER, targetClass = java.lang.String.class)
  @CollectionTable(name = "bean_method_task_params", joinColumns = @JoinColumn(name = "bean_method_tasks_id"))
  @Column(name = "param")
  private List<String> params;

  /** Disabled */
  @Column(name = "disabled", nullable = false)
  private Boolean disabled;

}