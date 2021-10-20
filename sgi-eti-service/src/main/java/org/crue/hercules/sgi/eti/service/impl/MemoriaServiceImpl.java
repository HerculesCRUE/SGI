package org.crue.hercules.sgi.eti.service.impl;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.crue.hercules.sgi.eti.config.RestApiProperties;
import org.crue.hercules.sgi.eti.config.SgiConfigProperties;
import org.crue.hercules.sgi.eti.dto.DocumentoOutput;
import org.crue.hercules.sgi.eti.dto.MemoriaPeticionEvaluacion;
import org.crue.hercules.sgi.eti.exceptions.ComiteNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EstadoRetrospectivaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.EvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.PeticionEvaluacionNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.rep.GetDataReportMxxException;
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
import org.crue.hercules.sgi.eti.service.ConfiguracionService;
import org.crue.hercules.sgi.eti.service.InformeService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.http.HttpEntityBuilder;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Memoria}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class MemoriaServiceImpl implements MemoriaService {

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

  /** Estado Retrospectiva repository */
  private final EstadoRetrospectivaRepository estadoRetrospectivaRepository;

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

  private final RestApiProperties restApiProperties;
  private final RestTemplate restTemplate;

  /** Tarea repository */
  private final TareaRepository tareaRepository;

  /** Configuracion service */
  private final ConfiguracionService configuracionService;

  public MemoriaServiceImpl(SgiConfigProperties sgiConfigProperties, MemoriaRepository memoriaRepository,
      EstadoMemoriaRepository estadoMemoriaRepository, EstadoRetrospectivaRepository estadoRetrospectivaRepository,
      EvaluacionRepository evaluacionRepository, ComentarioRepository comentarioRepository,
      InformeService informeService, PeticionEvaluacionRepository peticionEvaluacionRepository,
      ComiteRepository comiteRepository, DocumentacionMemoriaRepository documentacionMemoriaRepository,
      RespuestaRepository respuestaRepository, TareaRepository tareaRepository,
      ConfiguracionService configuracionService, RestApiProperties restApiProperties, RestTemplate restTemplate) {
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
    this.restApiProperties = restApiProperties;
    this.restTemplate = restTemplate;
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
        .map(documentacionMemoria -> {
          return new DocumentacionMemoria(null, memoriaCreada, documentacionMemoria.getTipoDocumento(),
              documentacionMemoria.getDocumentoRef(), documentacionMemoria.getNombre());
        }).collect(Collectors.toList());

    documentacionMemoriaRepository.saveAll(documentacionesMemoriaList);

    Page<Respuesta> respuestasPage = respuestaRepository.findByMemoriaIdAndMemoriaActivoTrue(memoria.getId(), null);

    List<Respuesta> respuestaList = respuestasPage.getContent().stream().map(respuesta -> {
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
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   */
  @Override
  public Memoria findById(final Long id) throws MemoriaNotFoundException {
    log.debug("Petición a get Memoria : {}  - start", id);
    final Memoria Memoria = memoriaRepository.findById(id).orElseThrow(() -> new MemoriaNotFoundException(id));
    log.debug("Petición a get Memoria : {}  - end", id);
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
   * Devuelve las memorias de una petición evaluación con su fecha límite y de
   * evaluación.
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
          // Si la memoria se cambió al estado anterior estando en evaluación, se
          // eliminará la evaluación.
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
      // eliminamos el estado a cambiar en el histórico
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
   * Actualiza el estado de la {@link Memoria} a 'En Secretaria' o 'En Secretaría
   * Revisión Mínima'
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
          "La memoria no está en un estado correcto para pasar al estado 'En secretaría'");

      Assert.isTrue(memoria.getPeticionEvaluacion().getPersonaRef().equals(personaRef),
          "El usuario no es el propietario de la petición evaluación.");

      boolean crearEvaluacion = false;

      Long tipoEvaluacion = Constantes.TIPO_EVALUACION_MEMORIA;

      // Si el estado es 'Completada', 'Pendiente de correcciones' o 'No procede
      // evaluar' se cambia el estado de la memoria a 'En secretaría'
      if (memoria.getEstadoActual().getId() == 2L || memoria.getEstadoActual().getId() == 7L
          || memoria.getEstadoActual().getId() == 8L) {
        updateEstadoMemoria(memoria, 3L);
      }

      // Si el estado es 'Favorable pendiente de modificaciones mínimas'
      // se cambia el estado de la memoria a 'En secretaría revisión mínima'
      if (memoria.getEstadoActual().getId() == 6L) {
        crearEvaluacion = true;
        updateEstadoMemoria(memoria, 4L);
      }

      // Si el estado es 'Completada seguimiento anual'
      // se cambia el estado de la memoria a 'En secretaría seguimiento anual'
      if (memoria.getEstadoActual().getId() == 11L) {
        tipoEvaluacion = Constantes.TIPO_EVALUACION_SEGUIMIENTO_ANUAL;
        updateEstadoMemoria(memoria, 12L);
      }

      // Si el estado es 'Completada seguimiento final'
      // se cambia el estado de la memoria a 'En secretaría seguimiento final'
      if (memoria.getEstadoActual().getId() == 16L) {
        tipoEvaluacion = Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL;
        updateEstadoMemoria(memoria, 17L);
      }

      // Si el estado es 'En aclaración seguimiento final'
      // se cambia el estado de la memoria a 'En secretaría seguimiento final
      // aclaraciones'
      if (memoria.getEstadoActual().getId() == 21L) {
        tipoEvaluacion = Constantes.TIPO_EVALUACION_SEGUIMIENTO_FINAL;
        crearEvaluacion = true;
        updateEstadoMemoria(memoria, 18L);
      }

      if (crearEvaluacion) {
        evaluacionRepository.findFirstByMemoriaIdAndActivoTrueOrderByVersionDesc(memoria.getId()).map(evaluacion -> {
          Evaluacion evaluacionNueva = new Evaluacion();
          BeanUtils.copyProperties(evaluacion, evaluacionNueva);
          evaluacionNueva.setId(null);
          evaluacionNueva.setVersion(memoria.getVersion() + 1);
          evaluacionNueva.setEsRevMinima(true);
          evaluacionNueva.setDictamen(null);
          evaluacionRepository.save(evaluacionNueva);

          return evaluacionNueva;
        }).orElseThrow(() -> new EvaluacionNotFoundException(idMemoria));

        memoria.setVersion(memoria.getVersion() + 1);
      }

      memoria.setFechaEnvioSecretaria(Instant.now());

      memoriaRepository.save(memoria);

      this.crearInforme(memoria, tipoEvaluacion);

      return memoria;
    }).orElseThrow(() -> new MemoriaNotFoundException(idMemoria));

    log.debug("enviarSecretaria(Long id) - end");
  }

  private void crearInforme(Memoria memoria, Long tipoEvaluacion) {
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

    // Se obtiene el informe en formato pdf creado mediante el servicio de reporting
    Resource informePdf = getMXX(memoria.getId());

    ResponseEntity<DocumentoOutput> documento = null;
    try {
      DataInputStream dis = new DataInputStream(informePdf.getInputStream());
      byte[] bytesData = new byte[(int) informePdf.contentLength()];
      dis.readFully(bytesData);

      MultipartFile multipartFile = new MockMultipartFile("file", "informePdf" + LocalDate.now(), "application/pdf",
          bytesData);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      Optional<HttpServletRequest> req = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
          .filter(ServletRequestAttributes.class::isInstance).map(ServletRequestAttributes.class::cast)
          .map(ServletRequestAttributes::getRequest);
      HttpServletRequest httpServletRequest = req.get();
      String authorization = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
      headers.set(HttpHeaders.AUTHORIZATION, authorization);

      MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
      bodyMap.add("archivo", new FileSystemResource(convert(multipartFile)));

      HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);

      // Llamada SGDOC para crear el documento y obtener el documentoRef
      documento = restTemplate.exchange(restApiProperties.getSgdocUrl() + "/api/sgdoc/documentos", HttpMethod.POST,
          requestEntity, DocumentoOutput.class);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    // Se crea el informe con el documentoRef obtenido del sgdoc
    if (documento != null && documento.hasBody()) {
      informe.setDocumentoRef(documento.getBody().getDocumentoRef());
      informeService.create(informe);
    }
  }

  public static File convert(MultipartFile file) {
    File convFile = new File("informePdf" + LocalDate.now(), file.getOriginalFilename());
    if (!convFile.getParentFile().exists()) {
      System.out.println("mkdir:" + convFile.getParentFile().mkdirs());
    }
    try {
      convFile.createNewFile();
      FileOutputStream fos = new FileOutputStream(convFile);
      fos.write(file.getBytes());
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return convFile;
  }

  /**
   * Devuelve un informe pdf del formulario M10, M20 o M30
   *
   * @param idMemoria Id de la memoria
   * @return EtiMXXReportOutput Datos a presentar en el informe
   */
  private Resource getMXX(Long idMemoria) {
    log.debug("getMXX(idMemoria)- start");
    Assert.notNull(idMemoria, "idMemoria no puede ser nulo");

    Resource informe = null;
    try {

      final ResponseEntity<Resource> response = restTemplate.exchange(
          restApiProperties.getRepUrl() + "/reports/mxx/" + idMemoria, HttpMethod.GET,
          new HttpEntityBuilder<>().withCurrentUserAuthorization().build(), Resource.class);

      informe = (Resource) response.getBody();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    if (null == informe) {
      throw new GetDataReportMxxException();
    }

    log.debug("getMXX(idMemoria) - end");
    return informe;
  }

  /**
   * 
   * Actualiza el estado de la Retrospectiva de {@link Memoria} a 'En Secretaria'
   * 
   * @param idMemoria de la memoria.
   */
  @Transactional
  @Override
  public void enviarSecretariaRetrospectiva(Long idMemoria, String personaRef) {
    log.debug("enviarSecretariaRetrospectiva(Long id) - start");
    Assert.notNull(idMemoria, "Memoria id no puede ser null para actualizar la memoria");

    memoriaRepository.findById(idMemoria).map(memoria -> {
      // Si el estado es 'Completada', Requiere retrospectiva y el comité es CEEA
      Assert.isTrue(
          (memoria.getEstadoActual().getId() >= 9L && memoria.getRequiereRetrospectiva()
              && memoria.getComite().getComite().equals("CEEA")
              && memoria.getRetrospectiva().getEstadoRetrospectiva().getId() == 2L),
          "La memoria no está en un estado correcto para pasar al estado 'En secretaría'");

      Assert.isTrue(memoria.getPeticionEvaluacion().getPersonaRef().equals(personaRef),
          "El usuario no es el propietario de la petición evaluación.");

      estadoRetrospectivaRepository.findById(3L).map(estadoRetrospectiva -> {

        memoria.getRetrospectiva().setEstadoRetrospectiva(estadoRetrospectiva);
        // TODO quitar despues de pruebas
        // memoriaRepository.save(memoria);
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
        "El identificador del comité no puede ser null para recuperar sus tipos de memoria asociados.");

    Assert.notNull(idPeticionEvaluacion,
        "El identificador de la petición de evaluación no puede ser null para recuperar sus tipos de memoria asociados.");

    return comiteRepository.findByIdAndActivoTrue(idComite).map(comite -> {
      log.debug("findByComiteAndPeticionEvaluacion(Long idComite, Long idPeticionEvaluacion, Pageable paging) - end");
      return memoriaRepository.findByComiteIdAndPeticionEvaluacionIdAndActivoTrueAndComiteActivoTrue(idComite,
          idPeticionEvaluacion, paging);

    }).orElseThrow(() -> new ComiteNotFoundException(idComite));

  }

  private void validacionesCreateMemoria(Memoria memoria) {
    log.debug("validacionesCreateMemoria(Memoria memoria) - start");

    Assert.isNull(memoria.getId(), "Memoria id tiene que ser null para crear una nueva memoria");
    Assert.notNull(memoria.getPeticionEvaluacion().getId(),
        "Petición evaluación id no puede ser null para crear una nueva memoria");

    peticionEvaluacionRepository.findByIdAndActivoTrue(memoria.getPeticionEvaluacion().getId())
        .orElseThrow(() -> new PeticionEvaluacionNotFoundException(memoria.getPeticionEvaluacion().getId()));

    comiteRepository.findByIdAndActivoTrue(memoria.getComite().getId())
        .orElseThrow(() -> new ComiteNotFoundException(memoria.getComite().getId()));

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
    StringBuffer sbNumReferencia = new StringBuffer();
    sbNumReferencia.append(comite.getFormulario().getNombre()).append("/").append(anioActual).append("/");

    String numMemoria = "001";

    switch (idTipoMemoria.intValue()) {
      case 1: {
        // NUEVA
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
      case 2: {
        // MODIFICACIÓN

        // Se recupera la última memoria modificada de la memoria de la que se realiza
        // la copia y del comité de la memoria.
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
        // RATIFICACIÓN
        // Se recupera la última memoria para el comité seleccionado
        Memoria ultimaMemoriaComite = memoriaRepository
            .findFirstByNumReferenciaContainingAndTipoMemoriaIdIsNotAndComiteIdOrderByNumReferenciaDesc(
                String.valueOf(anioActual), 2L, comite.getId());

        // Se incrementa el número de la memoria para el comité
        if (ultimaMemoriaComite != null) {
          Long numeroUltimaMemoria = Long.valueOf(ultimaMemoriaComite.getNumReferencia().split("/")[2].split("R")[0]);
          numeroUltimaMemoria++;

          StringBuffer sbReferencia = new StringBuffer();
          sbReferencia.append(String.format("%03d", numeroUltimaMemoria)).append("R");
          numMemoria = sbReferencia.toString();
        }

        break;
      }
    }
    ;

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
   * pasado "diasArchivadaPendienteCorrecciones" días desde la fecha de estado de
   * una memoria cuyo estado es "Pendiente Correcciones"
   * 
   * @return Los ids de memorias que pasan al estado "Archivado"
   */
  public List<Long> archivarNoPresentados() {
    log.debug("archivarNoPresentados() - start");
    Configuracion configuracion = configuracionService.findConfiguracion();
    // Devuelve un listado de {@link Memoria} que han
    // pasado "diasArchivadaPendienteCorrecciones" días desde la fecha de estado de
    // una memoria cuyo estado es "Pendiente Correcciones"
    Specification<Memoria> specsMemoriasByDiasArchivadaPendienteCorrecciones = MemoriaSpecifications.activos()
        .and(MemoriaSpecifications.estadoActualIn(Arrays.asList(Constantes.TIPO_ESTADO_MEMORIA_PENDIENTE_CORRECCIONES)))
        .and(MemoriaSpecifications
            .byFechaActualMayorFechaEstadoByDiasDiff(configuracion.getDiasArchivadaPendienteCorrecciones()));

    List<Memoria> memorias = memoriaRepository.findAll(specsMemoriasByDiasArchivadaPendienteCorrecciones);

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
   * pasado "mesesArchivadaInactivo" meses desde la fecha de estado de una memoria
   * cuyo estados son "Favorable Pendiente de Modificaciones Mínimas" o "No
   * procede evaluar" o "Solicitud modificación"
   * 
   * @return Los ids de memorias que pasan al estado "Archivado"
   */
  public List<Long> archivarInactivos() {
    log.debug("archivarInactivos() - start");
    Configuracion configuracion = configuracionService.findConfiguracion();
    // Devuelve un listado de {@link Memoria} que han pasado
    // "mesesArchivadaInactivo" meses desde la fecha de estado de una memoria cuyo
    // estados son "Favorable Pendiente de Modificaciones Mínimas" o "No procede
    // evaluar" o "Solicitud modificación"
    Specification<Memoria> specsMemoriasByMesesArchivadaInactivo = MemoriaSpecifications.activos()
        .and(MemoriaSpecifications.estadoActualIn(Arrays.asList(
            Constantes.TIPO_ESTADO_MEMORIA_FAVORABLE_PENDIENTE_MOD_MINIMAS,
            Constantes.TIPO_ESTADO_MEMORIA_NO_PROCEDE_EVALUAR, Constantes.TIPO_ESTADO_MEMORIA_SOLICITUD_MODIFICACION)))
        .and(MemoriaSpecifications.byFechaActualMayorFechaEstadoByMesesDiff(configuracion.getMesesArchivadaInactivo()));

    List<Memoria> memorias = memoriaRepository.findAll(specsMemoriasByMesesArchivadaInactivo);

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
}
