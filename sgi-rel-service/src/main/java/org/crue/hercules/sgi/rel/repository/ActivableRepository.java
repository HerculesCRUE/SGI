package org.crue.hercules.sgi.rel.repository;

import org.crue.hercules.sgi.framework.data.jpa.domain.Activable;

/**
 * Spring Data JPA repository para {@link Activable}.
 */
public interface ActivableRepository {
  boolean existsByIdAndActivoIsTrue(Long id);

  boolean existsByIdAndActivoIsFalse(Long id);
}
