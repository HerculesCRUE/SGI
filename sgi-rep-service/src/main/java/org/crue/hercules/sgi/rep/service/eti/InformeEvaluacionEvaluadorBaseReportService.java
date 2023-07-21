package org.crue.hercules.sgi.rep.service.eti;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluadorDto;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionEvaluadorReportOutput;
import org.crue.hercules.sgi.rep.dto.sgp.EmailDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiReportDocxService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import com.deepoove.poi.data.Includes;
import com.deepoove.poi.data.RenderData;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación base de informe evaluación
 */
@Service
@Slf4j
@Validated
public abstract class InformeEvaluacionEvaluadorBaseReportService extends SgiReportDocxService {

  private final EvaluacionService evaluacionService;
  private final SgiApiSgpService personaService;
  private final BaseApartadosRespuestasReportDocxService baseApartadosRespuestasService;

  private static final Long TIPO_ACTIVIDAD_PROYECTO_DE_INVESTIGACION = 1L;
  private static final Long TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA = 3L;
  private static final Long TIPO_INVESTIGACION_TUTELADA_TESIS_DOCTORAL = 1L;
  private static final Long TIPO_COMENTARIO_GESTOR = 1L;
  private static final Long TIPO_COMENTARIO_EVALUADOR = 2L;
  private static final Long DICTAMEN_NO_PROCEDE_EVALUAR = 4L;

  protected InformeEvaluacionEvaluadorBaseReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, SgiApiSgpService personaService,
      EvaluacionService evaluacionService, BaseApartadosRespuestasReportDocxService baseApartadosRespuestasService) {

    super(sgiConfigProperties, sgiApiConfService);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
    this.baseApartadosRespuestasService = baseApartadosRespuestasService;
  }

  protected XWPFDocument getReportFromIdEvaluacion(SgiReportDto sgiReport, Long idEvaluacion) {
    try {
      HashMap<String, Object> dataReport = new HashMap<>();
      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);

      dataReport.put("headerImg", getImageHeaderLogo());

      XWPFDocument document = getDocument(evaluacion, dataReport, getReportDefinitionStream(sgiReport.getPath()));

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

  protected void addDataPersona(String personaRef, HashMap<String, Object> dataReport) {
    generateDataInvestigadorWithAttribute(personaRef, dataReport, null);
  }

  protected void addDataPersona(String personaRef, HashMap<String, Object> dataReport, String attribute) {
    generateDataInvestigadorWithAttribute(personaRef, dataReport, attribute);
  }

  protected void generateDataInvestigadorWithAttribute(String personaRef, HashMap<String, Object> dataReport,
      String attribute) {
    String attributeElementData = "nombre" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeArticleData = "articulo" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeDelData = "fieldDel" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeElData = "fieldEl" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeFieldData = "field" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeFieldCapitalizeData = "fieldCapitalize"
        + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    String attributeEmailData = "email" + ((ObjectUtils.isEmpty(attribute)) ? "Investigador" : attribute);
    try {
      PersonaDto persona = personaService.findById(personaRef);
      dataReport.put(attributeElementData, persona.getNombre() + " " + persona.getApellidos());
      dataReport.put(attributeDelData, persona.getNombre() + " " + persona.getApellidos());
      String masculino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("investigador.masculino")
          : ApplicationContextSupport.getMessage(attribute.toLowerCase() + ".masculino"));
      String femenino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("investigador.femenino")
          : ApplicationContextSupport.getMessage(attribute.toLowerCase() + ".femenino"));

      String delMasculino = ApplicationContextSupport.getMessage("common.del");
      String deLaFemenino = ApplicationContextSupport.getMessage("common.dela");

      String elMasculino = ApplicationContextSupport.getMessage("common.el");
      String laFemenino = ApplicationContextSupport.getMessage("common.la");

      String fieldMasculino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("field.investigador.masculino")
          : ApplicationContextSupport.getMessage("field." + attribute.toLowerCase() + ".masculino"));
      String fieldFemenino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("field.investigador.femenino")
          : ApplicationContextSupport.getMessage("field." + attribute.toLowerCase() + ".femenino"));

      String fieldCapitalizeMasculino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("field.capitalize.investigador.masculino")
          : ApplicationContextSupport.getMessage("field.capitalize." + attribute.toLowerCase() + ".masculino"));
      String fieldCapitalizeFemenino = ((ObjectUtils.isEmpty(attribute))
          ? ApplicationContextSupport.getMessage("field.capitalize.investigador.femenino")
          : ApplicationContextSupport.getMessage("field.capitalize." + attribute.toLowerCase() + ".femenino"));

      String email = "";
      if (null != persona.getEmails() && !persona.getEmails().isEmpty()) {
        email = persona.getEmails().stream()
            .filter(e -> null != e.getPrincipal() && e.getPrincipal().equals(Boolean.TRUE)).findFirst()
            .orElse(new EmailDto())
            .getEmail();
      }

      dataReport.put(
          attributeArticleData,
          persona.getSexo().getId().equals("V") ? masculino : femenino);

      dataReport.put(
          attributeDelData,
          persona.getSexo().getId().equals("V") ? delMasculino : deLaFemenino);

      dataReport.put(
          attributeElData,
          persona.getSexo().getId().equals("V") ? elMasculino : laFemenino);

      dataReport.put(
          attributeFieldData,
          persona.getSexo().getId().equals("V") ? fieldMasculino : fieldFemenino);

      dataReport.put(
          attributeFieldCapitalizeData,
          persona.getSexo().getId().equals("V") ? fieldCapitalizeMasculino : fieldCapitalizeFemenino);

      dataReport.put(attributeEmailData, email);

    } catch (Exception e) {
      dataReport.put(attributeElementData, getErrorMessage(e));
      dataReport.put(attributeArticleData,
          ((ObjectUtils.isEmpty(attribute)) ? ApplicationContextSupport.getMessage("investigador.masculinoFemenino")
              : ApplicationContextSupport.getMessage(attribute.toLowerCase() + ".masculinoFemenino")));
      dataReport.put(attributeDelData, ApplicationContextSupport.getMessage("common.del.masculinoFemenino"));
      dataReport.put(attributeElData, ApplicationContextSupport.getMessage("common.el.masculinoFemenino"));
      dataReport.put(attributeFieldData,
          ((ObjectUtils.isEmpty(attribute))
              ? ApplicationContextSupport.getMessage("field.investigador.masculinoFemenino")
              : ApplicationContextSupport.getMessage("field." + attribute.toLowerCase() + ".masculinoFemenino")));
      dataReport.put(attributeFieldCapitalizeData,
          ((ObjectUtils.isEmpty(attribute))
              ? ApplicationContextSupport.getMessage("field.capitalize.investigador.masculinoFemenino")
              : ApplicationContextSupport
                  .getMessage("field.capitalize." + attribute.toLowerCase() + ".masculinoFemenino")));
      dataReport.put(attributeEmailData, getErrorMessage(e));
    }
  }

  protected void addDataEvaluacion(EvaluacionDto evaluacion, HashMap<String, Object> dataReport) {
    dataReport.put("tituloProyecto", evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());
    dataReport.put("referenciaProyecto", evaluacion.getMemoria().getPeticionEvaluacion().getCodigo());
    dataReport.put("comite", evaluacion.getMemoria().getComite().getComite());
    try {
      EvaluadorDto secretario = evaluacionService.findSecretarioEvaluacion(evaluacion.getId());
      if (ObjectUtils.isNotEmpty(secretario)) {
        addDataPersona(secretario.getPersonaRef(), dataReport, "Secretario");
      } else {
        dataReport.put("nombreSecretario", " - ");
        dataReport.put("articuloSecretario", ApplicationContextSupport.getMessage("secretario.masculinoFemenino"));
        dataReport.put("fieldDelSecretario", ApplicationContextSupport.getMessage("common.del.masculinoFemenino"));
        dataReport.put("fieldElSecretario", ApplicationContextSupport.getMessage("common.el.masculinoFemenino"));
        dataReport.put("fieldSecretario", ApplicationContextSupport.getMessage("field.secretario.masculinoFemenino"));
        dataReport.put("fieldCapitalizeSecretario",
            ApplicationContextSupport.getMessage("field.capitalize.secretario.masculinoFemenino"));
      }
    } catch (Exception e) {
      dataReport.put("nombreSecretario", getErrorMessage(e));
      dataReport.put("articuloSecretario", getErrorMessage(e));
      dataReport.put("fieldDelSecretario", getErrorMessage(e));
      dataReport.put("fieldElSecretario", getErrorMessage(e));
      dataReport.put("fieldSecretario", getErrorMessage(e));
      dataReport.put("fieldCapitalizeSecretario", getErrorMessage(e));
    }

    try {
      String presidenteRef = evaluacionService.findIdPresidenteByIdEvaluacion(evaluacion.getId());
      if (ObjectUtils.isNotEmpty(presidenteRef)) {
        addDataPersona(presidenteRef, dataReport, "Presidente");
      } else {
        dataReport.put("nombrePresidente", " - ");
        dataReport.put("articuloPresidente", ApplicationContextSupport.getMessage("presidente.masculinoFemenino"));
        dataReport.put("fieldDelPresidente", ApplicationContextSupport.getMessage("common.del.masculinoFemenino"));
        dataReport.put("fieldElPresidente", ApplicationContextSupport.getMessage("common.el.masculinoFemenino"));
        dataReport.put("fieldPresidente", ApplicationContextSupport.getMessage("field.presidente.masculinoFemenino"));
        dataReport.put("fieldCapitalizePresidente",
            ApplicationContextSupport.getMessage("field.capitalize.presidente.masculinoFemenino"));
      }
    } catch (Exception e) {
      dataReport.put("nombrePresidente", getErrorMessage(e));
      dataReport.put("articuloPresidente", getErrorMessage(e));
      dataReport.put("fieldDelPresidente", getErrorMessage(e));
      dataReport.put("fieldElPresidente", getErrorMessage(e));
      dataReport.put("fieldPresidente", getErrorMessage(e));
      dataReport.put("fieldCapitalizePresidente",
          getErrorMessage(e));
    }

    dataReport.put("nombreInvestigacion", evaluacion.getMemoria().getComite().getNombreInvestigacion());
    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.F)) {
      String i18nDela = ApplicationContextSupport.getMessage("common.dela");
      dataReport.put("del", i18nDela);
      String i18nLa = ApplicationContextSupport.getMessage("common.la");
      dataReport.put("el", StringUtils.capitalize(i18nLa));
      String i18nEsta = ApplicationContextSupport.getMessage("common.esta");
      dataReport.put("este", i18nEsta);
    } else {
      String i18nDel = ApplicationContextSupport.getMessage("common.del");
      dataReport.put("del", i18nDel);
      String i18nEl = ApplicationContextSupport.getMessage("common.el");
      dataReport.put("el", StringUtils.capitalize(i18nEl));
      String i18nEste = ApplicationContextSupport.getMessage("common.este");
      dataReport.put("este", i18nEste);
    }

    addDataActividad(evaluacion, dataReport);

    evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada();
  }

  protected void addDataActividad(EvaluacionDto evaluacion, HashMap<String, Object> dataReport) {
    if (ObjectUtils.isNotEmpty(evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad())
        && !evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getId()
            .equals(TIPO_ACTIVIDAD_INVESTIGACION_TUTELADA)) {
      dataReport.put("actividad",
          evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre().toLowerCase());
      if (evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getId()
          .equals(TIPO_ACTIVIDAD_PROYECTO_DE_INVESTIGACION)) {
        dataReport.put("fieldDelActividad", ApplicationContextSupport.getMessage("common.del"));
        dataReport.put("fieldDichoActividad", ApplicationContextSupport.getMessage("common.dicho"));
        dataReport.put("fieldRealizadoActividad", ApplicationContextSupport.getMessage("common.realizado"));
        dataReport.put("fieldAlActividad", ApplicationContextSupport.getMessage("common.al"));
      } else {
        dataReport.put("fieldDelActividad", ApplicationContextSupport.getMessage("common.dela"));
        dataReport.put("fieldDichoActividad", ApplicationContextSupport.getMessage("common.dicha"));
        dataReport.put("fieldRealizadoActividad", ApplicationContextSupport.getMessage("common.realizada"));
        dataReport.put("fieldAlActividad", ApplicationContextSupport.getMessage("common.a.la"));
      }
    } else if (ObjectUtils.isNotEmpty(evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad()) && ObjectUtils
        .isNotEmpty(evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada())) {
      dataReport.put("actividad",
          evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre().toLowerCase());
      if (!evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getId()
          .equals(TIPO_INVESTIGACION_TUTELADA_TESIS_DOCTORAL)) {
        dataReport.put("fieldDelActividad", ApplicationContextSupport.getMessage("common.del"));
        dataReport.put("fieldDichoActividad", ApplicationContextSupport.getMessage("common.dicho"));
        dataReport.put("fieldRealizadoActividad", ApplicationContextSupport.getMessage("common.realizado"));
        dataReport.put("fieldAlActividad", ApplicationContextSupport.getMessage("common.al"));
      } else {
        dataReport.put("fieldDelActividad", ApplicationContextSupport.getMessage("common.dela"));
        dataReport.put("fieldDichoActividad", ApplicationContextSupport.getMessage("common.dicha"));
        dataReport.put("fieldRealizadoActividad", ApplicationContextSupport.getMessage("common.realizada"));
        dataReport.put("fieldAlActividad", ApplicationContextSupport.getMessage("common.a.la"));
      }
    }

  }

  protected RenderData generarBloqueApartados(Long idDictamen,
      InformeEvaluacionEvaluadorReportOutput informeEvaluacionEvaluadorReportOutput) {
    Map<String, Object> subDataBloqueApartado = new HashMap<>();
    subDataBloqueApartado.put("idDictamen", idDictamen);
    subDataBloqueApartado.put("idDictamenNoProcedeEvaluar", DICTAMEN_NO_PROCEDE_EVALUAR);
    if (ObjectUtils.isNotEmpty(informeEvaluacionEvaluadorReportOutput)
        && ObjectUtils.isNotEmpty(informeEvaluacionEvaluadorReportOutput.getBloques())
        && !informeEvaluacionEvaluadorReportOutput.getBloques().isEmpty()) {
      if (informeEvaluacionEvaluadorReportOutput.getBloques().stream().findAny().isPresent()
          && informeEvaluacionEvaluadorReportOutput.getBloques().stream().findAny().get().getApartados().stream()
              .findAny().isPresent()) {
        subDataBloqueApartado.put("numComentarios",
            informeEvaluacionEvaluadorReportOutput.getBloques().stream().findAny().get().getApartados().stream()
                .findAny().get().getNumeroComentariosTotalesInforme());
      }
      subDataBloqueApartado.put("bloques", informeEvaluacionEvaluadorReportOutput.getBloques());
    } else {
      subDataBloqueApartado.put("numComentarios", null);
      subDataBloqueApartado.put("bloques", null);
      return null;
    }
    return Includes.ofStream(getReportDefinitionStream("rep-eti-bloque-apartado-docx"))
        .setRenderModel(subDataBloqueApartado).create();
  }

  /**
   * Devuelve un informe pdf del informe de evaluación
   *
   * @param idEvaluacion Id de la evaluación
   * @return EtiInformeEvaluacionEvaluadorReportOutput Datos a presentar en el
   *         informe
   */
  private InformeEvaluacionEvaluadorReportOutput getInformeEvaluadorEvaluacion(Long idEvaluacion,
      boolean isInformeEvaluacion) {
    log.debug("getInformeEvaluacion(idEvaluacion)- start");

    Assert.notNull(idEvaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity",
                ApplicationContextSupport.getMessage(EvaluacionDto.class))
            .build());

    InformeEvaluacionEvaluadorReportOutput informeEvaluacionEvaluadorReportOutput = new InformeEvaluacionEvaluadorReportOutput();
    informeEvaluacionEvaluadorReportOutput.setBloques(new ArrayList<>());

    try {

      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);
      informeEvaluacionEvaluadorReportOutput.setEvaluacion(evaluacion);

      Integer numComentarios = null;

      List<ComentarioDto> comentarios = null;

      if (isInformeEvaluacion) {
        comentarios = evaluacionService.findByEvaluacionIdGestor(idEvaluacion);
        numComentarios = evaluacionService.countByEvaluacionIdAndTipoComentarioId(evaluacion.getId(),
            TIPO_COMENTARIO_GESTOR);
      } else {
        comentarios = evaluacionService.findByEvaluacionIdEvaluador(idEvaluacion);
        numComentarios = evaluacionService.countByEvaluacionIdAndTipoComentarioId(evaluacion.getId(),
            TIPO_COMENTARIO_EVALUADOR);
      }

      if (null != comentarios && !comentarios.isEmpty()) {
        final Set<Long> apartados = new HashSet<>();
        comentarios.forEach(
            c -> baseApartadosRespuestasService.getApartadoService().findTreeApartadosById(apartados, c.getApartado()));

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
        .idMemoria(idEvaluacion)
        .idFormulario(idFormulario)
        .mostrarRespuestas(false)
        .mostrarContenidoApartado(false)
        .comentarios(comentarios)
        .apartados(apartados)
        .numeroComentarios(numComentarios)
        .build();
        // @formatter:on

        BloquesReportOutput reportOutput = baseApartadosRespuestasService
            .getDataFromApartadosAndRespuestas(etiBloquesReportInput);

        final int orden = informeEvaluacionEvaluadorReportOutput.getBloques().size();
        for (BloqueOutput bloque : reportOutput.getBloques()) {
          bloque.setOrden(bloque.getOrden() + orden);
        }

        informeEvaluacionEvaluadorReportOutput.getBloques().addAll(reportOutput.getBloques());
      } else {

        BloquesReportInput etiBloquesReportInput = BloquesReportInput.builder()
            .idMemoria(idEvaluacion)
            .idFormulario(0L)
            .mostrarRespuestas(false)
            .mostrarContenidoApartado(false)
            .comentarios(null)
            .apartados(null)
            .numeroComentarios(numComentarios)
            .build();

        BloquesReportOutput reportOutput = baseApartadosRespuestasService
            .getDataFromApartadosAndRespuestas(etiBloquesReportInput);

        if (informeEvaluacionEvaluadorReportOutput.getBloques().isEmpty() && ObjectUtils.isNotEmpty(reportOutput)) {
          informeEvaluacionEvaluadorReportOutput.setBloques(reportOutput.getBloques());
        } else if (ObjectUtils.isNotEmpty(reportOutput)) {
          informeEvaluacionEvaluadorReportOutput.getBloques().addAll(reportOutput.getBloques());
        }

      }

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return informeEvaluacionEvaluadorReportOutput;
  }

  protected InformeEvaluacionEvaluadorReportOutput getInformeEvaluacion(Long idEvaluacion) {
    return this.getInformeEvaluadorEvaluacion(idEvaluacion, Boolean.TRUE);
  }

  protected InformeEvaluacionEvaluadorReportOutput getInformeEvaluador(Long idEvaluacion) {
    return this.getInformeEvaluadorEvaluacion(idEvaluacion, Boolean.FALSE);
  }

  protected abstract XWPFDocument getDocument(EvaluacionDto evaluacion, HashMap<String, Object> dataReport,
      InputStream path);

}