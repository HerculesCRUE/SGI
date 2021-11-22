package org.crue.hercules.sgi.tp.repository;

import org.crue.hercules.sgi.tp.model.BeanMethodTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link BeanMethodTask}.
 */
@Repository
public interface BeanMethodTaskRepository
    extends JpaRepository<BeanMethodTask, Long>, JpaSpecificationExecutor<BeanMethodTask> {
}
