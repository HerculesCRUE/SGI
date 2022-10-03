package org.crue.hercules.sgi.prc.repository.custom;

import org.crue.hercules.sgi.prc.dto.ComiteEditorialResumen;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

/**
 * Custom repository para {@link ComiteEditorialResumen}.
 */
public interface CustomComiteEditorialRepository {
  /**
   * Recupera todas los {@link ComiteEditorialResumen} paginadas y/o filtradas
   * 
   * @param specs    Specification para filtrar
   * @param pageable la información de la paginación.
   * @return Listado paginado y/o filtrado de {@link ComiteEditorialResumen}
   */
  public Page<ComiteEditorialResumen> findAllComitesEditoriales(Specification<ProduccionCientifica> specs,
      Pageable pageable);
}
