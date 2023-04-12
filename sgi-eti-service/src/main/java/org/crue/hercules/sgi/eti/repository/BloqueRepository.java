package org.crue.hercules.sgi.eti.repository;

import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Bloque}.
 */
@Repository
public interface BloqueRepository extends JpaRepository<Bloque, Long>, JpaSpecificationExecutor<Bloque> {

  /**
   * Obtener todas las entidades {@link Bloque} paginadas de una
   * {@link Formulario}.
   * 
   * @param id       Id del formulario
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link Bloque} paginadas y/o filtradas.
   */
  Page<Bloque> findByFormularioId(Long id, Pageable pageable);

}