package org.crue.hercules.sgi.csp.service;

import org.crue.hercules.sgi.csp.model.EstadoValidacionIP;
import org.crue.hercules.sgi.csp.repository.EstadoValidacionIPRepository;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EstadoValidacionIPService {

  private final EstadoValidacionIPRepository estadoValidacionIPRepository;

  /**
   * Devuelve una lista de objetos de tipo {@link EstadoValidacionIP} filtrados
   * según la query de entrada
   * 
   * @param query  parámetros de entrada
   * @param paging {@link Pageable}
   * @return lista de objetos de tipo {@link EstadoValidacionIP}
   */
  public Page<EstadoValidacionIP> findAll(String query, Pageable paging) {
    return estadoValidacionIPRepository.findAll(SgiRSQLJPASupport.toSpecification(query), paging);
  }
}
