package org.crue.hercules.sgi.prc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.crue.hercules.sgi.prc.model.Config.OnCreate;
import org.crue.hercules.sgi.framework.validation.UniqueFieldValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This entity represent a configuration value.
 */
@Entity
@Table(name = "config")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueFieldValue(groups = { OnCreate.class }, entityClass = Config.class, fieldName = Config_.NAME)
public class Config extends BaseEntity {
  /** Name field max lenght */
  public static final int NAME_MAX_LENGTH = 50;
  /** Description field max lenght */
  public static final int DESCRIPTION_MAX_LENGTH = 250;

  /** Config name */
  @Id
  @Column(name = "name", length = NAME_MAX_LENGTH, nullable = false)
  private String name;

  /** Config description */
  @Column(name = "description", length = DESCRIPTION_MAX_LENGTH, nullable = true)
  private String description;

  /** Config value */
  @Column(name = "value", nullable = false, columnDefinition = "clob")
  private String value;

  /**
   * Validations on entity creation interface marker.
   */
  public interface OnCreate {
  }

}