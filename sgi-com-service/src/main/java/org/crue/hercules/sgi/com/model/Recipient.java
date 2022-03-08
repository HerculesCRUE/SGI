package org.crue.hercules.sgi.com.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
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
 * Recipient
 * <p>
 * Email recipient
 */
@Entity
@Table(name = "recipient")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Recipient extends BaseEntity {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipient_seq")
  @SequenceGenerator(name = "recipient_seq", sequenceName = "recipient_seq", allocationSize = 1)
  private Long id;

  /** Email Id */
  @Column(name = "email_id", nullable = false)
  private Long emailId;

  /** Recipient name */
  @Column(name = "name", length = NAME_LENGTH, nullable = true)
  private String name;

  /** Recipient email address */
  @Column(name = "address", length = EMAIL_LENGTH, nullable = true)
  private String address;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "email_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_RECIPIENT_EMAIL"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Email email = null;
}
