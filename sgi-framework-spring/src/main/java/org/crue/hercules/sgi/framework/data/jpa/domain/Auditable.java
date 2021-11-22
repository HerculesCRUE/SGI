package org.crue.hercules.sgi.framework.data.jpa.domain;

import java.time.Instant;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Auditable entities should subclass this class.
 */
@MappedSuperclass
@Data
@SuperBuilder
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
  /** Creator */
  @CreatedBy
  protected String createdBy;

  /** Creation date */
  @CreatedDate
  protected Instant creationDate;

  /** Last updater */
  @LastModifiedBy
  protected String lastModifiedBy;

  /** Last update date */
  @LastModifiedDate
  protected Instant lastModifiedDate;
}
