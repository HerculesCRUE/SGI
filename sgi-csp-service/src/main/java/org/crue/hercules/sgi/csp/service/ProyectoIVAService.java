package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoIVA;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface para gestionar {@link ProyectoIVA}.
 */

public interface ProyectoIVAService {

  /**
   * Guarda la entidad {@link ProyectoIVA}.
   * 
   * @param ProyectoIVA la entidad {@link ProyectoIVA} a guardar.
   * @return ProyectoIVA la entidad {@link ProyectoIVA} persistida.
   */
  ProyectoIVA create(ProyectoIVA ProyectoIVA);

  /**
   * Obtiene las {@link ProyectoIVA} para una {@link Proyecto}n.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoIVA} de la {@link Proyecto}
   *         paginadas.
   */
  Page<ProyectoIVA> findAllByProyectoIdOrderByIdDesc(Long proyectoId, Pageable pageable);

}
