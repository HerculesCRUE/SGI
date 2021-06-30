package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.FormacionEspecifica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link FormacionEspecifica}.
 */

@Repository
public interface FormacionEspecificaRepository
    extends JpaRepository<FormacionEspecifica, Long>, JpaSpecificationExecutor<FormacionEspecifica> {

}