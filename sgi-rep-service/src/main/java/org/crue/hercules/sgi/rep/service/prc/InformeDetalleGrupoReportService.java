package org.crue.hercules.sgi.rep.service.prc;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.apache.commons.collections4.ListUtils;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput;
import org.crue.hercules.sgi.rep.dto.prc.ReportInformeDetalleGrupo;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenCosteIndirectoOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenSexenioOutput;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiPrcService;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe detalle grupo de PRC
 */
@Service
@Slf4j
@Validated
public class InformeDetalleGrupoReportService extends SgiReportService {

  private static final String IMPORTE = "importe";
  private static final String PUNTOS = "puntos";
  private static final String NUMERO = "numero";

  private final SgiApiPrcService sgiApiPrcService;

  public InformeDetalleGrupoReportService(SgiConfigProperties sgiConfigProperties, SgiApiConfService sgiApiConfService,
      SgiApiPrcService sgiApiPrcService) {

    super(sgiConfigProperties, sgiApiConfService);
    this.sgiApiPrcService = sgiApiPrcService;
  }

  private DefaultTableModel getTableModelGeneral(DetalleGrupoInvestigacionOutput detalleGrupo) {

    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    columnsData.add("anio");
    elementsRow.add(detalleGrupo.getAnio());

    columnsData.add("grupo");
    elementsRow.add(detalleGrupo.getGrupo());

    columnsData.add("precioPuntoProduccion");
    elementsRow.add(detalleGrupo.getPrecioPuntoProduccion());

    columnsData.add("precioPuntoSexenio");
    elementsRow.add(detalleGrupo.getPrecioPuntoSexenio());

    columnsData.add("precioPuntoCostesIndirectos");
    elementsRow.add(detalleGrupo.getPrecioPuntoCostesIndirectos());

    columnsData.add("resourcesBaseURL");
    elementsRow.add(getRepResourcesBaseURL());

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  private DefaultTableModel getTableModelInvestigadores(DetalleGrupoInvestigacionOutput detalleGrupo) {
    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();

    columnsData.add("nombre");
    columnsData.add("puntosCostesIndirectos");
    columnsData.add("puntosProduccion");

    ListUtils.emptyIfNull(detalleGrupo.getInvestigadores()).stream().forEach(investigador -> {
      Vector<Object> elementsRow = new Vector<>();
      elementsRow.add(investigador.getInvestigador());
      elementsRow.add(investigador.getPuntosCostesIndirectos());
      elementsRow.add(investigador.getPuntosProduccion());

      rowsData.add(elementsRow);
    });

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  private DefaultTableModel getTableModelSexenios(DetalleGrupoInvestigacionOutput detalleGrupo) {
    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();

    columnsData.add(NUMERO);
    columnsData.add(PUNTOS);
    columnsData.add(IMPORTE);

    Vector<Object> elementsRow = new Vector<>();
    if (null == detalleGrupo.getSexenios()) {
      detalleGrupo.setSexenios(ResumenSexenioOutput.builder().build());
    }
    elementsRow.add(detalleGrupo.getSexenios().getNumero());
    elementsRow.add(detalleGrupo.getSexenios().getPuntos());
    elementsRow.add(detalleGrupo.getSexenios().getImporte());

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  private DefaultTableModel getTableModelProduccion(DetalleGrupoInvestigacionOutput detalleGrupo) {
    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();

    columnsData.add("tipo");
    columnsData.add(NUMERO);
    columnsData.add(PUNTOS);
    columnsData.add(IMPORTE);

    ListUtils.emptyIfNull(detalleGrupo.getProduccionesCientificas()).stream().forEach(prc -> {
      Vector<Object> elementsRow = new Vector<>();
      elementsRow.add(prc.getTipo());
      elementsRow.add(prc.getNumero());
      elementsRow.add(prc.getPuntos());
      elementsRow.add(prc.getImporte());

      rowsData.add(elementsRow);
    });

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  private DefaultTableModel getTableModelCostesIndirectos(DetalleGrupoInvestigacionOutput detalleGrupo) {
    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();

    columnsData.add(NUMERO);
    columnsData.add(PUNTOS);
    columnsData.add(IMPORTE);

    Vector<Object> elementsRow = new Vector<>();
    if (null == detalleGrupo.getCostesIndirectos()) {
      detalleGrupo.setCostesIndirectos(ResumenCosteIndirectoOutput.builder().build());
    }
    elementsRow.add(detalleGrupo.getCostesIndirectos().getNumero());
    elementsRow.add(detalleGrupo.getCostesIndirectos().getPuntos());
    elementsRow.add(detalleGrupo.getCostesIndirectos().getImporte());

    rowsData.add(elementsRow);

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  private DefaultTableModel getTableModelDineroTotal(DetalleGrupoInvestigacionOutput detalleGrupo) {
    Vector<Object> columnsData = new Vector<>();
    Vector<Vector<Object>> rowsData = new Vector<>();

    columnsData.add("tipo");
    columnsData.add(IMPORTE);

    ListUtils.emptyIfNull(detalleGrupo.getProduccionesCientificas()).stream().forEach(prc -> {
      Vector<Object> elementsRow = new Vector<>();
      elementsRow.add(prc.getTipo());
      elementsRow.add(prc.getImporte());

      rowsData.add(elementsRow);
    });

    ListUtils.emptyIfNull(detalleGrupo.getTotales()).stream().forEach(total -> {
      Vector<Object> elementsRow = new Vector<>();
      elementsRow.add(total.getTipo());
      elementsRow.add(total.getImporte());

      rowsData.add(elementsRow);
    });

    DefaultTableModel tableModel = new DefaultTableModel();
    tableModel.setDataVector(rowsData, columnsData);
    return tableModel;
  }

  public byte[] getReportDetalleGrupo(ReportInformeDetalleGrupo sgiReport, Integer anio, Long grupoId) {
    try {

      final MasterReport report = getReportDefinition(sgiReport.getPath());

      DetalleGrupoInvestigacionOutput detalleGrupo = sgiApiPrcService.getDataReportDetalleGrupo(anio, grupoId);

      String queryGeneral = QUERY_TYPE + SEPARATOR_KEY + NAME_GENERAL_TABLE_MODEL + SEPARATOR_KEY
          + "informeDetalleGrupo";
      DefaultTableModel tableModelGeneral = getTableModelGeneral(detalleGrupo);

      TableDataFactory dataFactory = new TableDataFactory();
      dataFactory.addTable(queryGeneral, tableModelGeneral);
      report.setDataFactory(dataFactory);

      String queryInvestigadores = QUERY_TYPE + SEPARATOR_KEY + "investigadores";
      DefaultTableModel tableModelInvestigadores = getTableModelInvestigadores(detalleGrupo);
      TableDataFactory dataFactorySubReportInvestigadores = new TableDataFactory();
      dataFactorySubReportInvestigadores.addTable(queryInvestigadores, tableModelInvestigadores);
      Band bandInvestigadores = (Band) report.getItemBand().getElement(1);
      SubReport subreportInvestigadores = (SubReport) bandInvestigadores.getElement(0);
      subreportInvestigadores.setDataFactory(dataFactorySubReportInvestigadores);

      String querySexenios = QUERY_TYPE + SEPARATOR_KEY + "sexenios";
      DefaultTableModel tableModelSexenios = getTableModelSexenios(detalleGrupo);
      TableDataFactory dataFactorySubReportSexenios = new TableDataFactory();
      dataFactorySubReportSexenios.addTable(querySexenios, tableModelSexenios);
      Band bandSexenios = (Band) report.getItemBand().getElement(3);
      SubReport subreportSexenios = (SubReport) bandSexenios.getElement(0);
      subreportSexenios.setDataFactory(dataFactorySubReportSexenios);

      String queryProduccion = QUERY_TYPE + SEPARATOR_KEY + "produccion";
      DefaultTableModel tableModelProduccion = getTableModelProduccion(detalleGrupo);
      TableDataFactory dataFactorySubReportProduccion = new TableDataFactory();
      dataFactorySubReportProduccion.addTable(queryProduccion, tableModelProduccion);
      Band bandProduccion = (Band) report.getItemBand().getElement(4);
      SubReport subreportProduccion = (SubReport) bandProduccion.getElement(0);
      subreportProduccion.setDataFactory(dataFactorySubReportProduccion);

      String queryCostesIndirectos = QUERY_TYPE + SEPARATOR_KEY + "costesIndirectos";
      DefaultTableModel tableModelCostesIndirectos = getTableModelCostesIndirectos(detalleGrupo);
      TableDataFactory dataFactorySubReportCostesIndirectos = new TableDataFactory();
      dataFactorySubReportCostesIndirectos.addTable(queryCostesIndirectos, tableModelCostesIndirectos);
      Band bandCostesIndirectos = (Band) report.getItemBand().getElement(5);
      SubReport subreportCostesIndirectos = (SubReport) bandCostesIndirectos.getElement(0);
      subreportCostesIndirectos.setDataFactory(dataFactorySubReportCostesIndirectos);

      String queryDineroTotal = QUERY_TYPE + SEPARATOR_KEY + "dineroTotal";
      DefaultTableModel tableModelDineroTotal = getTableModelDineroTotal(detalleGrupo);
      TableDataFactory dataFactorySubReportDineroTotal = new TableDataFactory();
      dataFactorySubReportDineroTotal.addTable(queryDineroTotal, tableModelDineroTotal);
      Band bandDineroTotal = (Band) report.getItemBand().getElement(6);
      SubReport subreportDineroTotal = (SubReport) bandDineroTotal.getElement(0);
      subreportDineroTotal.setDataFactory(dataFactorySubReportDineroTotal);

      sgiReport.setContent(generateReportOutput(sgiReport.getOutputType(), report));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return sgiReport.getContent();
  }
}