package org.crue.hercules.sgi.rep.service.eti;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.dto.eti.ActaComentariosMemoriaReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ActaComentariosReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ActaDto;
import org.crue.hercules.sgi.rep.dto.eti.AsistenteReportDataDto;
import org.crue.hercules.sgi.rep.dto.eti.AsistentesDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaEvaluadaDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeActa;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportDocxService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import com.deepoove.poi.data.Includes;
import com.deepoove.poi.data.RenderData;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe acta de ética
 */
@Service
@Slf4j
@Validated
public class InformeActaReportService extends SgiReportDocxService {

  private static final String DICTAMEN_FAVORABLE = "Favorable";
  private static final String TIPO_EVALUACION_MEMORIA = "Memoria";
  private static final String TIPO_EVALUACION_SEG_ANUAL = "Seguimiento anual";
  private static final String TIPO_EVALUACION_SEG_FINAL = "Seguimiento final";
  private static final Long TIPO_COMENTARIO_GESTOR = 1L;

  private final PersonaService personaService;
  private final ConvocatoriaReunionService convocatoriaReunionService;
  private final ActaService actaService;
  private final EvaluacionService evaluacionService;
  private final BaseApartadosRespuestasReportService baseApartadosRespuestasReportService;

  public InformeActaReportService(SgiConfigProperties sgiConfigProperties, PersonaService personaService,
      SgiApiConfService sgiApiConfService,
      ConvocatoriaReunionService convocatoriaReunionService, ActaService actaService,
      EvaluacionService evaluacionService,
      BaseApartadosRespuestasReportService baseApartadosRespuestasReportService) {

    super(sgiConfigProperties, sgiApiConfService);
    this.personaService = personaService;
    this.convocatoriaReunionService = convocatoriaReunionService;
    this.actaService = actaService;
    this.evaluacionService = evaluacionService;
    this.baseApartadosRespuestasReportService = baseApartadosRespuestasReportService;
  }

  private XWPFDocument getDocument(ActaDto acta, HashMap<String, Object> dataReport, InputStream path) {
    Assert.notNull(
        acta,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.rep.dto.eti.ActaDto.message"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(ActaDto.class))
            .build());

    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    Instant fecha = acta.getConvocatoriaReunion().getFechaEvaluacion();
    dataReport.put("fecha", formatInstantToString(fecha, pattern));

    dataReport.put("numeroActa", acta.getNumero());

    dataReport.put("comite", acta.getConvocatoriaReunion().getComite().getComite());

    dataReport.put("nombreInvestigacion", acta.getConvocatoriaReunion().getComite().getNombreInvestigacion());

    String patternFechaConv = String.format("dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    Instant fechaEvaluacion = acta.getConvocatoriaReunion().getFechaEvaluacion();
    dataReport.put("fechaConvocatoria", formatInstantToString(fechaEvaluacion, patternFechaConv));

    dataReport.put("lugar", acta.getConvocatoriaReunion().getLugar());

    dataReport.put("isVideoconferencia", acta.getConvocatoriaReunion().getVideoconferencia());

    LocalDateTime fechaInicio = LocalDateTime.now().withHour(acta.getHoraInicio()).withMinute(acta.getMinutoInicio());
    LocalDateTime fechaFin = LocalDateTime.now().withHour(acta.getHoraFin()).withMinute(acta.getMinutoFin());

    dataReport.put("horaInicio", fechaInicio.format(DateTimeFormatter.ofPattern("HH:mm")));

    dataReport.put("horaFin", fechaFin.format(DateTimeFormatter.ofPattern("HH:mm")));

    Duration duracionEntreFechas = Duration.between(fechaInicio, fechaFin);
    StringBuilder strDuracion = new StringBuilder();
    long durationHoras = duracionEntreFechas.toHours();
    long durationMinutos = duracionEntreFechas.toMinutes() % 60;
    String i18nY = ApplicationContextSupport.getMessage("common.y");
    String i18nHoras = ApplicationContextSupport.getMessage("common.horas");
    String i18nMinutos = ApplicationContextSupport.getMessage("common.minutos");
    if (durationHoras > 0) {
      strDuracion.append(durationHoras);
      strDuracion.append(" ");
      strDuracion.append(i18nHoras);
      strDuracion.append(" ");
      strDuracion.append(i18nY);
      strDuracion.append(" ");

    }
    strDuracion.append(durationMinutos);
    strDuracion.append(" ");
    strDuracion.append(i18nMinutos);
    dataReport.put("duracion", strDuracion.toString());

    dataReport.put("tipoConvocatoria", acta.getConvocatoriaReunion().getTipoConvocatoriaReunion().getNombre());

    dataReport.put("resumenActa", acta.getResumen());

    String codigoActa = acta.getNumero() + "/" + formatInstantToString(fechaEvaluacion, "YYYY") + "/" + acta
        .getConvocatoriaReunion().getComite().getComite();
    dataReport.put("codigoActa", codigoActa);

    Long numeroEvaluacionesNuevas = actaService.countEvaluacionesNuevas(acta.getId());
    dataReport.put("numeroEvaluacionesNuevas", null != numeroEvaluacionesNuevas ? numeroEvaluacionesNuevas : 0);

    Long numeroEvaluacionesRevisiones = actaService.countEvaluacionesRevisionSinMinima(acta.getId());
    dataReport.put("numeroEvaluacionesRevisiones",
        null != numeroEvaluacionesRevisiones ? numeroEvaluacionesRevisiones : 0);

    dataReport.put("ordenDelDia", acta.getConvocatoriaReunion().getOrdenDia());

    List<MemoriaEvaluadaDto> memorias = actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(acta.getId());

    Optional<MemoriaEvaluadaDto> memoriasEvaluadasNoFavorables = memorias
        .stream().filter(memoria -> !memoria.getDictamen().equals(DICTAMEN_FAVORABLE)
            && (memoria.getTipoEvaluacion().equals(TIPO_EVALUACION_MEMORIA)
                || memoria.getTipoEvaluacion().equals(TIPO_EVALUACION_SEG_ANUAL)
                || memoria.getTipoEvaluacion().equals(TIPO_EVALUACION_SEG_FINAL)))
        .findAny();

    dataReport.put("existsComentarios", memoriasEvaluadasNoFavorables.isPresent());

    addDataAsistentes(acta, dataReport);

    getTableMemoriasEvaluadas(acta, dataReport);

    dataReport.put("bloqueApartados",
        generarBloqueApartados(getActaComentariosSubReport(acta.getId())));

    return compileReportData(path, dataReport);
  }

  private void addDataAsistentes(ActaDto acta, HashMap<String, Object> dataReport) {

    List<AsistentesDto> asistentes = convocatoriaReunionService
        .findAsistentesByConvocatoriaReunionId(acta.getConvocatoriaReunion().getId());

    List<AsistenteReportDataDto> asistentesReportData = new ArrayList<>();
    asistentes.forEach(asistente -> {
      AsistenteReportDataDto asistenteReportData = new AsistenteReportDataDto();
      try {
        PersonaDto persona = personaService.findById(asistente.getEvaluador().getPersonaRef());
        asistenteReportData.setPersona(persona);
      } catch (Exception e) {
        log.error(e.getMessage());
      }
      asistenteReportData.setMotivo(asistente.getMotivo());
      asistentesReportData.add(asistenteReportData);
    });

    dataReport.put("asistentes", asistentesReportData);

  }

  public byte[] getReportInformeActa(ReportInformeActa sgiReport, Long idActa) {
    getReportFromIdActa(sgiReport, idActa);
    return sgiReport.getContent();
  }

  private XWPFDocument getReportFromIdActa(SgiReportDto sgiReport, Long idActa) {
    try {
      HashMap<String, Object> dataReport = new HashMap<>();
      ActaDto acta = actaService.findById(idActa);

      dataReport.put("headerImg", getImageHeaderLogo());

      XWPFDocument document = getDocument(acta, dataReport, getReportDefinitionStream(sgiReport.getPath()));

      ByteArrayOutputStream outputPdf = new ByteArrayOutputStream();
      PdfOptions pdfOptions = PdfOptions.create();

      PdfConverter.getInstance().convert(document, outputPdf, pdfOptions);

      sgiReport.setContent(outputPdf.toByteArray());
      return document;

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
  }

  private void getTableMemoriasEvaluadas(ActaDto acta, HashMap<String, Object> dataReport) {

    List<MemoriaEvaluadaDto> memorias = actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(acta.getId());

    dataReport.put("isMemoriasEvaluadas", !memorias.isEmpty());
    dataReport.put("memoriasEvaluadas", memorias);
    memorias.forEach(memoria -> {
      try {
        PersonaDto persona = personaService.findById(memoria.getPersonaRef());
        memoria.setResponsable(persona);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    });

  }

  /**
   * Devuelve el contenido para generar el subreport de comentarios del acta
   *
   * @param actaId Id del acta
   * @return ActaComentariosReportOutput Datos a presentar en el informe
   */
  protected ActaComentariosReportOutput getActaComentariosSubReport(Long actaId) {
    log.debug("getActaComentariosSubReport(actaId) - start");

    Assert.notNull(
        actaId,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(ActaDto.class))
            .build());

    ActaComentariosReportOutput actaComentariosSubReportOutput = new ActaComentariosReportOutput();
    actaComentariosSubReportOutput.setComentariosMemoria(new ArrayList<>());

    try {

      List<MemoriaEvaluadaDto> memorias = actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(actaId);

      memorias.stream().filter(memoria -> !memoria.getDictamen().equals(DICTAMEN_FAVORABLE)
          && (memoria.getTipoEvaluacion().equals(TIPO_EVALUACION_MEMORIA)
              || memoria.getTipoEvaluacion().equals(TIPO_EVALUACION_SEG_ANUAL)
              || memoria.getTipoEvaluacion().equals(TIPO_EVALUACION_SEG_FINAL)))
          .forEach(memoria -> {
            ActaComentariosMemoriaReportOutput comentariosMemoriaReportOutput = new ActaComentariosMemoriaReportOutput();
            comentariosMemoriaReportOutput.setNumReferenciaMemoria(memoria.getNumReferencia());
            comentariosMemoriaReportOutput.setTituloProyecto(memoria.getTitulo());
            comentariosMemoriaReportOutput.setDictamen(memoria.getDictamen());
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
            actaComentariosSubReportOutput.getComentariosMemoria().add(comentariosMemoriaReportOutput);
          });

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    log.debug("getActaComentariosSubReport(actaId) - end");

    return actaComentariosSubReportOutput;
  }

  protected RenderData generarBloqueApartados(ActaComentariosReportOutput actaComentariosReportOutput) {
    Map<String, Object> subDataBloqueApartado = new HashMap<>();
    if (ObjectUtils.isNotEmpty(actaComentariosReportOutput)
        && ObjectUtils.isNotEmpty(actaComentariosReportOutput.getComentariosMemoria())
        && ObjectUtils
            .isNotEmpty(actaComentariosReportOutput.getComentariosMemoria().stream().findAny().get().getBloques())
        && actaComentariosReportOutput.getComentariosMemoria().stream().findAny().get().getBloques().size() > 0) {
      List<BloqueOutput> bloquesOutput = new ArrayList<BloqueOutput>();
      subDataBloqueApartado.put("comentariosMemoria",
          actaComentariosReportOutput.getComentariosMemoria());
    } else {
      subDataBloqueApartado.put("comentariosMemoria", null);
      return null;
    }
    return Includes.ofStream(getReportDefinitionStream("rep-eti-bloque-apartado-acta-docx"))
        .setRenderModel(subDataBloqueApartado).create();
  }

}