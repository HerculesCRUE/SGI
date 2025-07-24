package org.crue.hercules.sgi.csp.service.impl;

import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaEntidadGestoraNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadGestora;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaEntidadGestoraRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaEntidadGestoraSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaEntidadGestoraService;
import org.crue.hercules.sgi.csp.util.AssertHelper;
import org.crue.hercules.sgi.csp.util.ConvocatoriaAuthorityHelper;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para gestion {@link ConvocatoriaEntidadGestora}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaEntidadGestoraServiceImpl implements ConvocatoriaEntidadGestoraService {
  private static final String MSG_KEY_ENTIDAD_REF = "entidadRef";

  private final ConvocatoriaEntidadGestoraRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConvocatoriaAuthorityHelper authorityHelper;

  public ConvocatoriaEntidadGestoraServiceImpl(ConvocatoriaEntidadGestoraRepository repository,
      ConvocatoriaRepository convocatoriaRepository,
      ConvocatoriaAuthorityHelper authorityHelper) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaEntidadGestora}.
   * 
   * @param convocatoriaEntidadGestora la entidad
   *                                   {@link ConvocatoriaEntidadGestora} a
   *                                   guardar.
   * @return ConvocatoriaEntidadGestora la entidad
   *         {@link ConvocatoriaEntidadGestora} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadGestora create(ConvocatoriaEntidadGestora convocatoriaEntidadGestora) {
    log.debug("create(ConvocatoriaEntidadGestora convocatoriaEntidadGestora) - start");

    AssertHelper.idIsNull(convocatoriaEntidadGestora.getId(), ConvocatoriaEntidadGestora.class);
    AssertHelper.idNotNull(convocatoriaEntidadGestora.getConvocatoriaId(), Convocatoria.class);
    AssertHelper.fieldNotNull(convocatoriaEntidadGestora.getEntidadRef(), ConvocatoriaEntidadGestora.class,
        MSG_KEY_ENTIDAD_REF);

    if (!convocatoriaRepository.existsById(convocatoriaEntidadGestora.getConvocatoriaId())) {
      throw new ConvocatoriaNotFoundException(convocatoriaEntidadGestora.getConvocatoriaId());
    }

    AssertHelper.fieldExists(
        !repository.findByConvocatoriaIdAndEntidadRef(convocatoriaEntidadGestora.getConvocatoriaId(),
            convocatoriaEntidadGestora.getEntidadRef()).isPresent(),
        Convocatoria.class, MSG_KEY_ENTIDAD_REF);

    ConvocatoriaEntidadGestora returnValue = repository.save(convocatoriaEntidadGestora);

    log.debug("create(ConvocatoriaEntidadGestora convocatoriaEntidadGestora) - end");
    return returnValue;
  }

  /**
   * Actualiza los datos del {@link ConvocatoriaEntidadGestora}.
   * 
   * @param convocatoriaEntidadGestora convocatoriaEntidadGestora
   *                                   {@link ConvocatoriaEntidadGestora} con los
   *                                   datos actualizados.
   * @return {@link ConvocatoriaEntidadGestora} actualizado.
   */
  @Override
  @Transactional
  public ConvocatoriaEntidadGestora update(ConvocatoriaEntidadGestora convocatoriaEntidadGestora) {
    log.debug("update(ConvocatoriaEntidadGestora convocatoriaEntidadGestora) - start");
    Long id = convocatoriaEntidadGestora.getId();
    AssertHelper.idNotNull(id, ConvocatoriaEntidadGestora.class);
    AssertHelper.idNotNull(convocatoriaEntidadGestora.getConvocatoriaId(), Convocatoria.class);
    AssertHelper.fieldNotNull(convocatoriaEntidadGestora.getEntidadRef(), convocatoriaEntidadGestora.getClass(),
        MSG_KEY_ENTIDAD_REF);
    return repository.findById(id).map(entidadGestora -> {
      entidadGestora.setConvocatoriaId(convocatoriaEntidadGestora.getConvocatoriaId());
      entidadGestora.setEntidadRef(convocatoriaEntidadGestora.getEntidadRef());
      ConvocatoriaEntidadGestora returnValue = repository.save(entidadGestora);
      log.debug("update(ConvocatoriaEntidadGestora convocatoriaEntidadGestora) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaEntidadGestoraNotFoundException(id));
  }

  /**
   * Elimina la {@link ConvocatoriaEntidadGestora}.
   *
   * @param id Id del {@link ConvocatoriaEntidadGestora}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    AssertHelper.idNotNull(id, ConvocatoriaEntidadGestora.class);
    if (!repository.existsById(id)) {
      throw new ConvocatoriaEntidadGestoraNotFoundException(id);
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene las {@link ConvocatoriaEntidadGestora} para una {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param query          la información del filtro.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaEntidadGestora} de la
   *         {@link Convocatoria} paginadas.
   */
  public Page<ConvocatoriaEntidadGestora> findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) {
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - start");

    authorityHelper.checkUserHasAuthorityViewConvocatoria(convocatoriaId);

    Specification<ConvocatoriaEntidadGestora> specs = ConvocatoriaEntidadGestoraSpecifications
        .byConvocatoriaId(convocatoriaId).and(SgiRSQLJPASupport.toSpecification(query));

    Page<ConvocatoriaEntidadGestora> returnValue = repository.findAll(specs, pageable);
    log.debug("findAllByConvocatoria(Long convocatoriaId, String query, Pageable pageable) - end");
    return returnValue;
  }

}
