package org.crue.hercules.sgi.eti.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
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
import org.crue.hercules.sgi.eti.model.Informe;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.Respuesta;
import org.crue.hercules.sgi.eti.model.Tarea;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.model.TipoEvaluacion;
import org.crue.hercules.sgi.eti.model.TipoMemoria;
import org.crue.hercules.sgi.eti.repository.ApartadoRepository;
import org.crue.hercules.sgi.eti.repository.BloqueRepository;
import org.crue.hercules.sgi.eti.repository.ComentarioRepository;
import org.crue.hercules.sgi.eti.repository.ComiteRepository;
import org.crue.hercules.sgi.eti.repository.DocumentacionMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoMemoriaRepository;
import org.crue.hercules.sgi.eti.repository.EstadoRetrospectivaRepository;
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
import org.crue.hercules.sgi.eti.service.SgdocService;
import org.crue.hercules.sgi.eti.service.sgi.SgiApiRepService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
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
 * Service Implementation para la gesti??n de {@link Memoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class MemoriaServiceImpl implements MemoriaService {

  private static final String TITULO_INFORME_MXX = "informeMemoriaPdf";
  private static final String TITULO_INFORME_RETROSPECTIVA = "informeRetrospectivaPdf";
  private static final String TITULO_INFORME_SA = "informeSAPdf";
  private static final String TITULO_INFORME_SF = "informeSFPdf";

  /** Propiedades de configuraci??n de la aplicaci??n */
  private final SgiConfigProperties sgiConfigProperties;

  /** Comentario repository */
  private final ComentarioRepository comentarioRepository;

  /** Comit?? repository */
  private final ComiteRepository comiteRepository;

  /** Documentacion memoria repository. */
  private final DocumentacionMemoriaRepository documentacionMemoriaRepository;

  /** Estado Memoria Repository. */
  private final EstadoMemoriaRepository estadoMemoriaRepository;

  /** Estado Retrospectiva repository */
  private final EstadoRetrospectivaRepository estadoRetrospectivaRepository;

  /** Evaluacion repository */
  private final EvaluacionRepository evaluacionRepository;

  /** Memoria repository */
  private final MemoriaRepository memoriaRepository;

  /** Petici??n evaluaci??n repository */
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

  private static final String TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA = "Investigaci??n tutelada";

  public MemoriaServiceImpl(SgiConfigProperties sgiConfigProperties, MemoriaRepository memoriaRepository,
      EstadoMemoriaRepository estadoMemoriaRepository, EstadoRetrospectivaRepository estadoRetrospectivaRepository,
      EvaluacionRepository evaluacionRepository, ComentarioRepository comentarioRepository,
      InformeService informeService, PeticionEvaluacionRepository peticionEvaluacionRepository,
      ComiteRepository comiteRepository, DocumentacionMemoriaRepository documentacionMemoriaRepository,
      RespuestaRepository respuestaRepository, TareaRepository tareaRepository,
      ConfiguracionService configuracionService, SgiApiRepService reportService, SgdocService sgdocService,
      BloqueRepository bloqueRepository, ApartadoRepository apartadoRepository, ComunicadosService comunicadosService) {
    this.sgiConfigProperties = sgiConfigProperties;
    this.memoriaRepository = memoriaRepository;
    this.estadoMemoriaRepository = estadoMemoriaRepository;
    this.estadoRetrospectivaRepository = estadoRetrospectivaRepository;
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

    // La memoria se crea con tipo estado memoria "En elaboraci??n".
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(Constantes.TIPO_ESTADO_MEMORIA_EN_ELABORACION);
    memoria.setEstadoActual(tipoEstadoMemoria);

    memoria.setNumReferencia(
        getReferenciaMemoria(memoria.getTipoMemoria().getId(), memoria.getNumReferencia(), memoria.getComite()));

    // Requiere retrospectiva
    memoria.setRequiereRetrospectiva(Boolean.FALSE);

    // Versi??n
    memoria.setVersion(0);

    // Activo
    memoria.setActivo(Boolean.TRUE);

    return memoriaRepository.save(memoria);
  }

  @Transactional
  @Override
  public Memoria createModificada(Memoria nuevaMemoria, Long id) {
    log.debug("Memoria createModificada(Memoria memoria, id) - start");

    validacionesCreateMemoria(nuevaMemoria);

    Assert.isTrue(nuevaMemoria.getTipoMemoria().getId().equals(2L),
        "La memoria no es del tipo adecuado para realizar una copia a partir de otra memoria.");

    Memoria memoria = memoriaRepository.findByIdAndActivoTrue(id).orElseThrow(() -> new MemoriaNotFoundException(id));

    nuevaMemoria.setRequiereRetrospectiva(memoria.getRequiereRetrospectiva());
    nuevaMemoria.setVersion(1);
    nuevaMemoria.setActivo(Boolean.TRUE);
    nuevaMemoria.setMemoriaOriginal(memoria);

    // La memoria se crea con tipo estado memoria "En elaboraci??n".
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(Constantes.TIPO_ESTADO_MEMORIA_EN_ELABORACION);
    nuevaMemoria.setEstadoActual(tipoEstadoMemoria);

    nuevaMemoria.setNumReferencia(
        getReferenciaMemoria(nuevaMemoria.getTipoMemoria().getId(), memoria.getNumReferencia(), memoria.getComite()));

    final Memoria memoriaCreada = memoriaRepository.save(nuevaMemoria);

    Page<DocumentacionMemoria> documentacionesMemoriaPage = documentacionMemoriaRepository
        .findByMemoriaIdAndMemoriaActivoTrue(memoria.getId(), null);

    List<DocumentacionMemoria> documentacionesMemoriaList = documentacionesMemoriaPage.getContent().stream()
        .map(documentacionMemoria -> {
          return new DocumentacionMemoria(null, memoriaCreada, documentacionMemoria.getTipoDocumento(),
              documentacionMemoria.getDocumentoRef(), documentacionMemoria.getNombre());
        }).collect(Collectors.toList());

    documentacionMemoriaRepository.saveAll(documentacionesMemoriaList);

    // Guardamos los ids de los apartados del formulario de retrospectiva
    List<Long> idsApartadosRetrospectiva = new ArrayList<Long>();
    Page<Bloque> bloques = bloqueRepository.findByFormularioId(Constantes.FORMULARIO_RETROSPECTIVA, null);
    bloques.getContent().stream().forEach(bloque -> {
      Page<Apartado> apartados = apartadoRepository.findByBloqueIdAndPadreIsNull(bloque.getId(), null);
      apartados.getContent().stream().forEach(apartado -> {
        idsApartadosRetrospectiva.add(apartado.getId());
      });
    });

    Page<Respuesta> respuestasPage = respuestaRepository.findByMemoriaIdAndMemoriaActivoTrue(memoria.getId(), null);
    /**
     * Filtramos por los ids de los apartados del formulario de retrospectiva para
     * no guardar las respuestas en caso de que exista
     */
    List<Respuesta> respuestaList = respuestasPage.getContent().stream()
        .filter(r -> idsApartadosRetrospectiva.indexOf(r.getApartado().getId()) == -1).map(respuesta -> {
          return new Respuesta(null, memoriaCreada, respuesta.getApartado(), respuesta.getTipoDocumento(),
              respuesta.getValor());
        }).collect(Collectors.toList());

    respuestaRepository.saveAll(respuestaList);

    /** Tarea */
    List<Tarea> tareasMemoriaOriginal = tareaRepository.findAllByMemoriaId(memoria.getId());
    if (!tareasMemoriaOriginal.isEmpty()) {
      List<Tarea> tareasMemoriaCopy = tareasMemoriaOriginal.stream().map(tarea -> {
        return new Tarea(null, tarea.getEquipoTrabajo(), nuevaMemoria, tarea.getTarea(), tarea.getFormacion(),
            tarea.getFormacionEspecifica(), tarea.getOrganismo(), tarea.getAnio(), tarea.getTipoTarea());
      }).collect(Collectors.toList());
      tareaRepository.saveAll(tareasMemoriaCopy);
    }

    log.debug("Memoria createModificada(Memoria memoria, id) - end");
    return memoriaCreada;
  }

  /**
   * Obtiene todas las entidades {@link MemoriaPeticionEvaluacion} paginadas y
   * filtadas.
   *
   * @param paging la informaci??n de paginaci??n.
   * @param query  informaci??n del filtro.
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
   * "En secretar??a seguimiento anual" y "En secretar??a seguimiento final" con la
   * fecha de env??o es igual o menor a la fecha l??mite de la convocatoria de
   * reuni??n.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de env??o es igual o menor a
   * la fecha l??mite de la convocatoria de reuni??n y las que tengan una
   * retrospectiva en estado "En secretar??a".
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
   * Comit?? Fecha L??mite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Ordinaria" o "Extraordinaria" devuelve las
   * memorias en estado "En secretaria" con la fecha de env??o es igual o menor a
   * la fecha l??mite de la convocatoria de reuni??n y las que tengan una
   * retrospectiva en estado "En secretar??a".
   * 
   * @param query    filtro de b??squeda.
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
   * Comit?? y Fecha L??mite de la convocatoria.
   * 
   * Si la convocatoria es de tipo "Seguimiento" devuelve las memorias en estado
   * "En secretar??a seguimiento anual" y "En secretar??a seguimiento final" con la
   * fecha de env??o es igual o menor a la fecha l??mite de la convocatoria de
   * reuni??n.
   * 
   * @param query    filtro de b??squeda.
   * @param pageable pageable
   */
  @Override
  public Page<Memoria> findAllAsignablesTipoConvocatoriaSeguimiento(String query, Pageable pageable) {
    log.debug("findAllAsignablesTipoConvocatoriaSeguimiento(String query,Pageable pageable) - start");

    Specification<Memoria> specs = MemoriaSpecifications.activos()
        .and(MemoriaSpecifications
            .estadoActualIn(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_ANUAL,
                Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_SEGUIMIENTO_FINAL)))
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
   * @throws MemoriaNotFoundException Si no existe ning??n {@link Memoria} con ese
   *                                  id.
   */
  @Override
  public Memoria findById(final Long id) throws MemoriaNotFoundException {
    log.debug("Petici??n a get Memoria : {}  - start", id);
    final Memoria Memoria = memoriaRepository.findById(id).orElseThrow(() -> new MemoriaNotFoundException(id));
    log.debug("Petici??n a get Memoria : {}  - end", id);
    return Memoria;

  }

  /**
   * Elimina una entidad {@link Memoria} por id.
   *
   * @param id el id de la entidad {@link Memoria}.
   */
  @Transactional
  @Override
  public void delete(Long id) throws MemoriaNotFoundException {
    log.debug("Petici??n a delete Memoria : {}  - start", id);
    Assert.notNull(id, "El id de Memoria no puede ser null.");
    if (!memoriaRepository.existsById(id)) {
      throw new MemoriaNotFoundException(id);
    }
    memoriaRepository.deleteById(id);
    log.debug("Petici??n a delete Memoria : {}  - end", id);
  }

  /**
   * Actualiza los datos del {@link Memoria}.
   * 
   * @param memoriaActualizar {@link Memoria} con los datos actualizados.
   * @return El {@link Memoria} actualizado.
   * @throws MemoriaNotFoundException Si no existe ning??n {@link Memoria} con ese
   *                                  id.
   * @throws IllegalArgumentException Si el {@link Memoria} no tiene id.
   */

  @Transactional
  @Override
  public Memoria update(final Memoria memoriaActualizar) {
    log.debug("update(Memoria MemoriaActualizar) - start");

    Assert.notNull(memoriaActualizar.getId(), "Memoria id no puede ser null para actualizar un tipo memoria");

    return memoriaRepository.findById(memoriaActualizar.getId()).map(memoria -> {

      // Se comprueba si se est?? desactivando la memoria
      if (memoria.getActivo() && !memoriaActualizar.getActivo()) {
        Assert.isTrue(
            memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_ELABORACION
                || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_COMPLETADA,
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
      memoria.setVersion(memoriaActualizar.getVersion());
      memoria.setCodOrganoCompetente(memoriaActualizar.getCodOrganoCompetente());
      memoria.setActivo(memoriaActualizar.getActivo());

      Memoria returnValue = memoriaRepository.save(memoria);
      log.debug("update(Memoria memoriaActualizar) - end");
      return returnValue;
    }).orElseThrow(() -> new MemoriaNotFoundException(memoriaActualizar.getId()));
  }

  /**
   * Devuelve las memorias de una petici??n evaluaci??n con su fecha l??mite y de
   * evaluaci??n.
   * 
   * @param idPeticionEvaluacion Identificador {@link PeticionEvaluacion}
   * @return lista de memorias de {@link PeticionEvaluacion}
   */
  @Override
  public List<MemoriaPeticionEvaluacion> findMemoriaByPeticionEvaluacionMaxVersion(Long idPeticionEvaluacion) {
    List<MemoriaPeticionEvaluacion> returnValue = memoriaRepository.findMemoriasEvaluacion(idPeticionEvaluacion, null);
    return returnValue;
  }

  /**
   * Se crea el nuevo estado para la memoria recibida y se actualiza el estado
   * actual de esta.
   * 
   * @param memoria             {@link Memoria} a actualizar estado.
   * @param idTipoEstadoMemoria identificador del estado nuevo de la memoria.
   */
  @Override
  public void updateEstadoMemoria(Memoria memoria, long idTipoEstadoMemoria) {
    log.debug("updateEstadoMemoria(Memoria memoria, Long idEstadoMemoria) - start");

    // se crea el nuevo estado para la memoria
    TipoEstadoMemoria tipoEstadoMemoria = new TipoEstadoMemoria();
    tipoEstadoMemoria.setId(idTipoEstadoMemoria);
    EstadoMemoria estadoMemoria = new EstadoMemoria(null, memoria, tipoEstadoMemoria, Instant.now());

    estadoMemoriaRepository.save(estadoMemoria);

    // Se actualiza la memoria con el nuevo tipo estado memoria

    memoria.setEstadoActual(tipoEstadoMemoria);
    memoriaRepository.save(memoria);

    log.debug("updateEstadoMemoria(Memoria memoria, Long idEstadoMemoria) - end");
  }

  /**
   * Obtiene todas las entidades {@link Memoria} paginadas y filtadas.
   *
   * @param paging     la informaci??n de paginaci??n.
   * @param query      informaci??n del filtro.
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
   * o creador de la petici??n de evaluaci??n
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
   * Actualiza el estado de la memoria a su estado anterior
   * 
   * @param id identificador del objeto {@link Memoria}
   * @return la {@link Memoria} si se ha podido actualizar el estado
   */
  @Transactional
  @Override
  public Memoria updateEstadoAnteriorMemoria(Long id) {

    Optional<Memoria> returnMemoria = memoriaRepository.findById(id);
    Memoria memoria = null;
    if (returnMemoria.isPresent()) {
      memoria = returnMemoria.get();
      if (memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA
          || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_REVISION_MINIMA
          || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_ARCHIVADO
          || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION) {

        try {
          // Si la memoria se cambi?? al estado anterior estando en evaluaci??n, se
          // eliminar?? la evaluaci??n.
          if (memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION) {
            Evaluacion evaluacion = evaluacionRepository.findByMemoriaIdAndVersionAndActivoTrue(memoria.getId(),
                memoria.getVersion());

            Assert.isTrue(evaluacion.getConvocatoriaReunion().getFechaEvaluacion().isAfter(Instant.now()),
                "La fecha de la convocatoria es anterior a la actual");

            Assert.isNull(evaluacion.getDictamen(), "No se pueden eliminar memorias que ya contengan un dictamen");

            Assert.isTrue(comentarioRepository.countByEvaluacionId(evaluacion.getId()) == 0L,
                "No se puede eliminar una memoria que tenga comentarios asociados");

            memoria.setVersion(memoria.getVersion() - 1);
            evaluacion.setActivo(Boolean.FALSE);
            evaluacionRepository.save(evaluacion);
          }

          if (memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA
              || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_REVISION_MINIMA) {
            // se eliminan los informes en caso de que las memorias tengan alguno asociado
            informeService.deleteInformeMemoria(memoria.getId());
          }

          // Se retrocede el estado de la memoria, no se hace nada con el estado de la
          // retrospectiva
          memoria = this.getEstadoAnteriorMemoria(memoria, false);
          // Se actualiza la memoria con el estado anterior
          return memoriaRepository.save(memoria);
        } catch (Exception e) {
          log.error("No se ha podido recuperar el estado anterior de la memoria", e);
          return null;
        }

      } else {
        Assert.isTrue(
            memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA
                || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_SECRETARIA_REVISION_MINIMA
                || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_ARCHIVADO
                || memoria.getEstadoActual().getId() == Constantes.TIPO_ESTADO_MEMORIA_EN_EVALUACION,
            "El estado actual de la memoria no es el correcto para recuperar el estado anterior");
        return null;
      }

    } else {
      throw new MemoriaNotFoundException(id);
    }
  }

  /**
   * Recupera la memoria con su estado anterior seteado ya sea memoria o
   * retrospectiva
   * 
   * @param memoria el objeto {@link Memoria}
   * @return la memoria o retrospectiva con su estado anterior
   */
  @Override
  public Memoria getEstadoAnteriorMemoria(Memoria memoria) {

    return this.getEstadoAnteriorMemoria(memoria, true);
  }

  /**
   * Recupera la memoria con su estado anterior seteado ya sea memoria o
   * retrospectiva
   * 
   * @param memoria                    el objeto {@link Memoria}
   * @param cambiarEstadoRetrospectiva si se desea cambiar o no el estado de la
   *                                   retrospectiva
   * @return la memoria o retrospectiva con su estado anterior
   */
  public Memoria getEstadoAnteriorMemoria(Memoria memoria, Boolean cambiarEstadoRetrospectiva) {

    if (memoria.getRetrospectiva() == null || !cambiarEstadoRetrospectiva) {
      List<EstadoMemoria> estadosMemoria = estadoMemoriaRepository
          .findAllByMemoriaIdOrderByFechaEstadoDesc(memoria.getId());

      Optional<EstadoMemoria> estadoAnteriorMemoria = estadosMemoria.stream()
          .filter(estadoMemoria -> estadoMemoria.getTipoEstadoMemoria().getId() != memoria.getEstadoActual().getId())
          .findFirst();

      Assert.isTrue(estadoAnteriorMemoria.isPresent(), "No se puede recuperar el estado anterior de la memoria");

      Optional<EstadoMemoria> estadoMemoriaActual = estadosMemoria.stream()
          .filter(estadoMemoria -> estadoMemoria.getTipoEstadoMemoria().getId() == memoria.getEstadoActual().getId())
          .findAny();

      Assert.isTrue(estadoMemoriaActual.isPresent(), "No se puede recuperar el estado actual de la memoria");

      memoria.setEstadoActual(estadoAnteriorMemoria.get().getTipoEstadoMemoria());
      // eliminamos el estado a cambiar en el hist??rico
      estadoMemoriaRepository.deleteById(estadoMemoriaActual.get().getId());
    } else {
      // El estado anterior de la retrospectiva es el estado con id anterior al que
      // tiene actualmente
      Optional<EstadoRetrospectiva> estadoRetrospectiva = estadoRetrospectivaRepository
          .findById(memoria.getRetrospectiva().getEstadoRetrospectiva().getId() - 1);

      Assert.isTrue(estadoRetrospectiva.isPresent(), "No se puede recuperar el estado anterior de la retrospectiva");
      if (estadoRetrospectiva.isPresent()) {
        memoria.getRetrospectiva().setEstadoRetrospectiva(estadoRetrospectiva.get());
      }
    }
    return memoria;
  }

  /**
   * Actualiza el estado de la {@link Memoria} a 'En Secretaria' o 'En Secretar??a
   * Revisi??n M??nima'
   * 
   * @param idMemoria de la memoria.
   */
  @Transactional
  @Override
  public void enviarSecretaria(Long idMemoria, String personaRef) {
    log.debug("enviarSecretaria(Long id) - start");
    Assert.notNull(idMemoria, "Memoria id no puede ser null para actualizar la memoria");

    memoriaRepository.findById(idMemoria).map(memoria -> {
      Assert.isTrue(
          memoria.getEstadoActual().getId() == 2L || memoria.getEstadoActual().getId() == 6L
              || memoria.getEstadoActual().getId() == 7L || memoria.getEstadoActual().getId() == 8L
              || memoria.getEstadoActual().getId() == 11L || memoria.getEstadoActual().getId() == 16L
              || memoria.getEstadoActual().getId() == 21L,
          "La memoria no est?? en un estado correcto para pasar al estado 'En secretar??a'");

      Assert.isTrue(memoria.getPeticionEvaluacion().getPersonaRef().equals(personaRef),
          "El usuario no es el propietario de la petici??n evaluaci??n.");

      boolean crearEvaluacion = false;

      Long tipoEvaluacion = Constantes.TIPO_EVALUACION_MEMORIA;

      // Si el estado es 'Completada', 'Pendiente de correcciones' o 'No procede
      // evaluar' se cambia el estado de la memoria a 'En secretar??a'
      if (memoria.getEstadoActual().getId() == 2L || memoria.getEstadoActual().getId() == 7L
          || memoria.getEstadoActual().getId() == 8L) {
        updateEstadoMemoria(memoria, 3L);
      }

      // Si el estado es 'Favorable pendiente de modificaciones m??nimas'
      // se cambia el estado de la memoria a 'En secretar??a revisi??n m??nima'
      if (memoria.getEstadoActual().getId() == 6L) {
        crearEvaluacion = true;
        updateEstadoMemoria(memoria, 4L);
      }

      // Si el estado es 'Completada seguimiento anual'
      // se cambia el estado de la memoria a 'En secretar??a seguimiento anual'
      if (memoria.getEstadoActual().getId() == 11L) {
        tipoEvaluacion = Constantes.TIPO_EVALUACION_SEGUIMIENTO_ANUAL;
        updateEstadoMemoria(memoria, 12L);
      }

      // Si el estado es 'Completada seguimiento final'
      // se cambia el estado de la memoria a 'En secretar??a seguimiento final'
      if (memoria.getEstadoActual().getId() == 16L) {
        tipoEvaluacion = Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL;
        updateEstadoMemoria(memoria, 17L);
      }

      // Si el estado es 'En aclaraci??n seguimiento final'
      // se cambia el estado de la memoria a 'En secretar??a seguimiento final
      // aclaraciones'
      if (memoria.getEstadoActual().getId() == 21L) {
        tipoEvaluacion = Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL;
        crearEvaluacion = true;
        updateEstadoMemoria(memoria, 18L);
      }

      if (crearEvaluacion) {
        this.crearEvaluacion(memoria, tipoEvaluacion);
        memoria.setVersion(memoria.getVersion() + 1);
      }

      memoria.setFechaEnvioSecretaria(Instant.now());

      memoriaRepository.save(memoria);

      this.crearInforme(memoria, tipoEvaluacion);

      return memoria;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    log.debug("enviarSecretaria(Long id) - end");
  }

  /**
   * Crea la evaluaci??n a partir de los datos de la memoria en caso de que sea
   * necesario
   * 
   * @param memoria        los datos de la {@link Memoria}
   * @param tipoEvaluacion el tipo de {@link Evaluacion}
   */
  private void crearEvaluacion(Memoria memoria, Long tipoEvaluacion) {
    log.debug("crearEvaluacion(memoria, tipoEvaluacion)- start");
    evaluacionRepository.findFirstByMemoriaIdAndActivoTrueOrderByVersionDesc(memoria.getId()).map(evaluacion -> {
      Evaluacion evaluacionNueva = new Evaluacion();
      BeanUtils.copyProperties(evaluacion, evaluacionNueva);
      evaluacionNueva.setId(null);
      evaluacionNueva.setVersion(memoria.getVersion() + 1);
      evaluacionNueva.setEsRevMinima(true);
      evaluacionNueva.setDictamen(null);
      evaluacionNueva.setTipoEvaluacion(new TipoEvaluacion());
      evaluacionNueva.getTipoEvaluacion().setId(tipoEvaluacion);
      evaluacionNueva.setActivo(true);
      evaluacionRepository.save(evaluacionNueva);

      return evaluacionNueva;
    }).orElseThrow(() -> new EvaluacionNotFoundException(memoria.getId()));
    log.debug("crearEvaluacion(memoria, tipoEvaluacion)- end");
  }

  private void crearInforme(Memoria memoria, Long tipoEvaluacion) {
    log.debug("crearInforme(memoria, tipoEvaluacion)- start");

    // Se crea un fichero en formato pdf con los datos del proyecto y con
    // los datos del formulario y subirlo al gestor documental y que el sistema
    // guarde en informes el identificador del documento.
    Informe informe = new Informe();
    Optional<Evaluacion> evaluacionAnterior = evaluacionRepository
        .findFirstByMemoriaIdAndTipoEvaluacionIdAndActivoTrueOrderByVersionDesc(memoria.getId(), tipoEvaluacion);

    if (evaluacionAnterior.isPresent()) {
      if (evaluacionAnterior.get().getEsRevMinima().equals(Boolean.FALSE)) {
        informe.setVersion(evaluacionAnterior.get().getVersion() + 1);
      } else {
        informe.setVersion(evaluacionAnterior.get().getVersion());
      }
    } else {
      informe.setVersion(1);
    }
    informe.setMemoria(memoria);
    informe.setTipoEvaluacion(new TipoEvaluacion());
    informe.getTipoEvaluacion().setId(tipoEvaluacion);

    Long idFormulario = null;
    String tituloInforme = TITULO_INFORME_MXX;
    switch (tipoEvaluacion.intValue()) {
      case Constantes.TIPO_EVALUACION_MEMORIA_INT:
        idFormulario = memoria.getComite().getFormulario().getId();
        break;
      case Constantes.TIPO_EVALUACION_SEGUIMIENTO_ANUAL_INT:
        idFormulario = Constantes.FORMULARIO_ANUAL;
        tituloInforme = TITULO_INFORME_SA;
        break;
      case Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL_INT:
        idFormulario = Constantes.FORMULARIO_FINAL;
        tituloInforme = TITULO_INFORME_SF;
        break;
      case Constantes.TIPO_EVALUACION_RETROSPECTIVA_INT:
        idFormulario = Constantes.FORMULARIO_RETROSPECTIVA;
        tituloInforme = TITULO_INFORME_RETROSPECTIVA;
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
   * 
   * Actualiza el estado de la Retrospectiva de {@link Memoria} a 'En Secretaria'
   * 
   * @param idMemoria  de la memoria.
   * @param personaRef id de la persona
   */
  @Transactional
  @Override
  public void enviarSecretariaRetrospectiva(Long idMemoria, String personaRef) {
    log.debug("enviarSecretariaRetrospectiva(Long id) - start");
    Assert.notNull(idMemoria, "Memoria id no puede ser null para actualizar la memoria");

    memoriaRepository.findById(idMemoria).map(memoria -> {
      // Si el estado es 'Completada', Requiere retrospectiva y el comit?? es CEEA
      Assert.isTrue(
          (memoria.getEstadoActual().getId() >= 9L && memoria.getRequiereRetrospectiva()
              && memoria.getComite().getComite().equals("CEEA")
              && memoria.getRetrospectiva().getEstadoRetrospectiva().getId() == 2L),
          "La memoria no est?? en un estado correcto para pasar al estado 'En secretar??a'");

      Assert.isTrue(memoria.getPeticionEvaluacion().getPersonaRef().equals(personaRef),
          "El usuario no es el propietario de la petici??n evaluaci??n.");

      estadoRetrospectivaRepository.findById(3L).map(estadoRetrospectiva -> {

        memoria.getRetrospectiva().setEstadoRetrospectiva(estadoRetrospectiva);
        memoriaRepository.save(memoria);
        return estadoRetrospectiva;

      }).orElseThrow(() -> new EstadoRetrospectivaNotFoundException(3L));

      this.crearInforme(memoria, Constantes.TIPO_EVALUACION_RETROSPECTIVA);

      return memoria;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    // FALTA: crear un fichero en formato pdf con los datos del proyecto y con los
    // datos del formulario y subirlo al gestor documental y que el sistema guarde
    // en informes el identificador del documento.

    log.debug("enviarSecretariaRetrospectiva(Long id) - end");
  }

  @Override
  public Page<Memoria> findByComiteAndPeticionEvaluacion(Long idComite, Long idPeticionEvaluacion, Pageable paging) {
    log.debug("findByComiteAndPeticionEvaluacion(Long idComite, Long idPeticionEvaluacion, Pageable paging) - start");

    Assert.notNull(idComite,
        "El identificador del comit?? no puede ser null para recuperar sus tipos de memoria asociados.");

    Assert.notNull(idPeticionEvaluacion,
        "El identificador de la petici??n de evaluaci??n no puede ser null para recuperar sus tipos de memoria asociados.");

    return comiteRepository.findByIdAndActivoTrue(idComite).map(comite -> {
      log.debug("findByComiteAndPeticionEvaluacion(Long idComite, Long idPeticionEvaluacion, Pageable paging) - end");
      return memoriaRepository.findByComiteIdAndPeticionEvaluacionIdAndActivoTrueAndComiteActivoTrue(idComite,
          idPeticionEvaluacion, paging);

    }).orElseThrow(() -> new ComiteNotFoundException(idComite));

  }

  /**
   * Recuperar aquellas memorias que requieren evaluaci??n retrospectiva y cuya
   * fecha de evaluaci??n retrospectiva se encuentre entre el d??a actual y el
   * n??mero de d??as guardado como par??metro de configuraci??n
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
      log.info("No existen memorias que requieran generar aviso de evaluaci??n de retrospectiva pendiente");
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
              memoria.getPeticionEvaluacion().getTitulo(), memoria.getCodOrganoCompetente(),
              memoria.getPersonaRef());
        } catch (Exception e) {
          log.debug("enviarComunicadoInformeRetrospectivaCeeaPendiente() - Error al enviar el comunicado", e);

        }
      });
    }
  }

  private void validacionesCreateMemoria(Memoria memoria) {
    log.debug("validacionesCreateMemoria(Memoria memoria) - start");

    Assert.isNull(memoria.getId(), "Memoria id tiene que ser null para crear una nueva memoria");
    Assert.notNull(memoria.getPeticionEvaluacion().getId(),
        "Petici??n evaluaci??n id no puede ser null para crear una nueva memoria");

    peticionEvaluacionRepository.findByIdAndActivoTrue(memoria.getPeticionEvaluacion().getId())
        .orElseThrow(() -> new PeticionEvaluacionNotFoundException(memoria.getPeticionEvaluacion().getId()));

    comiteRepository.findByIdAndActivoTrue(memoria.getComite().getId())
        .orElseThrow(() -> new ComiteNotFoundException(memoria.getComite().getId()));

    log.debug("validacionesCreateMemoria(Memoria memoria) - end");
  }

  /**
   * Recupera la referencia de una memoria seg??n su tipo y comit??.
   * 
   * @param idTipoMemoria Identificador {@link TipoMemoria}
   * @param numReferencia Referencia de la memoria copiada en caso de ser memoria
   *                      modificada.
   * @param comite        {??link {@link Comite}}
   * @return n??mero de referencia.
   */
  private String getReferenciaMemoria(Long idTipoMemoria, String numReferencia, Comite comite) {

    log.debug("getReferenciaMemoria(Long id, String numReferencia) - start");

    // Referencia memoria
    int anioActual = Instant.now().atZone(sgiConfigProperties.getTimeZone().toZoneId()).get(ChronoField.YEAR);
    StringBuffer sbNumReferencia = new StringBuffer();
    sbNumReferencia.append(comite.getFormulario().getNombre()).append("/").append(anioActual).append("/");

    String numMemoria = "001";

    switch (idTipoMemoria.intValue()) {
      case 1: {
        // NUEVA
        // Se recupera la ??ltima memoria para el comit?? seleccionado
        Memoria ultimaMemoriaComite = memoriaRepository
            .findFirstByNumReferenciaContainingAndTipoMemoriaIdIsNotAndComiteIdOrderByNumReferenciaDesc(
                String.valueOf(anioActual), 2L, comite.getId());

        // Se incrementa el n??mero de la memoria para el comit??
        if (ultimaMemoriaComite != null) {
          Long numeroUltimaMemoria = Long.valueOf(ultimaMemoriaComite.getNumReferencia().split("/")[2].split("R")[0]);
          numeroUltimaMemoria++;
          numMemoria = String.format("%03d", numeroUltimaMemoria);
        }

        break;
      }
      case 2: {
        // MODIFICACI??N

        // Se recupera la ??ltima memoria modificada de la memoria de la que se realiza
        // la copia y del comit?? de la memoria.
        Memoria ultimaMemoriaComite = memoriaRepository
            .findFirstByNumReferenciaContainingAndComiteIdOrderByNumReferenciaDesc(numReferencia, comite.getId());

        StringBuffer sbReferencia = new StringBuffer();
        sbReferencia.append(ultimaMemoriaComite.getNumReferencia().split("MR")[0].split("/")[2].split("R")[0])
            .append("MR");
        if (ultimaMemoriaComite != null && ultimaMemoriaComite.getNumReferencia().contains("MR")) {
          Long numeroUltimaMemoria = Long.valueOf(ultimaMemoriaComite.getNumReferencia().split("MR")[1]);
          numeroUltimaMemoria++;
          sbReferencia.append(numeroUltimaMemoria);

        } else {
          sbReferencia.append("1");
        }

        numMemoria = sbReferencia.toString();

        break;
      }
      case 3: {
        // RATIFICACI??N
        // Se recupera la ??ltima memoria para el comit?? seleccionado
        Memoria ultimaMemoriaComite = memoriaRepository
            .findFirstByNumReferenciaContainingAndTipoMemoriaIdIsNotAndComiteIdOrderByNumReferenciaDesc(
                String.valueOf(anioActual), 2L, comite.getId());

        // Se incrementa el n??mero de la memoria para el comit??
        Long numeroUltimaMemoria = 1L;
        if (ultimaMemoriaComite != null) {
          numeroUltimaMemoria = Long.valueOf(ultimaMemoriaComite.getNumReferencia().split("/")[2].split("R")[0]);
          numeroUltimaMemoria++;
        }

        StringBuffer sbReferencia = new StringBuffer();
        sbReferencia.append(String.format("%03d", numeroUltimaMemoria)).append("R");
        numMemoria = sbReferencia.toString();

        break;
      }
    }
    ;

    sbNumReferencia.append(numMemoria);

    log.debug("getReferenciaMemoria(Long id, String numReferencia) - end");
    return sbNumReferencia.toString();
  }

  /**
   * Comprobaci??n de si est??n o no los documentos obligatorios aportados para
   * pasar la memoria al estado en secretar??a
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
      respuestas.stream().map(resp -> resp.getTipoDocumento()).forEach(tipoDocumento -> {
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
   * pasado "mesesArchivadaPendienteCorrecciones" d??as desde la fecha de estado de
   * una memoria cuyo estado es "Pendiente Correcciones"
   * 
   * @return Los ids de memorias que pasan al estado "Archivado"
   */
  public List<Long> archivarNoPresentados() {
    log.debug("archivarNoPresentados() - start");
    Configuracion configuracion = configuracionService.findConfiguracion();
    // Devuelve un listado de {@link Memoria} que han
    // pasado "mesesArchivadaPendienteCorrecciones" d??as desde la fecha de estado de
    // una memoria cuyo estado es "Pendiente Correcciones"
    Specification<Memoria> specsMemoriasByMesesArchivadaPendienteCorrecciones = MemoriaSpecifications.activos()
        .and(MemoriaSpecifications.estadoActualIn(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_PENDIENTE_CORRECCIONES)))
        .and(MemoriaSpecifications
            .byFechaActualMayorFechaEstadoByMesesDiff(configuracion.getMesesArchivadaPendienteCorrecciones()));

    List<Memoria> memorias = memoriaRepository.findAll(specsMemoriasByMesesArchivadaPendienteCorrecciones);

    List<Long> memoriasArchivadas = new ArrayList<Long>();
    if (!CollectionUtils.isEmpty(memorias)) {
      memorias.forEach(memoria -> {
        try {
          this.updateEstadoMemoria(memoria, Constantes.TIPO_ESTADO_MEMORIA_ARCHIVADO);
          memoriasArchivadas.add(memoria.getId());
        } catch (Exception e) {
          log.debug("Error archivarNoPresentados() - ", e);
        }
      });
    }
    log.debug("archivarNoPresentados() - end");
    return memoriasArchivadas;
  }

  /**
   * Se actualiza el estado de la memoria a "Archivado" de {@link Memoria} que han
   * pasado "diasArchivadaInactivo" meses desde la fecha de estado de una memoria
   * cuyo estados son "Favorable Pendiente de Modificaciones M??nimas" o "No
   * procede evaluar" o "Solicitud modificaci??n"
   * 
   * @return Los ids de memorias que pasan al estado "Archivado"
   */
  public List<Long> archivarInactivos() {
    log.debug("archivarInactivos() - start");
    Configuracion configuracion = configuracionService.findConfiguracion();
    // Devuelve un listado de {@link Memoria} que han pasado
    // "diasArchivadaInactivo" meses desde la fecha de estado de una memoria cuyo
    // estados son "Favorable Pendiente de Modificaciones M??nimas" o "No procede
    // evaluar" o "Solicitud modificaci??n"
    Specification<Memoria> specsMemoriasByDiasArchivadaInactivo = MemoriaSpecifications.activos()
        .and(MemoriaSpecifications.estadoActualIn(Arrays.asList(
            Constantes.TIPO_ESTADO_MEMORIA_FAVORABLE_PENDIENTE_MOD_MINIMAS,
            Constantes.TIPO_ESTADO_MEMORIA_NO_PROCEDE_EVALUAR, Constantes.TIPO_ESTADO_MEMORIA_SOLICITUD_MODIFICACION)))
        .and(MemoriaSpecifications.byFechaActualMayorFechaEstadoByDiasDiff(configuracion.getDiasArchivadaInactivo()));

    List<Memoria> memorias = memoriaRepository.findAll(specsMemoriasByDiasArchivadaInactivo);

    List<Long> memoriasArchivadas = new ArrayList<Long>();
    if (!CollectionUtils.isEmpty(memorias)) {
      memorias.forEach(memoria -> {
        try {
          this.updateEstadoMemoria(memoria, Constantes.TIPO_ESTADO_MEMORIA_ARCHIVADO);
          memoriasArchivadas.add(memoria.getId());
        } catch (Exception e) {
          log.debug("Error archivarInactivos() - ", e);
        }
      });
    }
    log.debug("archivarInactivos() - end");
    return memoriasArchivadas;
  }

  private ZonedDateTime getLastInstantOfDay() {
    return Instant.now().atZone(this.sgiConfigProperties.getTimeZone().toZoneId())
        .with(LocalTime.MAX).withNano(0);
  }
}
