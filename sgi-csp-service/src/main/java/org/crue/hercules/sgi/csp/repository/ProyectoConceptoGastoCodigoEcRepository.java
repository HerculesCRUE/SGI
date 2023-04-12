package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoConceptoGastoCodigoEcRepository extends JpaRepository<ProyectoConceptoGastoCodigoEc, Long>,
    JpaSpecificationExecutor<ProyectoConceptoGastoCodigoEc> {

  /**
   * Se obtienen los códigos económicos de un {@link ProyectoConceptoGasto}
   * 
   * @param idProyectoConceptoGasto identificador {@link ProyectoConceptoGasto}
   * @return listado {@link ProyectoConceptoGastoCodigoEc}
   */
  List<ProyectoConceptoGastoCodigoEc> findAllByProyectoConceptoGastoId(Long idProyectoConceptoGasto);

  /**
   * Se obtienen los códigos económicos de todos los {@link ProyectoConceptoGasto}
   * de un {@link Proyecto}
   * 
   * @param proyectoId identificador {@link Proyecto}
   * @return listado {@link ProyectoConceptoGastoCodigoEc}
   */
  List<ProyectoConceptoGastoCodigoEc> findAllByProyectoConceptoGastoProyectoId(Long proyectoId);

  /**
   * Comprueba la existencia del {@link ProyectoConceptoGastoCodigoEc} por id de
   * {@link ProyectoConceptoGasto}
   *
   * @param id el id de la entidad {@link ProyectoConceptoGasto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsByProyectoConceptoGastoId(Long id);

}
