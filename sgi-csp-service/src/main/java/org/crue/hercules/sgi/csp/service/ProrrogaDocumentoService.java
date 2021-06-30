package org.crue.hercules.sgi.csp.service;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ProrrogaDocumento;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProrrogaDocumento}.
 */
public interface ProrrogaDocumentoService {

  /**
   * Guardar un nuevo {@link ProrrogaDocumento}.
   *
   * @param prorrogaDocumento la entidad {@link ProrrogaDocumento} a guardar.
   * @return la entidad {@link ProrrogaDocumento} persistida.
   */
  ProrrogaDocumento create(ProrrogaDocumento prorrogaDocumento);

  /**
   * Actualizar {@link ProrrogaDocumento}.
   *
   * @param prorrogaDocumentoActualizar la entidad {@link ProrrogaDocumento} a
   *                                    actualizar.
   * @return la entidad {@link ProrrogaDocumento} persistida.
   */
  ProrrogaDocumento update(ProrrogaDocumento prorrogaDocumentoActualizar);

  /**
   * Elimina el {@link ProrrogaDocumento}.
   *
   * @param id Id del {@link ProrrogaDocumento}.
   */
  void delete(Long id);

  /**
   * Obtiene {@link ProrrogaDocumento} por su id.
   *
   * @param id el id de la entidad {@link ProrrogaDocumento}.
   * @return la entidad {@link ProrrogaDocumento}.
   */
  ProrrogaDocumento findById(Long id);

  /**
   * Obtener todas las entidades {@link ProrrogaDocumento} paginadas y/o
   * filtradas.
   * 
   * @param id     id del {@link ProyectoProrroga}
   * @param query  la información del filtro.
   * @param paging la información de la paginación.
   * @return la lista de entidades {@link ProrrogaDocumento} paginadas y/o
   *         filtradas.
   */

  Page<ProrrogaDocumento> findAllByProyectoProrroga(Long id, String query, Pageable paging);

  /**
   * Obtener todas las entidades {@link ProrrogaDocumento} paginadas y/o
   * filtradas.
   * 
   * @param id id del {@link Proyecto}
   * @return la lista de entidades {@link ProrrogaDocumento} paginadas y/o
   *         filtradas.
   */
  List<ProrrogaDocumento> findAllByProyecto(Long id);

  /**
   * Comprueba si existen datos vinculados a {@link Proyecto} de
   * {@link ProrrogaDocumento}
   *
   * @param proyectoId Id del {@link Proyecto}.
   * @return true si existe y false en caso contrario.
   */
  boolean existsByProyecto(Long proyectoId);

}
