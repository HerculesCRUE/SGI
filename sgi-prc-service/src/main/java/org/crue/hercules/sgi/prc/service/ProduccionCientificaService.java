package org.crue.hercules.sgi.prc.service;

import java.util.List;
import java.util.stream.Collectors;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.security.core.context.SgiSecurityContextHolder;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.prc.dto.ActividadResumen;
import org.crue.hercules.sgi.prc.dto.ComiteEditorialResumen;
import org.crue.hercules.sgi.prc.dto.CongresoResumen;
import org.crue.hercules.sgi.prc.dto.DireccionTesisResumen;
import org.crue.hercules.sgi.prc.dto.ObraArtisticaResumen;
import org.crue.hercules.sgi.prc.dto.PublicacionResumen;
import org.crue.hercules.sgi.prc.dto.csp.GrupoDto;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaDataErrorException;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotFoundException;
import org.crue.hercules.sgi.prc.exceptions.ProduccionCientificaNotUpdatableException;
import org.crue.hercules.sgi.prc.exceptions.UserNotAuthorizedToAccessProduccionCientificaException;
import org.crue.hercules.sgi.prc.model.AutorGrupo;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica;
import org.crue.hercules.sgi.prc.model.EstadoProduccionCientifica.TipoEstadoProduccion;
import org.crue.hercules.sgi.prc.model.ProduccionCientifica;
import org.crue.hercules.sgi.prc.repository.ProduccionCientificaRepository;
import org.crue.hercules.sgi.prc.repository.specification.ProduccionCientificaSpecifications;
import org.crue.hercules.sgi.prc.service.sgi.SgiApiCspService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
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
  private final SgiApiCspService sgiApiCspService;
  private final AutorGrupoService autorGrupoService;

  public ProduccionCientificaService(
      ProduccionCientificaRepository produccionCientificaRepository,
      EstadoProduccionCientificaService estadoProduccionCientificaService,
      SgiApiCspService sgiApiCspService,
      AutorGrupoService autorGrupoService) {
    this.repository = produccionCientificaRepository;
    this.estadoProduccionCientificaService = estadoProduccionCientificaService;
    this.sgiApiCspService = sgiApiCspService;
    this.autorGrupoService = autorGrupoService;
  }

  /**
   * Recupera todas las {@link PublicacionResumen} con su
   * título, fecha y tipo de producción
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado paginado de {@link PublicacionResumen}
   */
  public Page<PublicacionResumen> findAllPublicaciones(String query, Pageable pageable) {
    log.debug("findAllPublicaciones(String query, Pageable pageable) - start");
    if (isInvestigador()) {
      log.debug("findAllPublicaciones(String query, Pageable pageable) - end");
      return repository.findAllPublicaciones(createInvestigadorFilter(), query,
          pageable);
    }
    log.debug("findAllPublicaciones(String query, Pageable pageable) - end");
    return repository.findAllPublicaciones(null, query, pageable);
  }

  /**
   * Recupera todas las entidades {@link ComiteEditorialResumen} paginadas y/o
   * filtradas
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado de entidades {@link ComiteEditorialResumen} paginadas y/o
   *         filtradas.
   */
  public Page<ComiteEditorialResumen> findAllComitesEditoriales(String query, Pageable pageable) {
    log.debug("findAllComitesEditoriales(String query, Pageable pageable) - start");
    if (isInvestigador()) {
      log.debug("findAllComitesEditoriales(String query, Pageable pageable) - end");
      return repository.findAllComitesEditoriales(createInvestigadorFilter(), query,
          pageable);
    }
    Page<ComiteEditorialResumen> returnValue = repository.findAllComitesEditoriales(null, query, pageable);
    log.debug("findAllComitesEditoriales(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Recupera todas las entidades {@link CongresoResumen} paginadas y/o
   * filtradas
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado de entidades {@link CongresoResumen} paginadas y/o
   *         filtradas.
   */
  public Page<CongresoResumen> findAllCongresos(String query, Pageable pageable) {
    log.debug("findAllCongresos(String query, Pageable pageable) - start");
    if (isInvestigador()) {
      log.debug("findAllCongresos(String query, Pageable pageable) - end");
      return repository.findAllCongresos(createInvestigadorFilter(), query,
          pageable);
    }
    Page<CongresoResumen> returnValue = repository.findAllCongresos(null, query, pageable);
    log.debug("findAllCongresos(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Recupera todas las entidades {@link ObraArtisticaResumen} paginadas y/o
   * filtradas
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado de entidades {@link ObraArtisticaResumen} paginadas y/o
   *         filtradas.
   */
  public Page<ObraArtisticaResumen> findAllObrasArtisticas(String query, Pageable pageable) {
    log.debug("findAllObrasArtisticas(String query, Pageable pageable) - start");
    if (isInvestigador()) {
      log.debug("findAllObrasArtisticas(String query, Pageable pageable) - end");
      return repository.findAllObrasArtisticas(createInvestigadorFilter(), query,
          pageable);
    }
    Page<ObraArtisticaResumen> returnValue = repository.findAllObrasArtisticas(null, query, pageable);
    log.debug("findAllObrasArtisticas(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Recupera todas las entidades {@link ActividadResumen} paginadas y/o
   * filtradas
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado de entidades {@link ActividadResumen} paginadas y/o
   *         filtradas.
   */
  public Page<ActividadResumen> findAllActividades(String query, Pageable pageable) {
    log.debug("findAllActividades(String query, Pageable pageable) - start");
    if (isInvestigador()) {
      log.debug("findAllActividades(String query, Pageable pageable) - end");
      return repository.findAllActividades(createInvestigadorFilter(), query,
          pageable);
    }
    Page<ActividadResumen> returnValue = repository.findAllActividades(null, query, pageable);
    log.debug("findAllActividades(String query, Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Recupera todas las entidades {@link DireccionTesisResumen} paginadas y/o
   * filtradas
   * 
   * @param query    la información del filtro.
   * @param pageable la información de la paginación.
   * @return Listado de entidades {@link DireccionTesisResumen} paginadas y/o
   *         filtradas.
   */
  public Page<DireccionTesisResumen> findAllDireccionesTesis(String query, Pageable pageable) {
    log.debug("findAllDireccionesTesis(String query, Pageable pageable) - start");
    if (isInvestigador()) {
      log.debug("findAllDireccionesTesis(String query, Pageable pageable) - end");
      return repository.findAllDireccionesTesis(createInvestigadorFilter(), query,
          pageable);
    }
    Page<DireccionTesisResumen> returnValue = repository.findAllDireccionesTesis(null, query, pageable);
    log.debug("findAllDireccionesTesis(String query, Pageable pageable) - end");
    return returnValue;
  }

  private Specification<ProduccionCientifica> createInvestigadorFilter() {
    List<Long> gruposRef = sgiApiCspService.findAllGruposByPersonaRef(this.getUserPersonaRef()).stream()
        .map(GrupoDto::getId).collect(Collectors.toList());
    return createInvestigadorFilter(gruposRef);
  }

  private Specification<ProduccionCientifica> createInvestigadorFilter(List<Long> gruposRef) {
    return ProduccionCientificaSpecifications.byExistsSubqueryInGrupoRef(gruposRef);
  }

  /**
   * Recupera el personaRef del usuario actual
   */
  private String getUserPersonaRef() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication.getName();
  }

  private boolean isInvestigador() {
    return SgiSecurityContextHolder.hasAuthority("PRC-VAL-INV-ER");
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
    final ProduccionCientifica returnValue = repository.findById(id)
        .orElseThrow(() -> new ProduccionCientificaNotFoundException(id.toString()));
    log.debug("findById(Long id)  - end");
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
  public ProduccionCientifica cambiarEstado(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate,
      String comentario) {
    log.debug("cambiarEstado(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate, String comentario) - start");
    Assert.notNull(id,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(ProduccionCientifica.class)).build());

    ProduccionCientifica returnValue;
    if (isInvestigador()) {
      returnValue = cambiarEstadoInvestigador(id, tipoEstadoProduccionToUpdate, comentario);
    } else {
      returnValue = cambiarEstadoGestor(id, tipoEstadoProduccionToUpdate, comentario);
    }

    log.debug("cambiarEstado(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate, String comentario) - end");
    return returnValue;
  }

  private ProduccionCientifica cambiarEstadoGestor(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate,
      String comentario) {
    log.debug(
        "cambiarEstadoGestor(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate, String comentario) - start");

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

  private ProduccionCientifica cambiarEstadoInvestigador(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate,
      String comentario) {
    log.debug(
        "cambiarEstadoInvestigador(Long id, TipoEstadoProduccion tipoEstadoProduccionToUpdate, String comentario) - start");

    // Obtener los grupos autorizados, es decir, en los que el investigador es IP o
    // persona autorizada
    List<Long> gruposRefAutorizados = sgiApiCspService.findAllGruposByPersonaRef(this.getUserPersonaRef()).stream()
        .map(GrupoDto::getId).collect(Collectors.toList());
    checkAccesibleByInvestigador(id, gruposRefAutorizados);

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

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ProduccionCientifica} con el id indicado puede ser consultada por un
   * investigador.
   * 
   * @param id de la {@link ProduccionCientifica}
   * @return true si si puede consultarla o false en caso contrario
   */
  public boolean accesibleByInvestigador(Long id) {
    log.debug("editableByInvestigador(Long id) - start");
    if (isInvestigador()) {
      final Specification<ProduccionCientifica> spec = ProduccionCientificaSpecifications.byId(id)
          .and(createInvestigadorFilter());
      log.debug("editableByInvestigador(Long id) - end");

      return repository.count(spec) > 0;
    }

    return Boolean.FALSE;
  }

  /**
   * Hace las comprobaciones necesarias para determinar si la
   * {@link ProduccionCientifica} con el id indicado puede ser editada por un
   * investigador.
   * 
   * @param id de la {@link ProduccionCientifica}
   * @return true si es editable o false en caso contrario
   */
  public boolean editableByInvestigador(Long id) {
    log.debug("editableByInvestigador(Long id) - start");
    if (isInvestigador()) {
      List<Long> gruposRefAutorizados = sgiApiCspService.findAllGruposByPersonaRef(this.getUserPersonaRef()).stream()
          .map(GrupoDto::getId).collect(Collectors.toList());
      final Specification<ProduccionCientifica> spec = ProduccionCientificaSpecifications.byId(id)
          .and(ProduccionCientificaSpecifications.isInEstadoEditable())
          .and(ProduccionCientificaSpecifications
              .byAutorGrupoEstadoAndAutorGrupoInGrupoRef(TipoEstadoProduccion.PENDIENTE, gruposRefAutorizados));
      log.debug("editableByInvestigador(Long id) - end");

      return repository.count(spec) > 0;
    }

    return Boolean.FALSE;
  }

  /**
   * Comprueba si la {@link ProduccionCientifica} es accesible por el
   * investigador, en caso contrario lanza una exception.
   * 
   * @param id de la {@link ProduccionCientifica}
   */
  public void checkAccesibleByInvestigador(Long id) {
    log.debug("checkEditableByInvestigador(Long id) - start");
    if (isInvestigador()) {
      final Specification<ProduccionCientifica> spec = ProduccionCientificaSpecifications.byId(id)
          .and(createInvestigadorFilter());
      if (repository.count(spec) == 0) {
        log.debug("checkEditableByInvestigador(Long id) - end");
        throw new UserNotAuthorizedToAccessProduccionCientificaException();

      }
    }
    log.debug("checkEditableByInvestigador(Long id) - end");
  }

  /**
   * Comprueba si la {@link ProduccionCientifica} es accesible por el
   * investigador, en caso contrario lanza una exception.
   * 
   * @param id        de la {@link ProduccionCientifica}
   * @param gruposRef lista de ids de los grupos en los que el investigador es
   *                  investigador principal o persona autorizada
   */
  private void checkAccesibleByInvestigador(Long id, List<Long> gruposRef) {
    log.debug("checkEditableByInvestigador(Long id, List<Long> gruposRef) - start");
    final Specification<ProduccionCientifica> spec = ProduccionCientificaSpecifications.byId(id)
        .and(createInvestigadorFilter(gruposRef));
    if (repository.count(spec) == 0) {
      log.debug("checkEditableByInvestigador(Long id, List<Long> gruposRef) - end");
      throw new UserNotAuthorizedToAccessProduccionCientificaException();

    }
    log.debug("checkEditableByInvestigador(Long id, List<Long> gruposRef) - end");
  }
}
