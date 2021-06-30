package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.ConvocatoriaPartida;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

/**
 * Service Interface para gestionar {@link ConvocatoriaPartida}.
 */
public interface ConvocatoriaPartidaService {

  /**
   * Guardar un nuevo {@link ConvocatoriaPartida}.
   *
   * @param convocatoriaPartida la partida {@link ConvocatoriaPartida} a guardar.
   * @return la partida {@link ConvocatoriaPartida} persistida.
   */
  ConvocatoriaPartida create(ConvocatoriaPartida convocatoriaPartida);

  /**
   * Actualizar {@link ConvocatoriaPartida}.
   *
   * @param convocatoriaPartidaActualizar la partida {@link ConvocatoriaPartida} a
   *                                      actualizar.
   * @return la partida {@link ConvocatoriaPartida} persistida.
   */
  ConvocatoriaPartida update(ConvocatoriaPartida convocatoriaPartidaActualizar);

  /**
   * Elimina el {@link ConvocatoriaPartida}.
   *
   * @param id Id del {@link ConvocatoriaPartida}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ConvocatoriaPartida} por su id.
   *
   * @param id el id de la partida {@link ConvocatoriaPartida}.
   * @return la partida {@link ConvocatoriaPartida}.
   */
  ConvocatoriaPartida findById(Long id);

  /**
   * Obtiene las {@link ConvocatoriaPartida} para una {@link Convocatoria}.
   *
   * @param idConvocatoria el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de partidas {@link ConvocatoriaPartida} de la
   *         {@link Convocatoria} paginadas.
   */
  Page<ConvocatoriaPartida> findAllByConvocatoria(Long idConvocatoria, String query, Pageable pageable);

}