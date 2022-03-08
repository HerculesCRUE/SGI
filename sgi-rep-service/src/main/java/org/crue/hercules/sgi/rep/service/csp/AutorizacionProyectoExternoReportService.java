package org.crue.hercules.sgi.rep.service.csp;

import java.time.Instant;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.csp.AutorizacionDto;
import org.crue.hercules.sgi.rep.dto.csp.AutorizacionReport;
import org.crue.hercules.sgi.rep.dto.csp.ConvocatoriaDto;
import org.crue.hercules.sgi.rep.dto.sgemp.EmpresaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgempService;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe autorizacionproyectoexterno
 */
@Service
@Slf4j
@Validated
public class AutorizacionProyectoExternoReportService extends SgiReportService {

  private final SgiApiSgpService personaService;
  private final AutorizacionProyectoExternoService autorizacionProyectoExternoService;
  private final ConvocatoriaService convocatoriaService;
  private final SgiApiSgempService empresaService;

  public AutorizacionProyectoExternoReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiSgpService personaService,
      AutorizacionProyectoExternoService autorizacionProyectoExternoService, ConvocatoriaService convocatoriaService,
      SgiApiSgempService empresaService) {

    super(sgiConfigProperties);
    this.personaService = personaService;
    this.autorizacionProyectoExternoService = autorizacionProyectoExternoService;
    this.convocatoriaService = convocatoriaService;
    this.empresaService = empresaService;
  }

  private DefaultTableModel getTableModelGeneral(AutorizacionDto autorizacionProyectoExterno) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    columnsData.add("nombre");
    columnsData.add("nif");
    try {
      PersonaDto persona = personaService.findById(autorizacionProyectoExterno.getSolicitanteRef());
      elementsRow.add(persona.getNombre() + " " + persona.getApellidos());
      elementsRow.add(persona.getNumeroDocumento());
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
      elementsRow.add(getErrorMessageToReport(e));
    }

    columnsData.add("catProfesional");
    columnsData.add("departamento");
    columnsData.add("centro");
    try {
      VinculacionDto vinculacionPersona = personaService
          .findVinculacionByPersonaId(autorizacionProyectoExterno.getSolicitanteRef());
      elementsRow.add(vinculacionPersona.getCategoriaProfesional() != null
          ? vinculacionPersona.getCategoriaProfesional().getNombre()
          : '-');
      elementsRow
          .add(vinculacionPersona.getDepartamento() != null ? vinculacionPersona.getDepartamento().getNombre() : "-");
      elementsRow
          .add(vinculacionPersona.getCentro() != null ? vinculacionPersona.getCentro().getNombre() : "-");
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
      elementsRow.add(getErrorMessageToReport(e));
      elementsRow.add(getErrorMessageToReport(e));
    }

    columnsData.add("datosConvocatoria");
    if (autorizacionProyectoExterno.getConvocatoriaId() != null) {
      try {
        ConvocatoriaDto convocatoria = convocatoriaService.findById(autorizacionProyectoExterno.getConvocatoriaId());
        elementsRow.add(convocatoria.getTitulo());
      } catch (Exception e) {
        elementsRow.add(getErrorMessageToReport(e));
      }
    } else {
      elementsRow.add(autorizacionProyectoExterno.getDatosConvocatoria());
    }

    columnsData.add("horasDedicacion");
    elementsRow.add(autorizacionProyectoExterno.getHorasDedicacion() != null
        ? autorizacionProyectoExterno.getHorasDedicacion() + " horas"
        : "");

    columnsData.add("tituloProyecto");
    elementsRow.add(autorizacionProyectoExterno.getTituloProyecto());

    columnsData.add("universidad");
    if (autorizacionProyectoExterno.getEntidadRef() != null) {
      try {
        EmpresaDto empresa = empresaService.findById(autorizacionProyectoExterno.getEntidadRef());
        elementsRow.add(empresa.getNombre());
      } catch (Exception e) {
        elementsRow.add(getErrorMessageToReport(e));
      }
    } else {
      elementsRow.add(autorizacionProyectoExterno.getDatosEntidad());
    }

    columnsData.add("investigador");
    if (autorizacionProyectoExterno.getResponsableRef() != null) {
      try {
        PersonaDto persona = personaService.findById(autorizacionProyectoExterno.getResponsableRef());
        elementsRow.add(persona.getNombre() + " " + persona.getApellidos());
      } catch (Exception e) {
        elementsRow.add(getErrorMessageToReport(e));
      }
    } else {
      elementsRow.add(autorizacionProyectoExterno.getDatosResponsable());
    }

    columnsData.add("fechaActual");
    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    elementsRow.add(formatInstantToString(Instant.now(), pattern));

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public byte[] getReportAutorizacionProyectoExterno(AutorizacionReport autorizacionReport, Long idAutorizacion) {
    try {

      final MasterReport report = getReportDefinition(autorizacionReport.getPath());

      AutorizacionDto autorizacionProyectoExterno = autorizacionProyectoExternoService
          .findById(idAutorizacion);

      String queryGeneral = QUERY_TYPE + SEPARATOR_KEY + NAME_GENERAL_TABLE_MODEL + SEPARATOR_KEY
          + "informeAutorizacion";
      DefaultTableModel tableModelGeneral = getTableModelGeneral(autorizacionProyectoExterno);

      TableDataFactory dataFactory = new TableDataFactory();
      dataFactory.addTable(queryGeneral, tableModelGeneral);
      report.setDataFactory(dataFactory);

      autorizacionReport.setContent(generateReportOutput(autorizacionReport.getOutputType(), report));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return autorizacionReport.getContent();
  }

}