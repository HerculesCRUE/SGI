package org.crue.hercules.sgi.eti.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.model.TipoMemoriaComite;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.repository.TipoMemoriaComiteRepository;
import org.crue.hercules.sgi.eti.service.TipoMemoriaComiteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link TipoMemoriaComite}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class TipoMemoriaComiteServiceImpl implements TipoMemoriaComiteService {

  /** Tipo memoria comite repository. */
  private final TipoMemoriaComiteRepository tipoMemoriaComiteRepository;

  /** Comite repository. */
  private final ComiteRepository comiteRepository;

  public TipoMemoriaComiteServiceImpl(TipoMemoriaComiteRepository tipoMemoriaComiteRepository,
      ComiteRepository comiteRepository) {
    this.tipoMemoriaComiteRepository = tipoMemoriaComiteRepository;
    this.comiteRepository = comiteRepository;
  }

  @Override
  public Page<TipoMemoria> findByComite(Long id, Pageable paging) {
    log.debug("findByComite(Long id, Pageable paging) - start");

    Assert.notNull(id, "El identificador del comité no puede ser null para recuperar sus tipos de memoria asociados.");

    return comiteRepository.findByIdAndActivoTrue(id).map(comite -> {
      Page<TipoMemoriaComite> tiposMemoriaComite = tipoMemoriaComiteRepository.findByComiteIdAndComiteActivoTrue(id,
          paging);

      List<TipoMemoria> listTipoMemoria = tiposMemoriaComite.getContent().stream()
          .map(tipoMemoria -> tipoMemoria.getTipoMemoria()).collect(Collectors.toList());

      log.debug("findByComite(Long id, Pageable paging) - end");
      return new PageImpl<TipoMemoria>(listTipoMemoria, paging, listTipoMemoria.size());
    }).orElseThrow(() -> new ComiteNotFoundException(id));

  }

}
