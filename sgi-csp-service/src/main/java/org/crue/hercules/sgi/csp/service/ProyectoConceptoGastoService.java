package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoConceptoGasto}.
 */
public interface ProyectoConceptoGastoService {

  /**
   * Guarda la entidad {@link ProyectoConceptoGasto}.
   * 
   * @param proyectoConceptoGasto la entidad {@link ProyectoConceptoGasto} a
   *                              guardar.
   * @return la entidad {@link ProyectoConceptoGasto} persistida.
   */
  ProyectoConceptoGasto create(ProyectoConceptoGasto proyectoConceptoGasto);

  /**
   * Actualiza la entidad {@link ProyectoConceptoGasto}.
   * 
   * @param proyectoConceptoGastoActualizar la entidad
   *                                        {@link ProyectoConceptoGasto} a
   *                                        guardar.
   * @return la entidad {@link ProyectoConceptoGasto} persistida.
   */
  ProyectoConceptoGasto update(ProyectoConceptoGasto proyectoConceptoGastoActualizar);

  /**
   * Elimina el {@link ProyectoConceptoGasto}.
   *
   * @param id Id del {@link ProyectoConceptoGasto}.
   */
  void delete(Long id);

  /**
   * Obtiene un {@link ProyectoConceptoGasto} por su id.
   *
   * @param id el id de la entidad {@link ProyectoConceptoGasto}.
   * @return la entidad {@link ProyectoConceptoGasto}.
   */
  ProyectoConceptoGasto findById(Long id);

  /**
   * Comprueba la existencia del {@link ProyectoConceptoGasto} por id.
   *
   * @param id el id de la entidad {@link ProyectoConceptoGasto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene todas las entidades {@link ProyectoConceptoGasto} paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link ProyectoConceptoGasto} paginadas y
   *         filtradas.
   */
  Page<ProyectoConceptoGasto> findAll(String query, Pageable paging);

  /**
   * Obtiene los {@link ProyectoConceptoGasto} permitidos para un {@link Proyecto}
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGasto} del
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoConceptoGasto> findAllByProyectoAndPermitidoTrue(Long proyectoId, Pageable pageable);

  /**
   * Obtiene los {@link ProyectoConceptoGasto} NO permitidos para un
   * {@link Proyecto}
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGasto} del
   *         {@link Proyecto} paginadas.
   */
  Page<ProyectoConceptoGasto> findAllByProyectoAndPermitidoFalse(Long proyectoId, Pageable pageable);

  /**
   * Comprueba si existen diferencias entre los codigos economicos del
   * {@link ProyectoConceptoGasto} y el {@link ConvocatoriaConceptoGasto}
   * relacionado.
   *
   * @param id el id de la entidad {@link ProyectoConceptoGasto}.
   * @return true si existen diferencias y false en caso contrario.
   */
  boolean hasDifferencesCodigosEcConvocatoria(final Long id);

  /**
   * Comprueba si alguno de los {@link ProyectoConceptoGasto} del {@link Proyecto}
   * tienen fechas
   * 
   * @param proyectoId el id del {@link Proyecto}.
   * @return true si existen y false en caso contrario.
   */
  boolean proyectoHasConceptosGastoWithDates(Long proyectoId);

  /**
   * Obtiene los {@link ProyectoConceptoGasto} de un {@link Proyecto}
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @return la lista de entidades {@link ProyectoConceptoGasto} del
   *         {@link Proyecto}.
   */
  List<ProyectoConceptoGasto> findAllByProyectoId(Long proyectoId);

  /**
   * Se valida la unicidad del concepto de gasto. Para un {@link Proyecto} el
   * mismo concepto de gasto solo puede aparecer una vez, salvo que lo haga en
   * periodos de meses no solapados (independientemente del valor del campo
   * "permitido").
   * 
   * @param proyectoConceptoGasto el {@link ProyectoConceptoGasto} a evaluar
   * @return true validación correcta/ false validacion incorrecta
   */
  boolean existsProyectoConceptoGastoConMesesSolapados(ProyectoConceptoGasto proyectoConceptoGasto);

}
