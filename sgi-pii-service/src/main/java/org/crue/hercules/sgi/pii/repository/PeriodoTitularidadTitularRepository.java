package org.crue.hercules.sgi.pii.repository;

import java.util.List;

import org.crue.hercules.sgi.pii.model.PeriodoTitularidad;
import org.crue.hercules.sgi.pii.model.PeriodoTitularidadTitular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link PeriodoTitularidadTitular}.
 */
@Repository
public interface PeriodoTitularidadTitularRepository
    extends JpaRepository<PeriodoTitularidadTitular, Long>, JpaSpecificationExecutor<PeriodoTitularidadTitular> {

  /**
   * Obtiene las entidades {@link PeriodoTitularidadTitular} asociadas al
   * {@link PeriodoTitularidad} pasado por parámetros.
   * 
   * @param periodoTitularidadId id del {@link PeriodoTitularidad}
   * @return listado de {@link PeriodoTitularidadTitular}
   */
  List<PeriodoTitularidadTitular> findAllByPeriodoTitularidadId(Long periodoTitularidadId);

  /**
   * Obtiene los {@link PeriodoTitularidadTitular} que no estén asociados a los
   * Titulares pasados por parámetros.
   * 
   * @param periodoTitularidadId id del {@link PeriodoTitularidad}
   * @param titularesRef         Listado identificadores de Titulares
   * @return listado de {@link PeriodoTitularidadTitular}
   */
  List<PeriodoTitularidadTitular> findAllByPeriodoTitularidadIdAndTitularRefNotIn(Long periodoTitularidadId,
      List<String> titularesRef);

  /**
   * Elimina los {@link PeriodoTitularidadTitular} asociados al
   * {@link PeriodoTitularidad} con el id pasado por parámetros
   * 
   * @param id identificador de {@link PeriodoTitularidad}.
   */
  void deleteByPeriodoTitularidadId(Long id);

}
