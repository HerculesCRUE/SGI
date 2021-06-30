package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.CargoComite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link CargoComite}.
 */

@Repository
public interface CargoComiteRepository extends JpaRepository<CargoComite, Long>, JpaSpecificationExecutor<CargoComite> {

}