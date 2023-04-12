package org.crue.hercules.sgi.rep.service.eti;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.ComiteDto.Genero;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionEvaluadorReportOutput;
import org.crue.hercules.sgi.rep.dto.sgp.EmailDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiConfService;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class InformeEvaluacionReportService extends BaseEvaluadorEvaluacionReportService {

  private final SgiApiSgpService personaService;
  private final EvaluacionService evaluacionService;
  private final ConfiguracionService configuracionService;

  private static final Long TIPO_COMENTARIO_GESTOR = 1L;
  private static final Long DICTAMEN_PENDIENTE_CORRECCIONES = 3L;
  private static final Long DICTAMEN_NO_PROCEDE_EVALUAR = 4L;
  public static final Long DICTAMEN_FAVORABLE_PENDIENTE_REVISION_MINIMA = 2L;
  public static final Long TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL = 13L;
  public static final Long TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL = 19L;
  public static final Long TIPO_EVALUACION_RETROSPECTIVA = 1L;

  @Autowired
  public InformeEvaluacionReportService(SgiConfigProperties sgiConfigProperties,
      SgiApiConfService sgiApiConfService, SgiApiSgpService personaService,
      BloqueService bloqueService, ApartadoService apartadoService, SgiFormlyService sgiFormlyService,
      RespuestaService respuestaService, EvaluacionService evaluacionService,
      ConfiguracionService configuracionService) {

    super(sgiConfigProperties, sgiApiConfService, bloqueService, apartadoService, sgiFormlyService, respuestaService);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
    this.configuracionService = configuracionService;
  }

  protected void getBloque0(Map<String, TableModel> hmTableModel, EvaluacionDto evaluacion) {

    Vector<Object> columnsDataTitulo = new Vector<>();
    Vector<Vector<Object>> rowsDataTitulo = new Vector<>();
    Vector<Object> elementsRow = new Vector<>();

    columnsDataTitulo.add("referenciaMemoria");
    elementsRow.add(evaluacion.getMemoria().getNumReferencia());

    columnsDataTitulo.add("version");
    elementsRow.add(evaluacion.getVersion());

    columnsDataTitulo.add("nombreResponsable");
    columnsDataTitulo.add("emailResponsable");

    try {
      String personaRef = evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef();
      PersonaDto persona = personaService.findById(personaRef);

      elementsRow.add(persona.getNombre() + " " + persona.getApellidos());

      String email = "";
      if (null != persona.getEmails() && !persona.getEmails().isEmpty()) {
        email = persona.getEmails().stream()
            .filter(e -> null != e.getPrincipal() && e.getPrincipal().equals(Boolean.TRUE)).findFirst()
            .orElse(new EmailDto())
            .getEmail();
      }
      elementsRow.add(email);
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
      elementsRow.add(getErrorMessageToReport(e));
      elementsRow.add(getErrorMessageToReport(e));
    }

    columnsDataTitulo.add("titulo");
    elementsRow.add(evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    columnsDataTitulo.add("fechaDictamen");
    String i18nDe = ApplicationContextSupport.getMessage("common.de");
    String pattern = String.format("EEEE dd '%s' MMMM '%s' yyyy", i18nDe, i18nDe);
    elementsRow.add(formatInstantToString(evaluacion.getConvocatoriaReunion().getFechaEvaluacion(), pattern));

    columnsDataTitulo.add("numeroActa");
    elementsRow.add(evaluacion.getConvocatoriaReunion().getNumeroActa());

    columnsDataTitulo.add("idComite");
    elementsRow.add(evaluacion.getMemoria().getComite().getId());

    columnsDataTitulo.add("comite");
    elementsRow.add(evaluacion.getMemoria().getComite().getComite());

    columnsDataTitulo.add("nombreInvestigacion");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreInvestigacion());

    columnsDataTitulo.add("retrospectiva");
    if (ObjectUtils.isNotEmpty(evaluacion.getMemoria().getEstadoActual())) {
      elementsRow.add(!evaluacion.getMemoria().getEstadoActual().getId().equals(
          TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL)
          && !evaluacion.getMemoria().getEstadoActual().getId()
              .equals(TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL)
          && ObjectUtils.isNotEmpty(evaluacion.getMemoria().getRetrospectiva())
          && evaluacion.getMemoria().getRetrospectiva().getEstadoRetrospectiva().getId() > 1
          && evaluacion.getTipoEvaluacion().getId().equals(TIPO_EVALUACION_RETROSPECTIVA));
    } else {
      elementsRow.add(false);
    }
    columnsDataTitulo.add("seguimientoAnual");
    if (ObjectUtils.isNotEmpty(evaluacion.getMemoria().getEstadoActual())) {
      elementsRow.add(
          evaluacion.getMemoria().getEstadoActual().getId().equals(
              TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_ANUAL));
    } else {
      elementsRow.add(false);
    }
    columnsDataTitulo.add("seguimientoFinal");
    if (ObjectUtils.isNotEmpty(evaluacion.getMemoria().getEstadoActual())) {
      elementsRow.add(
          evaluacion.getMemoria().getEstadoActual().getId()
              .equals(TIPO_ESTADO_MEMORIA_EN_EVALUACION_SEGUIMIENTO_FINAL));
    } else {
      elementsRow.add(false);
    }

    columnsDataTitulo.add("preposicionComite");
    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.M)) {
      elementsRow.add(ApplicationContextSupport.getMessage("common.del"));
    } else {
      elementsRow.add(ApplicationContextSupport.getMessage("common.dela"));
    }

    columnsDataTitulo.add("comisionComite");
    if (evaluacion.getMemoria().getComite().getGenero().equals(Genero.M)) {
      elementsRow.add(ApplicationContextSupport.getMessage("comite.comision.masculino"));
    } else {
      elementsRow.add(ApplicationContextSupport.getMessage("comite.comision.femenino"));
    }

    columnsDataTitulo.add("idDictamenPendienteCorrecciones");
    elementsRow.add(DICTAMEN_PENDIENTE_CORRECCIONES);

    columnsDataTitulo.add("idDictamenNoProcedeEvaluar");
    elementsRow.add(DICTAMEN_NO_PROCEDE_EVALUAR);

    columnsDataTitulo.add("idDictamenPendienteRevisionMinima");
    elementsRow.add(DICTAMEN_FAVORABLE_PENDIENTE_REVISION_MINIMA);

    columnsDataTitulo.add("idDictamen");
    elementsRow.add(evaluacion.getDictamen().getId());

    columnsDataTitulo.add("dictamen");
    elementsRow.add(evaluacion.getDictamen().getNombre());

    columnsDataTitulo.add("comentarioNoProcedeEvaluar");
    elementsRow.add(null != evaluacion.getComentario() ? evaluacion.getComentario() : "");

    columnsDataTitulo.add("mesesArchivadaPendienteCorrecciones");
    Integer mesesArchivadaPendienteCorrecciones = configuracionService.findConfiguracion()
        .getMesesArchivadaPendienteCorrecciones();
    elementsRow.add(mesesArchivadaPendienteCorrecciones);

    columnsDataTitulo.add("diasArchivadaPendienteCorrecciones");
    Integer diasArchivadaPendienteCorrecciones = configuracionService.findConfiguracion().getDiasArchivadaInactivo();
    elementsRow.add(diasArchivadaPendienteCorrecciones);

    rowsDataTitulo.add(elementsRow);

    DefaultTableModel tableModelTitulo = new DefaultTableModel();
    tableModelTitulo.setDataVector(rowsDataTitulo, columnsDataTitulo);
    hmTableModel.put(QUERY_TYPE + SEPARATOR_KEY + BLOQUE_0 + SEPARATOR_KEY + "informeEvaluacion", tableModelTitulo);
  }

  /**
   * Devuelve un informe pdf del informe de evaluación
   *
   * @param idEvaluacion Id de la evaluación
   * @return EtiInformeEvaluacionEvaluadorReportOutput Datos a presentar en el
   *         informe
   */
  protected InformeEvaluacionEvaluadorReportOutput getInformeEvaluadorEvaluacion(Long idEvaluacion) {
    log.debug("getInformeEvaluacion(idEvaluacion)- start");

    Assert.notNull(idEvaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(EvaluacionDto.class)).build());

    InformeEvaluacionEvaluadorReportOutput informeEvaluacionEvaluadorReportOutput = new InformeEvaluacionEvaluadorReportOutput();
    informeEvaluacionEvaluadorReportOutput.setBloques(new ArrayList<>());

    try {

      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);
      informeEvaluacionEvaluadorReportOutput.setEvaluacion(evaluacion);

      Integer numComentariosGestor = evaluacionService.countByEvaluacionIdAndTipoComentarioId(evaluacion.getId(),
          TIPO_COMENTARIO_GESTOR);

      List<ComentarioDto> comentarios = evaluacionService.findByEvaluacionIdGestor(idEvaluacion);
      if (null != comentarios && !comentarios.isEmpty()) {
        final Set<Long> apartados = new HashSet<>();
        comentarios.forEach(c -> getApartadoService().findTreeApartadosById(apartados, c.getApartado()));

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
        .numeroComentariosGestor(numComentariosGestor)
        .build();
        // @formatter:on

        BloquesReportOutput reportOutput = getDataFromApartadosAndRespuestas(etiBloquesReportInput);

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
            .numeroComentariosGestor(numComentariosGestor)
            .build();

        BloquesReportOutput reportOutput = getDataFromApartadosAndRespuestas(etiBloquesReportInput);

        informeEvaluacionEvaluadorReportOutput.getBloques().addAll(reportOutput.getBloques());

      }

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return informeEvaluacionEvaluadorReportOutput;
  }

}