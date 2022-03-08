package org.crue.hercules.sgi.com.repository;

import java.util.Optional;

import org.crue.hercules.sgi.com.model.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Param}.
 */
@Repository
public interface ParamRepository
    extends JpaRepository<Param, Long>, JpaSpecificationExecutor<Param> {

    Optional<Param> findByName(String name);
}
