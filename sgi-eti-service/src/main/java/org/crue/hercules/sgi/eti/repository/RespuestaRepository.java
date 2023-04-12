package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Respuesta}.
 */
@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long>, JpaSpecificationExecutor<Respuesta> {

  /**
   * Obtiene la Respuesta asociada a un Memoria y Apartado
   * 
   * @param id         Identidficador del Memoria
   * @param idApartado Identificaro del Apartado
   * @return Respuesta
   */
  Respuesta findByMemoriaIdAndApartadoId(Long id, Long idApartado);

  /**
   * Recupera una listado paginado de todas las respuestas de una memoria activa.
   * 
   * @param idMemoria Identificador {@link Memoria}.
   * @param pageable  Datos de la paginación.
   * @return listado paginado de respuestas.
   */
  Page<Respuesta> findByMemoriaIdAndMemoriaActivoTrue(Long idMemoria, Pageable pageable);

  Page<Respuesta> findByMemoriaIdAndTipoDocumentoIsNotNull(Long idMemoria, Pageable pageable);

  Optional<Respuesta> findTopByMemoriaIdOrderByApartadoBloqueOrdenDescApartadoOrdenDesc(Long memoriaId);

}