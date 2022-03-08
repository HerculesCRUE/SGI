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
 * ContentTpl
 * <p>
 * Template for email content.
 */
@Entity
@Table(name = "content_tpl", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name" }, name = "UK_CONTENTTPL_NAME") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ContentTpl extends BaseEntity {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "content_tpl_seq")
  @SequenceGenerator(name = "content_tpl_seq", sequenceName = "content_tpl_seq", allocationSize = 1)
  private Long id;

  /** Name */
  @Column(name = "name", length = NAME_LENGTH, nullable = false)
  private String name;

  /** Description */
  @Column(name = "description", length = DESCRIPTION_LENGTH, nullable = true)
  private String description;

  /** TEXT message template */
  @Column(name = "tpl_text", nullable = false, columnDefinition = "clob")
  private String tplText;

  /** HTML message template */
  @Column(name = "tpl_html", nullable = true, columnDefinition = "clob")
  private String tplHtml;

  /** Template params */
  @ManyToMany
  @JoinTable(name = "contenttpl_param", joinColumns = {
      @JoinColumn(name = "contenttpl_id", table = "content_tpl", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_CONTENTTPLPARAM_CONTENTTPL")) }, inverseJoinColumns = {
          @JoinColumn(name = "param_id", table = "param", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_CONTENTTPLPARAM_PARAM"))
      }, uniqueConstraints = {
          @UniqueConstraint(columnNames = {
              "contenttpl_id", "param_id" }, name = "UK_CONTENTTPLPARAM") })
  private List<Param> params;

  // Relation mappings for JPA metamodel generation only
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "contentTpl")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<EmailTpl> emailTpls = null;
}
