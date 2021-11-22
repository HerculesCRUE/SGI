package org.crue.hercules.sgi.csp.service;

import lombok.RequiredArgsConstructor;
import org.crue.hercules.sgi.csp.model.TipoFacturacion;
import org.crue.hercules.sgi.csp.repository.TipoFacturacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TipoFacturacionService {

  private final TipoFacturacionRepository tiposfacturacionRepository;

  /**
   * Devuelve una lista de objetos de tipo {@link TipoFacturacion}
   * @return Lista de objetos de tipo {@link TipoFacturacion}
   */
  public List<TipoFacturacion> findAll() {

    return this.tiposfacturacionRepository.findAll();
  }

}
