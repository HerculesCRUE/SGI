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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
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
 * Email
 */
@Entity
@Table(name = "email")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Email extends BaseEntity {

  /** Serial version */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_seq")
  @SequenceGenerator(name = "email_seq", sequenceName = "email_seq", allocationSize = 1)
  private Long id;

  /** Email template */
  @OneToOne
  @JoinColumn(name = "emailtpl_id", foreignKey = @ForeignKey(name = "FK_EMAIL_EMAILTPL"), nullable = false)
  private EmailTpl emailTpl;

  @OneToOne
  @PrimaryKeyJoinColumn(referencedColumnName = "id")
  private EmailRecipientDeferrable deferrableRecipients;

  @OneToOne
  @PrimaryKeyJoinColumn
  private EmailAttachmentDeferrable deferrableAttachments;

  @OneToOne
  @PrimaryKeyJoinColumn
  private EmailParamDeferrable deferrableParams;

  // Relation mappings for JPA metamodel generation only
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "email")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Recipient> recipients = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "email")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Attachment> attachments = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "email")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<EmailParam> emailParams = null;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "email")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Status> statuses = null;

  // Validation marker interfaces
  /** Validate on create */
  public interface OnCreate {
  }

  /** Validate on send */
  public interface OnSend {
  }
}
