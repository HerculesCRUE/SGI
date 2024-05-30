package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.DocumentacionConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link DocumentacionConvocatoriaReunion}.
 */

@Repository
public interface DocumentacionConvocatoriaReunionRepository
    extends JpaRepository<DocumentacionConvocatoriaReunion, Long>,
    JpaSpecificationExecutor<DocumentacionConvocatoriaReunion> {

  /**
   * Obtener todas las entidades paginadas
   * {@link DocumentacionConvocatoriaReunion} para una
   * determinada {@link ConvocatoriaReunion} y un {@link Formulario}
   *
   * @param id       Id de {@link ConvocatoriaReunion}.
   * @param pageable la información de la paginación.
   * @return la lista de entidades {@link DocumentacionConvocatoriaReunion}
   *         paginadas.
   */
  Page<DocumentacionConvocatoriaReunion> findByConvocatoriaReunionId(Long id, Pageable pageable);

  /**
   * Recupera una {@link DocumentacionConvocatoriaReunion} por id y su
   * convocatoriaReunion activa.
   * 
   * @param id                    Id {@link DocumentacionConvocatoriaReunion}
   * @param idConvocatoriaReunion Id {@link ConvocatoriaReunion}
   * 
   * @return documentacion convocatoriaReunion
   */
  Optional<DocumentacionConvocatoriaReunion> findByIdAndConvocatoriaReunionIdAndConvocatoriaReunionActivoTrue(Long id,
      Long idConvocatoriaReunion);

  /**
   * Devuelve una lista paginada de la documentación de una convocatoriaReunion.
   * 
   * @param idConvocatoriaReunion Identificador de {@link ConvocatoriaReunion}.
   * @param pageable              Datos de la paginación.
   * @return lista paginada de la documentación de una convocatoriaReunion
   */
  Page<DocumentacionConvocatoriaReunion> findByConvocatoriaReunionIdAndConvocatoriaReunionActivoTrue(
      Long idConvocatoriaReunion, Pageable pageable);

  /**
   * Comprueba si existen entidades {@link DocumentacionConvocatoriaReunion} para
   * una
   * determinada {@link ConvocatoriaReunion}
   *
   * @param id Id de {@link ConvocatoriaReunion}.
   * @return true si existen {@link DocumentacionConvocatoriaReunion} / false si
   *         no existen.
   */
  boolean existsByConvocatoriaReunionId(Long id);
}
