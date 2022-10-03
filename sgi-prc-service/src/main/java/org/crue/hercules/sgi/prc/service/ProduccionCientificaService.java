package org.crue.hercules.sgi.prc.service;

import java.util.List;

import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.prc.dto.ActividadResumen;
import org.crue.hercules.sgi.prc.dto.ComiteEditorialResumen;
import org.crue.hercules.sgi.prc.dto.CongresoResumen;
import org.crue.hercules.sgi.prc.dto.DireccionTesisResumen;
import org.crue.hercules.sgi.prc.dto.ObraArtisticaResumen;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaDataErrorException;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotUpdatableException;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.predicate.ActividadPredicateResolver;
import org.crue.hercules.sgi.prc.repository.predicate.ComiteEditorialPredicateResolver;
import org.crue.hercules.sgi.prc.repository.predicate.CongresoPredicateResolver;
import org.crue.hercules.sgi.prc.repository.predicate.DireccionTesisPredicateResolver;
import org.crue.hercules.sgi.prc.repository.predicate.ObraArtisticaPredicateResolver;
import org.crue.hercules.sgi.prc.repository.predicate.ProduccionCientificaPredicateResolver;
import org.crue.hercules.sgi.prc.repository.predicate.PublicacionPredicateResolver;
import org.crue.hercules.sgi.prc.repository.specification.ProduccionCientificaSpecifications;
import org.crue.hercules.sgi.prc.util.AssertHelper;
import org.crue.hercules.sgi.prc.util.ProduccionCientificaAuthorityHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Service para gestionar {@link ProduccionCientifica}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
@Validated
public class ProduccionCientificaService {

  private final ProduccionCientificaRepository repository;
  private final EstadoProduccionCientificaService estadoProduccionCientificaService;
  private final AutorGrupoService autorGrupoService;
  private final ProduccionCientificaAuthorityHelper authorityHelper;

  public ProduccionCientificaService(
      ProduccionCientificaRepository produccionCientificaRepository,
      EstadoProduccionCientificaService estadoProduccionCientificaService,
      AutorGrupoService autorGrupoService,
      ProduccionCientificaAuthorityHelper authorityHelper) {
    this.repository = produccionCientificaRepository;
    this.estadoProduccionCientificaService = estadoProduccionCientificaService;
    this.autorGrupoService = autorGrupoService;
    this.authorityHelper = authorityHelper;
  }

  /**
   * Recupera todas las {@link PublicacionResumen} con su
   * título, fecha y tipo de producción
   * 
   * @param query                la información del filtro.
   * @param pageable             la información de la paginación.
   * @param filterByInvestigador <code>true</code> para filtrar por el usuario
   *                             actual, <code>false</code> para obtener todas las
   *                             que cumplan el filtro <b>query</b>
   * @return Listado paginado de {@link PublicacionResumen}
   */
  public Page<PublicacionResumen> findAllPublicaciones(String query, Pageable pageable, boolean filterByInvestigador) {
    log.debug("findAllPublicaciones(String query, Pageable pageable, boolean filterByInvestigador) - start");

    Specification<ProduccionCientifica> specs = buildSpecifications(query, filterByInvestigador,
        PublicacionPredicateResolver.getInstance());

    log.debug("findAllPublicaciones(String query, Pageable pageable, boolean filterByInvestigador) - end");
    return repository.findAllPublicaciones(specs, pageable);
  }

  /**
   * Recupera todas las entidades {@link ComiteEditorialResumen} paginadas y/o
   * filtradas
   * 
   * @param query                la información del filtro.
   * @param pageable             la información de la paginación.
   * @param filterByInvestigador <code>true</code> para filtrar por el usuario
   *                             actual, <code>false</code> para obtener todas las
   *                             que cumplan el filtro <b>query</b>
   * @return Listado de entidades {@link ComiteEditorialResumen} paginadas y/o
   *         filtradas.
   */
  public Page<ComiteEditorialResumen> findAllComitesEditoriales(String query, Pageable pageable,
      boolean filterByInvestigador) {
    log.debug("findAllComitesEditoriales(String query, Pageable pageable, boolean filterByInvestigador) - start");

    Specification<ProduccionCientifica> specs = buildSpecifications(query, filterByInvestigador,
        ComiteEditorialPredicateResolver.getInstance());

    Page<ComiteEditorialResumen> returnValue = repository.findAllComitesEditoriales(specs, pageable);
    log.debug("findAllComitesEditoriales(String query, Pageable pageable, boolean filterByInvestigador) - end");
    return returnValue;
  }

  /**
   * Recupera todas las entidades {@link CongresoResumen} paginadas y/o
   * filtradas
   * 
   * @param query                la información del filtro.
   * @param pageable             la información de la paginación.
   * @param filterByInvestigador <code>true</code> para filtrar por el usuario
   *                             actual, <code>false</code> para obtener todas las
   *                             que cumplan el filtro <b>query</b>
   * @return Listado de entidades {@link CongresoResumen} paginadas y/o
   *         filtradas.
   */
  public Page<CongresoResumen> findAllCongresos(String query, Pageable pageable, boolean filterByInvestigador) {
    log.debug("findAllCongresos(String query, Pageable pageable, boolean filterByInvestigador) - start");

    Specification<ProduccionCientifica> specs = buildSpecifications(query, filterByInvestigador,
        CongresoPredicateResolver.getInstance());

    Page<CongresoResumen> returnValue = repository.findAllCongresos(specs, pageable);
    log.debug("findAllCongresos(String query, Pageable pageable, boolean filterByInvestigador) - end");
    return returnValue;
  }

  /**
   * Recupera todas las entidades {@link ObraArtisticaResumen} paginadas y/o
   * filtradas
   * 
   * @param query                la información del filtro.
   * @param pageable             la información de la paginación.
   * @param filterByInvestigador <code>true</code> para filtrar por el usuario
   *                             actual, <code>false</code> para obtener todas las
   *                             que cumplan el filtro <b>query</b>
   * @return Listado de entidades {@link ObraArtisticaResumen} paginadas y/o
   *         filtradas.
   */
  public Page<ObraArtisticaResumen> findAllObrasArtisticas(String query, Pageable pageable,
      boolean filterByInvestigador) {
    log.debug("findAllObrasArtisticas(String query, Pageable pageable, boolean filterByInvestigador) - start");

    Specification<ProduccionCientifica> specs = buildSpecifications(query, filterByInvestigador,
        ObraArtisticaPredicateResolver.getInstance());

    Page<ObraArtisticaResumen> returnValue = repository.findAllObrasArtisticas(specs, pageable);
    log.debug("findAllObrasArtisticas(String query, Pageable pageable, boolean filterByInvestigador) - end");
    return returnValue;
  }

  /**
   * Recupera todas las entidades {@link ActividadResumen} paginadas y/o
   * filtradas
   * 
   * @param query                la información del filtro.
   * @param pageable             la información de la paginación.
   * @param filterByInvestigador <code>true</code> para filtrar por el usuario
   *                             actual, <code>false</code> para obtener todas las
   *                             que cumplan el filtro <b>query</b>
   * @return Listado de entidades {@link ActividadResumen} paginadas y/o
   *         filtradas.
   */
  public Page<ActividadResumen> findAllActividades(String query, Pageable pageable, boolean filterByInvestigador) {
    log.debug("findAllActividades(String query, Pageable pageable, boolean filterByInvestigador) - start");

    Specification<ProduccionCientifica> specs = buildSpecifications(query, filterByInvestigador,
        ActividadPredicateResolver.getInstance());

    Page<ActividadResumen> returnValue = repository.findAllActividades(specs, pageable);
    log.debug("findAllActividades(String query, Pageable pageable, boolean filterByInvestigador) - end");
    return returnValue;
  }

  /**
   * Recupera todas las entidades {@link DireccionTesisResumen} paginadas y/o
   * filtradas
   * 
   * @param query                la información del filtro.
   * @param pageable             la información de la paginación.
   * @param filterByInvestigador <code>true</code> para filtrar por el usuario
   *                             actual, <code>false</code> para obtener todas las
   *                             que cumplan el filtro <b>query</b>
   * @return Listado de entidades {@link DireccionTesisResumen} paginadas y/o
   *         filtradas.
   */
  public Page<DireccionTesisResumen> findAllDireccionesTesis(String query, Pageable pageable,
      boolean filterByInvestigador) {
    log.debug("findAllDireccionesTesis(String query, Pageable pageable, boolean filterByInvestigador) - start");
    Specification<ProduccionCientifica> specs = buildSpecifications(query, filterByInvestigador,
        DireccionTesisPredicateResolver.getInstance());
    Page<DireccionTesisResumen> returnValue = repository.findAllDireccionesTesis(specs, pageable);
    log.debug("findAllDireccionesTesis(String query, Pageable pageable, boolean filterByInvestigador) - end");
    return returnValue;
  }

  /**
   * Obtener todas las entidades {@link ProduccionCientifica} paginadas y/o
   * filtradas.
   *
   * @param pageable la información de la paginación.
   * @param query    la información del filtro.
   * @return la lista de entidades {@link ProduccionCientifica} paginadas y/o
   *         filtradas.
   */
  public Page<ProduccionCientifica> findAll(String query, Pageable pageable) {
    log.debug("findAll(String query, Pageable pageable) - start");
    Specification<ProduccionCientifica> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<ProduccionCientifica> returnValue = repository.findAll(specs, pageable);
    log.debug("findAll(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene {@link ProduccionCientifica} por su id.
   *
   * @param id el id de la entidad {@link ProduccionCientifica}.
   * @return la entidad {@link ProduccionCientifica}.
   */
  public ProduccionCientifica findById(Long id) {
    log.debug("findById(Long id)  - start");
    authorityHelper.checkUserHasAuthorityViewProduccionCientifica(id);
    final ProduccionCientifica returnValue = repository.findById(id)
        .orElseThrow(() -> new ProduccionCientificaNotFoundException(id.toString()));
    log.debug("findById(Long id)  - end");
    return returnValue;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ProduccionCientifica} con el id indicado puede ser consultada por el
   * usuario actual.
   * 
   * @param id Identificador de la {@link ProduccionCientifica}
   * @return <code>true</code> si el usuario esta autorizado para ver la
   *         {@link ProduccionCientifica}, <code>false</code> en caso contrario
   */
  public boolean accesibleByInvestigador(Long id) {
    log.debug("accesible(Long id) - start");
    boolean returnValue = authorityHelper.hasAuthorityViewProduccionCientificaInvestigador(id);
    log.debug("accesible(Long id) - end");
    return returnValue;

  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ProduccionCientifica} con el id indicado puede ser editada por un
   * investigador.
   * 
   * @param id de la {@link ProduccionCientifica}
   * @return true si es editable o false en caso contrario
   */
  public boolean modificableByInvestigador(Long id) {
    log.debug("modificableByInvestigador(Long id) - start");
    boolean returnValue = authorityHelper.hasAuthorityModifyProduccionCientificaInvestigador(id);
    log.debug("modificableByInvestigador(Long id) - end");
    return returnValue;
  }

  /**
   * Cambia el estado de la entidad {@link ProduccionCientifica}.
   *
   * @param id                           el id de la entidad
   *                                     {@link ProduccionCientifica}.
   * @param tipoEstadoProduccionToUpdate estado al que se va a actualizar.
   * @param comentario                   motivo del rechazo.
   * @return la entidad {@link ProduccionCientifica} actualizada.
   */
  @Transactional
  public ProduccionCientifica cambiarEstadoGestor(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate,
      String comentario) {
    log.debug(
        "cambiarEstadoGestor(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate, String comentario) - start");
    AssertHelper.idNotNull(id, ProduccionCientifica.class);

    return repository.findById(id).map(produccionCientifica -> {
      checkTipoEstadoProduccionNoUpdatable(produccionCientifica.getEstado().getEstado());

      EstadoProduccionCientifica estadoProduccionCientificaToUpdate = createEstadoProduccionCientifica(
          tipoEstadoProduccionToUpdate, produccionCientifica.getId(), comentario);
      EstadoProduccionCientifica estadoProduccionCientificaUpdated = estadoProduccionCientificaService
          .create(estadoProduccionCientificaToUpdate);
      produccionCientifica.setEstado(estadoProduccionCientificaUpdated);

      ProduccionCientifica returnValue = repository.save(produccionCientifica);

      log.debug(
          "cambiarEstadoGestor(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate, String comentario) - end");
      return returnValue;
    }).orElseThrow(() -> new ProduccionCientificaNotFoundException(id.toString()));
  }

  /**
   * Cambia el estado de la entidad {@link ProduccionCientifica}.
   *
   * @param id                           el id de la entidad
   *                                     {@link ProduccionCientifica}.
   * @param tipoEstadoProduccionToUpdate estado al que se va a actualizar.
   * @param comentario                   motivo del rechazo.
   * @return la entidad {@link ProduccionCientifica} actualizada.
   */
  @Transactional
  public ProduccionCientifica cambiarEstadoInvestigador(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate,
      String comentario) {
    log.debug(
        "cambiarEstadoInvestigador(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate, String comentario) - start");
    AssertHelper.idNotNull(id, ProduccionCientifica.class);

    // Obtener los grupos autorizados, es decir, en los que el investigador es IP o
    // persona autorizada
    List<Long> gruposRefAutorizados = authorityHelper.getUserGrupos();

    authorityHelper.checkUserHasAuthorityViewProduccionCientificaInvestigador(id);

    return repository.findById(id).map(produccionCientifica -> {
      // Si está en un estado final no se puede cambiar el estado
      checkTipoEstadoProduccionNoUpdatable(produccionCientifica.getEstado().getEstado());

      // Obtener todos los AutorGrupo que pertenecen a la ProduccionCientifica y
      // pertenecen a grupos autorizados para el investigador
      List<AutorGrupo> autoresGruposToUpdate = autorGrupoService.findAllByProduccionCientificaIdAndInGruposRef(id,
          gruposRefAutorizados);

      // Si no existen registros de AutoGrupo relacionados con el grupo del
      // investigador, quiere decir que la ProduccionCientifica es inconsistente
      if (autoresGruposToUpdate.isEmpty()) {
        throw new ProduccionCientificaDataErrorException();
      }
      // Actualizar el estado de los registros de AutorGrupo
      autoresGruposToUpdate.stream().forEach(autorGrupo -> {
        autorGrupo.setEstado(tipoEstadoProduccionToUpdate);
        autorGrupoService.update(autorGrupo);
      });

      EstadoProduccionCientifica estadoProduccionCientificaToUpdate;
      // Si el estado al que hay que actualizar la ProduccionCientifica es RECHAZADO,
      // directamente se hace la actualizacion aunque existan otros grupos pendientes
      // validar/rechazar
      if (tipoEstadoProduccionToUpdate == TipoEstadoProduccion.RECHAZADO) {
        estadoProduccionCientificaToUpdate = createEstadoProduccionCientifica(
            tipoEstadoProduccionToUpdate, produccionCientifica.getId(), comentario);
      } else {
        // Si el estado al que hay que actualizar la ProduccionCientifica es VALIDADO,
        // es necesario comprobar si ya todos los grupos han validado o no
        List<AutorGrupo> autoresGruposByProduccionCientifica = autorGrupoService
            .findAllByProduccionCientificaId(produccionCientifica.getId());
        TipoEstadoProduccion tipoEstadoProduccionValidar = autoresGruposByProduccionCientifica.stream()
            .allMatch(autorGrupo -> autorGrupo.getEstado() == TipoEstadoProduccion.VALIDADO)
                ? TipoEstadoProduccion.VALIDADO
                : TipoEstadoProduccion.VALIDADO_PARCIALMENTE;
        estadoProduccionCientificaToUpdate = createEstadoProduccionCientifica(
            tipoEstadoProduccionValidar, produccionCientifica.getId(), comentario);
      }

      EstadoProduccionCientifica estadoProduccionCientificaUpdated = estadoProduccionCientificaService
          .create(estadoProduccionCientificaToUpdate);
      produccionCientifica.setEstado(estadoProduccionCientificaUpdated);

      ProduccionCientifica returnValue = repository.save(produccionCientifica);

      log.debug(
          "cambiarEstadoInvestigador(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate, String comentario) - end");
      return returnValue;
    }).orElseThrow(() -> new ProduccionCientificaNotFoundException(id.toString()));
  }

  private EstadoProduccionCientifica createEstadoProduccionCientifica(TipoEstadoProduccion tipoEstadoProduccion,
      Long produccionCientificaId, String comentario) {
    return EstadoProduccionCientifica
        .builder()
        .estado(tipoEstadoProduccion)
        .produccionCientificaId(produccionCientificaId)
        .comentario(comentario)
        .build();
  }

  /**
   * Lanza una {@link ProduccionCientificaNotUpdatableException} si el estado
   * actual es actualizable (VALIDADO o RECHAZADO son estados finales no
   * modificables).
   * 
   * @param estado a evaluar
   */
  private void checkTipoEstadoProduccionNoUpdatable(TipoEstadoProduccion estado) {
    if (estado == TipoEstadoProduccion.VALIDADO ||
        estado == TipoEstadoProduccion.RECHAZADO) {
      throw new ProduccionCientificaNotUpdatableException();
    }
  }

  private Specification<ProduccionCientifica> buildSpecifications(String query, boolean filterByInvestigador,
      ProduccionCientificaPredicateResolver predicateResolver) {
    Specification<ProduccionCientifica> specs = null;
    if (StringUtils.hasText(query)) {
      specs = SgiRSQLJPASupport.toSpecification(query, predicateResolver);
    }

    if (filterByInvestigador) {
      Specification<ProduccionCientifica> existsInGrupoRef = ProduccionCientificaSpecifications
          .byExistsInGrupoRef(authorityHelper.getUserGrupos());
      if (specs == null) {
        specs = existsInGrupoRef;
      } else {
        specs = specs.and(existsInGrupoRef);
      }
    }

    return specs;
  }

}
