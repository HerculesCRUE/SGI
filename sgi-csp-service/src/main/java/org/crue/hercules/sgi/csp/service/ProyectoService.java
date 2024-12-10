package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.ProyectoApartadosToBeCopied;
import org.crue.hercules.sgi.csp.dto.ProyectoApartadosWithDates;
import org.crue.hercules.sgi.csp.dto.ProyectoDto;
import org.crue.hercules.sgi.csp.dto.ProyectoPresupuestoTotales;
import org.crue.hercules.sgi.csp.dto.ProyectoSeguimientoEjecucionEconomica;
import org.crue.hercules.sgi.csp.dto.ProyectosCompetitivosPersonas;
import org.crue.hercules.sgi.csp.dto.RelacionEjecucionEconomica;
import org.crue.hercules.sgi.csp.model.EstadoProyecto;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio interfaz para la gestión de {@link Proyecto}.
 */
public interface ProyectoService {

  /**
   * Guarda la entidad {@link Proyecto}.
   * 
   * @param proyecto la entidad {@link Proyecto} a guardar.
   * @return proyecto la entidad {@link Proyecto} persistida.
   */
  Proyecto create(Proyecto proyecto);

  /**
   * Actualiza los datos del {@link Proyecto}.
   * 
   * @param proyecto {@link Proyecto} con los datos actualizados.
   * @return proyecto {@link Proyecto} actualizado.
   */
  Proyecto update(final Proyecto proyecto);

  /**
   * Marca la fecha de inicio del proyecto como inicializada y hace la copia de
   * los apartados de la convocatoria y de la solicitud dependientes de la
   * inicializacion de la fecha
   *
   * @param id Identificador de {@link Proyecto}.
   * @return {@link Proyecto} actualizado.
   */
  Proyecto initFechaInicio(Long id);

  /**
   * Reactiva el {@link Proyecto}.
   *
   * @param id Id del {@link Proyecto}.
   * @return la entidad {@link Proyecto} persistida.
   */
  Proyecto enable(Long id);

  /**
   * Desactiva el {@link Proyecto}.
   *
   * @param id Id del {@link Proyecto}.
   * @return la entidad {@link Proyecto} persistida.
   */
  Proyecto disable(Long id);

  /**
   * Obtiene una entidad {@link Proyecto} por id.
   * 
   * @param id Identificador de la entidad {@link Proyecto}.
   * @return Proyecto la entidad {@link Proyecto}.
   */
  Proyecto findById(final Long id);

  /**
   * Obtiene una entidad {@link Proyecto} por id.
   * Sin hacer comprobaciones de la Unidad de Gestión.
   * 
   * @param id Identificador de la entidad {@link Proyecto}.
   * @return Proyecto la entidad {@link Proyecto}.
   */
  Proyecto findProyectoResumenById(final Long id);

  /**
   * Obtiene todas las entidades {@link Proyecto} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  Page<Proyecto> findAllRestringidos(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link Proyecto} activas, que no estén en estado
   * borrador, en las que el usuario logueado está dentro del equipo o es un
   * responsable economico, paginadas y filtradas
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas a
   *         las que tiene acceso el investigador que ha inciado sesión
   */
  Page<Proyecto> findAllActivosInvestigador(String query, Pageable paging);

  /**
   * Obtiene todas las entidades {@link Proyecto} paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Proyecto} paginadas y filtradas.
   */
  Page<Proyecto> findAllTodosRestringidos(String query, Pageable paging);

  /**
   * Guarda la entidad {@link Proyecto} a partir de los datos de la entidad
   * {@link Solicitud}.
   * 
   * @param id       identificador de la entidad {@link Solicitud} a copiar datos.
   * @param proyecto datos necesarios para crear el {@link Proyecto}
   * @return proyecto la entidad {@link Proyecto} persistida.
   */
  Proyecto createProyectoBySolicitud(Long id, Proyecto proyecto);

  /**
   * Se hace el cambio de estado de un {@link Proyecto}.
   * 
   * @param id             Identificador de {@link Proyecto}.
   * @param estadoProyecto Estado al que se cambiará el Proyecto.
   * @return {@link Proyecto} actualizado.
   */
  Proyecto cambiarEstado(Long id, EstadoProyecto estadoProyecto);

  /**
   * Obtiene el {@link ProyectoPresupuestoTotales} de la {@link Proyecto}.
   * 
   * @param proyectoId Identificador de la entidad {@link Proyecto}.
   * @return {@link ProyectoPresupuestoTotales}.
   */
  ProyectoPresupuestoTotales getTotales(Long proyectoId);

  /**
   * Hace las comprobaciones necesarias para determinar si el {@link Proyecto}
   * puede ser modificado. También se utilizará para permitir la creación,
   * modificación o eliminación de ciertas entidades relacionadas con el
   * {@link Proyecto}.
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  boolean modificable(Long proyectoId);

  /**
   * Hace las comprobaciones necesarias para determinar si el {@link Proyecto}
   * puede ser visualizado.
   * 
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si puede ser visualizado / false si no puede ser visualizado
   */
  boolean visible(Long proyectoId);

  /**
   * Obtiene los ids de {@link Proyecto} modificados que esten
   * activos y con {@link Proyecto#confidencial} a <code>false</code> que cumplan
   * las condiciones indicadas en el filtro de búsqueda
   *
   * @param query información del filtro.
   * @return el listado de ids de {@link Proyecto}.
   */
  List<Long> findIdsProyectosModificados(String query);

  /**
   * Obtiene los ids de {@link Proyecto} modificados que no esten
   * activos y con {@link Proyecto#confidencial} a <code>false</code> que cumplan
   * las condiciones indicadas en el filtro de búsqueda
   *
   * @param query información del filtro.
   * @return el listado de ids de {@link Proyecto}.
   */
  List<Long> findIdsProyectosEliminados(String query);

  /**
   * Busca todos los objetos de tipo {@link ProyectoFacturacion} cuyo proyectoId
   * sea igual al recibido por parámetro
   * 
   * @param proyectoId id del {@link Proyecto} del que cuelgan la lista de objetos
   *                   {@link ProyectoFacturacion} a buscar
   * @param query      información del filtro.
   * @param paging     información de paginación
   * @return objeto {@link Page} con el listado de objetos de tipo
   *         {@link ProyectoFacturacion}
   */
  Page<ProyectoFacturacion> findAllProyectoFacturacionByProyectoId(Long proyectoId, String query, Pageable paging);

  /**
   * Obtiene una lista de identificadores de los objetos de tipo {@link Proyecto}
   * filtrados por solicitudId
   * 
   * @param solicitudId id del objeto de tipo {@link Solicitud} referenciado
   * @return lista de ids de los {@link Proyecto}
   */
  List<Long> findIdsBySolicitudId(Long solicitudId);

  /**
   * Devuelve una lista de {@link ProyectoDto} que se incorporarán a la baremación
   * de producción científica
   * 
   * @param anioInicio año inicio de baremación
   * @param anioFin    año fin de baremación
   * 
   * @return Lista de {@link ProyectoDto}
   */
  List<ProyectoDto> findProyectosProduccionCientifica(Integer anioInicio, Integer anioFin);

  /**
   * Devuelve una lista paginada y filtrada {@link RelacionEjecucionEconomica} que
   * se
   * encuentren dentro de la unidad de gestión del usuario logueado
   * 
   * @param query    filtro de búsqueda.
   * @param pageable {@link Pageable}.
   * @return el listado de entidades {@link RelacionEjecucionEconomica} activas
   *         paginadas y filtradas.
   */
  Page<RelacionEjecucionEconomica> findRelacionesEjecucionEconomicaProyectos(String query, Pageable pageable);

  /**
   * Obtiene los datos de proyectos competitivos de las personas.
   *
   * @param personasRef        Lista de id de las personas.
   * @param onlyAsRolPrincipal Indica si solo se comprueba la participacion con un
   *                           rol principal
   * @param exludedProyectoId  Excluye el {@link Proyecto} de la consulta
   * @return el {@link ProyectosCompetitivosPersonas}.
   */
  ProyectosCompetitivosPersonas getProyectosCompetitivosPersonas(List<String> personasRef, Boolean onlyAsRolPrincipal,
      Long exludedProyectoId);

  /**
   * Devuelve una lista paginada y filtrada
   * {@link ProyectoSeguimientoEjecucionEconomica} que se encuentren dentro de la
   * unidad de gestión del usuario logueado
   * 
   * @param proyectoSgeRef identificador del proyectoSGE
   * @param query          filtro de búsqueda.
   * @param pageable       {@link Pageable}.
   * @return el listado de entidades {@link ProyectoSeguimientoEjecucionEconomica}
   *         activas paginadas y filtradas.
   */
  Page<ProyectoSeguimientoEjecucionEconomica> findProyectosSeguimientoEjecucionEconomica(String proyectoSgeRef,
      String query,
      Pageable pageable);

  /**
   * Devuelve el {@link ProyectoApartadosWithDates} con la informacion de cuales
   * de los apartados tienen elementos con fechas.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return {@link ProyectoApartadosWithDates} correspondiente al id
   */
  ProyectoApartadosWithDates getProyectoApartadosWithDates(Long id);

  /**
   * Devuelve el {@link ProyectoApartadosToBeCopied} con la informacion de cuales
   * de los apartados tienen datos para ser copiados.
   * 
   * @param id Identificador de {@link Proyecto}.
   * @return {@link ProyectoApartadosToBeCopied}
   *         correspondiente al id
   */
  ProyectoApartadosToBeCopied getProyectoApartadosToBeCopied(Long id);

}
