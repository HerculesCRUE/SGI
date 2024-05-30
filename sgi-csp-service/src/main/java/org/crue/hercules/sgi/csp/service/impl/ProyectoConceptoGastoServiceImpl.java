package org.crue.hercules.sgi.csp.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.csp.exceptions.ConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoConceptoGastoNotFoundException;
import org.crue.hercules.sgi.csp.exceptions.ProyectoNotFoundException;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGasto;
import org.crue.hercules.sgi.csp.model.ConvocatoriaConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGasto;
import org.crue.hercules.sgi.csp.model.ProyectoConceptoGastoCodigoEc;
import org.crue.hercules.sgi.csp.repository.ConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ConvocatoriaConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoConceptoGastoCodigoEcRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoConceptoGastoRepository;
import org.crue.hercules.sgi.csp.repository.ProyectoRepository;
import org.crue.hercules.sgi.csp.repository.predicate.ProyectoConceptoGastoPredicateResolver;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoConceptoGastoCodigoEcSpecifications;
import org.crue.hercules.sgi.csp.repository.specification.ProyectoConceptoGastoSpecifications;
import org.crue.hercules.sgi.csp.service.ProyectoConceptoGastoService;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para {@link ProyectoConceptoGasto}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProyectoConceptoGastoServiceImpl implements ProyectoConceptoGastoService {

  private final ProyectoConceptoGastoRepository repository;
  private final ProyectoRepository proyectoRepository;
  private final ConceptoGastoRepository conceptoGastoRepository;
  private final ProyectoConceptoGastoCodigoEcRepository proyectoConceptoGastoCodigoEcRepository;
  private final ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository;

  public ProyectoConceptoGastoServiceImpl(ProyectoConceptoGastoRepository repository,
      ProyectoRepository proyectoRepository, ConceptoGastoRepository conceptoGastoRepository,
      ProyectoConceptoGastoCodigoEcRepository proyectoConceptoGastoCodigoEcRepository,
      ConvocatoriaConceptoGastoCodigoEcRepository convocatoriaConceptoGastoCodigoEcRepository) {
    this.repository = repository;
    this.proyectoRepository = proyectoRepository;
    this.conceptoGastoRepository = conceptoGastoRepository;
    this.proyectoConceptoGastoCodigoEcRepository = proyectoConceptoGastoCodigoEcRepository;
    this.convocatoriaConceptoGastoCodigoEcRepository = convocatoriaConceptoGastoCodigoEcRepository;
  }

  /**
   * Guarda la entidad {@link ProyectoConceptoGasto}.
   * 
   * @param proyectoConceptoGasto la entidad {@link ProyectoConceptoGasto} a
   *                              guardar.
   * @return la entidad {@link ProyectoConceptoGasto} persistida.
   */
  @Override
  @Transactional
  public ProyectoConceptoGasto create(ProyectoConceptoGasto proyectoConceptoGasto) {
    log.debug("create(ProyectoConceptoGasto proyectoConceptoGasto) - start");

    Assert.isNull(proyectoConceptoGasto.getId(), "Id tiene que ser null para crear ProyectoConceptoGasto");

    Assert.notNull(proyectoConceptoGasto.getProyectoId(),
        "Id Proyecto no puede ser null para crear ProyectoConceptoGasto");

    proyectoConceptoGasto.setConceptoGasto(
        conceptoGastoRepository.findById(proyectoConceptoGasto.getConceptoGasto().getId()).orElseThrow(
            () -> new ConceptoGastoNotFoundException(proyectoConceptoGasto.getConceptoGasto().getId())));
    Assert.isTrue(proyectoConceptoGasto.getConceptoGasto().getActivo(), "El ConceptoGasto debe estar activo");

    Proyecto proyecto = proyectoRepository.findById(proyectoConceptoGasto.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoConceptoGasto.getProyectoId()));

    Instant proyectoFechaFin = proyecto.getFechaFinDefinitiva() != null ? proyecto.getFechaFinDefinitiva()
        : proyecto.getFechaFin();

    if (proyectoConceptoGasto.getFechaInicio() != null && proyecto.getFechaInicio() != null) {
      if (proyectoConceptoGasto.getFechaFin() != null && proyectoFechaFin != null) {
        Assert.isTrue(!proyectoConceptoGasto.getFechaFin().isAfter(proyectoFechaFin),
            "La fecha de fin no puede ser posterior a la fecha de fin del proyecto");

        if (proyectoConceptoGasto.getFechaInicio() != null) {
          Assert.isTrue(proyectoConceptoGasto.getFechaInicio().isBefore(proyectoConceptoGasto.getFechaFin()),
              "La fecha de inicio debe ser anterior a la fecha de fin");
        }
      } else if (proyectoConceptoGasto.getFechaInicio() != null) {
        Assert.isTrue(proyectoFechaFin == null || proyectoConceptoGasto.getFechaInicio().isBefore(proyectoFechaFin),
            "La fecha de inicio no puede ser posterior a la fecha de fin del proyecto");
      }
    }

    // se comprueba que no exista el proyecto concepto gasto en el mismo rango
    // de meses que las que ya hay en bd
    Assert.isTrue(!existsProyectoConceptoGastoConMesesSolapados(proyectoConceptoGasto),
        "El concepto de gasto '" + proyectoConceptoGasto.getConceptoGasto()
            + "' ya está presente y tiene un periodo de vigencia que se solapa con el indicado");

    ProyectoConceptoGasto returnValue = repository.save(proyectoConceptoGasto);

    log.debug("create(ProyectoConceptoGasto proyectoConceptoGasto) - end");
    return returnValue;
  }

  /**
   * Actualiza la entidad {@link ProyectoConceptoGasto}.
   * 
   * @param proyectoConceptoGastoActualizar la entidad
   *                                        {@link ProyectoConceptoGasto} a
   *                                        guardar.
   * @return ProyectoConceptoGasto la entidad {@link ProyectoConceptoGasto}
   *         persistida.
   */
  @Override
  @Transactional
  public ProyectoConceptoGasto update(ProyectoConceptoGasto proyectoConceptoGastoActualizar) {
    log.debug("update(ProyectoConceptoGasto proyectoConceptoGastoActualizar) - start");

    Assert.notNull(proyectoConceptoGastoActualizar.getId(),
        "ProyectoConceptoGasto id no puede ser null para actualizar un ProyectoConceptoGasto");

    Assert.notNull(proyectoConceptoGastoActualizar.getProyectoId(),
        "Id Proyecto no puede ser null para actualizar ProyectoConceptoGasto");

    proyectoConceptoGastoActualizar.setConceptoGasto(
        conceptoGastoRepository.findById(proyectoConceptoGastoActualizar.getConceptoGasto().getId()).orElseThrow(
            () -> new ConceptoGastoNotFoundException(proyectoConceptoGastoActualizar.getConceptoGasto().getId())));

    Proyecto proyecto = proyectoRepository.findById(proyectoConceptoGastoActualizar.getProyectoId())
        .orElseThrow(() -> new ProyectoNotFoundException(proyectoConceptoGastoActualizar.getProyectoId()));

    Instant proyectoFechaFin = proyecto.getFechaFinDefinitiva() != null ? proyecto.getFechaFinDefinitiva()
        : proyecto.getFechaFin();

    if (proyectoConceptoGastoActualizar.getFechaInicio() != null && proyecto.getFechaInicio() != null) {
      if (proyectoConceptoGastoActualizar.getFechaFin() != null && proyectoFechaFin != null) {
        Assert.isTrue(!proyectoConceptoGastoActualizar.getFechaFin().isAfter(proyectoFechaFin),
            "La fecha de fin no puede ser posterior a la fecha de fin del proyecto");

        if (proyectoConceptoGastoActualizar.getFechaInicio() != null) {
          Assert.isTrue(
              proyectoConceptoGastoActualizar.getFechaInicio()
                  .isBefore(proyectoConceptoGastoActualizar.getFechaFin()),
              "La fecha de inicio debe ser anterior a la fecha de fin");
        }
      } else if (proyectoConceptoGastoActualizar.getFechaInicio() != null) {
        Assert.isTrue(
            proyectoFechaFin == null || proyectoConceptoGastoActualizar.getFechaInicio().isBefore(proyectoFechaFin),
            "La fecha de inicio no puede ser posterior a la fecha de fin del proyecto");
      }
    }

    // se comprueba que no exista el proyecto concepto gasto en el mismo rango
    // de meses que las que ya hay en bd
    Assert.isTrue(!existsProyectoConceptoGastoConMesesSolapados(proyectoConceptoGastoActualizar),
        "El concepto de gasto '" + proyectoConceptoGastoActualizar.getConceptoGasto()
            + "' ya está presente y tiene un periodo de vigencia que se solapa con el indicado");

    Assert.isTrue(!existsProyectoConceptoGastoAndCodigoEconomicoConMesesSolapados(proyectoConceptoGastoActualizar),
        "El concepto gasto '" + proyectoConceptoGastoActualizar.getConceptoGasto()
            + "' tiene códigos económicos que se solapan con otros códigos económicos del proyecto");

    return repository.findById(proyectoConceptoGastoActualizar.getId()).map(proyectoConceptoGasto -> {

      proyectoConceptoGasto.setConceptoGasto(proyectoConceptoGastoActualizar.getConceptoGasto());
      proyectoConceptoGasto.setProyectoId(proyectoConceptoGastoActualizar.getProyectoId());
      proyectoConceptoGasto.setImporteMaximo(proyectoConceptoGastoActualizar.getImporteMaximo());
      proyectoConceptoGasto.setFechaInicio(proyectoConceptoGastoActualizar.getFechaInicio());
      proyectoConceptoGasto.setFechaFin(proyectoConceptoGastoActualizar.getFechaFin());
      proyectoConceptoGasto.setObservaciones(proyectoConceptoGastoActualizar.getObservaciones());
      proyectoConceptoGasto.setPermitido(proyectoConceptoGastoActualizar.getPermitido());

      ProyectoConceptoGasto returnValue = repository.save(proyectoConceptoGasto);
      log.debug("update(ProyectoConceptoGasto proyectoConceptoGastoActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new ProyectoConceptoGastoNotFoundException(proyectoConceptoGastoActualizar.getId()));

  }

  /**
   * Elimina la {@link ProyectoConceptoGasto}.
   *
   * @param id Id del {@link ProyectoConceptoGasto}.
   */
  @Override
  @Transactional
  public void delete(Long id) {
    log.debug("delete(Long id) - start");

    Assert.notNull(id, "ProyectoConceptoGasto id no puede ser null para eliminar un ProyectoConceptoGasto");

    if (!repository.existsById(id)) {
      throw new ProyectoConceptoGastoNotFoundException(id);
    }

    List<ProyectoConceptoGastoCodigoEc> codigosEconomicos = proyectoConceptoGastoCodigoEcRepository
        .findAllByProyectoConceptoGastoId(id);

    if (codigosEconomicos != null) {
      codigosEconomicos.stream()
          .forEach(codigoEconomico -> proyectoConceptoGastoCodigoEcRepository.deleteById(codigoEconomico.getId()));
    }

    repository.deleteById(id);
    log.debug("delete(Long id) - end");
  }

  /**
   * Obtiene {@link ProyectoConceptoGasto} por su id.
   *
   * @param id el id de la entidad {@link ProyectoConceptoGasto}.
   * @return la entidad {@link ProyectoConceptoGasto}.
   */
  @Override
  public ProyectoConceptoGasto findById(Long id) {
    log.debug("findById(Long id)  - start");
    final ProyectoConceptoGasto returnValue = repository.findById(id)
        .orElseThrow(() -> new ProyectoConceptoGastoNotFoundException(id));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Comprueba la existencia del {@link ProyectoConceptoGasto} por id.
   *
   * @param id el id de la entidad {@link ProyectoConceptoGasto}.
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
   * Obtiene todas las entidades {@link ProyectoConceptoGasto} paginadas y
   * filtradas.
   *
   * @param query  información del filtro.
   * @param paging información de paginación.
   * @return el listado de entidades {@link ProyectoConceptoGasto} paginadas y
   *         filtradas.
   */
  @Override
  public Page<ProyectoConceptoGasto> findAll(String query, Pageable paging) {
    log.debug("findAll(String query, Pageable paging) - start");
    Specification<ProyectoConceptoGasto> specs = SgiRSQLJPASupport.toSpecification(query,
        ProyectoConceptoGastoPredicateResolver.getInstance());

    Page<ProyectoConceptoGasto> returnValue = repository.findAll(specs, paging);
    log.debug("findAll(String query, Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ProyectoConceptoGasto} permitidos para un
   * {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGasto} del
   *         {@link Proyecto} paginadas.
   */
  @Override
  public Page<ProyectoConceptoGasto> findAllByProyectoAndPermitidoTrue(Long proyectoId, Pageable pageable) {
    log.debug("findAllByProyectoAndPermitidoTrue(Long proyectoId, Pageable pageable)) - start");
    Page<ProyectoConceptoGasto> returnValue = repository
        .findAllByProyectoIdAndConceptoGastoActivoTrueAndPermitidoTrue(proyectoId, pageable);
    log.debug("findAllByProyectoAndPermitidoTrue(Long proyectoId, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Comprueba si existen diferencias entre los codigos economicos del
   * {@link ProyectoConceptoGasto} y el {@link ConvocatoriaConceptoGasto}
   * relacionado.
   *
   * @param id el id de la entidad {@link ProyectoConceptoGasto}.
   * @return true si existen diferencias y false en caso contrario.
   */
  @Override
  public boolean hasDifferencesCodigosEcConvocatoria(final Long id) {
    log.debug("hasDifferencesCodigosEcConvocatoria(final Long id)  - start", id);
    final boolean returnValue = repository.findById(id).map(proyectoConceptoGasto -> {
      List<ProyectoConceptoGastoCodigoEc> conceptosGastoProyecto = proyectoConceptoGastoCodigoEcRepository
          .findAllByProyectoConceptoGastoId(proyectoConceptoGasto.getId());

      if (proyectoConceptoGasto.getConvocatoriaConceptoGastoId() != null) {
        List<ConvocatoriaConceptoGastoCodigoEc> conceptosGastoConvocatoria = convocatoriaConceptoGastoCodigoEcRepository
            .findAllByConvocatoriaConceptoGastoId(proyectoConceptoGasto.getConvocatoriaConceptoGastoId());

        if (conceptosGastoProyecto.size() == conceptosGastoConvocatoria.size()) {
          return conceptosGastoProyecto.stream()
              .anyMatch(conceptoGastoProyecto -> compareWithCodigosEcConvocatoria(conceptoGastoProyecto,
                  conceptosGastoConvocatoria));
        } else {
          return true;
        }
      } else {
        return !conceptosGastoProyecto.isEmpty();
      }
    }).orElseThrow(() -> new ProyectoConceptoGastoNotFoundException(id));

    log.debug("hasDifferencesCodigosEcConvocatoria(final Long id)  - end", id);
    return returnValue;
  }

  /**
   * Comprueba si alguno de los {@link ProyectoConceptoGasto} del {@link Proyecto}
   * tienen fechas
   * 
   * @param proyectoId el id del {@link Proyecto}.
   * @return true si existen y false en caso contrario.
   */
  @Override
  public boolean proyectoHasConceptosGastoWithDates(Long proyectoId) {
    log.debug("proyectoHasConceptosGastoWithDates({})  - start", proyectoId);

    Specification<ProyectoConceptoGasto> specs = ProyectoConceptoGastoSpecifications.byProyecto(proyectoId)
        .and(ProyectoConceptoGastoSpecifications.withFechaInicioOrFechaFin());

    boolean hasConceptosGastoWithDates = repository.count(specs) > 0;
    log.debug("proyectoHasConceptosGastoWithDates({})  - end", proyectoId);
    return hasConceptosGastoWithDates;
  }

  private boolean compareWithCodigosEcConvocatoria(ProyectoConceptoGastoCodigoEc conceptoGastoProyecto,
      List<ConvocatoriaConceptoGastoCodigoEc> conceptosGastoConvocatoria) {

    ConvocatoriaConceptoGastoCodigoEc conceptoGastoConvocatoriaEncontrado = conceptosGastoConvocatoria.stream()
        .filter(conceptoGastoConvocatoria -> Objects.equals(conceptoGastoConvocatoria.getId(), conceptoGastoProyecto
            .getConvocatoriaConceptoGastoCodigoEcId()))
        .findFirst().orElse(null);

    if (conceptoGastoConvocatoriaEncontrado != null) {
      return !conceptoGastoProyecto.getCodigoEconomicoRef()
          .equals(conceptoGastoConvocatoriaEncontrado.getCodigoEconomicoRef())
          || !Objects.equals(conceptoGastoProyecto.getFechaInicio(),
              conceptoGastoConvocatoriaEncontrado.getFechaInicio())
          || !Objects.equals(conceptoGastoProyecto.getFechaFin(), conceptoGastoConvocatoriaEncontrado.getFechaFin())
          || (StringUtils.isNotBlank(conceptoGastoProyecto.getObservaciones()) != StringUtils
              .isNotBlank(conceptoGastoConvocatoriaEncontrado.getObservaciones())
              && !conceptoGastoProyecto.getObservaciones()
                  .equals(conceptoGastoConvocatoriaEncontrado.getObservaciones()));
    } else {
      return true;
    }
  }

  /**
   * Obtiene los {@link ProyectoConceptoGasto} NO permitidos para un *
   * {@link Proyecto}.
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @param pageable   la información de la paginación.
   * @return la lista de entidades {@link ProyectoConceptoGasto} del
   *         {@link Proyecto} paginadas.
   */
  @Override
  public Page<ProyectoConceptoGasto> findAllByProyectoAndPermitidoFalse(Long proyectoId, Pageable pageable) {
    log.debug("findAllByProyectoAndPermitidoTrue(Long proyectoId, Pageable pageable)) - start");
    Page<ProyectoConceptoGasto> returnValue = repository
        .findAllByProyectoIdAndConceptoGastoActivoTrueAndPermitidoFalse(proyectoId, pageable);
    log.debug("findAllByProyectoAndPermitidoTrue(Long proyectoId, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene los {@link ProyectoConceptoGasto} de un {@link Proyecto}
   *
   * @param proyectoId el id del {@link Proyecto}.
   * @return la lista de entidades {@link ProyectoConceptoGasto} del
   *         {@link Proyecto}.
   */
  @Override
  public List<ProyectoConceptoGasto> findAllByProyectoId(Long proyectoId) {
    log.debug("findAllByProyectoId(Long proyectoId) - start");
    List<ProyectoConceptoGasto> returnValue = repository
        .findAllByProyectoIdAndConceptoGastoActivoTrue(proyectoId);
    log.debug("findAllByProyectoId(Long proyectoId) - end");
    return returnValue;
  }

  /**
   * Se valida la unicidad del concepto de gasto. Para un {@link Proyecto} el
   * mismo concepto de gasto solo puede aparecer una vez, salvo que lo haga en
   * periodos de meses no solapados (independientemente del valor del campo
   * "permitido").
   * 
   * @param proyectoConceptoGasto el {@link ProyectoConceptoGasto} a evaluar
   * @return true validación correcta/ false validacion incorrecta
   */
  @Override
  public boolean existsProyectoConceptoGastoConMesesSolapados(ProyectoConceptoGasto proyectoConceptoGasto) {
    log.debug("existsProyectoConceptoGastoConMesesSolapados(ProyectoConceptoGasto proyectoConceptoGasto)");

    Specification<ProyectoConceptoGasto> specByProyecto = ProyectoConceptoGastoSpecifications
        .byProyecto(proyectoConceptoGasto.getProyectoId());
    Specification<ProyectoConceptoGasto> specByConceptoGastoProyectoActiva = ProyectoConceptoGastoSpecifications
        .byProyectoActivo();
    Specification<ProyectoConceptoGasto> specByConceptoGasto = ProyectoConceptoGastoSpecifications
        .byConceptoGasto(proyectoConceptoGasto.getConceptoGasto());
    Specification<ProyectoConceptoGasto> specByRangoMesesSolapados = ProyectoConceptoGastoSpecifications
        .byRangoFechasSolapados(proyectoConceptoGasto.getFechaInicio(), proyectoConceptoGasto.getFechaFin());
    Specification<ProyectoConceptoGasto> specByIdNotEqual = ProyectoConceptoGastoSpecifications
        .byIdNotEqual(proyectoConceptoGasto.getId());
    Specification<ProyectoConceptoGasto> specByPermitido = ProyectoConceptoGastoSpecifications
        .byPermitido(proyectoConceptoGasto.getPermitido());

    Specification<ProyectoConceptoGasto> specs = Specification.where(specByProyecto)
        .and(specByConceptoGastoProyectoActiva).and(specByConceptoGasto).and(specByRangoMesesSolapados)
        .and(specByIdNotEqual).and(specByPermitido);

    return repository.count(specs) > 0;
  }

  private boolean existsProyectoConceptoGastoAndCodigoEconomicoConMesesSolapados(
      ProyectoConceptoGasto proyectoConceptoGasto) {

    Specification<ProyectoConceptoGasto> specByProyecto = ProyectoConceptoGastoSpecifications
        .byProyecto(proyectoConceptoGasto.getProyectoId());
    Specification<ProyectoConceptoGasto> specByIdNotEqual = ProyectoConceptoGastoSpecifications
        .byIdNotEqual(proyectoConceptoGasto.getId());
    Specification<ProyectoConceptoGasto> specByRangoMesesSolapados = ProyectoConceptoGastoSpecifications
        .byRangoFechasSolapados(proyectoConceptoGasto.getFechaInicio(), proyectoConceptoGasto.getFechaFin());
    Specification<ProyectoConceptoGasto> specByConceptoGasto = ProyectoConceptoGastoSpecifications
        .byConceptoGasto(proyectoConceptoGasto.getConceptoGasto());

    Specification<ProyectoConceptoGasto> specs = Specification.where(specByProyecto).and(specByIdNotEqual)
        .and(specByRangoMesesSolapados).and(specByConceptoGasto);

    List<ProyectoConceptoGasto> proyectoConceptosGastoSolapados = repository.findAll(specs);

    Specification<ProyectoConceptoGastoCodigoEc> specCodigoEcByConceptosGasto = ProyectoConceptoGastoCodigoEcSpecifications
        .byProyectoConceptosGastoIds(
            proyectoConceptosGastoSolapados.stream().map(ProyectoConceptoGasto::getId).collect(Collectors.toList()));

    Specification<ProyectoConceptoGastoCodigoEc> specCodigoEcByConceptoGastoId = ProyectoConceptoGastoCodigoEcSpecifications
        .byProyectoConceptoGasto(proyectoConceptoGasto.getId());

    List<ProyectoConceptoGastoCodigoEc> proyectoCodigosEconomicosSolapados = proyectoConceptoGastoCodigoEcRepository
        .findAll(specCodigoEcByConceptosGasto);

    List<ProyectoConceptoGastoCodigoEc> proyectoCodigosEconomicosConceptoGasto = proyectoConceptoGastoCodigoEcRepository
        .findAll(specCodigoEcByConceptoGastoId);

    return proyectoCodigosEconomicosSolapados.stream()
        .anyMatch(
            codigoEconomico -> proyectoCodigosEconomicosConceptoGasto.stream()
                .anyMatch(codigo -> codigo.getCodigoEconomicoRef().equals(codigoEconomico.getCodigoEconomicoRef()))
                && proyectoConceptosGastoSolapados.stream()
                    .anyMatch(concepto -> concepto.getId().equals(codigoEconomico.getProyectoConceptoGastoId())
                        && isConceptosGastoSolapados(proyectoConceptoGasto, concepto)));
  }

  private boolean isConceptosGastoSolapados(ProyectoConceptoGasto proyectoConceptoGastoA,
      ProyectoConceptoGasto proyectoConceptoGastoB) {
    Instant fechaInicioProyectoConceptoGastoA = proyectoConceptoGastoA.getFechaInicio() != null
        ? proyectoConceptoGastoA.getFechaInicio()
        : Instant.MIN;
    Instant fechaFinProyectoConceptoGastoA = proyectoConceptoGastoA.getFechaFin() != null
        ? proyectoConceptoGastoA.getFechaFin()
        : Instant.MAX;
    Instant fechaInicioProyectoConceptoGastoB = proyectoConceptoGastoB.getFechaInicio() != null
        ? proyectoConceptoGastoB.getFechaInicio()
        : Instant.MIN;
    Instant fechaFinProyectoConceptoGastoB = proyectoConceptoGastoB.getFechaFin() != null
        ? proyectoConceptoGastoB.getFechaFin()
        : Instant.MAX;

    return !fechaInicioProyectoConceptoGastoA.isAfter(fechaFinProyectoConceptoGastoA)
        && !fechaInicioProyectoConceptoGastoB.isAfter(fechaFinProyectoConceptoGastoB);
  }

}
