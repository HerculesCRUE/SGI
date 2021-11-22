package org.crue.hercules.sgi.framework.data.jpa.repository.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Bring {@link javax.persistence.EntityManager#refresh} method to the Spring
 * Data repositories.
 * <p>
 * The refresh method refreshes the state of an instance from the database, and
 * overwrites the copy held by the EntityManager. This ensures the EntityManager
 * has the most up to date version of the data.
 */
@NoRepositoryBean
public interface SgiRepository<T, I> extends JpaRepository<T, I> {
  /**
   * Refreshes the state of an instance from the database, and overwrites the copy
   * held by the EntityManager. This ensures the EntityManager has the most up to
   * date version of the data.
   * 
   * @param t the instance to be refreshed
   */
  void refresh(T t);
}
