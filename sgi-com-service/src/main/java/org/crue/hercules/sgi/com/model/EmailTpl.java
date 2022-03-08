package org.crue.hercules.sgi.com.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * EmailTpl
 * <p>
 * Email template.
 */
@Entity
@Table(name = "email_tpl", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "name" }, name = "UK_EMAILTPL_NAME") })
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmailTpl extends BaseEntity {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_tpl_seq")
  @SequenceGenerator(name = "email_tpl_seq", sequenceName = "email_tpl_seq", allocationSize = 1)
  private Long id;

  /** Name */
  @Column(name = "name", length = NAME_LENGTH, nullable = false)
  private String name;

  /** Description */
  @Column(name = "description", length = DESCRIPTION_LENGTH, nullable = true)
  private String description;

  /** Subject Template */
  @ManyToOne
  @JoinTable(name = "emailtpl_subjecttpl", joinColumns = {
      @JoinColumn(name = "emailtpl_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_EMAILTPLSUBJECTTPL_EMAILTPL"))
  }, inverseJoinColumns = {
      @JoinColumn(name = "subjecttpl_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_EMAILTPLSUBJECTTPL_SUBJECTTPL"))
  })
  private SubjectTpl subjectTpl;

  /** Content Template */
  @ManyToOne
  @JoinTable(name = "emailtpl_contenttpl", joinColumns = {
      @JoinColumn(name = "emailtpl_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_EMAILTPLCONTENTTPL_EMAILTPL"))
  }, inverseJoinColumns = {
      @JoinColumn(name = "contenttpl_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_EMAILTPLCONTENTTPL_CONTENTTPL"))
  })
  private ContentTpl contentTpl;
}
