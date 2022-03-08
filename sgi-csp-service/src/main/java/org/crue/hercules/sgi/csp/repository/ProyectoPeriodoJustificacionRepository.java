package org.crue.hercules.sgi.csp.repository;

import java.time.Instant;
import java.util.List;

import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoPeriodoJustificacionRepository
    extends JpaRepository<ProyectoPeriodoJustificacion, Long>, JpaSpecificationExecutor<ProyectoPeriodoJustificacion> {

  List<ProyectoPeriodoJustificacion> findByProyectoId(Long proyectoId);

  List<ProyectoPeriodoJustificacion> findByFechaInicioPresentacionBetween(Instant firtDay,
      Instant lastDay);
}
