package org.crue.hercules.sgi.cnf.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.cnf.model.Config.OnCreate;
import org.crue.hercules.sgi.framework.validation.UniqueFieldValue;
import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This entity represent a resource.
 */
@Entity
@Table(name = "resources")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueFieldValue(groups = { OnCreate.class }, entityClass = Config.class, fieldName = Config_.NAME)
public class Resource extends BaseEntity {
  /** Name field max lenght */
  public static final int NAME_MAX_LENGTH = 50;
  /** Description field max lenght */
  public static final int DESCRIPTION_MAX_LENGTH = 250;

  /** Resource name */
  @Id
  @Column(name = "name", length = NAME_MAX_LENGTH, nullable = false)
  private String name;

  /** Resource description */
  @Column(name = "description", length = DESCRIPTION_MAX_LENGTH, nullable = true)
  private String description;

  /** Resource value */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Type(type = "org.hibernate.type.BinaryType")
  @Column(name = "value", nullable = true, columnDefinition = "blob")
  private byte[] value;

  /** Resource default value */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Type(type = "org.hibernate.type.BinaryType")
  @Column(name = "default_value", nullable = true, columnDefinition = "blob")
  private byte[] defaultValue;

  @Column(name = "public_access", nullable = false)
  @NotNull
  private boolean publicAccess;

}
