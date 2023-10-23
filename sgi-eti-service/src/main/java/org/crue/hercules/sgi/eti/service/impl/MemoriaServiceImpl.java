package org.crue.hercules.sgi.eti.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EstadoMemoriaIndicarSubsanacionNotValidException;
import org.crue.hercules.sgi.eti.exceptions.EstadoRetrospectivaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.model.Apartado;
import org.crue.hercules.sgi.eti.model.Bloque;
import org.crue.hercules.sgi.eti.model.Comite;
import org.crue.hercules.sgi.eti.model.Configuracion;
import org.crue.hercules.sgi.eti.model.ConvocatoriaReunion;
import org.crue.hercules.sgi.eti.model.DocumentacionMemoria;
import org.crue.hercules.sgi.eti.model.EstadoMemoria;
import org.crue.hercules.sgi.eti.model.EstadoRetrospectiva;
import org.crue.hercules.sgi.eti.model.Evaluacion;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.model.Retrospectiva;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria.Tipo;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.ApartadoRepository;
import org.crue.hercules.sgi.eti.repository.BloqueRepository;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.MemoriaRepository;
import org.crue.hercules.sgi.eti.repository.PeticionEvaluacionRepository;
import org.crue.hercules.sgi.eti.repository.RespuestaRepository;
import org.crue.hercules.sgi.eti.repository.TareaRepository;
import org.crue.hercules.sgi.eti.repository.specification.MemoriaSpecifications;
import org.crue.hercules.sgi.eti.service.ComunicadosService;
import org.crue.hercules.sgi.eti.service.ConfiguracionService;
import org.crue.hercules.sgi.eti.service.InformeService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.RetrospectivaService;
import org.crue.hercules.sgi.eti.service.SgdocService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiRepService;
import org.crue.hercules.sgi.eti.util.AssertHelper;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Memoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class MemoriaServiceImpl implements MemoriaService {

  private static final String TITULO_INFORME_MXX = "informeMemoriaPdf";
  private static final String TITULO_INFORME_RETROSPECTIVA = "informeRetrospectivaPdf";
  private static final String TITULO_INFORME_SA = "informeSAPdf";
  private static final String TITULO_INFORME_SF = "informeSFPdf";
  private static final String PROBLEM_MESSAGE_PARAMETER_FIELD = "field";
  private static final String PROBLEM_MESSAGE_PARAMETER_ENTITY = "entity";
  private static final String PROBLEM_MESSAGE_NOTNULL = "notNull";

  /** Propiedades de configuración de la aplicación */
  private final SgiConfigProperties sgiConfigProperties;

  /** Comentario repository */
  private final ComentarioRepository comentarioRepository;

  /** Comité repository */
  private final ComiteRepository comiteRepository;

  /** Documentacion memoria repository. */
  private final DocumentacionMemoriaRepository documentacionMemoriaRepository;

  /** Estado Memoria Repository. */
  private final EstadoMemoriaRepository estadoMemoriaRepository;

  /** Evaluacion repository */
  private final EvaluacionRepository evaluacionRepository;

  /** Memoria repository */
  private final MemoriaRepository memoriaRepository;

  /** Petición evaluación repository */
  private final PeticionEvaluacionRepository peticionEvaluacionRepository;

  /** Respuesta repository */
  private final RespuestaRepository respuestaRepository;

  /** Informe service */
  private final InformeService informeService;

  private final SgiApiRepService reportService;
  private final SgdocService sgdocService;

  /** Tarea repository */
  private final TareaRepository tareaRepository;

  /** Configuracion service */
  private final ConfiguracionService configuracionService;

  /** Bloque repository */
  private final BloqueRepository bloqueRepository;

  /** Apartado repository */
  private final ApartadoRepository apartadoRepository;

  /** Comunicado service */
  private final ComunicadosService comunicadosService;

  private final RetrospectivaService retrospectivaService;

  private static final String TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA = "Investigación tutelada";

  public MemoriaServiceImpl(SgiConfigProperties sgiConfigProperties, MemoriaRepository memoriaRepository,
      EstadoMemoriaRepository estadoMemoriaRepository,
      EvaluacionRepository evaluacionRepository, ComentarioRepository comentarioRepository,
      InformeService informeService, PeticionEvaluacionRepository peticionEvaluacionRepository,
      ComiteRepository comiteRepository, DocumentacionMemoriaRepository documentacionMemoriaRepository,
      RespuestaRepository respuestaRepository, TareaRepository tareaRepository,
      ConfiguracionService configuracionService, SgiApiRepService reportService, SgdocService sgdocService,
      BloqueRepository bloqueRepository, ApartadoRepository apartadoRepository, ComunicadosService comunicadosService,
      RetrospectivaService retrospectivaService) {
    this.sgiConfigProperties = sgiConfigProperties;
    this.memoriaRepository = memoriaRepository;
    this.estadoMemoriaRepository = estadoMemoriaRepository;
    this.evaluacionRepository = evaluacionRepository;
    this.comentarioRepository = comentarioRepository;
    this.informeService = informeService;
    this.peticionEvaluacionRepository = peticionEvaluacionRepository;
    this.comiteRepository = comiteRepository;
    this.documentacionMemoriaRepository = documentacionMemoriaRepository;
    this.respuestaRepository = respuestaRepository;
    this.tareaRepository = tareaRepository;
    this.configuracionService = configuracionService;
    this.reportService = reportService;
    this.sgdocService = sgdocService;
    this.bloqueRepository = bloqueRepository;
    this.apartadoRepository = apartadoRepository;
    this.comunicadosService = comunicadosService;
    this.retrospectivaService = retrospectivaService;
  }

  /**
   * Guarda la entidad {@link Memoria}.
   *
   * @param memoria la entidad {@link Memoria} a guardar.
   * @return la entidad {@link Memoria} persistida.
   */
  @Transactional
  @Override
  public Memoria create(Memoria memoria) {
    log.debug("Memoria create(Memoria memoria) - start");

    validacionesCreateMemoria(memoria);

    Assert.isTrue(memoria.getTipoMemoria().getId().equals(1L) || memoria.getTipoMemoria().getId().equals(3L),
        "La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria.");

    // La memoria se crea con tipo estado memoria "En elaboración".
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(Constantes.TIPO_ESTADO_MEMORIA_EN_ELABORACION);
    memoria.setEstadoActual(tipoEstadoMemoria);

    memoria.setNumReferencia(
        getReferenciaMemoria(memoria.getTipoMemoria().getId(), memoria.getNumReferencia(), memoria.getComite()));

    // Requiere retrospectiva
    memoria.setRequiereRetrospectiva(Boolean.FALSE);

    // Versión
    memoria.setVersion(0); // Se crea con version 0 y se incrementa con cada enviarSecretaria

    // Activo
    memoria.setActivo(Boolean.TRUE);

    return memoriaRepository.save(memoria);
  }

  @Transactional
  @Override
  public Memoria createModificada(Memoria nuevaMemoria, Long id) {
    log.debug("createModificada(Memoria memoria, Long id) - start");

    validacionesCreateMemoria(nuevaMemoria);

    Assert.isTrue(nuevaMemoria.getTipoMemoria().getTipo().equals(TipoMemoria.Tipo.MODIFICACION),
        "La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria.");

    Memoria memoria = memoriaRepository.findByIdAndActivoTrue(id).orElseThrow(() -> new MemoriaNotFoundException(id));

    nuevaMemoria.setRequiereRetrospectiva(memoria.getRequiereRetrospectiva());
    nuevaMemoria.setVersion(0);
    nuevaMemoria.setActivo(Boolean.TRUE);
    nuevaMemoria.setMemoriaOriginal(memoria);

    if (Boolean.TRUE.equals(nuevaMemoria.getRequiereRetrospectiva())) {
      Retrospectiva retrospectiva = Retrospectiva.builder()
          .estadoRetrospectiva(EstadoRetrospectiva.builder().id(Constantes.ESTADO_RETROSPECTIVA_PENDIENTE).build())
          .fechaRetrospectiva(memoria.getRetrospectiva().getFechaRetrospectiva()).build();
      nuevaMemoria.setRetrospectiva(retrospectivaService.create(retrospectiva));
    }

    // La memoria se crea con tipo estado memoria "En elaboración".
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(Constantes.TIPO_ESTADO_MEMORIA_EN_ELABORACION);
    nuevaMemoria.setEstadoActual(tipoEstadoMemoria);

    nuevaMemoria.setNumReferencia(
        getReferenciaMemoria(nuevaMemoria.getTipoMemoria().getId(), memoria.getNumReferencia(), memoria.getComite()));

    final Memoria memoriaCreada = memoriaRepository.save(nuevaMemoria);

    Page<DocumentacionMemoria> documentacionesMemoriaPage = documentacionMemoriaRepository
        .findByMemoriaIdAndMemoriaActivoTrue(memoria.getId(), null);

    List<DocumentacionMemoria> documentacionesMemoriaList = documentacionesMemoriaPage.getContent().stream()
        .map(documentacionMemoria -> new DocumentacionMemoria(null, memoriaCreada,
            documentacionMemoria.getTipoDocumento(),
            documentacionMemoria.getDocumentoRef(), documentacionMemoria.getNombre()))
        .collect(Collectors.toList());

    documentacionMemoriaRepository.saveAll(documentacionesMemoriaList);

    // Guardamos los ids de los apartados del formulario de retrospectiva
    List<Long> idsApartadosRetrospectiva = new ArrayList<>();
    Page<Bloque> bloques = bloqueRepository.findByFormularioId(Constantes.FORMULARIO_RETROSPECTIVA,
        null);
    bloques.getContent().stream().forEach(bloque -> {
      Page<Apartado> apartados = apartadoRepository.findByBloqueIdAndPadreIsNull(bloque.getId(), null);
      apartados.getContent().stream().forEach(apartado -> idsApartadosRetrospectiva.add(apartado.getId()));
    });

    Page<Respuesta> respuestasPage = respuestaRepository.findByMemoriaIdAndMemoriaActivoTrue(memoria.getId(), null);
    /**
     * Filtramos por los ids de los apartados del formulario de retrospectiva para
     * no guardar las respuestas en caso de que exista
     */
    List<Respuesta> respuestaList = respuestasPage.getContent().stream()
        .filter(r -> idsApartadosRetrospectiva.indexOf(r.getApartado().getId()) == -1)
        .map(respuesta -> new Respuesta(null, memoriaCreada, respuesta.getApartado(), respuesta.getTipoDocumento(),
            respuesta.getValor()))
        .collect(Collectors.toList());

    respuestaRepository.saveAll(respuestaList);

    /** Tarea */
    List<Tarea> tareasMemoriaOriginal = tareaRepository
        .findAllByMemoriaId(memoria.getId());
    if (!tareasMemoriaOriginal.isEmpty()) {
      List<Tarea> tareasMemoriaCopy = tareasMemoriaOriginal.stream()
          .map(tarea -> new Tarea(null, tarea.getEquipoTrabajo(), nuevaMemoria, tarea.getTarea(), tarea.getFormacion(),
              tarea.getFormacionEspecifica(), tarea.getOrganismo(), tarea.getAnio(), tarea.getTipoTarea()))
          .collect(Collectors.toList());
      tareaRepository.saveAll(tareasMemoriaCopy);
    }

    log.debug("Memoria createModificada(Memoria memoria, id) - end");
    return memoriaCreada;
  }

  /**
   * Obtiene todas las entidades {@link MemoriaPeticionEvaluacion} paginadas y
   * filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link MemoriaPeticionEvaluacion} paginadas y
   *         filtradas.
   */
  @Override
  public Page<MemoriaPeticionEvaluacion> findAll(String query, Pageable paging) {
    log.debug("findAll(String query,Pageable paging) - start");
    Specification<Memoria> specs = MemoriaSpecifications.activos().and(SgiRSQLJPASupport.toSpecification(query));

    Page<MemoriaPeticionEvaluacion> returnValue = memoriaRepository.findAllMemoriasEvaluaciones(specs, paging, null);
    log.debug("findAll(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * 
   * Devuelve una lista paginada de {@link Memoria} asignables para una
   * convocatoria determinada
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param idConvocatoriaReunion Identificador del {@link ConvocatoriaReunion}
   * @return lista de memorias asignables a la convocatoria.
   */
  @Override
  public List<Memoria> findAllMemoriasAsignablesConvocatoria(Long idConvocatoriaReunion) {
    log.debug("findAllMemoriasAsignables(Long idConvocatoriaReunion) - start");
    List<Memoria> returnValue = memoriaRepository.findAllMemoriasAsignablesConvocatoria(idConvocatoriaReunion);
    log.debug("findAllMemoriasAsignables(Long idConvocatoriaReunion) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Ordinaria" o "Extraordinaria".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   */
  @Override
  public Page<Memoria> findAllAsignablesTipoConvocatoriaOrdExt(String query, Pageable pageable) {
    log.debug("findAllAsignablesTipoConvocatoriaOrdExt(String query,Pageable pageable) - start");

    Specification<Memoria> specs = MemoriaSpecifications.activos()
        .and(MemoriaSpecifications.estadoActualIn(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA)).or(
            MemoriaSpecifications.estadoRetrospectivaIn(Arrays.asList(Constantes.ESTADO_RETROSPECTIVA_EN_SECRETARIA))))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Memoria> returnValue = memoriaRepository.findAll(specs, pageable);

    log.debug("findAllAsignablesTipoConvocatoriaOrdExt(String query,Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Devuelve una lista paginada y filtrada con las entidades {@link Memoria}
   * asignables a una Convocatoria de tipo "Seguimiento".
   * 
   * Para determinar si es asignable es necesario especificar en el filtro el
   * Comité y Fecha Límite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * @param query    filtro de búsqueda.
   * @param pageable pageable
   */
  @Override
  public Page<Memoria> findAllAsignablesTipoConvocatoriaSeguimiento(String query, Pageable pageable) {
    log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(String query,Pageable pageable) - start");

    Specification<Memoria> specs = MemoriaSpecifications.activos()
        .and(MemoriaSpecifications
            .estadoActualIn(Arrays.asList(TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_ANUAL.getId(),
                TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_FINAL.getId())))
        .and(SgiRSQLJPASupport.toSpecification(query));

    Page<Memoria> returnValue = memoriaRepository.findAll(specs, pageable);

    log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(String query,Pageable pageable) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   * @return la entidad {@link Memoria}.
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   */
  @Override
  public Memoria findById(final Long id) throws MemoriaNotFoundException {
    log.debug("Petición a get Memoria : {}  - start", id);
    final Memoria memoria = memoriaRepository.findById(id).orElseThrow(() -> new MemoriaNotFoundException(id));
    log.debug("Petición a get Memoria : {}  - end", id);
    return memoria;

  }

  /**
   * Elimina una entidad {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   */
  @Transactional
  @Override
  public void delete(Long id) throws MemoriaNotFoundException {
    log.debug("Petición a delete Memoria : {}  - start", id);
    Assert.notNull(id, "El id de Memoria no puede ser null.");
    if (!memoriaRepository.existsById(id)) {
      throw new MemoriaNotFoundException(id);
    }
    memoriaRepository.deleteById(id);
    log.debug("Petición a delete Memoria : {}  - end", id);
  }

  /**
   * Actualiza los datos del {@link Memoria}.
   * 
   * @param memoriaActualizar {@link Memoria} con los datos actualizados.
   * @return El {@link Memoria} actualizado.
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   * @throws IllegalArgumentException Si el {@link Memoria} no tiene id.
   */

  @Transactional
  @Override
  public Memoria update(final Memoria memoriaActualizar) {
    log.debug("update(Memoria MemoriaActualizar) - start");

    Assert.notNull(memoriaActualizar.getId(), "Memoria id no puede ser null para actualizar un tipo memoria");

    return memoriaRepository.findById(memoriaActualizar.getId()).map(memoria -> {

      // Se comprueba si se está desactivando la memoria
      if (Boolean.TRUE.equals(memoria.getActivo()) && Boolean.FALSE.equals(memoriaActualizar.getActivo())) {
        Assert.isTrue(
            Objects.equals(memoria.getEstadoActual().getId(), Constantes.TIPO_ESTADO_MEMORIA_EN_ELABORACION)
                || Objects.equals(memoria.getEstadoActual().getId(), Constantes.TIPO_ESTADO_MEMORIA_COMPLETADA),
            "El estado actual de la memoria no es el correcto para desactivar la memoria");
      }

      memoria.setNumReferencia(memoriaActualizar.getNumReferencia());
      memoria.setPeticionEvaluacion(memoriaActualizar.getPeticionEvaluacion());
      memoria.setComite((memoriaActualizar.getComite()));
      memoria.setTitulo(memoriaActualizar.getTitulo());
      memoria.setPersonaRef(memoriaActualizar.getPersonaRef());
      memoria.setTipoMemoria(memoriaActualizar.getTipoMemoria());
      memoria.setEstadoActual(memoriaActualizar.getEstadoActual());
      memoria.setFechaEnvioSecretaria(memoriaActualizar.getFechaEnvioSecretaria());
      memoria.setRequiereRetrospectiva(memoriaActualizar.getRequiereRetrospectiva());
      memoria.setRetrospectiva(memoriaActualizar.getRetrospectiva());
      memoria.setActivo(memoriaActualizar.getActivo());

      Memoria returnValue = memoriaRepository.save(memoria);
      log.debug("update(Memoria memoriaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(memoriaActualizar.getId()));
  }

  /**
   * Devuelve las memorias de una petición evaluación con su fecha límite y de
   * evaluación.
   * 
   * @param idPeticionEvaluacion Identificador {@link PeticionEvaluacion}
   * @return lista de memorias de {@link PeticionEvaluacion}
   */
  @Override
  public List<MemoriaPeticionEvaluacion> findMemoriaByPeticionEvaluacionMaxVersion(Long idPeticionEvaluacion) {
    return memoriaRepository.findMemoriasEvaluacion(idPeticionEvaluacion, null);
  }

  /**
   * Se crea el nuevo estado para la memoria recibida y se actualiza el estado
   * actual de esta.
   * 
   * @param memoria             {@link Memoria} a actualizar estado.
   * @param idTipoEstadoMemoria identificador del estado nuevo de la memoria.
   */
  @Transactional
  @Override
  public void updateEstadoMemoria(Memoria memoria, long idTipoEstadoMemoria) {
    log.debug("updateEstadoMemoria(Memoria memoria, Long idEstadoMemoria) - start");
    updateEstadoMemoria(memoria, idTipoEstadoMemoria, null);
    log.debug("updateEstadoMemoria(Memoria memoria, Long idEstadoMemoria) - end");
  }

  /**
   * Se crea el nuevo estado para la memoria recibida y se actualiza el estado
   * actual de esta.
   * 
   * @param memoria             {@link Memoria} a actualizar estado.
   * @param idTipoEstadoMemoria identificador del estado nuevo de la memoria.
   * @param comentario          un comentario
   */
  private void updateEstadoMemoria(Memoria memoria, long idTipoEstadoMemoria, String comentario) {
    log.debug("updateEstadoMemoria(Memoria memoria, Long idEstadoMemoria, String comentario) - start");

    // se crea el nuevo estado para la memoria
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(idTipoEstadoMemoria);
    EstadoMemoria estadoMemoria = new EstadoMemoria(null, memoria, tipoEstadoMemoria, Instant.now(), comentario);

    estadoMemoriaRepository.save(estadoMemoria);

    // Se actualiza la memoria con el nuevo tipo estado memoria

    memoria.setEstadoActual(tipoEstadoMemoria);
    memoriaRepository.save(memoria);

    log.debug("updateEstadoMemoria(Memoria memoria, Long idEstadoMemoria, String comentario ) - end");
  }

  /**
   * Obtiene todas las entidades {@link Memoria} paginadas y filtadas.
   *
   * @param paging     la información de paginación.
   * @param query      información del filtro.
   * @param personaRef la referencia de la persona
   * @return el listado de entidades {@link Memoria} paginadas y filtradas.
   */
  @Override
  public Page<MemoriaPeticionEvaluacion> findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(
      String query, Pageable paging, String personaRef) {
    log.debug(
        "findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(String query,Pageable paging, String personaRef) - start");
    // TODO: Eliminar cuando el custom repository contemple Predicates a null
    Specification<Memoria> specs = null;
    if (StringUtils.isNotBlank(query)) {
      specs = SgiRSQLJPASupport.toSpecification(query);
    }

    Page<MemoriaPeticionEvaluacion> page = memoriaRepository.findAllMemoriasEvaluaciones(specs, paging, personaRef);
    log.debug(
        "findAllMemoriasWithPersonaRefCreadorPeticionesEvaluacionOrResponsableMemoria(String query,Pageable paging, String personaRef) - end");
    return page;
  }

  /**
   * Devuelve si la {@link Memoria} existe para la persona responsable de memorias
   * o creador de la petición de evaluación
   * 
   * @param personaRef usuario
   * @param idMemoria  identificador de la {@link Memoria}
   * @return la entidad {@link Memoria}
   */
  @Override
  public Boolean isMemoriaWithPersonaRefCreadorPeticionEvaluacionOrResponsableMemoria(String personaRef,
      Long idMemoria) {
    log.debug(
        "isMemoriaWithPersonaRefCreadorPeticionEvaluacionOrResponsableMemoria(String personaRef, Long idMemoria) - start");
    Specification<Memoria> specsMem = MemoriaSpecifications.byId(idMemoria);

    Page<MemoriaPeticionEvaluacion> returnValue = memoriaRepository.findAllMemoriasEvaluaciones(specsMem,
        PageRequest.of(0, 1), personaRef);

    log.debug(
        "isMemoriaWithPersonaRefCreadorPeticionEvaluacionOrResponsableMemoria(String personaRef, Long idMemoria) - end");
    return returnValue.hasContent();
  }

  /**
   * Actualiza el estado de la memoria a su estado anterior, baja la version de la
   * memoria y elimina la evaluacion si la memoria se encuentra en el estado
   * EN_EVALUACION
   * 
   * @param id identificador del objeto {@link Memoria}
   * @return la {@link Memoria} si se ha podido actualizar el estado
   */
  @Transactional
  @Override
  public Memoria updateEstadoAnteriorMemoria(Long id) {

    Memoria memoria = memoriaRepository.findById(id).orElseThrow(() -> new MemoriaNotFoundException(id));

    TipoEstadoMemoria.Tipo tipoEstadoMemoriaActual = memoria.getEstadoActual().getTipo();

    Assert.isTrue(Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.EN_SECRETARIA)
        || Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.EN_SECRETARIA_REVISION_MINIMA)
        || Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.ARCHIVADA)
        || Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.EN_EVALUACION),
        "El estado actual de la memoria no es el correcto para recuperar el estado anterior");

    // Si la memoria se cambió al estado anterior estando en evaluación, se
    // eliminará la evaluación.
    if (Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.EN_EVALUACION)) {
      Evaluacion evaluacion = evaluacionRepository
          .findFirstByMemoriaIdAndActivoTrueOrderByVersionDescCreationDateDesc(memoria.getId()).orElse(null);

      Assert.notNull(evaluacion, "La memoria no tiene evaluacion");

      Assert.isTrue(evaluacion.getConvocatoriaReunion().getFechaEvaluacion().isAfter(Instant.now()),
          "La fecha de la convocatoria es anterior a la actual");

      Assert.isNull(evaluacion.getDictamen(), "No se pueden eliminar memorias que ya contengan un dictamen");

      Assert.isTrue(comentarioRepository.countByEvaluacionId(evaluacion.getId()) == 0L,
          "No se puede eliminar una memoria que tenga comentarios asociados");

      evaluacion.setActivo(Boolean.FALSE);
      evaluacionRepository.save(evaluacion);
    }

    if (Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.EN_SECRETARIA)
        || Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.EN_SECRETARIA_REVISION_MINIMA)) {
      informeService.deleteLastInformeMemoria(memoria.getId());
    }

    // Se retrocede el estado de la memoria, no se hace nada con el estado de la
    // retrospectiva
    this.updateMemoriaToEstadoAnterior(memoria, true);

    // Se actualiza la memoria con el estado anterior
    return memoriaRepository.save(memoria);
  }

  /**
   * Recupera la memoria con su estado anterior seteado ya sea memoria o
   * retrospectiva, manteniendo la version de la memoria y elimina el estado
   * actual de la memoria
   * 
   * @param memoria el objeto {@link Memoria}
   * @return la memoria o retrospectiva con su estado anterior
   */
  @Override
  public Memoria getMemoriaWithEstadoAnterior(Memoria memoria) {
    if (Boolean.TRUE.equals(memoria.getRequiereRetrospectiva()
        && Objects.nonNull(memoria.getRetrospectiva()))
        && memoria.getRetrospectiva().getEstadoRetrospectiva().getTipo()
            .equals(EstadoRetrospectiva.Tipo.EN_EVALUACION)) {
      updateRetrospectivaToEstadoAnterior(memoria.getRetrospectiva());
    } else {
      this.updateMemoriaToEstadoAnterior(memoria, false);
    }
    return memoria;
  }

  /**
   * Recupera la memoria con su estado anterior seteado, devuelve la memoria a
   * la version anterior (si downgradeVersion es true) y elimina el estado
   * actual de la memoria
   * 
   * @param memoria          el objeto {@link Memoria}
   * @param downgradeVersion si es true se baja la version de la memoria
   */
  private void updateMemoriaToEstadoAnterior(Memoria memoria, boolean downgradeVersion) {
    log.debug(
        "updateMemoriaToEstadoAnterior(memoriaId: {}, downgradeVersion: {}) - start",
        memoria.getId(), downgradeVersion);

    List<EstadoMemoria> estadosMemoria = estadoMemoriaRepository
        .findAllByMemoriaIdOrderByFechaEstadoDesc(memoria.getId());

    Assert.isTrue(estadosMemoria.size() > 1, "No se puede recuperar el estado anterior de la memoria");

    EstadoMemoria estadoMemoriaActual = estadosMemoria.remove(0);
    EstadoMemoria estadoMemoriaAnterior = estadosMemoria.remove(0);

    // eliminamos el estado actual del histórico
    estadoMemoriaRepository.deleteById(estadoMemoriaActual.getId());

    memoria.setEstadoActual(estadoMemoriaAnterior.getTipoEstadoMemoria());

    if (downgradeVersion) {
      memoria.setVersion(memoria.getVersion() - 1);
    }

    log.debug(
        "updateMemoriaToEstadoAnterior(memoriaId: {}, downgradeVersion: {}) - end",
        memoria.getId(), downgradeVersion);
  }

  /**
   * Recupera la retrospectiva con su estado anterior seteado
   * 
   * @param retrospectiva el objeto {@link Retrospectiva}
   */
  private void updateRetrospectivaToEstadoAnterior(Retrospectiva retrospectiva) {
    log.debug("updateRetrospectivaToEstadoAnterior(retrospectivaId: {}) - start", retrospectiva.getId());

    EstadoRetrospectiva.Tipo estadoRetrospectivaAnterior = null;
    switch (retrospectiva.getEstadoRetrospectiva().getTipo()) {
      case PENDIENTE:
      case COMPLETADA:
        estadoRetrospectivaAnterior = EstadoRetrospectiva.Tipo.PENDIENTE;
        break;
      case EN_SECRETARIA:
        estadoRetrospectivaAnterior = EstadoRetrospectiva.Tipo.COMPLETADA;
        break;
      case EN_EVALUACION:
        estadoRetrospectivaAnterior = EstadoRetrospectiva.Tipo.EN_SECRETARIA;
        break;
      case FIN_EVALUACION:
        estadoRetrospectivaAnterior = EstadoRetrospectiva.Tipo.EN_EVALUACION;
        break;
      default:
        throw new EstadoRetrospectivaNotFoundException(retrospectiva.getEstadoRetrospectiva().getId());
    }

    retrospectiva.setEstadoRetrospectiva(EstadoRetrospectiva.builder().id(estadoRetrospectivaAnterior.getId()).build());
    log.debug("updateRetrospectivaToEstadoAnterior(retrospectivaId: {}) - end", retrospectiva.getId());
  }

  /**
   * Actualiza el estado de la {@link Memoria} al estado en secretaria
   * correspondiente al {@link TipoEvaluacion} y {@link TipoEstadoMemoria}
   * actuales de la {@link Memoria}.
   * 
   * Se crea el informe asociado a la version actual de la memoria y si esta en un
   * estado de revision minima se crea tambien la evaluacion de revision minima.
   * 
   * @param idMemoria  identificador de la {@link Memoria}.
   * @param personaRef Identificador de la persona que realiza la accion
   */
  @Transactional
  @Override
  public void enviarSecretaria(Long idMemoria, String personaRef) {
    log.debug("enviarSecretaria(memoriaId: {}, personaRef: {}) - start", idMemoria, personaRef);
    Assert.notNull(idMemoria, "Memoria id no puede ser null para actualizar la memoria");

    Memoria memoria = memoriaRepository.findById(idMemoria).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));
    Evaluacion lastEvaluacion = evaluacionRepository
        .findFirstByMemoriaIdAndActivoTrueOrderByVersionDescCreationDateDesc(idMemoria)
        .orElse(null);

    TipoEvaluacion.Tipo tipoEvaluacion = TipoEvaluacion.Tipo.MEMORIA;

    if (lastEvaluacion != null) {
      tipoEvaluacion = lastEvaluacion.getTipoEvaluacion().getTipo();
    }

    TipoEstadoMemoria.Tipo tipoEstadoMemoriaActual = memoria.getEstadoActual().getTipo();

    Assert.isTrue(
        Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.COMPLETADA)
            || Objects.equals(tipoEstadoMemoriaActual,
                TipoEstadoMemoria.Tipo.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS)
            || Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.PENDIENTE_CORRECCIONES)
            || Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.NO_PROCEDE_EVALUAR)
            || Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_ANUAL)
            || Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.SOLICITUD_MODIFICACION_SEGUIMIENTO_ANUAL)
            || Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_FINAL)
            || Objects.equals(tipoEstadoMemoriaActual, TipoEstadoMemoria.Tipo.EN_ACLARACION_SEGUIMIENTO_FINAL),
        "No se puede realizar la acción porque la memoria ya ha sido enviada a secretaría");

    Assert.isTrue(memoria.getPeticionEvaluacion().getPersonaRef().equals(personaRef),
        "El usuario no es el propietario de la petición evaluación.");

    boolean crearEvaluacionRevMinima = false;

    switch (tipoEstadoMemoriaActual) {
      case COMPLETADA:
      case PENDIENTE_CORRECCIONES:
      case NO_PROCEDE_EVALUAR:
        updateEstadoMemoria(memoria, TipoEstadoMemoria.Tipo.EN_SECRETARIA.getId());
        break;
      case FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS:
        crearEvaluacionRevMinima = true;
        updateEstadoMemoria(memoria, TipoEstadoMemoria.Tipo.EN_SECRETARIA_REVISION_MINIMA.getId());
        break;
      case COMPLETADA_SEGUIMIENTO_ANUAL:
        tipoEvaluacion = TipoEvaluacion.Tipo.SEGUIMIENTO_ANUAL;
        updateEstadoMemoria(memoria, TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_ANUAL.getId());
        break;
      case SOLICITUD_MODIFICACION_SEGUIMIENTO_ANUAL:
        crearEvaluacionRevMinima = true;
        tipoEvaluacion = TipoEvaluacion.Tipo.SEGUIMIENTO_ANUAL;
        updateEstadoMemoria(memoria, TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_ANUAL_MODIFICACION.getId());
        break;
      case COMPLETADA_SEGUIMIENTO_FINAL:
        tipoEvaluacion = TipoEvaluacion.Tipo.SEGUIMIENTO_FINAL;
        updateEstadoMemoria(memoria, TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_FINAL.getId());
        break;
      case EN_ACLARACION_SEGUIMIENTO_FINAL:
        tipoEvaluacion = TipoEvaluacion.Tipo.SEGUIMIENTO_FINAL;
        crearEvaluacionRevMinima = true;
        updateEstadoMemoria(memoria, TipoEstadoMemoria.Tipo.EN_SECRETARIA_SEGUIMIENTO_FINAL_ACLARACIONES.getId());
        break;
      default:
        log.info(
            "enviarSecretaria(memoriaId: {}, personaRef: {}) - No se hace ningun cambio de estado desde el estado: {}",
            idMemoria, personaRef, tipoEstadoMemoriaActual.getId());
        break;
    }

    // Incrementa la version de la memoria y actualiza la fecha de envio a
    // secretaria
    memoria.setFechaEnvioSecretaria(Instant.now());
    memoria.setVersion(memoria.getVersion() + 1);
    memoriaRepository.save(memoria);

    if (crearEvaluacionRevMinima) {
      this.crearEvaluacionRevMinima(memoria, tipoEvaluacion);
    }

    this.crearInforme(memoria, tipoEvaluacion.getId());

    log.debug("enviarSecretaria(memoriaId: {}, personaRef: {}) - end", idMemoria, personaRef);
  }

  /**
   * Crea una evaluación de revision minima a partir de los datos de la memoria
   * 
   * @param memoria        la {@link Memoria} para la que se crea la evaluacion
   * @param tipoEvaluacion el tipo de {@link Evaluacion}
   */
  private void crearEvaluacionRevMinima(Memoria memoria, TipoEvaluacion.Tipo tipoEvaluacion) {
    log.debug("crearEvaluacionRevMinima(Memoria memoria, TipoEvaluacion.Tipo tipoEvaluacion) - start");
    Evaluacion evaluacion = evaluacionRepository
        .findFirstByMemoriaIdAndTipoEvaluacionIdAndActivoTrueOrderByVersionDesc(memoria.getId(), tipoEvaluacion.getId())
        .orElseThrow(() -> new EvaluacionNotFoundException(memoria.getId()));

    Evaluacion evaluacionNueva = new Evaluacion();
    BeanUtils.copyProperties(evaluacion, evaluacionNueva);
    evaluacionNueva.setId(null);
    evaluacionNueva.setVersion(evaluacion.getVersion() + 1);
    evaluacionNueva.setEsRevMinima(true);
    evaluacionNueva.setDictamen(null);
    evaluacionNueva.setTipoEvaluacion(TipoEvaluacion.builder().id(tipoEvaluacion.getId()).build());
    evaluacionNueva.setActivo(true);
    evaluacionRepository.save(evaluacionNueva);

    log.debug("crearEvaluacionRevMinima(Memoria memoria, TipoEvaluacion.Tipo tipoEvaluacion) - end");
  }

  private void crearInforme(Memoria memoria, Long tipoEvaluacion) {
    log.debug("crearInforme(memoria, tipoEvaluacion)- start");

    // Se crea un fichero en formato pdf con los datos del proyecto y con
    // los datos del formulario y subirlo al gestor documental y que el sistema
    // guarde en informes el identificador del documento.
    Informe informe = new Informe();
    informe.setVersion(memoria.getVersion());
    informe.setMemoria(memoria);
    informe.setTipoEvaluacion(TipoEvaluacion.builder().id(tipoEvaluacion).build());

    Long idFormulario = null;
    String tituloInforme = TITULO_INFORME_MXX;
    switch (TipoEvaluacion.Tipo.fromId(tipoEvaluacion)) {
      case MEMORIA:
        idFormulario = memoria.getComite().getFormulario().getId();
        break;
      case SEGUIMIENTO_ANUAL:
        idFormulario = Formulario.Tipo.SEGUIMIENTO_ANUAL.getId();
        tituloInforme = TITULO_INFORME_SA;
        break;
      case SEGUIMIENTO_FINAL:
        idFormulario = Formulario.Tipo.SEGUIMIENTO_FINAL.getId();
        tituloInforme = TITULO_INFORME_SF;
        break;
      case RETROSPECTIVA:
        idFormulario = Formulario.Tipo.RETROSPECTIVA.getId();
        tituloInforme = TITULO_INFORME_RETROSPECTIVA;
        break;
      default:
        log.warn("Tipo de Evaluación {} no encontrado", tipoEvaluacion.intValue());
        break;
    }

    // Se obtiene el informe en formato pdf creado mediante el servicio de reporting
    Resource informePdf = reportService.getMXX(memoria.getId(), idFormulario);

    // Se sube el informe a sgdoc
    String fileName = tituloInforme + "_" + memoria.getId() + LocalDate.now() + ".pdf";
    DocumentoOutput documento = sgdocService.uploadInforme(fileName, informePdf);

    // Se adjunta referencia del documento a sgdoc y se crea el informe
    informe.setDocumentoRef(documento.getDocumentoRef());
    informeService.create(informe);

    log.debug("crearInforme(memoria, tipoEvaluacion)- end");
  }

  /**
   * Actualiza el estado de la Retrospectiva de {@link Memoria} a 'En Secretaria'
   * 
   * @param idMemoria  de la memoria.
   * @param personaRef id de la persona
   */
  @Transactional
  @Override
  public void enviarSecretariaRetrospectiva(Long idMemoria, String personaRef) {
    log.debug("enviarSecretariaRetrospectiva(memoriaId: {}, personaRef: {}) - start", idMemoria, personaRef);
    Assert.notNull(idMemoria, "Memoria id no puede ser null para actualizar la memoria");

    Memoria memoria = memoriaRepository.findById(idMemoria)
        .orElseThrow(() -> new EstadoRetrospectivaNotFoundException(3L));
    // Si el estado es 'Completada', Requiere retrospectiva y el comité es CEEA
    Assert.isTrue(
        (memoria.getEstadoActual().getId() >= 9L && memoria.getRequiereRetrospectiva()
            && memoria.getComite().getTipo().equals(Comite.Tipo.CEEA)
            && memoria.getRetrospectiva().getEstadoRetrospectiva()
                .getTipo().equals(EstadoRetrospectiva.Tipo.COMPLETADA)),
        "La memoria no está en un estado correcto para pasar al estado 'En secretaría'");

    Assert.isTrue(memoria.getPeticionEvaluacion().getPersonaRef().equals(personaRef),
        "El usuario no es el propietario de la petición evaluación.");

    memoria.getRetrospectiva().setEstadoRetrospectiva(
        EstadoRetrospectiva.builder().id(EstadoRetrospectiva.Tipo.EN_SECRETARIA.getId()).build());
    memoria.setVersion(memoria.getVersion() + 1);
    memoriaRepository.save(memoria);

    this.crearInforme(memoria, TipoEvaluacion.Tipo.RETROSPECTIVA.getId());

    log.debug("enviarSecretariaRetrospectiva(memoriaId: {}, personaRef: {}) - end", idMemoria, personaRef);
  }

  @Override
  public Page<Memoria> findAllMemoriasPeticionEvaluacionModificables(Long idComite, Long idPeticionEvaluacion,
      Pageable paging) {
    log.debug(
        "findAllMemoriasPeticionEvaluacionModificables(Long idComite, Long idPeticionEvaluacion, Pageable paging) - start");

    Assert.notNull(idComite,
        "El identificador del comité no puede ser null para recuperar sus tipos de memoria asociados.");

    Assert.notNull(idPeticionEvaluacion,
        "El identificador de la petición de evaluación no puede ser null para recuperar sus tipos de memoria asociados.");

    return comiteRepository.findByIdAndActivoTrue(idComite).map(comite -> {
      log.debug(
          "findAllMemoriasPeticionEvaluacionModificables(Long idComite, Long idPeticionEvaluacion, Pageable paging) - end");
      return memoriaRepository.findAllMemoriasPeticionEvaluacionModificables(idComite, idPeticionEvaluacion, paging);

    }).orElseThrow(() -> new ComiteNotFoundException(idComite));

  }

  /**
   * Recuperar aquellas memorias que requieren evaluación retrospectiva y cuya
   * fecha de evaluación retrospectiva se encuentre entre el día actual y el
   * número de días guardado como parámetro de configuración
   * 
   * @return lista de {@link Memoria}
   */
  public List<Memoria> recuperarMemoriasAvisoFechaRetrospectiva() {
    log.debug("recuperarMemoriasAvisoFechaRetrospectiva() - start");
    Configuracion configuracion = configuracionService.findConfiguracion();
    long diasPreaviso = configuracion.getDiasAvisoRetrospectiva();

    Instant fechaInicio = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MIN).withNano(0).plusDays(diasPreaviso).toInstant();

    Instant fechaFin = this.getLastInstantOfDay().plusDays(diasPreaviso)
        .toInstant();

    Specification<Memoria> specsMemoriasByDiasAvisoRetrospectivaAndRequiereRetrospectiva = MemoriaSpecifications
        .requiereRetrospectiva()
        .and(MemoriaSpecifications.byFechaRetrospectivaBetween(fechaInicio, fechaFin));

    List<Memoria> memoriasPendientesAviso = memoriaRepository
        .findAll(specsMemoriasByDiasAvisoRetrospectivaAndRequiereRetrospectiva);

    log.debug("recuperarMemoriasAvisoFechaRetrospectiva() - end");
    return memoriasPendientesAviso;
  }

  public void sendComunicadoInformeRetrospectivaCeeaPendiente() {
    List<Memoria> memorias = recuperarMemoriasAvisoFechaRetrospectiva();
    if (CollectionUtils.isEmpty(memorias)) {
      log.info("No existen memorias que requieran generar aviso de evaluación de retrospectiva pendiente");
    } else {
      memorias.stream().forEach(memoria -> {
        String tipoActividad;
        if (!memoria.getPeticionEvaluacion().getTipoActividad().getNombre()
            .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
          tipoActividad = memoria.getPeticionEvaluacion().getTipoActividad().getNombre();
        } else {
          tipoActividad = memoria.getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre();
        }
        try {
          this.comunicadosService.enviarComunicadoInformeRetrospectivaCeeaPendiente(
              memoria.getComite().getNombreInvestigacion(),
              memoria.getComite().getGenero().toString(), memoria.getNumReferencia(), tipoActividad,
              memoria.getPeticionEvaluacion().getTitulo(),
              memoria.getPeticionEvaluacion().getPersonaRef());
        } catch (Exception e) {
          log.error("enviarComunicadoInformeRetrospectivaCeeaPendiente(memoriaId: {}) - Error al enviar el comunicado",
              memoria.getId(), e);
        }
      });
    }
  }

  public void sendComunicadoInformeSeguimientoFinalPendiente() {
    List<Memoria> memorias = recuperaInformesAvisoSeguimientoFinalPendiente();
    if (CollectionUtils.isEmpty(memorias)) {
      log.info("No existen evaluaciones que requieran generar aviso de informe de evaluación final pendiente.");
      return;
    }
    memorias.stream().forEach(memoria -> {
      String tipoActividad;
      if (!memoria.getPeticionEvaluacion().getTipoActividad().getNombre()
          .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
        tipoActividad = memoria.getPeticionEvaluacion().getTipoActividad().getNombre();
      } else {
        tipoActividad = memoria.getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre();
      }
      try {
        this.comunicadosService.enviarComunicadoInformeSeguimientoFinal(
            memoria.getComite().getNombreInvestigacion(),
            memoria.getNumReferencia(),
            tipoActividad,
            memoria.getPeticionEvaluacion().getTitulo(),
            memoria.getPeticionEvaluacion().getPersonaRef());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    });
  }

  public void sendComunicadoMemoriaArchivadaAutomaticamentePorInactividad(List<Memoria> memorias) {
    memorias.stream().forEach(memoria -> {
      String tipoActividad;
      if (!memoria.getPeticionEvaluacion().getTipoActividad().getNombre()
          .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
        tipoActividad = memoria.getPeticionEvaluacion().getTipoActividad().getNombre();
      } else {
        tipoActividad = memoria.getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre();
      }
      try {
        this.comunicadosService.enviarComunicadoMemoriaArchivadaAutomaticamentePorInactividad(
            memoria.getComite().getNombreInvestigacion(),
            memoria.getNumReferencia(),
            tipoActividad,
            memoria.getPeticionEvaluacion().getTitulo(),
            memoria.getPeticionEvaluacion().getPersonaRef());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    });

  }

  /**
   * Recuperar aquellas memorias con informe final pendiente
   * 
   * @return lista de {@link Memoria}
   */
  public List<Memoria> recuperaInformesAvisoSeguimientoFinalPendiente() {
    log.debug("recuperaInformesAvisoSeguimientoFinalPendiente() - start");

    Instant fechaInicio = Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MIN).withNano(0).minusYears(1L).toInstant();

    Instant fechaFin = this.getLastInstantOfDay().minusYears(1L)
        .toInstant();

    // Se buscan memorias con Peticiones de Evaluación activas y cuya fecha fin
    // cumpla un año durante el día de hoy
    Specification<Memoria> specsMemoriasComunicadoInfAnual = MemoriaSpecifications.peticionesActivas()
        .and(MemoriaSpecifications.byEstado(14L))
        .and(MemoriaSpecifications.byPeticionFechaFin(fechaInicio, fechaFin));

    List<Memoria> memoriasPendientesAviso = memoriaRepository
        .findAll(specsMemoriasComunicadoInfAnual);

    log.debug("recuperaInformesAvisoSeguimientoFinalPendiente() - end");
    return memoriasPendientesAviso;
  }

  private void validacionesCreateMemoria(Memoria memoria) {
    log.debug("validacionesCreateMemoria(Memoria memoria) - start");

    Assert.isNull(memoria.getId(), "Memoria id tiene que ser null para crear una nueva memoria");
    Assert.notNull(memoria.getPeticionEvaluacion().getId(),
        "Petición evaluación id no puede ser null para crear una nueva memoria");

    if (!peticionEvaluacionRepository.findByIdAndActivoTrue(memoria.getPeticionEvaluacion().getId()).isPresent()) {
      throw new PeticionEvaluacionNotFoundException(memoria.getPeticionEvaluacion().getId());
    }

    if (!comiteRepository.findByIdAndActivoTrue(memoria.getComite().getId()).isPresent()) {
      throw new ComiteNotFoundException(memoria.getComite().getId());
    }

    log.debug("validacionesCreateMemoria(Memoria memoria) - end");
  }

  /**
   * Recupera la referencia de una memoria según su tipo y comité.
   * 
   * @param idTipoMemoria Identificador {@link TipoMemoria}
   * @param numReferencia Referencia de la memoria copiada en caso de ser memoria
   *                      modificada.
   * @param comite        {ælink {@link Comite}}
   * @return número de referencia.
   */
  private String getReferenciaMemoria(Long idTipoMemoria, String numReferencia, Comite comite) {

    log.debug("getReferenciaMemoria(Long id, String numReferencia) - start");

    // Referencia memoria
    int anioActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).get(ChronoField.YEAR);
    StringBuilder sbNumReferencia = new StringBuilder(comite.getFormulario().getNombre())
        .append("/")
        .append(anioActual)
        .append("/");

    String numMemoria = "001";

    switch (TipoMemoria.Tipo.fromId(idTipoMemoria)) {
      case NUEVA: {
        // Se recupera la última memoria para el comité seleccionado
        Memoria ultimaMemoriaComite = memoriaRepository
            .findFirstByNumReferenciaContainingAndTipoMemoriaIdIsNotAndComiteIdOrderByNumReferenciaDesc(
                String.valueOf(anioActual), 2L, comite.getId());

        // Se incrementa el número de la memoria para el comité
        if (ultimaMemoriaComite != null) {
          Long numeroUltimaMemoria = Long.valueOf(ultimaMemoriaComite.getNumReferencia().split("/")[2].split("R")[0]);
          numeroUltimaMemoria++;
          numMemoria = String.format("%03d", numeroUltimaMemoria);
        }

        break;
      }
      case MODIFICACION: {
        // Se recupera la última memoria modificada de la memoria de la que se realiza
        // la copia y del comité de la memoria.
        Memoria ultimaMemoriaComite = memoriaRepository
            .findFirstByNumReferenciaContainingAndComiteIdOrderByNumReferenciaDesc(numReferencia, comite.getId());

        StringBuilder sbReferencia = new StringBuilder();
        sbReferencia.append(ultimaMemoriaComite.getNumReferencia().split("MR")[0].split("/")[2].split("R")[0])
            .append("MR");
        if (ultimaMemoriaComite.getNumReferencia().contains("MR")) {
          Long numeroUltimaMemoria = Long.valueOf(ultimaMemoriaComite.getNumReferencia().split("MR")[1]);
          numeroUltimaMemoria++;
          sbReferencia.append(numeroUltimaMemoria);

        } else {
          sbReferencia.append("1");
        }

        numMemoria = sbReferencia.toString();

        break;
      }
      case RATIFICACION: {
        // Se recupera la última memoria para el comité seleccionado
        Memoria ultimaMemoriaComite = memoriaRepository
            .findFirstByNumReferenciaContainingAndTipoMemoriaIdIsNotAndComiteIdOrderByNumReferenciaDesc(
                String.valueOf(anioActual), 2L, comite.getId());

        // Se incrementa el número de la memoria para el comité
        Long numeroUltimaMemoria = 1L;
        if (ultimaMemoriaComite != null) {
          numeroUltimaMemoria = Long.valueOf(ultimaMemoriaComite.getNumReferencia().split("/")[2].split("R")[0]);
          numeroUltimaMemoria++;
        }

        numMemoria = new StringBuilder(String.format("%03d", numeroUltimaMemoria))
            .append("R").toString();

        break;
      }
      default:
        log.warn("Tipo de Memoria {} no resuleto", idTipoMemoria.intValue());
        break;
    }

    sbNumReferencia.append(numMemoria);

    log.debug("getReferenciaMemoria(Long id, String numReferencia) - end");
    return sbNumReferencia.toString();
  }

  /**
   * Comprobación de si están o no los documentos obligatorios aportados para
   * pasar la memoria al estado en secretaría
   * 
   * @param idMemoria Id de {@link Memoria}
   * @param paging    pageable
   * @return true si existen documentos adjuntos obligatorios / false Si no se
   *         existen documentos adjuntos obligatorios
   */
  public Boolean checkDatosAdjuntosExists(Long idMemoria, Pageable paging) {
    Page<Respuesta> returnValue = respuestaRepository.findByMemoriaIdAndTipoDocumentoIsNotNull(idMemoria, paging);
    Boolean[] arr = { true };
    if (returnValue.hasContent()) {
      List<Respuesta> respuestas = returnValue.getContent();
      Long idFormulario = respuestas.get(0).getApartado().getBloque().getFormulario().getId();
      respuestas.stream().map(Respuesta::getTipoDocumento).forEach(tipoDocumento -> {
        if (!documentacionMemoriaRepository.existsByMemoriaIdAndTipoDocumentoIdAndTipoDocumentoFormularioId(idMemoria,
            tipoDocumento.getId(), idFormulario)) {
          arr[0] = false;
        }
      });
    }
    return arr[0];
  }

  /**
   * Se actualiza el estado de la memoria a "Archivado" de {@link Memoria} que han
   * pasado "mesesArchivadaPendienteCorrecciones" días desde la fecha de estado de
   * una memoria cuyo estado es "Pendiente Correcciones"
   * 
   * @return Los ids de memorias que pasan al estado "Archivado"
   */
  @Transactional
  public List<Long> archivarNoPresentados() {
    log.debug("archivarNoPresentados() - start");
    Configuracion configuracion = configuracionService.findConfiguracion();
    // Devuelve un listado de {@link Memoria} que han
    // pasado "mesesArchivadaPendienteCorrecciones" días desde la fecha de estado de
    // una memoria cuyo estado es "Pendiente Correcciones"
    Specification<Memoria> specsMemoriasByMesesArchivadaPendienteCorrecciones = MemoriaSpecifications.activos()
        .and(
            MemoriaSpecifications.estadoActualIn(Arrays.asList(TipoEstadoMemoria.Tipo.PENDIENTE_CORRECCIONES.getId())));

    List<Memoria> memorias = memoriaRepository.findAll(specsMemoriasByMesesArchivadaPendienteCorrecciones).stream()
        .filter(memoria -> {
          EstadoMemoria lastEstado = this.estadoMemoriaRepository
              .findTopByMemoriaIdOrderByFechaEstadoDesc(memoria.getId());
          return lastEstado.getFechaEstado().isBefore(Instant.now().atZone(ZoneOffset.UTC)
              .minus(Period.ofMonths(configuracion.getMesesArchivadaPendienteCorrecciones())).toInstant());
        }).map(memoria -> {
          try {
            this.updateEstadoMemoria(memoria, TipoEstadoMemoria.Tipo.ARCHIVADA.getId());
          } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
          }
          return memoria;
        }).filter(Objects::nonNull)
        .collect(Collectors.toList());

    if (!CollectionUtils.isEmpty(memorias)) {
      this.sendComunicadoMemoriaArchivadaAutomaticamentePorInactividad(memorias);
    }

    log.debug("archivarNoPresentados() - end");
    return memorias.stream().map(Memoria::getId).collect(Collectors.toList());

  }

  /**
   * Se actualiza el estado de la memoria a "Archivado" de las {@link Memoria}
   * para las que han pasado "diasArchivadaInactivo" dias desde la fecha desde el
   * ultimo cambio de estado si esta en alguno de los siguientes estados:
   * FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS, NO_PROCEDE_EVALUAR,
   * SOLICITUD_MODIFICACION, EN_ACLARACION_SEGUIMIENTO_FINAL, DESFAVORABLE y
   * PENDIENTE_CORRECCIONES
   * 
   * @return Los ids de las memorias que pasan al estado "Archivado"
   */
  @Transactional
  public List<Long> archivarInactivos() {
    log.debug("archivarInactivos() - start - end");
    Configuracion configuracion = configuracionService.findConfiguracion();

    Specification<Memoria> specsMemoriasByDiasArchivadaInactivo = MemoriaSpecifications.activos()
        .and(MemoriaSpecifications.estadoActualIn(Arrays.asList(
            TipoEstadoMemoria.Tipo.FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS.getId(),
            TipoEstadoMemoria.Tipo.NO_PROCEDE_EVALUAR.getId(),
            TipoEstadoMemoria.Tipo.SOLICITUD_MODIFICACION.getId(),
            TipoEstadoMemoria.Tipo.EN_ACLARACION_SEGUIMIENTO_FINAL.getId(),
            TipoEstadoMemoria.Tipo.DESFAVORABLE.getId())));

    return memoriaRepository.findAll(specsMemoriasByDiasArchivadaInactivo).stream()
        .filter(memoria -> {
          EstadoMemoria lastEstado = this.estadoMemoriaRepository
              .findTopByMemoriaIdOrderByFechaEstadoDesc(memoria.getId());

          return lastEstado.getFechaEstado().isBefore(Instant.now().atZone(ZoneOffset.UTC)
              .minus(Period.ofDays(configuracion.getDiasArchivadaInactivo())).toInstant());
        }).map(memoria -> {
          try {
            this.updateEstadoMemoria(memoria, TipoEstadoMemoria.Tipo.ARCHIVADA.getId());
            this.sendComunicadoMemoriaRevisionMinimaArchivada(memoria);
          } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
          }
          return memoria.getId();
        }).filter(Objects::nonNull)
        .collect(Collectors.toList());

  }

  public void sendComunicadoMemoriaRevisionMinimaArchivada(Memoria memoria) {
    String tipoActividad;
    if (!memoria.getPeticionEvaluacion().getTipoActividad().getNombre()
        .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
      tipoActividad = memoria.getPeticionEvaluacion().getTipoActividad().getNombre();
    } else {
      tipoActividad = memoria.getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre();
    }
    try {
      this.comunicadosService.enviarComunicadoMemoriaRevisionMinimaArchivada(
          memoria.getComite().getNombreInvestigacion(),
          memoria.getNumReferencia(),
          tipoActividad,
          memoria.getPeticionEvaluacion().getTitulo(),
          memoria.getPeticionEvaluacion().getPersonaRef());
    } catch (Exception e) {
      log.error("sendComunicadoMemoriaRevisionMinimaArchivada(memoriaId: {}) - Error al enviar el comunicado",
          memoria.getId(), e);
    }
  }

  private ZonedDateTime getLastInstantOfDay() {
    return Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MAX).withNano(0);
  }

  /**
   * Devuelve un listado de {@link Memoria} para una determinada petición de
   * evaluación en dos posibles estados
   * 
   * @param idPeticionEvaluacion Identificador {@link PeticionEvaluacion}.
   * @param tipoEstadoMemoria    identificador del {@link TipoEstadoMemoria}
   * @return listado de memorias
   */
  public List<Memoria> findAllByPeticionEvaluacionIdAndEstadoActualId(Long idPeticionEvaluacion,
      Long tipoEstadoMemoria) {
    return memoriaRepository.findAllByPeticionEvaluacionIdAndEstadoActualId(
        idPeticionEvaluacion, tipoEstadoMemoria);
  }

  /**
   * Desactiva la {@link Memoria}.
   *
   * @param id Id de la {@link Memoria}.
   * @return Entidad {@link Memoria} persistida desactivada.
   */
  @Transactional
  @Override
  public Memoria desactivar(Long id) {
    Assert.notNull(id,
        () -> ProblemMessage.builder().key(Assert.class, PROBLEM_MESSAGE_NOTNULL)
            .parameter(PROBLEM_MESSAGE_PARAMETER_FIELD, ApplicationContextSupport.getMessage("id"))
            .parameter(PROBLEM_MESSAGE_PARAMETER_ENTITY,
                ApplicationContextSupport.getMessage(Memoria.class))
            .build());

    return this.memoriaRepository.findById(id).map(memoria -> {

      if (Boolean.FALSE.equals(memoria.getActivo())) {
        return memoria;
      }
      memoria.setActivo(Boolean.FALSE);

      return this.memoriaRepository.save(memoria);
    }).orElseThrow(() -> new MemoriaNotFoundException(id));
  }

  /**
   * Cambia el estado de la memoria a {@link Tipo#SUBSANACION} con el comentario
   * 
   * @param id         Id de la {@link Memoria}.
   * @param comentario comentario subsanacion
   */
  @Transactional
  @Override
  public void indicarSubsanacion(Long id, String comentario) {
    log.debug("indicarSubsanacion(Long id, String comentario) - start");

    AssertHelper.idNotNull(id, Memoria.class);

    Memoria memoria = memoriaRepository.findById(id).orElseThrow(() -> new MemoriaNotFoundException(id));

    if (!Objects.equals(memoria.getEstadoActual().getId(), TipoEstadoMemoria.Tipo.EN_SECRETARIA.getId())) {
      throw new EstadoMemoriaIndicarSubsanacionNotValidException();
    }

    updateEstadoMemoria(memoria, TipoEstadoMemoria.Tipo.SUBSANACION.getId(), comentario);

    try {
      String tipoActividad;
      if (!memoria.getPeticionEvaluacion().getTipoActividad().getNombre()
          .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
        tipoActividad = memoria.getPeticionEvaluacion().getTipoActividad().getNombre();
      } else {
        tipoActividad = memoria.getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre();
      }

      this.comunicadosService.enviarComunicadoIndicarSubsanacion(
          memoria.getComite().getNombreInvestigacion(),
          comentario,
          memoria.getNumReferencia(),
          tipoActividad,
          memoria.getPeticionEvaluacion().getTitulo(),
          memoria.getPeticionEvaluacion().getPersonaRef());
    } catch (Exception e) {
      log.error("indicarSubsanacion(memoriaId: {}) - Error al enviar el comunicado", memoria.getId(), e);
    }

    log.debug("indicarSubsanacion(Long id, String comentario) - end");
  }

  /**
   * Devuelve el estado actual de la memoria
   * 
   * @param id Id de la {@link Memoria}.
   * @return el estado de la memoria
   */
  @Transactional
  @Override
  public EstadoMemoria getEstadoActualMemoria(Long id) {
    log.debug("getEstadoActualMemoria(Long id) - start");

    AssertHelper.idNotNull(id, Memoria.class);

    EstadoMemoria returnValue = this.estadoMemoriaRepository.findTopByMemoriaIdOrderByFechaEstadoDesc(id);

    log.debug("getEstadoActualMemoria(Long id) - end");

    return returnValue;
  }

  /**
   * 
   * Devuelve una lista paginada de {@link Memoria} asignables para una
   * convocatoria determinada
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretaría seguimiento anual" y "En secretaría seguimiento final" con la
   * fecha de envío es igual o menor a la fecha límite de la convocatoria de
   * reunión.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de envío es igual o menor a
   * la fecha límite de la convocatoria de reunión y las que tengan una
   * retrospectiva en estado "En secretaría".
   * 
   * @param idPeticionEvaluacion Identificador del {@link PeticionEvaluacion}
   * @return lista de memorias asignables a la petición de evaluación.
   */
  @Override
  public List<Memoria> findAllMemoriasAsignablesPeticionEvaluacion(Long idPeticionEvaluacion) {
    log.debug("findAllMemoriasAsignablesPeticionEvaluacion(Long idPeticionEvaluacion) - start");
    List<Memoria> returnValue = memoriaRepository.findAllMemoriasAsignablesPeticionEvaluacion(idPeticionEvaluacion);
    log.debug("findAllMemoriasAsignablesPeticionEvaluacion(Long idPeticionEvaluacion) - end");
    return returnValue;
  }

}
