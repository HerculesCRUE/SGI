package org.crue.hercules.sgi.eti.repository;

import java.util.Optional;

import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para {@link DocumentacionMemoria}.
 */

@Repository
public interface DocumentacionMemoriaRepository
    extends JpaRepository<DocumentacionMemoria, Long>, JpaSpecificationExecutor<DocumentacionMemoria> {

  /**
   * Obtener todas las entidades paginadas {@link DocumentacionMemoria} para una
   * determinada {@link Memoria} y un {@link Formulario}
   *
   * @param id           Id de {@link Memoria}.
   * @param idFormulario Id de {@link Formulario}
   * @param pageable     la información de la paginación.
   * @return la lista de entidades {@link DocumentacionMemoria} paginadas.
   */
  Page<DocumentacionMemoria> findByMemoriaIdAndTipoDocumentoFormularioId(Long id, Long idFormulario, Pageable pageable);

  /**
   * Recupera una {@link DocumentacionMemoria} por id y su memoria activa.
   * 
   * @param id           Id {@link DocumentacionMemoria}
   * @param idMemoria    Id {@link Memoria}
   * @param idFormulario Id {@link Formulario}
   * 
   * @return documentacion memoria
   */
  Optional<DocumentacionMemoria> findByIdAndMemoriaIdAndTipoDocumentoFormularioIdAndMemoriaActivoTrue(Long id,
      Long idMemoria, Long idFormulario);

  /**
   * Devuelve una lista paginada de la documentación de una memoria.
   * 
   * @param idMemoria Identificador de {@link Memoria}.
   * @param pageable  Datos de la paginación.
   * @return lista paginada de la documentación de una memoria
   */
  Page<DocumentacionMemoria> findByMemoriaIdAndMemoriaActivoTrue(Long idMemoria, Pageable pageable);

  /**
   * Comprueba si existen entidades {@link DocumentacionMemoria} para una
   * determinada {@link Memoria} y un {@link Formulario}
   *
   * @param id              Id de {@link Memoria}.
   * @param idTipoDocumento Id de {@link TipoDocumento}
   * @param idFormulario    Id de {@link Formulario}
   * @return true si existen {@link DocumentacionMemoria} / false si no existen.
   */
  boolean existsByMemoriaIdAndTipoDocumentoIdAndTipoDocumentoFormularioId(Long id, Long idTipoDocumento,
      Long idFormulario);
}
