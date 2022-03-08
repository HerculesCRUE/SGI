package org.crue.hercules.sgi.com.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * EmailParam
 */
@Entity
@Table(name = "email_param")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EmailParam extends BaseEntity {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  EmailParamPK pk;
  /** Param for the given email Id */

  @ManyToOne
  @JoinColumn(name = "param_id", foreignKey = @ForeignKey(name = "FK_EMAILPARAM_PARAM"), nullable = false)
  @MapsId("paramId")
  private Param param;

  /** Param value for the given email Id */
  @Column(name = "value", nullable = false, columnDefinition = "clob")
  private String value;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "email_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_EMAILPARAM_EMAIL"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Email email = null;
}
