package org.crue.hercules.sgi.rep.service.eti;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Vector;
import java.util.stream.Stream;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.crue.hercules.sgi.framework.spring.context.support.ApplicationContextSupport;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiReportDto;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ElementOutput;
import org.crue.hercules.sgi.rep.dto.eti.FormularioDto;
import org.crue.hercules.sgi.rep.dto.eti.MXXReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaDto;
import org.crue.hercules.sgi.rep.dto.eti.MemoriaPeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.eti.PeticionEvaluacionDto;
import org.crue.hercules.sgi.rep.dto.sgp.PersonaDto;
import org.crue.hercules.sgi.rep.dto.sgp.EmailDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.sgi.SgiApiSgpService;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informes de ética M10, M20, M30
 */
@Service
@Slf4j
@Validated
public class MXXReportService extends BaseApartadosRespuestasReportService {

  private final MemoriaService memoriaService;
  private final PeticionEvaluacionService peticionEvaluacionService;
  private final SgiApiSgpService personaService;

  private static final String NO_REF_CEID = "NoRefCEID: ";
  private static final String DATOS_SOLICITANTE_TYPE = "datosSolicitanteType";
  private static final String TITULO = "titulo";

  @Autowired
  public MXXReportService(SgiConfigProperties sgiConfigProperties, MemoriaService memoriaService,
      PeticionEvaluacionService peticionEvaluacionService, SgiApiSgpService personaService, BloqueService bloqueService,
      ApartadoService apartadoService, SgiFormlyService sgiFormlyService, RespuestaService respuestaService) {

    super(sgiConfigProperties, bloqueService, apartadoService, sgiFormlyService, respuestaService);
    this.memoriaService = memoriaService;
    this.peticionEvaluacionService = peticionEvaluacionService;
    this.personaService = personaService;
  }

  /**
   * Devuelve un formulario M10, M20 o M30 a partir de un json de pruebas
   * 
   * @param reportOutput   SgiReport
   * @param outputJsonPath String
   * @param memoria        MemoriaDto
   */
  public void getReportMXXFromJson(SgiReportDto reportOutput, String outputJsonPath, MemoriaDto memoria) {

    Map<String, TableModel> hmTableModel = createMockQueryMxxDataModel(outputJsonPath, memoria);

    generateXls(memoria.getComite().getFormulario().getNombre(), hmTableModel, Arrays.asList(COLUMNS_TABLE_MODEL));

    getTituloMXX(hmTableModel, memoria);

    getReportMXXIntern(reportOutput, hmTableModel);
  }

  /**
   * A partir de un json (EtiReporOutput) crea un mock de DefaultTableModel para
   * la carga de datos en el report y un xls para la carga directa de datos de
   * prueba desde el report designer
   * 
   * @param outputJsonPath String
   * @param memoria        MemoriaDto
   * @return Map<String, TableModel>
   */
  private Map<String, TableModel> createMockQueryMxxDataModel(String outputJsonPath, MemoriaDto memoria) {
    Map<String, TableModel> hmTableModel = null;

    try (InputStream jsonApartadoInputStream = this.getClass().getClassLoader().getResourceAsStream(outputJsonPath)) {
      try (Scanner scanner = new Scanner(jsonApartadoInputStream, "UTF-8").useDelimiter("\\Z")) {
        String outputJson = scanner.next();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MXXReportOutput mxxReportOutput = objectMapper.readValue(outputJson, MXXReportOutput.class);

        hmTableModel = generateTableModelFromReportOutput(mxxReportOutput);

        getTituloMXX(hmTableModel, memoria);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return hmTableModel;
  }

  /**
   * Devuelve un formulario M10, M20 o M30 a partir del microservicio de eti
   * 
   * @param reportOutput SgiReport
   * @param idMemoria    Id de la memoria
   * @param idFormulario Id del formulario
   * @return byte[] Report
   */
  public byte[] getReportMXX(SgiReportDto reportOutput, Long idMemoria, Long idFormulario) {
    Assert.notNull(idMemoria,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(MemoriaDto.class)).build());
    Assert.notNull(
        idFormulario,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key(Assert.class, "notNull")
            .parameter("field", ApplicationContextSupport.getMessage("id"))
            .parameter("entity", ApplicationContextSupport.getMessage(FormularioDto.class)).build());

    MXXReportOutput mxxReportOutput = this.getMXX(idMemoria, idFormulario);

    Map<String, TableModel> hmTableModel = generateTableModelFromReportOutput(mxxReportOutput);

    getTituloMXX(hmTableModel, mxxReportOutput.getMemoria());

    getReportMXXIntern(reportOutput, hmTableModel);

    return reportOutput.getContent();
  }

  private void getTituloMXX(Map<String, TableModel> hmTableModel, MemoriaDto memoria) {
    if (null != hmTableModel && null != memoria && null != memoria.getComite()
        && null != memoria.getComite().getFormulario()) {
      Vector<Object> columnsDataTitulo = new Vector<>();
      Vector<Vector<Object>> rowsDataTitulo = new Vector<>();
      Vector<Object> elementsRow = new Vector<>();
      columnsDataTitulo.add(TITULO);
      elementsRow.add(memoria.getComite().getNombreInvestigacion());
      columnsDataTitulo.add("formulario");
      elementsRow.add(memoria.getComite().getFormulario().getNombre());
      columnsDataTitulo.add("comite");
      elementsRow.add(memoria.getComite().getComite());

      rowsDataTitulo.add(elementsRow);

      DefaultTableModel tableModelTitulo = new DefaultTableModel();
      tableModelTitulo.setDataVector(rowsDataTitulo, columnsDataTitulo);
      hmTableModel.put(QUERY_TYPE + SEPARATOR_KEY + TITULO + SEPARATOR_KEY + "mXX", tableModelTitulo);
    }
  }

  /**
   * Devuelve un formulario M10, M20 o M30
   * 
   * @param reportOutput SgiReport
   * @param hmTableModel Map<String, TableModel>
   */
  private void getReportMXXIntern(SgiReportDto reportOutput, Map<String, TableModel> hmTableModel) {

    if (null != hmTableModel && !hmTableModel.isEmpty()) {
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

        MasterReport report = getReportDefinition(reportOutput.getPath());

        getDataFactoryFromTableModels(hmTableModel, report);

        PdfReportUtil.createPDF(report, baos);

        reportOutput.setContent(baos.toByteArray());

      } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new GetDataReportException();
      }
    }
  }

  private void getDataFactoryFromTableModels(Map<String, TableModel> hmTableModel, MasterReport report) {
    TableDataFactory dataFactory = new TableDataFactory();

    for (Entry<String, TableModel> entryDataModel : hmTableModel.entrySet()) {
      String queryKey = entryDataModel.getKey();
      TableModel tableModel = entryDataModel.getValue();

      String[] queryKeys = queryKey.split(SEPARATOR_KEY);
      String actionType = queryKeys[1];
      String elementType = queryKeys[2];

      if (elementType.equals(DATOS_SOLICITANTE_TYPE)) {
        parseSubReportDatosSolicitante(report, queryKey, tableModel);
      } else if (actionType.equals(TITULO)) {
        parseSubReportTitulo(report, queryKey, tableModel);
      } else if (actionType.equals(SUBREPORT_TYPE)) {
        String elementKey = queryKeys[3];
        if (elementType.equals(SgiFormlyService.TABLE_CRUD_TYPE)) {
          generateSubreportTableCrud(report, queryKey, tableModel, elementKey);
        }
      } else {
        dataFactory.addTable(queryKey, tableModel);
      }
    }
    report.setDataFactory(dataFactory);
  }

  private void parseSubReportTitulo(MasterReport report, String queryKey, TableModel tableModel) {
    SubReport subReport = (SubReport) report.getReportHeader().getElement(0);
    subReport.setQuery(queryKey);
    TableDataFactory dataFactorySubReport = new TableDataFactory();
    dataFactorySubReport.addTable(queryKey, tableModel);
    subReport.setDataFactory(dataFactorySubReport);
  }

  private void parseSubReportDatosSolicitante(MasterReport report, String queryKey, TableModel tableModel) {
    for (SubReport subReport : report.getItemBand().getSubReports()) {
      if (subReport.getName().contains(DATOS_SOLICITANTE_TYPE)) {
        subReport.setQuery(queryKey);
        TableDataFactory dataFactorySubReport = new TableDataFactory();
        dataFactorySubReport.addTable(queryKey, tableModel);
        subReport.setDataFactory(dataFactorySubReport);
      }
    }
  }

  /**
   * Devuelve un informe pdf del formulario M10, M20, M30, Seguimiento anual,
   * final o retrospectiva
   *
   * @param idMemoria    Id de la memoria
   * @param idFormulario Id del formulario
   * @return MXXReportOutput Datos a presentar en el informe
   */
  private MXXReportOutput getMXX(Long idMemoria, Long idFormulario) {
    log.debug("getMXX(idMemoria, idFormulario)- start");

    MXXReportOutput mxxReportOutput = new MXXReportOutput();
    mxxReportOutput.setBloques(new ArrayList<>());

    try {

      MemoriaDto memoria = memoriaService.findById(idMemoria);
      Long idPeticionEvaluacion = memoria.getPeticionEvaluacion().getId();

      List<MemoriaPeticionEvaluacionDto> memorias = peticionEvaluacionService
          .findMemoriaByPeticionEvaluacionMaxVersion(idPeticionEvaluacion);
      mxxReportOutput.setMemoria(memoria);

      getDataBloque0MXX(memoria, memorias, mxxReportOutput);

      // @formatter:off
      BloquesReportInput etiBloquesReportInput = BloquesReportInput.builder()
        .idMemoria(idMemoria)
        .idFormulario(idFormulario)
        .mostrarRespuestas(true)
        .mostrarContenidoApartado(true)
        .build();
      // @formatter:on

      BloquesReportOutput reportOutput = getDataFromApartadosAndRespuestas(etiBloquesReportInput);

      final int orden = mxxReportOutput.getBloques().size();
      for (BloqueOutput bloque : reportOutput.getBloques()) {
        bloque.setOrden(bloque.getOrden() + orden);
      }

      mxxReportOutput.getBloques().addAll(reportOutput.getBloques());

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return mxxReportOutput;
  }

  /**
   * Obtenemos los datos relacionados ccon la petición de evaluación
   * 
   * @param memoria         Memoria
   * @param memorias        List<MemoriaPeticionEvaluacion>
   * @param mxxReportOutput
   */
  private void getDataBloque0MXX(MemoriaDto memoria, List<MemoriaPeticionEvaluacionDto> memorias,
      MXXReportOutput mxxReportOutput) {
    PeticionEvaluacionDto peticionEvaluacion = memoria.getPeticionEvaluacion();

    int bloqueIndex = mxxReportOutput.getBloques().size() + 1;
    // @formatter:off
    BloqueOutput bloqueOutput = BloqueOutput.builder()
      .id(Long.valueOf(bloqueIndex))
      .nombre(NO_REF_CEID + memoria.getNumReferencia())
      .orden(bloqueIndex)
      .apartados(new ArrayList<>())
      .build();
    // @formatter:on

    getApartadoDatosSolicitante(bloqueOutput, peticionEvaluacion.getPersonaRef());
    getApartadoTituloProyecto(bloqueOutput, peticionEvaluacion.getTitulo());
    getApartadoTipoActividad(bloqueOutput, peticionEvaluacion.getTipoActividad().getNombre());
    getApartadoFinanciacion(bloqueOutput, peticionEvaluacion);
    getApartadoFechasClave(bloqueOutput, peticionEvaluacion);
    getApartadoResumen(bloqueOutput, peticionEvaluacion.getResumen());
    getApartadoMemorias(bloqueOutput, memorias);

    mxxReportOutput.getBloques().add(bloqueOutput);
  }

  private void getApartadoDatosSolicitante(BloqueOutput bloqueOutput, String personaRef) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;
    String apartadoTitle = "mxx.datosSolicitante";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    // @formatter:off
    ElementOutput dataSolicitanteElement = ElementOutput.builder()
      .nombre("")
      .tipo(DATOS_SOLICITANTE_TYPE)
      .content(StringUtils.hasText(personaRef) ? personaRef: "")
      .build();
    // @formatter:on

    apartadoOutput.getElementos().add(dataSolicitanteElement);

    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoTituloProyecto(BloqueOutput bloqueOutput, String titulo) {

    int apartadoIndex = bloqueOutput.getApartados().size() + 1;
    String apartadoTitle = "mxx.tituloProyecto";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    String question = "";
    String answer = null != titulo ? titulo : "";
    ElementOutput tituloElementOutput = generateTemplateElementOutput(question, answer);

    apartadoOutput.getElementos().add(tituloElementOutput);
    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoTipoActividad(BloqueOutput bloqueOutput, String tipoActividad) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;
    String apartadoTitle = "mxx.tipoActividad";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    String question = "";
    String answer = null != tipoActividad ? tipoActividad : "";
    ElementOutput tipoActividadElementOutput = generateTemplateElementOutput(question, answer);

    apartadoOutput.getElementos().add(tipoActividadElementOutput);
    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoFinanciacion(BloqueOutput bloqueOutput, PeticionEvaluacionDto peticionEvaluacion) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;

    String apartadoTitle = "mxx.financiacion";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    String question = "¿Se dispone de financiación para la realización del proyecto?: ";
    String disponeFinanciacion = Boolean.TRUE.equals(peticionEvaluacion.getExisteFinanciacion()) ? "Sí" : "No";
    ElementOutput disponeFinanciacionElementOutput = generateTemplateElementOutput(question, disponeFinanciacion);
    apartadoOutput.getElementos().add(disponeFinanciacionElementOutput);

    question = "Indicar la fuente de financiación: ";
    String fuenteFinanciacion = null != peticionEvaluacion.getFuenteFinanciacion()
        ? peticionEvaluacion.getFuenteFinanciacion()
        : "";
    ElementOutput fuenteFinanciacionElementOutput = generateTemplateElementOutput(question, fuenteFinanciacion);
    apartadoOutput.getElementos().add(fuenteFinanciacionElementOutput);

    question = "Estado de la financiación: ";
    String estadoFinanciacion = null != peticionEvaluacion.getEstadoFinanciacion()
        ? EstadoFinanciacionI18n.getI18nMessageFromValorSocialEnum(peticionEvaluacion.getEstadoFinanciacion().name())
        : "";
    ElementOutput estadoElementOutput = generateTemplateElementOutput(question, estadoFinanciacion);
    apartadoOutput.getElementos().add(estadoElementOutput);

    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoFechasClave(BloqueOutput bloqueOutput, PeticionEvaluacionDto peticionEvaluacion) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;

    String apartadoTitle = "mxx.fechasProyecto";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    String question = "Fecha prevista inicio del proyecto: ";
    String fechaInicio = formatInstantToString(peticionEvaluacion.getFechaInicio());
    ElementOutput fechaInicioElementOutput = generateTemplateElementOutput(question, fechaInicio);
    apartadoOutput.getElementos().add(fechaInicioElementOutput);

    question = "Fecha prevista fin del proyecto: ";
    String fechaFin = formatInstantToString(peticionEvaluacion.getFechaFin());
    ElementOutput fechaFinElementOutput = generateTemplateElementOutput(question, fechaFin);
    apartadoOutput.getElementos().add(fechaFinElementOutput);

    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoResumen(BloqueOutput bloqueOutput, String resumen) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;
    String apartadoTitle = "mxx.resumenProyecto";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    String question = "";
    String answer = null != resumen ? resumen : "";
    ElementOutput tipoActividadElementOutput = generateTemplateElementOutput(question, answer);

    apartadoOutput.getElementos().add(tipoActividadElementOutput);
    bloqueOutput.getApartados().add(apartadoOutput);
  }

  private void getApartadoMemorias(BloqueOutput bloqueOutput, List<MemoriaPeticionEvaluacionDto> memorias) {
    int apartadoIndex = bloqueOutput.getApartados().size() + 1;
    String apartadoTitle = "mxx.memoriasProyecto";
    ApartadoOutput apartadoOutput = generateApartadoOutputBasic(apartadoIndex, apartadoTitle);

    List<List<ElementOutput>> elementsTableCrud = new ArrayList<>();

    memorias.forEach(memoriaPeticionEvaluacion -> {
      List<ElementOutput> rowElementsTableCrud = new ArrayList<>();
      // @formatter:off
      ElementOutput elementOutputTableCrud = ElementOutput.builder()
        .nombre("Comité")
        .content(memoriaPeticionEvaluacion.getComite().getComite())
        .build();
      rowElementsTableCrud.add(elementOutputTableCrud);

      elementOutputTableCrud = ElementOutput.builder()
        .nombre("NoRefCEID")
        .content(memoriaPeticionEvaluacion.getNumReferencia())
        .build();
      rowElementsTableCrud.add(elementOutputTableCrud);

      elementOutputTableCrud = ElementOutput.builder()
        .nombre("Nombre")
        .content(memoriaPeticionEvaluacion.getTitulo())
        .build();
      rowElementsTableCrud.add(elementOutputTableCrud);

      elementOutputTableCrud = ElementOutput.builder()
        .nombre("Estado")
        .content(memoriaPeticionEvaluacion.getEstadoActual().getNombre())
        .build();
      // @formatter:on
      rowElementsTableCrud.add(elementOutputTableCrud);

      elementsTableCrud.add(rowElementsTableCrud);
    });

    String contentTableCrud = "";

    if (!elementsTableCrud.isEmpty()) {
      try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, elementsTableCrud);
        contentTableCrud = new String(out.toByteArray());
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }

    // @formatter:off
    ElementOutput elementOutput = ElementOutput.builder()
      .nombre("memoriasProyecto")
      .tipo(SgiFormlyService.TABLE_CRUD_TYPE)
      .content(contentTableCrud)
      .build();
    // @formatter:on

    apartadoOutput.getElementos().add(elementOutput);
    bloqueOutput.getApartados().add(apartadoOutput);
  }

  /**
   * Genera un tableModel en función del tipo de elemento que le pasemos
   * 
   * @param hmTableModel Map con el nombre de la query y el TableModel
   * @param elemento     ElementOutput
   */
  protected void parseElementTypeFromTableModel(Map<String, TableModel> hmTableModel, ElementOutput elemento) {
    try {
      if (elemento.getTipo().equals(DATOS_SOLICITANTE_TYPE)) {

        DefaultTableModel tableModelDatosSolicitante = new DefaultTableModel();

        Vector<Object> columnsDataSolicitante = new Vector<>();
        Vector<Vector<Object>> rowsDataSolicitante = new Vector<>();
        Vector<Object> elementsRow = new Vector<>();

        String personaRef = elemento.getContent();

        columnsDataSolicitante.add("nombre");
        columnsDataSolicitante.add("telefono");
        columnsDataSolicitante.add("email");
        columnsDataSolicitante.add("departamento");
        columnsDataSolicitante.add("area");
        getDatosPersona(elementsRow, personaRef);

        rowsDataSolicitante.add(elementsRow);

        tableModelDatosSolicitante.setDataVector(rowsDataSolicitante, columnsDataSolicitante);
        hmTableModel.put(QUERY_TYPE + SEPARATOR_KEY + BAND_TYPE + SEPARATOR_KEY + DATOS_SOLICITANTE_TYPE,
            tableModelDatosSolicitante);
      } else if (elemento.getTipo().equals(SgiFormlyService.TABLE_CRUD_TYPE)) {

        generateKeyTableModelFromTableCrud(hmTableModel, elemento);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  private void getDatosPersona(Vector<Object> elementsRow, String personaRef) {
    try {
      PersonaDto persona = personaService.findById(personaRef);
      if (null != persona) {
        persona.setDatosContacto(personaService.findDatosContactoByPersonaId(personaRef));
        persona.setVinculacion(personaService.findVinculacionByPersonaId(personaRef));
      }

      String telefono = "";
      if (null != persona.getDatosContacto() && null != persona.getDatosContacto().getTelefonos()
          && !persona.getDatosContacto().getTelefonos().isEmpty()) {
        telefono = persona.getDatosContacto().getTelefonos().get(0);
      }
      String email = "";
      if (null != persona.getDatosContacto() && null != persona.getDatosContacto().getEmails()
          && !persona.getDatosContacto().getEmails().isEmpty()) {
        email = persona.getDatosContacto().getEmails().stream()
            .filter(e -> null != e.getPrincipal() && e.getPrincipal().equals(Boolean.TRUE)).findFirst()
            .orElse(new EmailDto())
            .getEmail();
      }

      String departamento = "";
      if (null != persona.getVinculacion() && null != persona.getVinculacion().getDepartamento()) {
        departamento = ObjectUtils.defaultIfNull(persona.getVinculacion().getDepartamento().getNombre(), "");
      }

      String area = "";
      if (null != persona.getVinculacion() && null != persona.getVinculacion().getAreaConocimiento()) {
        area = ObjectUtils.defaultIfNull(persona.getVinculacion().getAreaConocimiento().getNombre(), "");
      }

      elementsRow.add(persona.getNombre());
      elementsRow.add(telefono);
      elementsRow.add(email);
      elementsRow.add(departamento);
      elementsRow.add(area);
    } catch (Exception e) {
      String errorMessage = getErrorMessageToReport(e);
      for (int i = 0; i < 6; i++) {
        elementsRow.add(errorMessage);
      }
    }
  }

  /** Estado financiacion */
  public enum EstadoFinanciacionI18n {
    /** Solicitado */
    SOLICITADO("enum.estado-financiacion.SOLICITADO"),
    /** Concedido */
    CONCEDIDO("enum.estado-financiacion.CONCEDIDO"),
    /** Denegado */
    DENEGADO("enum.estado-financiacion.DENEGADO");

    private final String i18nMessage;

    private EstadoFinanciacionI18n(String i18nMessage) {
      this.i18nMessage = i18nMessage;
    }

    public String getI18nMessage() {
      return this.i18nMessage;
    }

    public static String getI18nMessageFromValorSocialEnum(final String estadoFinanciacion) {
      String message = "";
      if (StringUtils.hasText(estadoFinanciacion)) {
        EstadoFinanciacionI18n estadoFinanciacionBusq = Stream.of(EstadoFinanciacionI18n.values())
            .filter(tvs -> estadoFinanciacion.equals(tvs.name())).findFirst().orElse(null);
        if (null != estadoFinanciacionBusq) {
          message = ApplicationContextSupport.getMessage(estadoFinanciacionBusq.i18nMessage);
        }
      }
      return message;
    }
  }
}