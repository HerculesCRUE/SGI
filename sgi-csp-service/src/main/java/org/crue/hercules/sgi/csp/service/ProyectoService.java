package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.dto.ProyectoPresupuestoTotales;
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
   * Obtiene todas las entidades {@link Proyecto} activas paginadas y filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link Proyecto} activas paginadas y
   *         filtradas.
   */
  Page<Proyecto> findAllRestringidos(String query, Pageable paging);

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
   * @param proyectoId  Id del {@link Proyecto}.
   * @param authorities Authorities a validar
   * @return true si puede ser modificada / false si no puede ser modificada
   */
  boolean modificable(Long proyectoId, String[] authorities);

  /**
   * Obtiene todos los ids de {@link Proyecto} que cumplan las condiciones
   * indicadas en la query.
   *
   * @param query información del filtro.
   * @return el listado de ids de {@link Proyecto}.
   */
  List<Long> findIds(String query);

  /**
   * Busca todos los objetos de tipo {@link ProyectoFacturacion} cuyo proyectoId sea igual al recibido por parámetro
   * @param proyectoId id del {@link Proyecto} del que cuelgan la lista de objetos {@link ProyectoFacturacion} a buscar
   * @param query información del filtro.
   * @param paging información de paginación
   * @return objeto {@link Page} con el listado de objetos de tipo {@link ProyectoFacturacion}
   */
  Page<ProyectoFacturacion> findAllProyectoFacturacionByProyectoId(Long proyectoId, String query, Pageable paging);

  /**
   * Obtiene una lista de identificadores de los objetos de tipo {@link Proyecto} filtrados por solicitudId
   * @param solicitudId id del objeto de tipo {@link Solicitud} referenciado
   * @return lista de ids de los {@link Proyecto}
   */
  List<Long> findIdsBySolicitudId(Long solicitudId);
}
