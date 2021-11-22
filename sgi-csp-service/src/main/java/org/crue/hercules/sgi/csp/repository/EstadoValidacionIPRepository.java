package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.EstadoValidacionIP;
import org.crue.hercules.sgi.csp.model.ProyectoFacturacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EstadoValidacionIPRepository
    extends JpaRepository<EstadoValidacionIP, Long>, JpaSpecificationExecutor<EstadoValidacionIP> {

  /**
   * Devuelve todos los estados asociados a un {@link ProyectoFacturacion}
   * @param proyectoFacturacionId id del proyecto_facturacion
   * @return lista de {@link EstadoValidacionIP}
   */
  List<EstadoValidacionIP> findByProyectoFacturacionId(Long proyectoFacturacionId);

  /**
   * Elimina todos los {@link EstadoValidacionIP} asociados con el id del {@link ProyectoFacturacion}
   * con el que están relacionados
   * @param proyectoFacturacionId id del objeto {@link ProyectoFacturacion} relacionado
   */
  void deleteByProyectoFacturacionId(Long proyectoFacturacionId);
}
