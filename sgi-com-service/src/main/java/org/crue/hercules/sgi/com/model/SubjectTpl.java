package org.crue.hercules.sgi.com.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * SubjectTpl
 * <p>
 * Email subject template.
 */
@Entity
@Table(name = "subject_tpl", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name" }, name = "UK_SUBJECTTPL_NAME") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubjectTpl extends BaseEntity {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subject_tpl_seq")
  @SequenceGenerator(name = "subject_tpl_seq", sequenceName = "subject_tpl_seq", allocationSize = 1)
  private Long id;

  /** Name */
  @Column(name = "name", length = NAME_LENGTH, nullable = false)
  private String name;

  /** Description */
  @Column(name = "description", length = DESCRIPTION_LENGTH, nullable = true)
  private String description;

  /** Template */
  @Column(name = "tpl", nullable = false, columnDefinition = "clob")
  private String tpl;

  @ManyToMany
  @JoinTable(name = "subjecttpl_param", joinColumns = {
      @JoinColumn(name = "subjecttpl_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_SUBJECTTPLPARAM_SUBJECTTPL"))
  }, inverseJoinColumns = {
      @JoinColumn(name = "param_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_SUBJECTTPLPARAM_PARAM"))
  }, uniqueConstraints = {
      @UniqueConstraint(columnNames = {
          "subjecttpl_id", "param_id" }, name = "UK_SUBJECTTPLPARAM") })
  private List<Param> params;

  // Relation mappings for JPA metamodel generation only
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "subjectTpl")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<EmailTpl> emailTpls = null;
}
