package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoConceptoGastoCodigoEc}.
 */
public interface ProyectoConceptoGastoCodigoEcService {

  /**
   * Obtiene un {@link ProyectoConceptoGastoCodigoEc} por su id.
   *
   * @param id el id de la entidad {@link ProyectoConceptoGastoCodigoEc}.
   * @return la entidad {@link ProyectoConceptoGastoCodigoEc}.
   */
  ProyectoConceptoGastoCodigoEc findById(Long id);

  /**
   * Obtener todas las entidades {@link ProyectoConceptoGastoCodigoEc} paginadas
   * y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ProyectoConceptoGastoCodigoEc} paginadas
   *         y/o filtradas.
   */
  Page<ProyectoConceptoGastoCodigoEc> findAll(String query, Pageable pageable);

  /**
   * Obtiene los {@link ProyectoConceptoGastoCodigoEc} para un
   * {@link ProyectoConceptoGasto}
   *
   * @param proyectoConceptoGastoId el id del {@link ProyectoConceptoGasto}.
   * @param pageable                la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGastoCodigoEc} del
   *         {@link ProyectoConceptoGasto} paginadas.
   */
  Page<ProyectoConceptoGastoCodigoEc> findAllByProyectoConceptoGasto(Long proyectoConceptoGastoId, Pageable pageable);

  /**
   * Obtiene los {@link ProyectoConceptoGastoCodigoEc} permitidos para un
   * {@link Proyecto}
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGastoCodigoEc} del
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoConceptoGastoCodigoEc> findAllByProyectoAndPermitidoTrue(Long proyectoId, Pageable pageable);

  /**
   * Obtiene los {@link ProyectoConceptoGastoCodigoEc} NO permitidos para un
   * {@link Proyecto}
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGastoCodigoEc} del
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoConceptoGastoCodigoEc> findAllByProyectoAndPermitidoFalse(Long proyectoId, Pageable pageable);

  /**
   * Actualiza el listado de {@link ProyectoConceptoGastoCodigoEc} del
   * {@link ProyectoConceptoGasto} con el listado proyectoConceptoGastoCodigoEcs
   * añadiendo, editando o eliminando los elementos segun proceda.
   *
   * @param proyectoConceptoGastoId        Id de la {@link ProyectoConceptoGasto}.
   * @param proyectoConceptoGastoCodigoEcs lista con los nuevos
   *                                       {@link ProyectoConceptoGastoCodigoEc} a
   *                                       guardar.
   * @return la lista de entidades {@link ProyectoConceptoGastoCodigoEc}
   *         persistidas.
   */
  List<ProyectoConceptoGastoCodigoEc> update(Long proyectoConceptoGastoId,
      List<ProyectoConceptoGastoCodigoEc> proyectoConceptoGastoCodigoEcs);

  /**
   * Comprueba la existencia del {@link ProyectoConceptoGastoCodigoEc} por id de
   * {@link ProyectoConceptoGasto}
   *
   * @param id el id de la entidad {@link ProyectoConceptoGasto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsByProyectoConceptoGasto(final Long id);

}
