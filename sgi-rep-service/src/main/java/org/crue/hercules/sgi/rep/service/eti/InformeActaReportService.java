package org.crue.hercules.sgi.rep.service.eti;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ActaComentariosMemoriaReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ActaComentariosReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ActaDto;
import org.crue.hercules.sgi.rep.dto.eti.AsistentesDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaEvaluadaDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeActa;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe acta de ética
 */
@Service
@Slf4j
@Validated
public class InformeActaReportService extends SgiReportService {

  private static final String DICTAMEN_FAVORABLE = "Favorable";
  private static final String TIPO_EVALUACION_MEMORIA = "Memoria";
  private static final String TIPO_EVALUACION_SEG_ANUAL = "Seguimiento anual";
  private static final String TIPO_EVALUACION_SEG_FINAL = "Seguimiento final";

  private final SgiApiSgpService personaService;
  private final ConvocatoriaReunionService convocatoriaReunionService;
  private final ActaService actaService;
  private final EvaluacionService evaluacionService;
  private final BaseActaComentariosReportService baseActaComentariosReportService;

  public InformeActaReportService(SgiConfigProperties sgiConfigProperties, SgiApiSgpService personaService,
      SgiApiConfService sgiApiConfService,
      ConvocatoriaReunionService convocatoriaReunionService, ActaService actaService,
      EvaluacionService evaluacionService,
      BaseActaComentariosReportService baseActaComentariosReportService) {

    super(sgiConfigProperties, sgiApiConfService);
    this.personaService = personaService;
    this.convocatoriaReunionService = convocatoriaReunionService;
    this.actaService = actaService;
    this.evaluacionService = evaluacionService;
    this.baseActaComentariosReportService = baseActaComentariosReportService;
  }

  private DefaultTableModel getTableModelGeneral(ActaDto acta) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    columnsData.add("fecha");
    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    Instant fecha = acta.getConvocatoriaReunion().getFechaEvaluacion();
    elementsRow.add(formatInstantToString(fecha, pattern));

    columnsData.add("numeroActa");
    Integer numeroActa = acta.getNumero();
    elementsRow.add(numeroActa);

    columnsData.add("comite");
    String comite = acta.getConvocatoriaReunion().getComite().getComite();
    elementsRow.add(comite);

    columnsData.add("nombreInvestigacion");
    elementsRow.add(acta.getConvocatoriaReunion().getComite().getNombreInvestigacion());

    columnsData.add("preposicionComite");
    if (acta.getConvocatoriaReunion().getComite().getGenero().equals(Genero.M)) {
      elementsRow.add(ApplicationContextSupport.getMessage("common.del"));
    } else {
      elementsRow.add(ApplicationContextSupport.getMessage("common.dela"));
    }

    columnsData.add("fechaConvocatoria");
    String patternFechaConv = String.format("dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    Instant fechaEvaluacion = acta.getConvocatoriaReunion().getFechaEvaluacion();
    elementsRow.add(formatInstantToString(fechaEvaluacion, patternFechaConv));

    columnsData.add("lugar");
    elementsRow.add(acta.getConvocatoriaReunion().getLugar());

    LocalDateTime fechaInicio = LocalDateTime.now().withHour(acta.getHoraInicio()).withMinute(acta.getMinutoInicio());
    LocalDateTime fechaFin = LocalDateTime.now().withHour(acta.getHoraFin()).withMinute(acta.getMinutoFin());

    columnsData.add("horaInicio");
    elementsRow.add(fechaInicio.format(DateTimeFormatter.ofPattern("HH:mm")));

    columnsData.add("horaFin");
    elementsRow.add(fechaFin.format(DateTimeFormatter.ofPattern("HH:mm")));

    columnsData.add("duracion");
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
    elementsRow.add(strDuracion.toString());

    columnsData.add("tipoConvocatoria");
    elementsRow.add(acta.getConvocatoriaReunion().getTipoConvocatoriaReunion().getNombre());

    columnsData.add("resumenActa");
    elementsRow.add(acta.getResumen());

    columnsData.add("codigoActa");
    String codigoActa = numeroActa + "/" + formatInstantToString(fechaEvaluacion, "YYYY") + "/" + comite;
    elementsRow.add(codigoActa);

    columnsData.add("numeroEvaluacionesNuevas");
    Long numeroEvaluacionesNuevas = actaService.countEvaluacionesNuevas(acta.getId());
    elementsRow.add(null != numeroEvaluacionesNuevas ? numeroEvaluacionesNuevas : 0);

    columnsData.add("numeroEvaluacionesRevisiones");
    Long numeroEvaluacionesRevisiones = actaService.countEvaluacionesRevisionSinMinima(acta.getId());
    elementsRow.add(null != numeroEvaluacionesRevisiones ? numeroEvaluacionesRevisiones : 0);

    columnsData.add("ordenDelDia");
    elementsRow.add(acta.getConvocatoriaReunion().getOrdenDia());

    List<MemoriaEvaluadaDto> memorias = actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(acta.getId());

    Optional<MemoriaEvaluadaDto> memoriaEvaluada = memorias
        .stream().filter(memoria -> !memoria.getDictamen().equals(DICTAMEN_FAVORABLE)
            && (memoria.getTipoEvaluacion().equals(TIPO_EVALUACION_MEMORIA)
                || memoria.getTipoEvaluacion().equals(TIPO_EVALUACION_SEG_ANUAL)
                || memoria.getTipoEvaluacion().equals(TIPO_EVALUACION_SEG_FINAL)))
        .findFirst();

    if (memoriaEvaluada.isPresent()) {
      columnsData.add("referenciaMemoria");
      elementsRow.add(memoriaEvaluada.get().getNumReferencia());

      columnsData.add("tituloProyecto");
      elementsRow.add(memoriaEvaluada.get().getTitulo());

      columnsData.add("responsableMemoria");
      elementsRow.add(memoriaEvaluada.get().getPersonaRef());

      columnsData.add("dictamen");
      elementsRow.add(memoriaEvaluada.get().getDictamen());
    }

    columnsData.add("resourcesBaseURL");
    elementsRow.add(getRepResourcesBaseURL());

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  private DefaultTableModel getTableModelAsistentes(ActaDto acta) {
    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();

    List<AsistentesDto> asistentes = convocatoriaReunionService
        .findAsistentesByConvocatoriaReunionId(acta.getConvocatoriaReunion().getId());

    columnsData.add("nombreInvestigador");
    columnsData.add("motivoNoAsistencia");

    asistentes.forEach(asistente -> {
      Vector<Object> elementsRow = new Vector<>();
      try {
        PersonaDto persona = personaService.findById(asistente.getEvaluador().getPersonaRef());
        elementsRow.add(persona.getNombre() + " " + persona.getApellidos());
      } catch (Exception e) {
        elementsRow.add(getErrorMessageToReport(e));
      }
      elementsRow.add(asistente.getMotivo());

      rowsData.add(elementsRow);
    });

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public byte[] getReportInformeActa(ReportInformeActa sgiReport, Long idActa) {
    try {

      final MasterReport report = getReportDefinition(sgiReport.getPath());

      ActaDto acta = actaService.findById(idActa);

      String queryGeneral = QUERY_TYPE + SEPARATOR_KEY + NAME_GENERAL_TABLE_MODEL + SEPARATOR_KEY + "informeActa";
      DefaultTableModel tableModelGeneral = getTableModelGeneral(acta);

      TableDataFactory dataFactory = new TableDataFactory();
      dataFactory.addTable(queryGeneral, tableModelGeneral);
      report.setDataFactory(dataFactory);

      String queryAsistentes = QUERY_TYPE + SEPARATOR_KEY + "asistentes";
      DefaultTableModel tableModelAsistentes = getTableModelAsistentes(acta);
      TableDataFactory dataFactorySubReportAsistentes = new TableDataFactory();
      dataFactorySubReportAsistentes.addTable(queryAsistentes, tableModelAsistentes);
      Band bandAsistentes = (Band) report.getItemBand().getElement(1);
      SubReport subreportAsistentes = (SubReport) bandAsistentes.getElement(0);
      subreportAsistentes.setDataFactory(dataFactorySubReportAsistentes);

      String queryMemoriasEvaluadas = QUERY_TYPE + SEPARATOR_KEY + "memorias_evaluadas";
      DefaultTableModel tableModelMemoriasEvaluadas = getTableModelMemoriasEvaluadas(acta);
      TableDataFactory dataFactorySubReportMemoriasEvaluadas = new TableDataFactory();
      dataFactorySubReportMemoriasEvaluadas.addTable(queryMemoriasEvaluadas, tableModelMemoriasEvaluadas);
      Band bandMemoriasEvaluadas = (Band) report.getItemBand().getElement(5);
      SubReport subreportMemoriasEvaluadas = (SubReport) bandMemoriasEvaluadas.getElement(0);
      subreportMemoriasEvaluadas.setDataFactory(dataFactorySubReportMemoriasEvaluadas);

      ActaComentariosReportOutput actaComentariosReportOutput = this.getActaComentariosSubReport(idActa);
      Map<String, TableModel> hmTableModel = baseActaComentariosReportService
          .generateTableModelFromReportOutput(actaComentariosReportOutput);
      if (hmTableModel != null && !hmTableModel.isEmpty()) {
        Map.Entry<String, TableModel> entry = hmTableModel.entrySet().iterator().next();
        String queryComentarios = entry.getKey();
        TableModel tableModelComentarios = entry.getValue();
        TableDataFactory dataFactorySubReportComentarios = new TableDataFactory();
        dataFactorySubReportComentarios.addTable(queryComentarios, tableModelComentarios);
        Band bandComentarios = (Band) report.getItemBand().getElement(6);
        SubReport subReportComentarios = (SubReport) bandComentarios.getElement(0);
        subReportComentarios.setDataFactory(dataFactorySubReportComentarios);
        if (actaComentariosReportOutput.getComentariosMemoria().isEmpty()) {
          report.getItemBand().removeElement(bandComentarios);
        }
      } else {
        Band bandComentarios = (Band) report.getItemBand().getElement(6);
        report.getItemBand().removeElement(bandComentarios);
      }

      sgiReport.setContent(generateReportOutput(sgiReport.getOutputType(), report));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return sgiReport.getContent();
  }

  private DefaultTableModel getTableModelMemoriasEvaluadas(ActaDto acta) {
    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();

    List<MemoriaEvaluadaDto> memorias = actaService.findAllMemoriasEvaluadasSinRevMinimaByActaId(acta.getId());

    columnsData.add("numeroReferencia");
    columnsData.add("responsable");
    columnsData.add("dictamen");
    columnsData.add("version");
    columnsData.add("tipoInforme");

    memorias.forEach(memoria -> {
      Vector<Object> elementsRow = new Vector<>();

      elementsRow.add(memoria.getNumReferencia());

      try {
        PersonaDto persona = personaService.findById(memoria.getPersonaRef());
        elementsRow.add(persona.getNombre() + " " + persona.getApellidos());
      } catch (Exception e) {
        elementsRow.add(getErrorMessageToReport(e));
      }

      elementsRow.add(memoria.getDictamen());
      elementsRow.add(memoria.getVersion());
      elementsRow.add(memoria.getTipoEvaluacion());

      rowsData.add(elementsRow);
    });

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
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
            .parameter("entity", ApplicationContextSupport.getMessage(ActaDto.class)).build());

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
              comentariosMemoriaReportOutput.setResponsable(persona.getNombre() + " " + persona.getApellidos());
            } catch (Exception e) {
              comentariosMemoriaReportOutput.setResponsable(getErrorMessageToReport(e));
            }

            List<ComentarioDto> comentarios = evaluacionService.findByEvaluacionIdGestor(memoria.getEvaluacionId());

            if (null != comentarios && !comentarios.isEmpty()) {
              comentariosMemoriaReportOutput.setNumComentarios(comentarios.size());

              final Set<Long> apartados = new HashSet<>();
              comentarios
                  .forEach(c -> baseActaComentariosReportService.getApartadoService().findTreeApartadosById(apartados,
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
              .idMemoria(memoria.getEvaluacionId())
              .idFormulario(idFormulario)
              .mostrarRespuestas(false)
              .mostrarContenidoApartado(false)
              .comentarios(comentarios)
              .apartados(apartados)
              .build();
              // @formatter:on

              BloquesReportOutput reportOutput = baseActaComentariosReportService
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

}