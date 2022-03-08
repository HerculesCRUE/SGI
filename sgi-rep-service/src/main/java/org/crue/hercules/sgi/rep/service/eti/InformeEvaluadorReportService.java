package org.crue.hercules.sgi.rep.service.eti;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.EvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.InformeEvaluacionEvaluadorReportOutput;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe de evaluador de ética
 */
@Service
@Slf4j
@Validated
public class InformeEvaluadorReportService extends BaseEvaluadorEvaluacionReportService {

  private final SgiApiSgpService personaService;
  private final EvaluacionService evaluacionService;

  private static final Long TIPO_COMENTARIO_EVALUADOR = 2L;

  public InformeEvaluadorReportService(SgiConfigProperties sgiConfigProperties, SgiApiSgpService personaService,
      BloqueService bloqueService, ApartadoService apartadoService, SgiFormlyService sgiFormlyService,
      RespuestaService respuestaService, EvaluacionService evaluacionService) {

    super(sgiConfigProperties, bloqueService, apartadoService, sgiFormlyService, respuestaService);
    this.personaService = personaService;
    this.evaluacionService = evaluacionService;
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
    try {
      PersonaDto persona = personaService.findById(evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef());
      elementsRow.add(persona.getNombre() + " " + persona.getApellidos());
    } catch (Exception e) {
      elementsRow.add(getErrorMessageToReport(e));
    }

    columnsDataTitulo.add("tipoActividad");
    String tipoActividad = "";
    if (null != evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada() && StringUtils
        .hasText(evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre())) {
      tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoInvestigacionTutelada().getNombre();
    } else if (null != evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad()
        && StringUtils.hasText(evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre())) {
      tipoActividad = evaluacion.getMemoria().getPeticionEvaluacion().getTipoActividad().getNombre();
    }
    elementsRow.add(tipoActividad);

    columnsDataTitulo.add("titulo");
    elementsRow.add(evaluacion.getMemoria().getPeticionEvaluacion().getTitulo());

    columnsDataTitulo.add("fechaInicio");
    elementsRow.add(formatInstantToString(evaluacion.getMemoria().getPeticionEvaluacion().getFechaInicio()));

    columnsDataTitulo.add("fechaFin");
    elementsRow.add(formatInstantToString(evaluacion.getMemoria().getPeticionEvaluacion().getFechaFin()));

    columnsDataTitulo.add("financiacion");
    elementsRow.add(evaluacion.getMemoria().getPeticionEvaluacion().getFuenteFinanciacion());

    columnsDataTitulo.add("resumen");
    elementsRow.add(evaluacion.getMemoria().getPeticionEvaluacion().getResumen());

    columnsDataTitulo.add("numeroComentarios");
    Integer numComentariosEvaluador = evaluacionService.countByEvaluacionIdAndTipoComentarioId(evaluacion.getId(),
        TIPO_COMENTARIO_EVALUADOR);
    elementsRow.add(numComentariosEvaluador);

    columnsDataTitulo.add("comite");
    elementsRow.add(evaluacion.getMemoria().getComite().getComite());

    columnsDataTitulo.add("nombreInvestigacion");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreInvestigacion());

    rowsDataTitulo.add(elementsRow);

    DefaultTableModel tableModelTitulo = new DefaultTableModel();
    tableModelTitulo.setDataVector(rowsDataTitulo, columnsDataTitulo);
    hmTableModel.put(QUERY_TYPE + SEPARATOR_KEY + BLOQUE_0 + SEPARATOR_KEY + "informeEvaluador", tableModelTitulo);
  }

  /**
   * Devuelve un informe pdf del informe de evaluador
   *
   * @param idEvaluacion Id de la evaluación
   * @return EtiInformeEvaluacionEvaluadorReportOutput Datos a presentar en el
   *         informe
   */
  protected InformeEvaluacionEvaluadorReportOutput getInformeEvaluadorEvaluacion(Long idEvaluacion) {
    log.debug("getInformeEvaluadorEvaluacion(idEvaluacion)- start");

    Assert.notNull(idEvaluacion,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(EvaluacionDto.class)).build());

    InformeEvaluacionEvaluadorReportOutput iInformeEvaluacionEvaluadorReportOutput = new InformeEvaluacionEvaluadorReportOutput();
    iInformeEvaluacionEvaluadorReportOutput.setBloques(new ArrayList<>());

    try {

      EvaluacionDto evaluacion = evaluacionService.findById(idEvaluacion);
      iInformeEvaluacionEvaluadorReportOutput.setEvaluacion(evaluacion);

      List<ComentarioDto> comentarios = evaluacionService.findByEvaluacionIdEvaluador(idEvaluacion);
      if (null != comentarios && !comentarios.isEmpty()) {
        final Set<Long> apartados = new HashSet<>();
        comentarios.forEach(c -> getApartadoService().findTreeApartadosById(apartados, c.getApartado()));
        Long idFormulario = comentarios.get(0).getApartado().getBloque().getFormulario().getId();

        // @formatter:off
        BloquesReportInput bloquesReportInput = BloquesReportInput.builder()
          .idMemoria(idEvaluacion)
          .idFormulario(idFormulario)
          .mostrarRespuestas(false)
          .mostrarContenidoApartado(false)
          .comentarios(comentarios)
          .apartados(apartados)
          .build();
        // @formatter:on

        BloquesReportOutput reportOutput = getDataFromApartadosAndRespuestas(bloquesReportInput);

        final int orden = iInformeEvaluacionEvaluadorReportOutput.getBloques().size();
        for (BloqueOutput bloque : reportOutput.getBloques()) {
          bloque.setOrden(bloque.getOrden() + orden);
        }

        iInformeEvaluacionEvaluadorReportOutput.getBloques().addAll(reportOutput.getBloques());
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return iInformeEvaluacionEvaluadorReportOutput;
  }

}