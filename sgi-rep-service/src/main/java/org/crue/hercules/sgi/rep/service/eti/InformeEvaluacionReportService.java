package org.crue.hercules.sgi.rep.service.eti;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.ReportInformeEvaluacion;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe de evaluación de ética
 */
@Service
@Slf4j
@Validated
public class InformeEvaluacionReportService extends InformeEvaluacionEvaluadorBaseReportService {

  private final ConfiguracionService configuracionService;

  private static final Long DICTAMEN_PENDIENTE_CORRECCIONES = 3L;
  private static final Long DICTAMEN_NO_PROCEDE_EVALUAR = 4L;
  public static final Long DICTAMEN_FAVORABLE_PENDIENTE_REVISION_MINIMA = 2L;
  public static final Long TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL = 13L;
  public static final Long TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL = 19L;
  public static final Long TIPO_EVALUACION_RETROSPECTIVA = 1L;
  public static final Long TIPO_EVALUACION_SEGUIMIENTO_ANUAL = 3L;
  public static final Long TIPO_EVALUACION_SEGUIMIENTO_FINAL = 4L;

  public InformeEvaluacionReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, SgiApiSgpService personaService,
      EvaluacionService evaluacionService,
      ConfiguracionService configuracionService,
      BaseApartadosRespuestasReportDocxService baseApartadosRespuestasService) {

    super(sgiConfigProperties, sgiApiConfService, personaService, evaluacionService, baseApartadosRespuestasService);
    this.configuracionService = configuracionService;
  }

  protected XWPFDocument getDocument(EvaluacionDto evaluacion, HashMap<String, Object> dataReport, InputStream path) {

    Assert.notNull(
        evaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field",
                ApplicationContextSupport.getMessage("org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto.message"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(EvaluacionDto.class))
            .build());

    dataReport.put("referenciaMemoria", evaluacion.getMemoria().getNumReferencia());

    dataReport.put("version", evaluacion.getVersion());

    addDataPersona(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef(), dataReport);

    dataReport.put("titulo", evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    dataReport.put("fechaDictamen",
        formatInstantToString(evaluacion.getConvocatoriaReunion().getFechaEvaluacion(), pattern));

    dataReport.put("numeroActa", evaluacion.getConvocatoriaReunion().getNumeroActa());

    dataReport.put("idComite", evaluacion.getMemoria().getComite().getId());

    dataReport.put("comite", evaluacion.getMemoria().getComite().getComite());

    dataReport.put("nombreInvestigacion", evaluacion.getMemoria().getComite().getNombreInvestigacion());

    if (ObjectUtils.isNotEmpty(evaluacion.getMemoria().getEstadoActual())) {
      dataReport.put("retrospectiva", !evaluacion.getMemoria().getEstadoActual().getId().equals(
          TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL)
          && !evaluacion.getMemoria().getEstadoActual().getId()
              .equals(TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL)
          && ObjectUtils.isNotEmpty(evaluacion.getMemoria().getRetrospectiva())
          && evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva().getId() > 1
          && evaluacion.getTipoEvaluacion().getId().equals(TIPO_EVALUACION_RETROSPECTIVA));
    } else {
      dataReport.put("retrospectiva", false);
    }

    if (ObjectUtils.isNotEmpty(evaluacion.getTipoEvaluacion())) {
      dataReport.put("seguimientoAnual",
          evaluacion.getTipoEvaluacion().getId().equals(TIPO_EVALUACION_SEGUIMIENTO_ANUAL));
    } else {
      dataReport.put("seguimientoAnual", false);
    }

    if (ObjectUtils.isNotEmpty(evaluacion.getTipoEvaluacion())) {
      dataReport.put("seguimientoFinal",
          evaluacion.getTipoEvaluacion().getId().equals(TIPO_EVALUACION_SEGUIMIENTO_FINAL));
    } else {
      dataReport.put("seguimientoFinal", false);
    }

    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.M)) {
      dataReport.put("preposicionComite", ApplicationContextSupport.getMessage("common.del"));
    } else {
      dataReport.put("preposicionComite", ApplicationContextSupport.getMessage("common.dela"));
    }

    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.M)) {
      dataReport.put("comisionComite", ApplicationContextSupport.getMessage("comite.comision.masculino"));
    } else {
      dataReport.put("comisionComite", ApplicationContextSupport.getMessage("comite.comision.femenino"));
    }

    dataReport.put("idDictamenPendienteCorrecciones", DICTAMEN_PENDIENTE_CORRECCIONES);

    dataReport.put("idDictamenNoProcedeEvaluar", DICTAMEN_NO_PROCEDE_EVALUAR);

    dataReport.put("idDictamenPendienteRevisionMinima", DICTAMEN_FAVORABLE_PENDIENTE_REVISION_MINIMA);

    dataReport.put("idDictamen", evaluacion.getDictamen().getId());

    dataReport.put("dictamen", evaluacion.getDictamen().getNombre());

    dataReport.put("comentarioNoProcedeEvaluar", null != evaluacion.getComentario() ? evaluacion.getComentario() : "");

    Integer mesesArchivadaPendienteCorrecciones = configuracionService.findConfiguracion()
        .getMesesArchivadaPendienteCorrecciones();
    dataReport.put("mesesArchivadaPendienteCorrecciones", mesesArchivadaPendienteCorrecciones);

    Integer diasArchivadaPendienteCorrecciones = configuracionService.findConfiguracion().getDiasArchivadaInactivo();
    dataReport.put("diasArchivadaPendienteCorrecciones", diasArchivadaPendienteCorrecciones);

    addDataActividad(evaluacion, dataReport);

    dataReport.put("bloqueApartados",
        generarBloqueApartados(evaluacion.getDictamen().getId(),
            getInformeEvaluacion(evaluacion.getId())));

    return compileReportData(path, dataReport);
  }

  public byte[] getReportInformeEvaluacion(ReportInformeEvaluacion sgiReport, Long idEvaluacion) {
    getReportFromIdEvaluacion(sgiReport, idEvaluacion);
    return sgiReport.getContent();
  }

}