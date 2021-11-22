package org.crue.hercules.sgi.framework.data.jpa.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Activable entities should subclass this class.
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class Activable extends Auditable {
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @Builder.Default
  private Boolean activo = Boolean.TRUE;
}
