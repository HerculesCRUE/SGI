package org.crue.hercules.sgi.csp.repository;

import java.util.List;

import org.crue.hercules.sgi.csp.model.ProyectoSocio;
import org.crue.hercules.sgi.csp.repository.custom.CustomProyectoSocioRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoSocioRepository
    extends JpaRepository<ProyectoSocio, Long>, JpaSpecificationExecutor<ProyectoSocio>, CustomProyectoSocioRepository {

  boolean existsByProyectoIdAndRolSocioCoordinador(Long proyectoId, boolean isCoordinador);

  boolean existsByProyectoId(Long proyectoId);

  List<ProyectoSocio> findByProyectoId(Long proyectoId);
}
