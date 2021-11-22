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
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
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

  private final PersonaService personaService;
  private final EvaluacionService evaluacionService;
  private final ConfiguracionService configuracionService;

  private static final Long TIPO_COMENTARIO_GESTOR = 1L;
  private static final Long DICTAMEN_PENDIENTE_CORRECCIONES = 3L;
  private static final Long DICTAMEN_NO_PROCEDE_EVALUAR = 4L;

  @Autowired
  public InformeEvaluacionReportService(SgiConfigProperties sgiConfigProperties, PersonaService personaService,
      BloqueService bloqueService, ApartadoService apartadoService, SgiFormlyService sgiFormlyService,
      RespuestaService respuestaService, EvaluacionService evaluacionService,
      ConfiguracionService configuracionService) {

    super(sgiConfigProperties, bloqueService, apartadoService, sgiFormlyService, respuestaService);
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
    columnsDataTitulo.add("nifResponsable");
    columnsDataTitulo.add("emailResponsable");

    try {
      String personaRef = evaluacion.getMemoria().getPeticionEvaluacion().getPersonaRef();
      PersonaDto persona = personaService.findById(personaRef);
      persona.setDatosContacto(personaService.findDatosContactoByPersonaId(personaRef));

      elementsRow.add(persona.getNombre() + " " + persona.getApellidos());

      elementsRow.add(persona.getNumeroDocumento());

      String email = "";
      if (null != persona.getDatosContacto() && null != persona.getDatosContacto().getTelefonos()
          && !persona.getDatosContacto().getTelefonos().isEmpty()) {
        email = persona.getDatosContacto().getTelefonos().get(0);
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
    elementsRow.add(formatInstantToString(evaluacion.getFechaDictamen(), pattern));

    columnsDataTitulo.add("numeroActa");
    elementsRow.add(evaluacion.getConvocatoriaReunion().getNumeroActa());

    columnsDataTitulo.add("comite");
    elementsRow.add(evaluacion.getMemoria().getComite().getComite());

    columnsDataTitulo.add("nombreInvestigacion");
    elementsRow.add(evaluacion.getMemoria().getComite().getNombreInvestigacion());

    columnsDataTitulo.add("idDictamenPendienteCorrecciones");
    elementsRow.add(DICTAMEN_PENDIENTE_CORRECCIONES);

    columnsDataTitulo.add("idDictamenNoProcedeEvaluar");
    elementsRow.add(DICTAMEN_NO_PROCEDE_EVALUAR);

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

    columnsDataTitulo.add("numeroComentarios");
    Integer numComentariosGestor = evaluacionService.countByEvaluacionIdAndTipoComentarioId(evaluacion.getId(),
        TIPO_COMENTARIO_GESTOR);
    elementsRow.add(numComentariosGestor);

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

      List<ComentarioDto> comentarios = evaluacionService.findByEvaluacionIdGestor(idEvaluacion);
      final Set<Long> apartados = new HashSet<>();
      if (null != comentarios && !comentarios.isEmpty()) {
        comentarios.forEach(c -> getApartadoService().findTreeApartadosById(apartados, c.getApartado()));
      }

      // @formatter:off
      BloquesReportInput etiBloquesReportInput = BloquesReportInput.builder()
        .idMemoria(idEvaluacion)
        .idFormulario(evaluacion.getMemoria().getComite().getFormulario().getId())
        .mostrarRespuestas(false)
        .mostrarContenidoApartado(false)
        .comentarios(comentarios)
        .apartados(apartados)
        .build();
      // @formatter:on

      BloquesReportOutput reportOutput = getDataFromApartadosAndRespuestas(etiBloquesReportInput);

      final int orden = informeEvaluacionEvaluadorReportOutput.getBloques().size();
      for (BloqueOutput bloque : reportOutput.getBloques()) {
        bloque.setOrden(bloque.getOrden() + orden);
      }

      informeEvaluacionEvaluadorReportOutput.getBloques().addAll(reportOutput.getBloques());

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return informeEvaluacionEvaluadorReportOutput;
  }

}