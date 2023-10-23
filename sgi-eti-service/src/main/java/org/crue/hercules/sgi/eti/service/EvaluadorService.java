package org.crue.hercules.sgi.eti.service;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.eti.exceptions.EvaluadorNotFoundException;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Evaluador;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Evaluador}.
 */
public interface EvaluadorService {
  /**
   * Guardar {@link Evaluador}.
   *
   * @param evaluador la entidad {@link Evaluador} a guardar.
   * @return la entidad {@link Evaluador} persistida.
   */
  Evaluador create(Evaluador evaluador);

  /**
   * Actualizar {@link Evaluador}.
   *
   * @param evaluador la entidad {@link Evaluador} a actualizar.
   * @return la entidad {@link Evaluador} persistida.
   */
  Evaluador update(Evaluador evaluador);

  /**
   * Obtener todas las entidades {@link Evaluador} paginadas y/o filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link Evaluador} paginadas y/o filtradas.
   */
  Page<Evaluador> findAll(String query, Pageable pageable);

  /**
   * Devuelve los evaluadores activos del comité indicado que no entre en
   * conflicto de intereses con ningún miembro del equipo investigador de la
   * memoria.
   * 
   * @param idComite        Identificador del {@link Comite}
   * @param idMemoria       Identificador de la {@link Memoria}
   * @param fechaEvaluacion la fecha de Evaluación de la
   *                        {@link ConvocatoriaReunion}
   * @return lista de evaluadores sin conflictos de intereses
   */
  List<Evaluador> findAllByComiteSinconflictoInteresesMemoria(Long idComite, Long idMemoria, Instant fechaEvaluacion);

  /**
   * Obtiene {@link Evaluador} por id.
   *
   * @param id el id de la entidad {@link Evaluador}.
   * @return la entidad {@link Evaluador}.
   */
  Evaluador findById(Long id);

  /**
   * Elimina el {@link Evaluador} por id.
   *
   * @param id el id de la entidad {@link Evaluador}.
   */
  void delete(Long id) throws EvaluadorNotFoundException;

  /**
   * Devuelve los evaluadores activos del comité indicado
   * 
   * @param comite Nombre del {@link Comite}
   * @return lista de evaluadores
   */
  List<Evaluador> findAllByComite(String comite);

  /**
   * Busca un secretario {@link Evaluador} activo dentro de la fecha indicada
   *
   * @param fecha  la fecha de actividad del secretario.
   * @param comite el nombre del {@link Comite} al que pertenece
   * @return el secretario {@link Evaluador}
   */
  Evaluador findSecretarioInFechaAndComite(Instant fecha, String comite);

  /**
   * Comprueba si la persona es evaluador activo en algun {@link Comite}
   * 
   * @param personaRef identificador de la persona
   * @return si es evaluador o no
   */
  boolean isEvaluador(String personaRef);

  /**
   * Comprueba si la persona correspondiente al evaluador esta activa en el
   * {@link Comite}
   * 
   * @param evaluadorId identificador del {@link Evaluador}
   * @param comiteId    identificador del {@link Comite}
   * @return si esta activo o no
   */
  boolean isEvaluadorActivoComite(Long evaluadorId, Long comiteId);

}