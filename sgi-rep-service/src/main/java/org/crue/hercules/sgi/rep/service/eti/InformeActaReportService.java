package org.crue.hercules.sgi.rep.service.eti;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ActaDto;
import org.crue.hercules.sgi.rep.dto.eti.AsistentesDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaEvaluadaDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeActa;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe acta de ética
 */
@Service
@Slf4j
@Validated
public class InformeActaReportService extends SgiReportService {

  private final PersonaService personaService;
  private final ConvocatoriaReunionService convocatoriaReunionService;
  private final ActaService actaService;

  public InformeActaReportService(SgiConfigProperties sgiConfigProperties, PersonaService personaService,
      ConvocatoriaReunionService convocatoriaReunionService, ActaService actaService) {

    super(sgiConfigProperties);
    this.personaService = personaService;
    this.convocatoriaReunionService = convocatoriaReunionService;
    this.actaService = actaService;
  }

  private DefaultTableModel getTableModelGeneral(ActaDto acta) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    columnsData.add("numeroActa");
    Integer numeroActa = acta.getNumero();
    elementsRow.add(numeroActa);

    columnsData.add("comite");
    String comite = acta.getConvocatoriaReunion().getComite().getComite();
    elementsRow.add(comite);

    columnsData.add("fechaConvocatoria");
    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    Instant fechaEvaluacion = acta.getConvocatoriaReunion().getFechaEvaluacion();
    elementsRow.add(formatInstantToString(fechaEvaluacion, pattern));

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

  public void getReportInformeActa(ReportInformeActa sgiReport, Long idActa) {
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
      Band bandMemoriasEvaluadas = (Band) report.getItemBand().getElement(3);
      SubReport subreportMemoriasEvaluadas = (SubReport) bandMemoriasEvaluadas.getElement(0);
      subreportMemoriasEvaluadas.setDataFactory(dataFactorySubReportMemoriasEvaluadas);

      sgiReport.setContent(generateReportOutput(sgiReport.getOutputReportType(), report));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }
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

}