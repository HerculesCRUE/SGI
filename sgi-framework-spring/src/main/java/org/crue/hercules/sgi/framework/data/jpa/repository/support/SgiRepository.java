package org.crue.hercules.sgi.framework.data.jpa.repository.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SgiRepository<T, ID> extends JpaRepository<T, ID> {
  /**
   * Bring EntityManager.refresh to all of Spring Data repositories.
   * <p>
   * The refresh method refreshes the state of an instance from the database, and
   * overwrites the copy held by the EntityManager. This ensures the EntityManager
   * has the most up to date version of the data.
   * 
   * @param t the instance to be refreshed
   */
  void refresh(T t);
}
