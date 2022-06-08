package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Autorizacion;
import org.crue.hercules.sgi.csp.model.NotificacionProyectoExternoCVN;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NotificacionProyectoExternoCVNRepository extends JpaRepository<NotificacionProyectoExternoCVN, Long>,
    JpaSpecificationExecutor<NotificacionProyectoExternoCVN> {

  /**
   * Comprueba si existe algun {@link NotificacionProyectoExternoCVN} asociado a
   * algún {@link Autorizacion}.
   * 
   * @param autorizacionId id del {@link Autorizacion}.
   * @return true si existe, false si no existe.
   */
  boolean existsByAutorizacionId(Long autorizacionId);

  /**
   * Comprueba si existe algun {@link NotificacionProyectoExternoCVN} asociado a
   * algún {@link Proyecto}.
   * 
   * @param proyectoId id del {@link Proyecto}.
   * @return <code>true</code> Si existe, <code>false</code> en cualquier otro
   *         caso.
   */
  boolean existsByProyectoId(Long proyectoId);

  /**
   * Devuelve la {@link NotificacionProyectoExternoCVN} asociada a la
   * {@link Autorizacion}.
   * 
   * @param autorizacionId id del {@link Autorizacion}.
   * @return optional conteniendo la {@link NotificacionProyectoExternoCVN}.
   */
  Optional<NotificacionProyectoExternoCVN> findByAutorizacionId(Long autorizacionId);

  /**
   * Recupera una lista de objetos {@link NotificacionProyectoExternoCVN} de un
   * {@link Proyecto}
   * 
   * @param proyectoId Identificador del {@link Proyecto}
   * @return lista de {@link NotificacionProyectoExternoCVN}
   */
  List<NotificacionProyectoExternoCVN> findByProyectoId(Long proyectoId);
}
