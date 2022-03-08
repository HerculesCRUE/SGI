package org.crue.hercules.sgi.com.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.crue.hercules.sgi.com.enums.ParamType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Param
 */
@Entity
@Table(name = "param", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name" }, name = "UK_PARAM_NAME") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Param extends BaseEntity {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "param_seq")
  @SequenceGenerator(name = "param_seq", sequenceName = "param_seq", allocationSize = 1)
  private Long id;

  /** Name */
  @Column(name = "name", length = NAME_LENGTH, nullable = false)
  private String name;

  /** Description */
  @Column(name = "description", length = DESCRIPTION_LENGTH, nullable = true)
  private String description;

  /** Type */
  @Column(name = "type", length = TYPE_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  ParamType type;

  // Relation mappings for JPA metamodel generation only
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "params")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<SubjectTpl> subjectTpls = null;

  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "params")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ContentTpl> contentTpls = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "param")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<EmailParam> emailParams = null;
}
