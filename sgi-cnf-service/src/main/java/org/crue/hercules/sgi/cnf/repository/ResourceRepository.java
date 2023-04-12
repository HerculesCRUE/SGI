package org.crue.hercules.sgi.cnf.repository;

import java.util.Optional;

import org.crue.hercules.sgi.cnf.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Resource}.
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, String>, JpaSpecificationExecutor<Resource> {

  <T> Optional<T> findByNameAndPublicAccessTrue(String name, Class<T> clazz);

  <T> Optional<T> findByName(String name, Class<T> clazz);

}