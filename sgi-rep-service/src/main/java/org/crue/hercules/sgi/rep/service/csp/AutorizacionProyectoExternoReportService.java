package org.crue.hercules.sgi.rep.service.csp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.dto.csp.AutorizacionDto;
import org.crue.hercules.sgi.rep.dto.csp.AutorizacionReport;
import org.crue.hercules.sgi.rep.dto.csp.ConvocatoriaDto;
import org.crue.hercules.sgi.rep.dto.sgemp.EmpresaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto.VinculacionDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportDocxService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgempService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe autorizacionproyectoexterno
 */
@Service
@Slf4j
@Validated
public class AutorizacionProyectoExternoReportService extends SgiReportDocxService {

  private final PersonaService personaService;
  private final AutorizacionProyectoExternoService autorizacionProyectoExternoService;
  private final ConvocatoriaService convocatoriaService;
  private final SgiApiSgempService empresaService;

  public AutorizacionProyectoExternoReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, PersonaService personaService,
      AutorizacionProyectoExternoService autorizacionProyectoExternoService, ConvocatoriaService convocatoriaService,
      SgiApiSgempService empresaService) {

    super(sgiConfigProperties, sgiApiConfService);
    this.personaService = personaService;
    this.autorizacionProyectoExternoService = autorizacionProyectoExternoService;
    this.convocatoriaService = convocatoriaService;
    this.empresaService = empresaService;
  }

  private XWPFDocument getDocument(AutorizacionDto autorizacionProyectoExterno, HashMap<String, Object> dataReport,
      InputStream path) {
    Assert.notNull(
        autorizacionProyectoExterno,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.rep.dto.eti.AutorizacionDto.message"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(AutorizacionDto.class))
            .build());

    getDatosSolicitante(autorizacionProyectoExterno, dataReport);

    String datosConvocatoriaString = null;
    if (autorizacionProyectoExterno.getConvocatoriaId() != null) {
      try {
        ConvocatoriaDto convocatoria = convocatoriaService.findById(autorizacionProyectoExterno.getConvocatoriaId());
        datosConvocatoriaString = convocatoria.getTitulo();
      } catch (Exception e) {
        datosConvocatoriaString = getErrorMessage(e);
      }
    } else {
      datosConvocatoriaString = autorizacionProyectoExterno.getDatosConvocatoria();
    }
    dataReport.put("datosConvocatoria", datosConvocatoriaString);

    dataReport.put("horasDedicacion", autorizacionProyectoExterno.getHorasDedicacion() != null
        ? autorizacionProyectoExterno.getHorasDedicacion() + " horas"
        : "");

    dataReport.put("tituloProyecto", autorizacionProyectoExterno.getTituloProyecto());

    String universidadString = null;
    if (autorizacionProyectoExterno.getEntidadRef() != null) {
      try {
        EmpresaDto empresa = empresaService.findById(autorizacionProyectoExterno.getEntidadRef());
        universidadString = empresa.getNombre();
      } catch (Exception e) {
        universidadString = getErrorMessage(e);
      }
    } else {
      universidadString = autorizacionProyectoExterno.getDatosEntidad();
    }
    dataReport.put("universidad", universidadString);

    String investigadorString = null;
    String fieldInvestigador = "-";
    if (autorizacionProyectoExterno.getResponsableRef() != null) {
      try {
        PersonaDto persona = personaService.findById(autorizacionProyectoExterno.getResponsableRef());
        investigadorString = persona.getNombre() + " " + persona.getApellidos();
        if (persona.getSexo().getId().equals("V")) {
          fieldInvestigador = ApplicationContextSupport.getMessage("field.capitalize.investigador.masculino");
        } else {
          fieldInvestigador = ApplicationContextSupport.getMessage("field.capitalize.investigador.femenino");
        }
      } catch (Exception e) {
        investigadorString = getErrorMessage(e);
      }
    } else {
      investigadorString = autorizacionProyectoExterno.getDatosResponsable();
      fieldInvestigador = ApplicationContextSupport.getMessage("field.capitalize.investigador.masculinoFemenino");
    }
    dataReport.put("investigador", investigadorString);
    dataReport.put("fieldCapitalizeInvestigador", fieldInvestigador);

    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    dataReport.put("fechaActual", formatInstantToString(Instant.now(), pattern));

    return compileReportData(path, dataReport);
  }

  private void getDatosSolicitante(AutorizacionDto autorizacionProyectoExterno, HashMap<String, Object> dataReport) {
    boolean isSolicitanteMasculino = true;
    try {
      PersonaDto persona = personaService.findById(autorizacionProyectoExterno.getSolicitanteRef());
      dataReport.put("solicitanteNombre", persona.getNombre() + " " + persona.getApellidos());
      dataReport.put("solicitanteNif", persona.getNumeroDocumento());
      if (!persona.getSexo().getId().equals("V")) {
        isSolicitanteMasculino = false;
      }
    } catch (Exception e) {
      dataReport.put("solicitanteNombre", getErrorMessage(e));
      dataReport.put("solicitanteNif", getErrorMessage(e));
    }
    dataReport.put("isSolicitanteMasculino", isSolicitanteMasculino);

    try {
      VinculacionDto vinculacionPersona = personaService
          .getVinculacion(autorizacionProyectoExterno.getSolicitanteRef());
      dataReport.put("solicitanteCatProfesional", vinculacionPersona.getCategoriaProfesional() != null
          ? vinculacionPersona.getCategoriaProfesional().getNombre()
          : '-');
      dataReport.put("solicitanteDepartamento",
          vinculacionPersona.getDepartamento() != null ? vinculacionPersona.getDepartamento().getNombre() : "-");
      dataReport.put("solicitanteCentro",
          vinculacionPersona.getCentro() != null ? vinculacionPersona.getCentro().getNombre() : "-");
    } catch (Exception e) {
      dataReport.put("solicitanteCatProfesional", getErrorMessage(e));
      dataReport.put("solicitanteDepartamento", getErrorMessage(e));
      dataReport.put("solicitanteCentro", getErrorMessage(e));
    }
  }

  private XWPFDocument getReportFromAutorizacionProyectoExterno(SgiReportDto sgiReport, Long idAutorizacion) {
    try {
      HashMap<String, Object> dataReport = new HashMap<>();
      AutorizacionDto autorizacionProyectoExterno = autorizacionProyectoExternoService
          .findById(idAutorizacion);

      dataReport.put("headerImg", getImageHeaderLogo());

      XWPFDocument document = getDocument(autorizacionProyectoExterno, dataReport,
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

  public byte[] getReportAutorizacionProyectoExterno(AutorizacionReport sgiReport,
      Long idAutorizacion) {
    getReportFromAutorizacionProyectoExterno(sgiReport, idAutorizacion);
    return sgiReport.getContent();
  }
}