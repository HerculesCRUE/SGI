package org.crue.hercules.sgi.eti.service.impl;

import java.util.Objects;

import org.crue.hercules.sgi.eti.exceptions.FormularioNotFoundException;
import org.crue.hercules.sgi.eti.exceptions.MemoriaNotFoundException;
import org.crue.hercules.sgi.eti.model.Formulario;
import org.crue.hercules.sgi.eti.model.FormularioReport;
import org.crue.hercules.sgi.eti.model.FormularioReportPK;
import org.crue.hercules.sgi.eti.model.Memoria;
import org.crue.hercules.sgi.eti.model.TipoEstadoMemoria;
import org.crue.hercules.sgi.eti.repository.FormularioReportRepository;
import org.crue.hercules.sgi.eti.repository.FormularioRepository;
import org.crue.hercules.sgi.eti.service.FormularioService;
import org.crue.hercules.sgi.eti.service.MemoriaService;
import org.crue.hercules.sgi.eti.service.RetrospectivaService;
import org.crue.hercules.sgi.eti.util.Constantes;
import org.crue.hercules.sgi.framework.i18n.Language;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLJPASupport;
import org.crue.hercules.sgi.framework.util.AssertHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation para la gestión de {@link Formulario}.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class FormularioServiceImpl implements FormularioService {

  private final FormularioRepository formularioRepository;
  private final MemoriaService memoriaService;
  private final RetrospectivaService retrospectivaService;
  private final FormularioReportRepository formularioReportRepository;

  public FormularioServiceImpl(
      FormularioRepository formularioRepository,
      MemoriaService memoriaService,
      RetrospectivaService retrospectivaService,
      FormularioReportRepository formularioReportRepository) {
    this.formularioRepository = formularioRepository;
    this.memoriaService = memoriaService;
    this.retrospectivaService = retrospectivaService;
    this.formularioReportRepository = formularioReportRepository;
  }

  /**
   * Obtiene todas las entidades {@link Formulario} paginadas y filtadas.
   *
   * @param paging la información de paginación.
   * @param query  información del filtro.
   * @return el listado de entidades {@link Formulario} paginadas y filtradas.
   */
  public Page<Formulario> findAll(String query, Pageable paging) {
    log.debug("findAllFormulario(String query,Pageable paging) - start");
    Specification<Formulario> specs = SgiRSQLJPASupport.toSpecification(query);

    Page<Formulario> returnValue = formularioRepository.findAll(specs, paging);
    log.debug("findAllFormulario(String query,Pageable paging) - end");
    return returnValue;
  }

  /**
   * Obtiene una entidad {@link Formulario} por id.
   *
   * @param id el id de la entidad {@link Formulario}.
   * @return la entidad {@link Formulario}.
   * @throws FormularioNotFoundException Si no existe ningún {@link Formulario}e
   *                                     con ese id.
   */
  public Formulario findById(final Long id) throws FormularioNotFoundException {
    log.debug("Petición a get Formulario : {}  - start", id);
    final Formulario formulario = formularioRepository.findById(id)
        .orElseThrow(() -> new FormularioNotFoundException(id));
    log.debug("Petición a get Formulario : {}  - end", id);
    return formulario;

  }

  /**
   * Obtiene el report asociado al {@link Formulario} en el idioma especificado
   * 
   * @param id       el id de la entidad {@link Formulario}.
   * @param language Idioma
   * @return Report asociado
   */
  public byte[] getReport(Long id, Language language) throws FormularioNotFoundException {
    FormularioReport report = formularioReportRepository.findById(new FormularioReportPK(id, language))
        .orElseThrow(() -> new FormularioNotFoundException(id));
    return report.getValue();
  }

  /**
   * Comprueba si existe un report asociado al {@link Formulario} en el idioma
   * especificado
   * 
   * @param id       el id de la entidad {@link Formulario}.
   * @param language Idioma
   * @return <code>true</code> si existe un report asociado
   */
  public boolean existReport(Long id, Language language) {
    return formularioReportRepository.existsById(new FormularioReportPK(id, language));
  }

  /**
   * Actualiza el estado de la memoria o de la retrospectiva al estado final
   * correspondiente al tipo de formulario completado.
   *
   * @param memoriaId      Identificador de la {@link Memoria}.
   * @param formularioTipo Tipo de formulario {@link Formulario.Tipo}
   * @throws MemoriaNotFoundException Si no existe ningún {@link Memoria} con ese
   *                                  id.
   */
  @Transactional
  public void completado(Long memoriaId, Formulario.Tipo formularioTipo) throws MemoriaNotFoundException {
    Memoria memoria = memoriaService.findById(memoriaId);

    switch (formularioTipo) {
      case SEGUIMIENTO_ANUAL:
        if (memoria.getEstadoActual().getId() < TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_ANUAL.getId()) {
          memoriaService.updateEstadoMemoria(memoria, TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_ANUAL.getId());
        }
        break;
      case SEGUIMIENTO_FINAL:
        if (memoria.getEstadoActual().getId() < TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_FINAL.getId()) {
          memoriaService.updateEstadoMemoria(memoria, TipoEstadoMemoria.Tipo.COMPLETADA_SEGUIMIENTO_FINAL.getId());
        }
        break;
      case RETROSPECTIVA:
        if (memoria.getRetrospectiva() != null
            && memoria.getRetrospectiva().getEstadoRetrospectiva()
                .getId() < Constantes.ESTADO_RETROSPECTIVA_COMPLETADA) {
          retrospectivaService.updateEstadoRetrospectiva(memoria.getRetrospectiva(),
              Constantes.ESTADO_RETROSPECTIVA_COMPLETADA);
        }
        break;
      default:
        if (memoria.getEstadoActual().getId() < TipoEstadoMemoria.Tipo.COMPLETADA.getId()
            || Objects.equals(memoria.getEstadoActual().getId(), TipoEstadoMemoria.Tipo.SUBSANACION.getId())) {
          memoriaService.updateEstadoMemoria(memoria, Constantes.TIPO_ESTADO_MEMORIA_COMPLETADA);
        }
        break;
    }
  }

  /**
   * Actualiza o crea el report asociado al {@link Formulario} en el idioma
   * especificado
   * 
   * @param id          el id de la entidad {@link Formulario}.
   * @param language    Idioma
   * @param reportValue report
   * @return Formulario del report asociado
   */
  @Transactional
  public Formulario updateReport(Long id, Language language, byte[] reportValue) {
    log.debug("updateReport(Long id, Language language, byte[] reportValue) - start");

    AssertHelper.idNotNull(id, Formulario.class);

    FormularioReport report = null;
    if (this.existReport(id, language)) {
      // Actualiza el value del report
      report = formularioReportRepository.findById(new FormularioReportPK(id, language))
          .orElseThrow(() -> new FormularioNotFoundException(id));
      report.setValue(reportValue);
    } else {
      // Crea una nueva entidad report con el value indicado
      report = new FormularioReport(id, language, reportValue);
    }

    FormularioReport returnValue = formularioReportRepository.saveAndFlush(report);

    log.debug("updateReport(Long id, Language language, byte[] reportValue) - end");
    return this.findById(returnValue.getFormularioId());
  }

}
