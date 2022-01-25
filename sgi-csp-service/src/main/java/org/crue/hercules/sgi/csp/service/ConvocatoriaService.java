package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.enums.FormularioSolicitud;
import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link Convocatoria}.
 */

public interface ConvocatoriaService {

  /**
   * Guarda la entidad {@link Convocatoria}.
   * 
   * @param convocatoria la entidad {@link Convocatoria} a guardar.
   * @return Convocatoria la entidad {@link Convocatoria} persistida.
   */
  Convocatoria create(Convocatoria convocatoria);

  /**
   * Actualiza los datos del {@link Convocatoria}.
   * 
   * @param convocatoria {@link Convocatoria} con los datos actualizados.
   * @return Convocatoria {@link Convocatoria} actualizado.
   */
  Convocatoria update(final Convocatoria convocatoria);

  /**
   * Registra una {@link Convocatoria} actualizando su estado de 'Borrador' a
   * 'Registrada'
   * 
   * @param id Identificador de la {@link Convocatoria}.
   * @return Convocatoria {@link Convocatoria} actualizada.
   */
  Convocatoria registrar(final Long id);

  /**
   * Reactiva el {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return la entidad {@link Convocatoria} persistida.
   */
  Convocatoria enable(Long id);

  /**
   * Desactiva el {@link Convocatoria}.
   *
   * @param id Id del {@link Convocatoria}.
   * @return la entidad {@link Convocatoria} persistida.
   */
  Convocatoria disable(Long id);

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede ser modificada. También se utilizará para permitir la creación,
   * modificación o eliminación de ciertas entidades relacionadas con la propia
   * {@link Convocatoria}.
   *
   * @param id                 Id del {@link Convocatoria}.
   * @param unidadConvocatoria unidadGestionRef {@link Convocatoria}.
   * @param authorities        Authorities a validar
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  boolean isRegistradaConSolicitudesOProyectos(Long id, String unidadConvocatoria, String[] authorities);

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede pasar a estado 'Registrada'.
   *
   * @param id Id del {@link Convocatoria}.
   * @return true si puede ser registrada / false si no puede ser registrada
   */
  boolean registrable(Long id);

  /**
   * Comprueba la existencia del {@link Convocatoria} por id.
   *
   * @param id el id de la entidad {@link Convocatoria}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsById(Long id);

  /**
   * Obtiene una entidad {@link Convocatoria} por id.
   * 
   * @param id Identificador de la entidad {@link Convocatoria}.
   * @return Convocatoria la entidad {@link Convocatoria}.
   */
  Convocatoria findById(final Long id);

  /**
   * Obtiene todas las entidades {@link Convocatoria} activas paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} activas paginadas y
   *         filtradas.
   */
  Page<Convocatoria> findAll(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link Convocatoria} que puede visualizar un
   * investigador paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} que puede visualizar un
   *         investigador paginadas y filtradas.
   */
  Page<Convocatoria> findAllInvestigador(String query, Pageable paging);

  /**
   * Devuelve todas las convocatorias activas registradas que se encuentren dentro
   * de la unidad de gestión del usuario logueado.
   * 
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  Page<Convocatoria> findAllRestringidos(String query, Pageable paging);

  /**
   * Devuelve todas las convocatorias activas que se encuentren dentro de la
   * unidad de gestión del usuario logueado.
   * 
   * @param query  información del filtro.
   * @param paging información de paginación.
   * 
   * @return el listado de entidades {@link Convocatoria} paginadas y filtradas.
   */
  Page<Convocatoria> findAllTodosRestringidos(String query, Pageable paging);

  /**
   * Hace las comprobaciones necesarias para determinar si la {@link Convocatoria}
   * puede tramitarse.
   *
   * @param id Id del {@link Convocatoria}.
   * @return true si puede ser tramitada / false si no puede ser tramitada
   */
  boolean tramitable(Long id);

  /**
   * Devuelve si tiene alguna {@link Solicitud} asociada
   * 
   * @param convocatoriaId id de la {@link Convocatoria}
   * @return true o false
   */
  boolean hasAnySolicitudReferenced(Long convocatoriaId);

  /**
   * Devuelve si tiene algún {@link Proyecto} asociado
   * 
   * @param convocatoriaId id de la {@link Convocatoria}
   * @return true o false
   */
  boolean hasAnyProyectoReferenced(Long convocatoriaId);

  /**
   * Clona una {@link Convocatoria} cuya fuente es la que corresponde con el id
   * pasado por parámetro
   * 
   * @param convocatoriaId Id de la convocatoria a clonar
   * @return un objeto de tipo {@link Convocatoria}
   */
  Convocatoria clone(Long convocatoriaId);

  /**
   * Devuelve la {@link Convocatoria} asociada a la {@link Solicitud} con el id
   * indicado si el usuario que realiza la peticion es el solicitante de la
   * {@link Solicitud}.
   * 
   * @param solicitudId Identificador de {@link Solicitud}.
   * @return {@link Convocatoria} correspondiente a la {@link Solicitud}.
   */
  Convocatoria findBySolicitudIdAndUserIsSolicitante(Long solicitudId);

  /**
   * Devuelve la {@link Convocatoria} asociada a la {@link Autorizacion} con el id
   * indicado si el usuario que realiza la peticion es el solicitante de la
   * {@link Autorizacion}.
   * 
   * @param autorizacionId Identificador de {@link Autorizacion}.
   * @return {@link Convocatoria} correspondiente a la {@link Autorizacion}.
   */
  Convocatoria findByAutorizacionIdAndUserIsSolicitante(Long autorizacionId);

  /**
   * Devuelve el {@link FormularioSolicitud} de la {@link Convocatoria}
   * 
   * @param id Identificador de {@link Convocatoria}.
   * @return {@link FormularioSolicitud} correspondiente a la
   *         {@link Convocatoria}.
   */
  FormularioSolicitud findFormularioSolicitudById(Long id);

}
