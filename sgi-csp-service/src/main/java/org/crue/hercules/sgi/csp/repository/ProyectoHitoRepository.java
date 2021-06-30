package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoHito;
import org.crue.hercules.sgi.csp.model.TipoHito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoHitoRepository
    extends JpaRepository<ProyectoHito, Long>, JpaSpecificationExecutor<ProyectoHito> {

  /**
   * Busca un {@link ProyectoHito} por su {@link Proyecto}, {@link TipoHito} y
   * fecha.
   * 
   * @param proyectoId Id de la Proyecto de la {@link ProyectoHito}
   * @param fecha      fecha de la {@link ProyectoHito}
   * @param tipoHitoId Id de la {@link TipoHito}
   * @return un {@link ProyectoHito}
   */
  Optional<ProyectoHito> findByProyectoIdAndFechaAndTipoHitoId(Long proyectoId, Instant fecha, Long tipoHitoId);

  /**
   * Comprueba si existe algun {@link ProyectoHito} asociado a algún
   * {@link Proyecto}.
   * 
   * @param proyectoId id del {@link Proyecto}.
   * @return true si existe, false si no existe.
   */
  boolean existsByProyectoId(Long proyectoId);

}
