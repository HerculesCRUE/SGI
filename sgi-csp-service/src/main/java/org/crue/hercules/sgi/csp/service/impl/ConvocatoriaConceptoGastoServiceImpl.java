package org.crue.hercules.sgi.csp.service.impl;

import java.util.List;

import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConfiguracionSolicitudNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ConvocatoriaNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.UserNotAuthorizedToAccessConvocatoriaException;
import org.crue.hercules.sgi.csp.model.ConfiguracionSolicitud;
import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.Convocatoria.Estado;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.repository.ConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConfiguracionSolicitudRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaRepository;
import org.crue.hercules.sgi.csp.repository.specification.ConvocatoriaConceptoGastoSpecifications;
import org.crue.hercules.sgi.csp.service.ConvocatoriaConceptoGastoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para {@link ConvocatoriaConceptoGasto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ConvocatoriaConceptoGastoServiceImpl implements ConvocatoriaConceptoGastoService {

  private static final String MESSAGE_NUMBERO_MESES_MAYOR_DURACION_CONVOCATORIA = "El número de meses no puede ser mayor a la duración de meses de la convocatoria";
  private final ConvocatoriaConceptoGastoRepository repository;
  private final ConvocatoriaRepository convocatoriaRepository;
  private final ConceptoGastoRepository conceptoGastoRepository;
  private final ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;
  private final ConfiguracionSolicitudRepository configuracionSolicitudRepository;

  public ConvocatoriaConceptoGastoServiceImpl(ConvocatoriaConceptoGastoRepository repository,
      ConvocatoriaRepository convocatoriaRepository, ConceptoGastoRepository conceptoGastoRepository,
      ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository,
      ConfiguracionSolicitudRepository configuracionSolicitudRepository) {
    this.repository = repository;
    this.convocatoriaRepository = convocatoriaRepository;
    this.conceptoGastoRepository = conceptoGastoRepository;
    this.convocatoriaConceptoGastoCodigoEcRepository = convocatoriaConceptoGastoCodigoEcRepository;
    this.configuracionSolicitudRepository = configuracionSolicitudRepository;
  }

  /**
   * Guarda la entidad {@link ConvocatoriaConceptoGasto}.
   * 
   * @param convocatoriaConceptoGasto la entidad {@link ConvocatoriaConceptoGasto}
   *                                  a guardar.
   * @return ConvocatoriaConceptoGasto la entidad
   *         {@link ConvocatoriaConceptoGasto} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaConceptoGasto create(ConvocatoriaConceptoGasto convocatoriaConceptoGasto) {
    log.debug("create(ConvocatoriaConceptoGasto convocatoriaConceptoGasto) - start");

    Assert.isNull(convocatoriaConceptoGasto.getId(), "Id tiene que ser null para crear ConvocatoriaConceptoGasto");

    Assert.notNull(convocatoriaConceptoGasto.getConvocatoriaId(),
        "Id Convocatoria no puede ser null para crear ConvocatoriaConceptoGasto");

    if (convocatoriaConceptoGasto.getConceptoGasto() != null) {
      if (convocatoriaConceptoGasto.getConceptoGasto().getId() != null) {
        convocatoriaConceptoGasto.setConceptoGasto(
            conceptoGastoRepository.findById(convocatoriaConceptoGasto.getConceptoGasto().getId()).orElseThrow(
                () -> new ConceptoGastoNotFoundException(convocatoriaConceptoGasto.getConceptoGasto().getId())));
        Assert.isTrue(convocatoriaConceptoGasto.getConceptoGasto().getActivo(), "El ConceptoGasto debe estar activo");
      } else {
        convocatoriaConceptoGasto.setConceptoGasto(null);
      }
    }

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaConceptoGasto.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaConceptoGasto.getConvocatoriaId()));

    if (convocatoriaConceptoGasto.getMesInicial() != null && convocatoria.getDuracion() != null) {
      if (convocatoriaConceptoGasto.getMesFinal() != null) {
        Assert.isTrue(convocatoriaConceptoGasto.getMesInicial() < convocatoriaConceptoGasto.getMesFinal(),
            "El MesInicial debe ser anterior al MesFinal");
        Assert.isTrue(
            (convocatoriaConceptoGasto.getMesFinal() - convocatoriaConceptoGasto.getMesInicial()) <= convocatoria
                .getDuracion(),
            MESSAGE_NUMBERO_MESES_MAYOR_DURACION_CONVOCATORIA);
      } else {
        Assert.isTrue((12 - convocatoriaConceptoGasto.getMesInicial()) <= convocatoria.getDuracion(),
            MESSAGE_NUMBERO_MESES_MAYOR_DURACION_CONVOCATORIA);
      }
    }

    // se comprueba que no exista la convocatoria concepto gasto en el mismo rango
    // de meses que las que ya hay en bd
    Assert.isTrue(!existsConvocatoriaConceptoGastoConMesesSolapados(convocatoriaConceptoGasto),
        "El concepto de gasto '" + convocatoriaConceptoGasto.getConceptoGasto()
            + "' ya está presente y tiene un periodo de vigencia que se solapa con el indicado");

    ConvocatoriaConceptoGasto returnValue = repository.save(convocatoriaConceptoGasto);

    log.debug("create(ConvocatoriaConceptoGasto convocatoriaConceptoGasto) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ConvocatoriaConceptoGasto}.
   * 
   * @param convocatoriaConceptoGastoActualizar la entidad
   *                                            {@link ConvocatoriaConceptoGasto}
   *                                            a guardar.
   * @return ConvocatoriaConceptoGasto la entidad
   *         {@link ConvocatoriaConceptoGasto} persistida.
   */
  @Override
  @Transactional
  public ConvocatoriaConceptoGasto update(ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizar) {
    log.debug("update(ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizar) - start");

    Assert.notNull(convocatoriaConceptoGastoActualizar.getId(),
        "ConvocatoriaConceptoGasto id no puede ser null para actualizar un ConvocatoriaConceptoGasto");

    Assert.notNull(convocatoriaConceptoGastoActualizar.getConvocatoriaId(),
        "Id Convocatoria no puede ser null para actualizar ConvocatoriaConceptoGasto");

    if (convocatoriaConceptoGastoActualizar.getConceptoGasto() != null) {
      if (convocatoriaConceptoGastoActualizar.getConceptoGasto().getId() != null) {
        convocatoriaConceptoGastoActualizar.setConceptoGasto(
            conceptoGastoRepository.findById(convocatoriaConceptoGastoActualizar.getConceptoGasto().getId())
                .orElseThrow(() -> new ConceptoGastoNotFoundException(
                    convocatoriaConceptoGastoActualizar.getConceptoGasto().getId())));
      } else {
        convocatoriaConceptoGastoActualizar.setConceptoGasto(null);
      }
    }

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaConceptoGastoActualizar.getConvocatoriaId())
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaConceptoGastoActualizar.getConvocatoriaId()));

    if (convocatoriaConceptoGastoActualizar.getMesInicial() != null && convocatoria.getDuracion() != null) {
      if (convocatoriaConceptoGastoActualizar.getMesFinal() != null) {
        Assert.isTrue(
            convocatoriaConceptoGastoActualizar.getMesInicial() < convocatoriaConceptoGastoActualizar.getMesFinal(),
            "El MesInicial debe ser anterior al MesFinal");
        Assert.isTrue(
            (convocatoriaConceptoGastoActualizar.getMesFinal()
                - convocatoriaConceptoGastoActualizar.getMesInicial()) <= convocatoria.getDuracion(),
            MESSAGE_NUMBERO_MESES_MAYOR_DURACION_CONVOCATORIA);
      } else {
        Assert.isTrue((12 - convocatoriaConceptoGastoActualizar.getMesInicial()) <= convocatoria.getDuracion(),
            MESSAGE_NUMBERO_MESES_MAYOR_DURACION_CONVOCATORIA);
      }

    }

    // se comprueba que no exista la convocatoria concepto gasto en el mismo rango
    // de meses que las que ya hay en bd
    Assert.isTrue(!existsConvocatoriaConceptoGastoConMesesSolapados(convocatoriaConceptoGastoActualizar),
        "El concepto de gasto '" + convocatoriaConceptoGastoActualizar.getConceptoGasto()
            + "' ya está presente y tiene un periodo de vigencia que se solapa con el indicado");

    return repository.findById(convocatoriaConceptoGastoActualizar.getId()).map(convocatoriaConceptoGasto -> {

      convocatoriaConceptoGasto.setConceptoGasto(convocatoriaConceptoGastoActualizar.getConceptoGasto());
      convocatoriaConceptoGasto.setConvocatoriaId(convocatoriaConceptoGastoActualizar.getConvocatoriaId());
      convocatoriaConceptoGasto.setImporteMaximo(convocatoriaConceptoGastoActualizar.getImporteMaximo());
      convocatoriaConceptoGasto.setMesInicial(convocatoriaConceptoGastoActualizar.getMesInicial());
      convocatoriaConceptoGasto.setMesFinal(convocatoriaConceptoGastoActualizar.getMesFinal());
      convocatoriaConceptoGasto.setObservaciones(convocatoriaConceptoGastoActualizar.getObservaciones());
      convocatoriaConceptoGasto.setPermitido(convocatoriaConceptoGastoActualizar.getPermitido());

      ConvocatoriaConceptoGasto returnValue = repository.save(convocatoriaConceptoGasto);
      log.debug("update(ConvocatoriaConceptoGasto convocatoriaConceptoGastoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ConvocatoriaConceptoGastoNotFoundException(convocatoriaConceptoGastoActualizar.getId()));

  }

  /**
   * Elimina la {@link ConvocatoriaConceptoGasto}.
   *
   * @param id Id del {@link ConvocatoriaConceptoGasto}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ConvocatoriaConceptoGasto id no puede ser null para eliminar un ConvocatoriaConceptoGasto");

    repository.findById(id).map(convocatoriaConvocatoriaConceptoGasto -> convocatoriaConvocatoriaConceptoGasto)
        .orElseThrow(() -> new ConvocatoriaConceptoGastoNotFoundException(id));

    List<ConvocatoriaConceptoGastoCodigoEc> codigosEconomicos = convocatoriaConceptoGastoCodigoEcRepository
        .findAllByConvocatoriaConceptoGastoId(id);

    if (codigosEconomicos != null) {
      codigosEconomicos.stream()
          .forEach(codigoEconomico -> convocatoriaConceptoGastoCodigoEcRepository.deleteById(codigoEconomico.getId()));
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ConvocatoriaConceptoGasto} por su id.
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGasto}.
   * @return la entidad {@link ConvocatoriaConceptoGasto}.
   */
  @Override
  public ConvocatoriaConceptoGasto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ConvocatoriaConceptoGasto returnValue = repository.findById(id)
        .orElseThrow(() -> new ConvocatoriaConceptoGastoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link ConvocatoriaConceptoGasto} por id.
   *
   * @param id el id de la entidad {@link ConvocatoriaConceptoGasto}.
   * @return true si existe y false en caso contrario.
   */
  @Override
  public boolean existsById(final Long id) {
    log.debug("existsById(final Long id)  - start", id);
    final boolean existe = repository.existsById(id);
    log.debug("existsById(final Long id)  - end", id);
    return existe;
  }

  /**
   * Obtiene todas las entidades {@link ConvocatoriaConceptoGasto} paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link ConvocatoriaConceptoGasto} paginadas y
   *         filtradas.
   */
  @Override
  public Page<ConvocatoriaConceptoGasto> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<ConvocatoriaConceptoGasto> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<ConvocatoriaConceptoGasto> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaConceptoGasto} permitidos para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGasto} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaConceptoGasto> findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Pageable pageable) {
    log.debug("findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Pageable pageable)) - start");

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));
    if (hasAuthorityViewInvestigador()) {
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findByConvocatoriaId(convocatoriaId)
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(convocatoriaId));
      if (!convocatoria.getEstado().equals(Estado.REGISTRADA)
          || Boolean.FALSE.equals(configuracionSolicitud.getTramitacionSGI())) {
        throw new UserNotAuthorizedToAccessConvocatoriaException();
      }
    }

    Page<ConvocatoriaConceptoGasto> returnValue = repository
        .findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoTrue(convocatoriaId, pageable);
    log.debug("findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene las {@link ConvocatoriaConceptoGasto} NO permitidos para una
   * {@link Convocatoria}.
   *
   * @param convocatoriaId el id de la {@link Convocatoria}.
   * @param pageable       la información de la paginación.
   * @return la lista de entidades {@link ConvocatoriaConceptoGasto} de la
   *         {@link Convocatoria} paginadas.
   */
  @Override
  public Page<ConvocatoriaConceptoGasto> findAllByConvocatoriaAndPermitidoFalse(Long convocatoriaId,
      Pageable pageable) {
    log.debug("findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Pageable pageable)) - start");

    Convocatoria convocatoria = convocatoriaRepository.findById(convocatoriaId)
        .orElseThrow(() -> new ConvocatoriaNotFoundException(convocatoriaId));
    if (hasAuthorityViewInvestigador()) {
      ConfiguracionSolicitud configuracionSolicitud = configuracionSolicitudRepository
          .findByConvocatoriaId(convocatoriaId)
          .orElseThrow(() -> new ConfiguracionSolicitudNotFoundException(convocatoriaId));
      if (!convocatoria.getEstado().equals(Estado.REGISTRADA)
          || Boolean.FALSE.equals(configuracionSolicitud.getTramitacionSGI())) {
        throw new UserNotAuthorizedToAccessConvocatoriaException();
      }
    }

    Page<ConvocatoriaConceptoGasto> returnValue = repository
        .findAllByConvocatoriaIdAndConceptoGastoActivoTrueAndPermitidoFalse(convocatoriaId, pageable);
    log.debug("findAllByConvocatoriaAndPermitidoTrue(Long convocatoriaId, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Se valida la unicidad del concepto de gasto. Para una {@link Convocatoria} el
   * mismo concepto de gasto solo puede aparecer una vez, salvo que lo haga en
   * periodos de meses no solapados (independientemente del valor del campo
   * "permitido").
   * 
   * @param convocatoriaConceptoGasto la {@link ConvocatoriaConceptoGasto} a
   *                                  evaluar
   * @return true validación correcta/ false validacion incorrecta
   */
  private boolean existsConvocatoriaConceptoGastoConMesesSolapados(
      ConvocatoriaConceptoGasto convocatoriaConceptoGasto) {
    log.debug("existsConvocatoriaConceptoGastoConMesesSolapados(ConvocatoriaConceptoGasto convocatoriaConceptoGasto)");

    Specification<ConvocatoriaConceptoGasto> specByConvocatoria = ConvocatoriaConceptoGastoSpecifications
        .byConvocatoria(convocatoriaConceptoGasto.getConvocatoriaId());
    Specification<ConvocatoriaConceptoGasto> specByConceptoGastoConvocatoriaActiva = ConvocatoriaConceptoGastoSpecifications
        .byConvocatoriaActiva();
    Specification<ConvocatoriaConceptoGasto> specByConceptoGasto = ConvocatoriaConceptoGastoSpecifications
        .byConceptoGasto(convocatoriaConceptoGasto.getConceptoGasto());
    Specification<ConvocatoriaConceptoGasto> specByRangoMesesSolapados = ConvocatoriaConceptoGastoSpecifications
        .byRangoMesesSolapados(convocatoriaConceptoGasto.getMesInicial(), convocatoriaConceptoGasto.getMesFinal());
    Specification<ConvocatoriaConceptoGasto> specByIdNotEqual = ConvocatoriaConceptoGastoSpecifications
        .byIdNotEqual(convocatoriaConceptoGasto.getId());
    Specification<ConvocatoriaConceptoGasto> specByPermitido = ConvocatoriaConceptoGastoSpecifications
        .byPermitido(convocatoriaConceptoGasto.getPermitido());

    Specification<ConvocatoriaConceptoGasto> specs = Specification.where(specByConvocatoria)
        .and(specByConceptoGastoConvocatoriaActiva).and(specByConceptoGasto).and(specByRangoMesesSolapados)
        .and(specByIdNotEqual).and(specByPermitido);

    Page<ConvocatoriaConceptoGasto> convocatoriaConceptoGastos = repository.findAll(specs, Pageable.unpaged());

    Boolean returnValue = !convocatoriaConceptoGastos.isEmpty();
    log.debug(
        "existsConvocatoriaConceptoGastoConMesesSolapados(ConvocatoriaConceptoGasto convocatoriaConceptoGasto) - end");

    return returnValue;
  }

  private boolean hasAuthorityViewInvestigador() {
    return SgiSecurityContextHolder.hasAuthorityForAnyUO("CSP-CON-INV-V");
  }

}
