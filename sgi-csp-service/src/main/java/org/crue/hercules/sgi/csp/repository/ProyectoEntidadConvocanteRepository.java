package org.crue.hercules.sgi.csp.repository;

import java.util.Optional;

import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link ProyectoEntidadConvocante}.
 */
@Repository
public interface ProyectoEntidadConvocanteRepository
    extends JpaRepository<ProyectoEntidadConvocante, Long>, JpaSpecificationExecutor<ProyectoEntidadConvocante> {

  /**
   * Busca un {@link ProyectoEntidadConvocante} por su {@link Proyecto} y
   * entidadRef.
   * 
   * @param proyectoId Id del {@link Proyecto}
   * @param entidadRef Id de la Entidad Convocante
   * @return true si existe la {@link ProyectoEntidadConvocante} y false en caso
   *         contrario
   */
  boolean existsByProyectoIdAndEntidadRef(Long proyectoId, String entidadRef);

  /**
   * Busca un {@link ProyectoEntidadConvocante} por su {@link Proyecto} y
   * entidadRef.
   * 
   * @param proyectoId Id del {@link Proyecto}
   * @param entidadRef Id de la Entidad Convocante
   * @return la entidad {@link ProyectoEntidadConvocante}
   */
  Optional<ProyectoEntidadConvocante> findByProyectoIdAndEntidadRef(Long proyectoId, String entidadRef);

}
