package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.repository.custom.CustomFormularioRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Formulario}.
 */
@Repository
public interface FormularioRepository
    extends JpaRepository<Formulario, Long>, JpaSpecificationExecutor<Formulario>, CustomFormularioRepository {

}