package org.crue.hercules.sgi.rel.repository;

import org.crue.hercules.sgi.rel.model.Relacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RelacionRepository extends JpaRepository<Relacion, Long>, JpaSpecificationExecutor<Relacion> {
}
