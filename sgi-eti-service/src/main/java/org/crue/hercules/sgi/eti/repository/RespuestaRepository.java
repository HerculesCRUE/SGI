package org.crue.hercules.sgi.eti.repository;

import java.util.List;

import org.crue.hercules.sgi.eti.model.Formulario;
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

  /**
   * Indica la existencia o no de un bloque
   * 
   * @param id    identificador de la {@link Respuesta}
   * @param orden orden del bloque
   * @return boolean true or false
   */
  boolean existsByIdAndApartadoBloqueOrden(Long id, Integer orden);

  /**
   * Obtiene la Respuesta asociada un bloque y apartado
   * 
   * @param ordenBloque   El bloque del apartado
   * @param ordenApartado El apartado del bloque
   * @param idFormulario  identificador del {@link Formulario}
   * @param idMemoria     identificador de la {@link Memoria}
   * @return Respuesta
   */
  Respuesta findByApartadoBloqueOrdenAndApartadoOrdenAndApartadoBloqueFormularioIdAndMemoriaId(Integer ordenBloque,
      Integer ordenApartado, Long idFormulario, Long idMemoria);

  Page<Respuesta> findByMemoriaIdAndTipoDocumentoIsNotNull(Long idMemoria, Pageable pageable);

  List<Respuesta> findByMemoriaIdOrderByApartadoBloqueDesc(Long idMemoria);

  /**
   * Obtiene la Respuesta asociada un bloque y apartado sin apartados padres
   * 
   * @param ordenBloque   El bloque del apartado
   * @param ordenApartado El apartado del bloque
   * @param idFormulario  identificador del {@link Formulario}
   * @param idMemoria     identificador de la {@link Memoria}
   * @return Respuesta
   */
  Respuesta findByApartadoBloqueOrdenAndApartadoPadreIsNullAndApartadoOrdenAndApartadoBloqueFormularioIdAndMemoriaId(
      Integer ordenBloque, Integer ordenApartado, Long idFormulario, Long idMemoria);

  /**
   * Obtiene la Respuesta asociada un bloque y apartado
   * 
   * @param ordenBloque        El bloque del apartado
   * @param ordenApartadoPadre El apartado padre
   * @param ordenApartado      El apartado del bloque
   * @param idFormulario       identificador del {@link Formulario}
   * @param idMemoria          identificador de la {@link Memoria}
   * @return Respuesta
   */
  Respuesta findByApartadoBloqueOrdenAndApartadoPadreOrdenAndApartadoOrdenAndApartadoBloqueFormularioIdAndMemoriaId(
      Integer ordenBloque, Integer ordenApartadoPadre, Integer ordenApartado, Long idFormulario, Long idMemoria);
}