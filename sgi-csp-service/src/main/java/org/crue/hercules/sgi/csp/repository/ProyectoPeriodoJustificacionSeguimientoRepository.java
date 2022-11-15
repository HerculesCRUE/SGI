package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.ProyectoPeriodoJustificacionSeguimiento;
import org.crue.hercules.sgi.csp.repository.custom.CustomProyectoPeriodoJustificacionSeguimientoRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProyectoPeriodoJustificacionSeguimientoRepository
    extends JpaRepository<ProyectoPeriodoJustificacionSeguimiento, Long>,
    CustomProyectoPeriodoJustificacionSeguimientoRepository {

}
