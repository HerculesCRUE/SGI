package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoIVA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoIVARepository extends JpaRepository<ProyectoIVA, Long>, JpaSpecificationExecutor<ProyectoIVA> {

  /**
   * Obtiene las {@link ProyectoIVA} para una {@link Proyecto}.
   *
   * @param idProyecto el id de la {@link Proyecto}.
   * @param paging     la información de la paginación.
   * @return la lista de entidades {@link ProyectoIVA} de la {@link Proyecto}
   *         paginadas.
   */
  Page<ProyectoIVA> findAllByProyectoIdAndIvaIsNotNullOrderByIdDesc(Long idProyecto, Pageable paging);

}
