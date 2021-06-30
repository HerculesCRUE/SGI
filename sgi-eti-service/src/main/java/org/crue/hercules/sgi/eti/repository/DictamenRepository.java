package org.crue.hercules.sgi.eti.repository;

import java.util.List;

import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Dictamen}.
 */
@Repository
public interface DictamenRepository extends JpaRepository<Dictamen, Long>, JpaSpecificationExecutor<Dictamen> {

  /**
   * Obtiene una lista {@link Dictamen} a partir de los ids enviados.
   *
   * @param ids los ids de la entidad {@link Dictamen}.
   * @return el listado {@link Dictamen} de los ids correspondientes.
   */
  List<Dictamen> findByIdIn(List<Long> ids);

  /**
   * Obtiene una lista {@link Dictamen} a partir de los ids enviados.
   *
   * @param tipoEvaluacionId los ids de la entidad {@link TipoEvaluacion}.
   * @return el listado {@link Dictamen} de dichos tipos de evaluacion.
   */
  List<Dictamen> findByTipoEvaluacionId(Long tipoEvaluacionId);
}