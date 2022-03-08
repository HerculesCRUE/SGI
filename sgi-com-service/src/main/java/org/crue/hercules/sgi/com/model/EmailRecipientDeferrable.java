package org.crue.hercules.sgi.com.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
 * EmailRecipientDeferrable
 */
@Entity
@Table(name = "email_recipient_deferrable")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class EmailRecipientDeferrable extends Deferrable {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  // Relation mappings for JPA metamodel generation only
  @OneToOne(fetch = FetchType.LAZY, mappedBy = "deferrableRecipients")
  @JoinColumn(name = "id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_EMAILRECIPIENTDEFERABLE_EMAIL"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Email email = null;
}
