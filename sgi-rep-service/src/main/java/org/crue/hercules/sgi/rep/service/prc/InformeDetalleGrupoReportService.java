package org.crue.hercules.sgi.rep.service.prc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenCosteIndirectoOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenSexenioOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenTotalOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleProduccionInvestigadorOutput;
import org.crue.hercules.sgi.rep.dto.prc.ReportInformeDetalleGrupo;
import org.crue.hercules.sgi.rep.dto.prc.ReportInformeDetalleProduccionInvestigador;
import org.crue.hercules.sgi.rep.dto.prc.ReportInformeResumenPuntuacionGrupos;
import org.crue.hercules.sgi.rep.dto.prc.ResumenPuntuacionGrupoAnioOutput;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportDocxService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiPrcService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe detalle grupo de PRC
 */
@Service
@Slf4j
@Validated
public class InformeDetalleGrupoReportService extends SgiReportDocxService {

  private final SgiApiPrcService sgiApiPrcService;

  public InformeDetalleGrupoReportService(SgiConfigProperties sgiConfigProperties, SgiApiConfService sgiApiConfService,
      SgiApiPrcService sgiApiPrcService) {

    super(sgiConfigProperties, sgiApiConfService);
    this.sgiApiPrcService = sgiApiPrcService;

  }

  private XWPFDocument getDocument(DetalleGrupoInvestigacionOutput detalleGrupo, HashMap<String, Object> dataReport,
      InputStream path) {

    Assert.notNull(
        detalleGrupo,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport
                    .getMessage("org.crue.hercules.sgi.rep.dto.eti.DetalleGrupoInvestigacionOutput.message"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(DetalleGrupoInvestigacionOutput.class))
            .build());

    dataReport.put("anio", detalleGrupo.getAnio());

    dataReport.put("grupo", detalleGrupo.getGrupo());

    dataReport.put("precioPuntoProduccion", detalleGrupo.getPrecioPuntoProduccion());

    dataReport.put("precioPuntoSexenio", detalleGrupo.getPrecioPuntoSexenio());

    dataReport.put("precioPuntoCostesIndirectos", detalleGrupo.getPrecioPuntoCostesIndirectos());

    addTableDatosInvestigadores(detalleGrupo, dataReport);

    addTableSexenios(detalleGrupo, dataReport);

    addTableProduccion(detalleGrupo, dataReport);

    addTableCostesIndirectos(detalleGrupo, dataReport);

    addTableDineroTotal(detalleGrupo, dataReport);

    return compileReportData(path, dataReport);
  }

  private void addTableDatosInvestigadores(DetalleGrupoInvestigacionOutput detalleGrupo,
      HashMap<String, Object> dataReport) {
    if (CollectionUtils.isNotEmpty(detalleGrupo.getInvestigadores())) {
      dataReport.put("investigadores", detalleGrupo.getInvestigadores());
    } else {
      dataReport.put("investigadores", null);
    }
  }

  private void addTableSexenios(DetalleGrupoInvestigacionOutput detalleGrupo, HashMap<String, Object> dataReport) {
    if (ObjectUtils.isEmpty(detalleGrupo.getSexenios())) {
      detalleGrupo.setSexenios(ResumenSexenioOutput.builder().build());
    }
    dataReport.put("sexeniosNumero", detalleGrupo.getSexenios().getNumero());
    dataReport.put("sexeniosPuntos", detalleGrupo.getSexenios().getPuntos());
    dataReport.put("sexeniosImporte", detalleGrupo.getSexenios().getImporte());
  }

  private void addTableProduccion(DetalleGrupoInvestigacionOutput detalleGrupo, HashMap<String, Object> dataReport) {

    if (CollectionUtils.isNotEmpty(detalleGrupo.getProduccionesCientificas())) {
      dataReport.put("produccionesCientificas", detalleGrupo.getProduccionesCientificas());
    } else {
      dataReport.put("produccionesCientificas", null);
    }
  }

  private void addTableCostesIndirectos(DetalleGrupoInvestigacionOutput detalleGrupo,
      HashMap<String, Object> dataReport) {

    if (ObjectUtils.isEmpty(detalleGrupo.getCostesIndirectos())) {
      detalleGrupo.setCostesIndirectos(ResumenCosteIndirectoOutput.builder().build());
    }
    dataReport.put("costesIndirectosNumero", detalleGrupo.getCostesIndirectos().getNumero());
    dataReport.put("costesIndirectosPuntos", detalleGrupo.getCostesIndirectos().getPuntos());
    dataReport.put("costesIndirectosImporte", detalleGrupo.getCostesIndirectos().getImporte());
  }

  private void addTableDineroTotal(DetalleGrupoInvestigacionOutput detalleGrupo,
      HashMap<String, Object> dataReport) {
    List<ResumenTotalOutput> dineroTotalList = new ArrayList();
    ListUtils.emptyIfNull(detalleGrupo.getProduccionesCientificas()).stream().forEach(prc -> {
      ResumenTotalOutput dineroTotal = new ResumenTotalOutput();
      dineroTotal.setTipo(prc.getTipo());
      dineroTotal.setImporte(prc.getImporte());
      dineroTotalList.add(dineroTotal);
    });

    ListUtils.emptyIfNull(detalleGrupo.getTotales()).stream().forEach(total -> {
      ResumenTotalOutput dineroTotal = new ResumenTotalOutput();
      dineroTotal.setTipo(total.getTipo());
      dineroTotal.setImporte(total.getImporte());
      dineroTotalList.add(dineroTotal);
    });

    dataReport.put("dineroTotal", dineroTotalList);
  }

  private XWPFDocument getReportFromDetalleGrupo(ReportInformeDetalleGrupo sgiReport, Integer anio,
      Long grupoId) {
    try {

      HashMap<String, Object> dataReport = new HashMap<>();

      dataReport.put("headerImg", getImageHeaderLogo());

      DetalleGrupoInvestigacionOutput detalleGrupo = sgiApiPrcService.getDataReportDetalleGrupo(anio, grupoId);

      XWPFDocument document = getDocument(detalleGrupo, dataReport,
          getReportDefinitionStream(sgiReport.getPath()));

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

  public byte[] getReportDetalleGrupo(ReportInformeDetalleGrupo sgiReport, Integer anio, Long grupoId) {
    getReportFromDetalleGrupo(sgiReport, anio, grupoId);
    return sgiReport.getContent();
  }

  private XWPFDocument getDocumentResumenPuntuacionGrupos(ResumenPuntuacionGrupoAnioOutput resumenGrupo,
      HashMap<String, Object> dataReport,
      InputStream path) {

    Assert.notNull(
        resumenGrupo,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport
                    .getMessage("org.crue.hercules.sgi.rep.dto.eti.DetalleGrupoInvestigacionOutput.message"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(DetalleGrupoInvestigacionOutput.class))
            .build());

    dataReport.put("anio", resumenGrupo.getAnio());

    dataReport.put("puntuacionesGrupos", resumenGrupo.getPuntuacionesGrupos());

    return compileReportData(path, dataReport);
  }

  private XWPFDocument getReportFromResumenPuntuacionGrupos(ReportInformeResumenPuntuacionGrupos sgiReport,
      Integer anio) {
    try {

      HashMap<String, Object> dataReport = new HashMap<>();

      dataReport.put("headerImg", getImageHeaderLogo());

      ResumenPuntuacionGrupoAnioOutput resumen = sgiApiPrcService.getDataReportResumenPuntuacionGrupos(anio);

      XWPFDocument document = getDocumentResumenPuntuacionGrupos(resumen, dataReport,
          getReportDefinitionStream(sgiReport.getPath()));

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

  public byte[] getReportResumenPuntuacionGrupos(ReportInformeResumenPuntuacionGrupos sgiReport, Integer anio) {
    getReportFromResumenPuntuacionGrupos(sgiReport, anio);
    return sgiReport.getContent();
  }

  private XWPFDocument getDocumentDetalleProduccionInvestigador(
      DetalleProduccionInvestigadorOutput detalleProduccionInvestigador,
      HashMap<String, Object> dataReport,
      InputStream path) {

    Assert.notNull(
        detalleProduccionInvestigador,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport
                    .getMessage("org.crue.hercules.sgi.rep.dto.eti.DetalleProduccionInvestigadorOutput.message"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(DetalleProduccionInvestigadorOutput.class))
            .build());

    dataReport.put("anio", detalleProduccionInvestigador.getAnio());

    dataReport.put("investigador", detalleProduccionInvestigador.getInvestigador());

    dataReport.put("producciones", detalleProduccionInvestigador.getTipos());

    return compileReportData(path, dataReport);
  }

  private XWPFDocument getReportFromDetalleProduccionInvestigador(
      ReportInformeDetalleProduccionInvestigador sgiReport,
      Integer anio, String personaRef) {
    try {

      HashMap<String, Object> dataReport = new HashMap<>();

      dataReport.put("headerImg", getImageHeaderLogo());

      DetalleProduccionInvestigadorOutput detalleProduccionInvestigador = sgiApiPrcService
          .getDataReportDetalleProduccionInvestigador(anio, personaRef);

      XWPFDocument document = getDocumentDetalleProduccionInvestigador(detalleProduccionInvestigador, dataReport,
          getReportDefinitionStream(sgiReport.getPath()));

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

  public byte[] getReportDetalleProduccionInvestigador(ReportInformeDetalleProduccionInvestigador sgiReport,
      Integer anio,
      String personaRef) {
    getReportFromDetalleProduccionInvestigador(sgiReport, anio, personaRef);
    return sgiReport.getContent();
  }
}