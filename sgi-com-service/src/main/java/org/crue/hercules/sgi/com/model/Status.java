package org.crue.hercules.sgi.com.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.com.enums.ErrorType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Status
 * <p>
 * Historical Email status
 */
@Entity
@Table(name = "status")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Status extends BaseEntity {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "param_seq")
  @SequenceGenerator(name = "param_seq", sequenceName = "param_seq", allocationSize = 1)
  private Long id;

  /** Email Id */
  @Column(name = "email_id", nullable = false)
  private Long emailId;

  /** Error type (if there was an error) */
  @Column(name = "error", length = TYPE_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private ErrorType error;

  /** Send status message */
  @Column(name = "message", nullable = true, columnDefinition = "clob")
  private String message;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "email_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_STATUS_EMAIL"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Email email = null;
}
