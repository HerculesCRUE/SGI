package org.crue.hercules.sgi.csp.repository.custom;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoPalabraClave;
import org.springframework.data.jpa.repository.Modifying;

public interface CustomProyectoPalabraClaveRepository {
  /**
   * Elimina todos los {@link ProyectoPalabraClave} cuyo proyectoId coincide
   * con el indicado.
   * 
   * @param proyectoId el identificador de la {@link Proyecto} cuyas palabras
   *                   claves se desean eliminar
   * @return el número de registros eliminados
   */
  @Modifying
  int deleteInBulkByProyectoId(long proyectoId);
}
