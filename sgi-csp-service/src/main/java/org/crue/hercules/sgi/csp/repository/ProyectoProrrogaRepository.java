package org.crue.hercules.sgi.csp.repository;

import java.util.List;
import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoProrroga;
import org.crue.hercules.sgi.csp.repository.custom.CustomProyectoProrrogaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProyectoProrrogaRepository extends JpaRepository<ProyectoProrroga, Long>,
    JpaSpecificationExecutor<ProyectoProrroga>, CustomProyectoProrrogaRepository {

  /**
   * 
   * 
   * Obtiene la {@link ProyectoProrroga} más reciente para el {@link Proyecto}
   * indicado.
   * 
   * @param proyectoId Id de la {@link Convocatoria}
   * @return {@link ProyectoProrroga} con fecha concesión más reciente
   */
  Optional<ProyectoProrroga> findFirstByProyectoIdOrderByFechaConcesionDesc(Long proyectoId);

  /**
   * 
   * 
   * Obtiene la {@link ProyectoProrroga} más reciente para un {@link Proyecto}
   * excluyendo de la búsqueda el identificador indicado.
   * 
   * @param id         Id del {@link ProyectoProrroga} a excluír de la búsqueda
   * @param proyectoId Id del {@link Proyecto}
   * @return {@link ProyectoProrroga} con fecha concesión más reciente
   */
  Optional<ProyectoProrroga> findFirstByIdNotAndProyectoIdOrderByFechaConcesionDesc(Long id, Long proyectoId);

  /**
   * Obtiene un listado de {@link ProyectoProrroga} por su {@link Proyecto}
   * ordenado por la fecha de concesión
   * 
   * @param proyectoId Id de la Proyecto de la {@link ProyectoProrroga}
   * @return un listado {@link ProyectoProrroga} ordenado por la fecha de
   *         concesión
   */
  List<ProyectoProrroga> findAllByProyectoIdOrderByFechaConcesion(Long proyectoId);

  /**
   * Indica si existen {@link ProyectoProrroga} de un {@link Proyecto}
   * 
   * @param proyectoId identificador de la {@link Proyecto}
   * @return si existe la entidad {@link ProyectoProrroga}
   */
  boolean existsByProyectoId(Long proyectoId);

}
