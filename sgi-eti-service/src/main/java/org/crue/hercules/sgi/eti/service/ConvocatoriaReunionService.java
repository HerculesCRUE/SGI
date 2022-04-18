package org.crue.hercules.sgi.eti.service;

import java.util.List;

import org.crue.hercules.sgi.eti.dto.ConvocatoriaReunionDatosGenerales;
import org.crue.hercules.sgi.eti.exceptions.ConvocatoriaReunionNotFoundException;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Dictamen;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ConvocatoriaReunion}.
 */
public interface ConvocatoriaReunionService {

  /**
   * Crea {@link ConvocatoriaReunion}.
   *
   * @param convocatoriaReunion La entidad {@link ConvocatoriaReunion} a crear.
   * @return La entidad {@link ConvocatoriaReunion} creada.
   * @throws IllegalArgumentException Si la entidad {@link ConvocatoriaReunion}
   *                                  tiene id.
   */
  ConvocatoriaReunion create(ConvocatoriaReunion convocatoriaReunion) throws IllegalArgumentException;

  /**
   * Actualiza {@link ConvocatoriaReunion}.
   *
   * @param convocatoriaReunionActualizar La entidad {@link ConvocatoriaReunion} a
   *                                      actualizar.
   * @return La entidad {@link ConvocatoriaReunion} actualizada.
   * @throws ConvocatoriaReunionNotFoundException Si no existe ninguna entidad
   *                                              {@link ConvocatoriaReunion} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si la entidad
   *                                              {@link ConvocatoriaReunion} no
   *                                              tiene id.
   */
  ConvocatoriaReunion update(ConvocatoriaReunion convocatoriaReunionActualizar)
      throws ConvocatoriaReunionNotFoundException, IllegalArgumentException;

  /**
   * Elimina todas las entidades {@link ConvocatoriaReunion}.
   *
   */
  void deleteAll();

  /**
   * Elimina {@link ConvocatoriaReunion} por id.
   *
   * @param id El id de la entidad {@link ConvocatoriaReunion}.
   * @throws ConvocatoriaReunionNotFoundException Si no existe ninguna entidad
   *                                              {@link ConvocatoriaReunion} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si no se informa Id.
   */
  void delete(Long id) throws ConvocatoriaReunionNotFoundException, IllegalArgumentException;

  /**
   * Obtiene las entidades {@link ConvocatoriaReunion} filtradas y paginadas según
   * los criterios de búsqueda.
   *
   * @param query  filtro de búsqueda.
   * @param paging pageable
   * @return el listado de entidades {@link ConvocatoriaReunion} paginadas y
   *         filtradas.
   */
  Page<ConvocatoriaReunion> findAll(String query, Pageable paging);

  /**
   * Obtiene {@link ConvocatoriaReunion} por id.
   *
   * @param id El id de la entidad {@link ConvocatoriaReunion}.
   * @return La entidad {@link ConvocatoriaReunion}.
   * @throws ConvocatoriaReunionNotFoundException Si no existe ninguna entidad
   *                                              {@link ConvocatoriaReunion} con
   *                                              ese id.
   * @throws IllegalArgumentException             Si no se informa Id.
   */
  ConvocatoriaReunion findById(Long id) throws ConvocatoriaReunionNotFoundException, IllegalArgumentException;

  /**
   * Obtiene {@link ConvocatoriaReunionDatosGenerales} por id con el número de
   * evaluaciones activas que no son revisión mínima.
   *
   * @param id El id de la entidad {@link ConvocatoriaReunionDatosGenerales}.
   * @return La entidad {@link ConvocatoriaReunionDatosGenerales}.
   * @throws ConvocatoriaReunionNotFoundException Si no existe ninguna entidad
   *                                              {@link ConvocatoriaReunionDatosGenerales}
   *                                              con ese id.
   * @throws IllegalArgumentException             Si no se informa Id.
   */
  ConvocatoriaReunionDatosGenerales findByIdWithDatosGenerales(Long id)
      throws ConvocatoriaReunionNotFoundException, IllegalArgumentException;

  /**
   * Devuelve una lista de convocatorias de reunión que no tengan acta
   *
   * @return la lista de convocatorias de reunión
   */

  List<ConvocatoriaReunion> findConvocatoriasSinActa();

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ConvocatoriaReunion} puede ser eliminada.
   *
   * @param id Id del {@link ConvocatoriaReunion}.
   * @return true si puede ser eliminada / false si no puede ser eliminada
   */
  Boolean eliminable(Long id);

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ConvocatoriaReunion} puede ser modificada.
   *
   * @param id Id de la {@link ConvocatoriaReunion}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  Boolean modificable(Long id);

  /**
   * Retorna la fecha convocatoria y acta (codigo convocatoria) de la última
   * evaluación de tipo memoria de la memoria original (y que no sea revisión
   * mínima)
   * 
   * @param idEvaluacion Id de la {@link Evaluacion}
   * @param idDictamen   Id del {@link Dictamen}
   * @return ConvocatoriaReunion
   */
  ConvocatoriaReunion findConvocatoriaUltimaEvaluacionTipoMemoria(Long idEvaluacion, Long idDictamen);

  /**
   * Permite enviar el comunicado de {@link ConvocatoriaReunion}
   *
   * @param id Id del {@link ConvocatoriaReunion}.
   * @return true si puede ser enviado / false si no puede ser enviado
   */
  Boolean enviarComunicado(Long id);
}
