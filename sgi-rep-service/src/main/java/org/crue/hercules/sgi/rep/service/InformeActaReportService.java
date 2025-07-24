package org.crue.hercules.sgi.rep.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.framework.i18n.I18nHelper;
import org.crue.hercules.sgi.rep.dto.eti.ActaComentariosMemoriaReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ActaDto;
import org.crue.hercules.sgi.rep.dto.eti.AsistentesDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaEvaluadaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.enums.Dictamen;
import org.crue.hercules.sgi.rep.enums.TipoEvaluacion;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.report.data.BloqueApartadoActaReportData;
import org.crue.hercules.sgi.rep.report.data.ActaReportData;
import org.crue.hercules.sgi.rep.report.data.objects.AsistenteObject;
import org.crue.hercules.sgi.rep.report.data.objects.MemoriaEvaluadaObject;
import org.crue.hercules.sgi.rep.service.eti.ActaService;
import org.crue.hercules.sgi.rep.service.eti.BaseApartadosRespuestasReportService;
import org.crue.hercules.sgi.rep.service.eti.ConvocatoriaReunionService;
import org.crue.hercules.sgi.rep.service.eti.EvaluacionService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.crue.hercules.sgi.rep.util.SgiReportContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.deepoove.poi.data.DocxRenderData;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe acta de ética
 */
@Service
@Slf4j
@Validated
public class InformeActaReportService extends SgiDocxReportService {

  private final PersonaService personaService;
  private final ConvocatoriaReunionService convocatoriaReunionService;
  private final ActaService actaService;
  private final EvaluacionService evaluacionService;
  private final BaseApartadosRespuestasReportService baseApartadosRespuestasReportService;

  public InformeActaReportService(PersonaService personaService,
      SgiApiConfService sgiApiConfService,
      ConvocatoriaReunionService convocatoriaReunionService, ActaService actaService,
      EvaluacionService evaluacionService,
      BaseApartadosRespuestasReportService baseApartadosRespuestasReportService) {

    super(sgiApiConfService);
    this.personaService = personaService;
    this.convocatoriaReunionService = convocatoriaReunionService;
    this.actaService = actaService;
    this.evaluacionService = evaluacionService;
    this.baseApartadosRespuestasReportService = baseApartadosRespuestasReportService;
  }

  public byte[] getReport(Long idActa) {
    try {
      ActaReportData reportData = new ActaReportData();
      ActaDto acta = actaService.findById(idActa);

      reportData.setHeaderLogo(getHeaderLogo());
      reportData.setActa(acta);
      reportData.setNumEvaluacionesNuevas(actaService.countEvaluacionesNuevas(acta.getId()));
      reportData.setNumEvaluacionesRevisioSinMinima(actaService.countEvaluacionesRevisionSinMinima(acta.getId()));
      reportData.setExistComentarios(getExistsComentarios(acta.getId()));
      reportData.setAsistentes(getAsistentes(acta.getConvocatoriaReunion().getId()));
      reportData.setMemoriasEvaluadas(getMemoriasEvaluadas(acta.getId()));
      reportData.setBloqueApartados(buildBloqueApartadosSubreport(getActaComentariosSubReport(acta.getId())));

      return buildPDF(reportData);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException(e);
    }
  }

  private Boolean getExistsComentarios(Long actaId) {
    List<MemoriaEvaluadaDto> memorias = actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(actaId);

    Optional<MemoriaEvaluadaDto> memoriasEvaluadasNoFavorables = memorias
        .stream().filter(memoria -> !memoria.getDictamenId().equals(Dictamen.FAVORABLE.getId())
            && (memoria.getTipoEvaluacionId().equals(TipoEvaluacion.MEMORIA.getId())
                || memoria.getTipoEvaluacionId().equals(
                    TipoEvaluacion.SEGUIMIENTO_ANUAL.getId())
                || memoria.getTipoEvaluacionId().equals(
                    TipoEvaluacion.SEGUIMIENTO_FINAL.getId())))
        .findAny();

    return memoriasEvaluadasNoFavorables.isPresent();
  }

  private List<AsistenteObject> getAsistentes(Long convocatoriaId) {
    List<AsistentesDto> asistentes = convocatoriaReunionService
        .findAsistentesByConvocatoriaReunionId(convocatoriaId);

    List<AsistenteObject> asistentesReportData = new ArrayList<>();
    asistentes.forEach(asistente -> {
      PersonaDto persona = null;
      try {
        persona = personaService.findById(asistente.getEvaluador().getPersonaRef());
      } catch (Exception e) {
        log.error(e.getMessage());
      }
      AsistenteObject asistenteObject = new AsistenteObject(asistente, persona);
      asistentesReportData.add(asistenteObject);
    });

    return asistentesReportData;
  }

  private List<MemoriaEvaluadaObject> getMemoriasEvaluadas(Long actaId) {
    List<MemoriaEvaluadaDto> memorias = actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(actaId);

    List<MemoriaEvaluadaObject> memoriasEvaluadas = new ArrayList<>();
    memorias.forEach(memoria -> {
      PersonaDto responsable = null;
      try {
        responsable = personaService.findById(memoria.getPersonaRef());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
      MemoriaEvaluadaObject memoriaEvaluada = new MemoriaEvaluadaObject(memoria, responsable);
      memoriasEvaluadas.add(memoriaEvaluada);
    });
    return memoriasEvaluadas;
  }

  /**
   * Devuelve el contenido para generar el subreport de comentarios del acta
   *
   * @param actaId Id del acta
   * @return ActaComentariosReportOutput Datos a presentar en el informe
   */
  private List<ActaComentariosMemoriaReportOutput> getActaComentariosSubReport(Long actaId) {
    log.debug("getActaComentariosSubReport(actaId) - start");

    List<ActaComentariosMemoriaReportOutput> comentariosMemoria = new ArrayList<>();

    List<MemoriaEvaluadaDto> memorias = actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(actaId);

    memorias.stream().filter(memoria -> !memoria.getDictamenId().equals(Dictamen.FAVORABLE.getId())
        && (memoria.getTipoEvaluacionId().equals(
            TipoEvaluacion.MEMORIA.getId())
            || memoria.getTipoEvaluacionId().equals(
                TipoEvaluacion.SEGUIMIENTO_ANUAL.getId())
            || memoria.getTipoEvaluacionId().equals(
                TipoEvaluacion.SEGUIMIENTO_FINAL.getId())))
        .forEach(memoria -> {
          ActaComentariosMemoriaReportOutput comentariosMemoriaReportOutput = new ActaComentariosMemoriaReportOutput();
          comentariosMemoriaReportOutput.setNumReferenciaMemoria(memoria.getNumReferencia());
          comentariosMemoriaReportOutput
              .setTituloProyecto(I18nHelper.getFieldValue(memoria.getTitulo(), SgiReportContextHolder.getLanguage()));
          comentariosMemoriaReportOutput
              .setDictamen(Dictamen.fromCode(memoria.getDictamenId()).toString());
          comentariosMemoriaReportOutput.setBloques(new ArrayList<>());

          try {
            PersonaDto persona = personaService.findById(memoria.getPersonaRef());
            comentariosMemoriaReportOutput.setResponsable(persona.getNombre() + " " +
                persona.getApellidos());
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }

          List<ComentarioDto> comentarios = evaluacionService.findByEvaluacionIdGestor(memoria.getEvaluacionId());

          if (null != comentarios && !comentarios.isEmpty()) {
            comentariosMemoriaReportOutput.setNumComentarios(comentarios.size());

            final Set<Long> apartados = new HashSet<>();
            comentarios
                .forEach(
                    c -> baseApartadosRespuestasReportService.getApartadoService().findTreeApartadosById(apartados,
                        c.getApartado()));

            Long idFormulario = 0L;

            Optional<FormularioDto> formulario = comentarios.stream()
                .map(c -> c.getApartado().getBloque().getFormulario())
                .filter(f -> f != null)
                .findFirst();

            if (formulario.isPresent()) {
              idFormulario = formulario.get().getId();
            }

        // @formatter:off
            BloquesReportInput etiBloquesReportInput = BloquesReportInput.builder()
              .idMemoria(memoria.getId())
              .idFormulario(idFormulario)
              .mostrarRespuestas(false)
              .mostrarContenidoApartado(false)
              .comentarios(comentarios)
              .apartados(apartados)
              .build();
            // @formatter:on

            BloquesReportOutput reportOutput = baseApartadosRespuestasReportService
                .getDataFromApartadosAndRespuestas(etiBloquesReportInput);

            final int orden = comentariosMemoriaReportOutput.getBloques().size();
            for (BloqueOutput bloque : reportOutput.getBloques()) {
              bloque.setOrden(bloque.getOrden() + orden);
            }

            comentariosMemoriaReportOutput.getBloques().addAll(reportOutput.getBloques());
          }
          comentariosMemoria.add(comentariosMemoriaReportOutput);
        });

    log.debug("getActaComentariosSubReport(actaId) - end");

    return comentariosMemoria;
  }

  private DocxRenderData buildBloqueApartadosSubreport(List<ActaComentariosMemoriaReportOutput> comentariosMemoria) {
    BloqueApartadoActaReportData reportData = new BloqueApartadoActaReportData();
    if (!comentariosMemoria.isEmpty()
        && ObjectUtils
            .isNotEmpty(comentariosMemoria.stream().findAny().get().getBloques())
        && !comentariosMemoria.stream().findAny().get().getBloques().isEmpty()) {
      reportData.setComentarios(comentariosMemoria);
    } else {
      return null;
    }
    return buildSubReport(reportData);
  }

}