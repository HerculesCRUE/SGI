package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.custom.CustomMemoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link Memoria}.
 */
@Repository
public interface MemoriaRepository
    extends JpaRepository<Memoria, Long>, JpaSpecificationExecutor<Memoria>, CustomMemoriaRepository {

  /**
   * Recupera la memoria activa con el id recibido por parámetro.
   * 
   * @param idMemoria Id {@link Memoria}
   * @return memoria
   */
  Optional<Memoria> findByIdAndActivoTrue(Long idMemoria);

  /**
   * Recupera la útlima memoria cuyo tipo no corresponda con el recibido por
   * parámetro, su comité sea el recibido y su número de referencia contenga el
   * año.
   * 
   * @param anio          Año a buscar en la referencia de la memoria.
   * @param tipoMemoriaId Identificador {@link TipoMemoria}
   * @param idComite      Identificador {@link Comite}
   * @return {@link Memoria}
   */
  Memoria findFirstByNumReferenciaContainingAndTipoMemoriaIdIsNotAndComiteIdOrderByNumReferenciaDesc(String anio,
      Long tipoMemoriaId, Long idComite);

  /**
   * Recupera la última memoria que contenga el número de referencia recibido y
   * que corresponda al comité.
   * 
   * @param numReferencia Número de la referencia.
   * @param idComite      Identificador {@link Comite}.
   * @return {@link Memoria}
   */
  Memoria findFirstByNumReferenciaContainingAndComiteIdOrderByNumReferenciaDesc(String numReferencia, Long idComite);

  /**
   * Devuelve una lista paginada de memorias activas asociadas al comité (que
   * también esté activo).
   * 
   * @param idComite Identificador {@link Comite}
   * @param paging   Datos de la paginación.
   * @return lista paginada de memorias
   */
  Page<Memoria> findByComiteIdAndActivoTrueAndComiteActivoTrue(Long idComite, Pageable paging);

}