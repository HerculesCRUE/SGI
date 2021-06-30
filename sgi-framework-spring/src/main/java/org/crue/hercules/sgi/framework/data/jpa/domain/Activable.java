package org.crue.hercules.sgi.framework.data.jpa.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
public class Activable extends Auditable {
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;
}
