package org.crue.hercules.sgi.com.repository;

import org.crue.hercules.sgi.com.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Status}.
 */
@Repository
public interface StatusRepository
    extends JpaRepository<Status, Long>, JpaSpecificationExecutor<Status> {

  Long deleteByEmailId(Long id);
}
