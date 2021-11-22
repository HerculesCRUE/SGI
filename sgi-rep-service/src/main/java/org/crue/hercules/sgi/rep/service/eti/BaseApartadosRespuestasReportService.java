package org.crue.hercules.sgi.rep.service.eti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.validation.Valid;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiColumReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.TypeColumnReportEnum;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoDto;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloqueDto;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportInput;
import org.crue.hercules.sgi.rep.dto.eti.BloquesReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ComentarioDto;
import org.crue.hercules.sgi.rep.dto.eti.ElementOutput;
import org.crue.hercules.sgi.rep.dto.eti.RespuestaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiDynamicReportService;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio base de generación de informes relacionado con apartados y
 * respuestas de ética
 */
@Service
@Slf4j
public abstract class BaseApartadosRespuestasReportService extends SgiDynamicReportService {

  private final BloqueService bloqueService;
  private final ApartadoService apartadoService;
  private final SgiFormlyService sgiFormlyService;
  private final RespuestaService respuestaService;

  // @formatter:off
  protected static final String[] COLUMNS_TABLE_MODEL = new String[] { 
    "bloque_id", "bloque_nombre", "bloque_orden", 
    "apartado_id", "apartado_nombre", "apartado_orden", 
    "apartado_hijo_padre_id", "apartado_hijo_id", "apartado_hijo_nombre", "apartado_hijo_orden",
    "apartado_nieto_padre_id", "apartado_nieto_id", "apartado_nieto_nombre", "apartado_nieto_orden",
    "apartado_bisnieto_padre_id", "apartado_bisnieto_id", "apartado_bisnieto_nombre", "apartado_bisnieto_orden",
    "apartado_tataranieto_padre_id", "apartado_tataranieto_id", "apartado_tataranieto_nombre", "apartado_tataranieto_orden",
    COMPONENT_ID, COMPONENT_TYPE, "content", "content_orden" };
  // @formatter:on

  protected BaseApartadosRespuestasReportService(SgiConfigProperties sgiConfigProperties, BloqueService bloqueService,
      ApartadoService apartadoService, SgiFormlyService sgiFormlyService, RespuestaService respuestaService) {
    super(sgiConfigProperties);
    this.bloqueService = bloqueService;
    this.apartadoService = apartadoService;
    this.sgiFormlyService = sgiFormlyService;
    this.respuestaService = respuestaService;
  }

  protected ApartadoService getApartadoService() {
    return this.apartadoService;
  }

  /**
   * Obtenemos los datos relacionados con los apartados y/o sus correspondientes
   * respuestas y/o comentarios
   * 
   * @param input BloquesReportInput
   * @return BloquesReportOutput
   */
  protected BloquesReportOutput getDataFromApartadosAndRespuestas(@Valid BloquesReportInput input) {
    log.debug("getDataFromApartadosAndRespuestas(EtiBloquesReportInput) - start");

    BloquesReportOutput bloquesReportOutput = new BloquesReportOutput();
    bloquesReportOutput.setBloques(new ArrayList<>());

    try {
      List<BloqueDto> bloques = bloqueService.findByFormularioId(input.getIdFormulario());

      final int tamBloques = bloquesReportOutput.getBloques().size();

      for (BloqueDto bloque : bloques) {

        boolean parseBloque = true;
        if (null != input.getBloques()) {
          parseBloque = input.getBloques().contains(bloque.getId());
        }

        if (parseBloque) {
          parseBloque(input, bloquesReportOutput, bloque, tamBloques);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new GetDataReportException();
    }
    return bloquesReportOutput;
  }

  private void parseBloque(BloquesReportInput input, BloquesReportOutput bloquesReportOutput, BloqueDto bloque,
      int tamBloques) {
    // @formatter:off
    BloqueOutput bloqueOutput = BloqueOutput.builder()
      .id(bloque.getId())
      .nombre(bloque.getOrden() + ". " + bloque.getNombre())
      .orden(tamBloques + bloque.getOrden())
      .apartados(new ArrayList<>())
      .build();
    // @formatter:on
    List<ApartadoDto> apartados = apartadoService.findByBloqueId(bloque.getId());

    for (ApartadoDto apartado : apartados) {
      boolean parseApartado = true;
      if (null != input.getApartados()) {
        parseApartado = input.getApartados().contains(apartado.getId());
      }

      if (parseApartado) {
        ApartadoOutput apartadoOutput = parseApartadoAndHijos(input, apartado);
        bloqueOutput.getApartados().add(apartadoOutput);
      }
    }

    if (null != bloqueOutput.getApartados() && !bloqueOutput.getApartados().isEmpty()) {
      bloquesReportOutput.getBloques().add(bloqueOutput);
    }
  }

  private ApartadoOutput parseApartadoAndHijos(BloquesReportInput input, ApartadoDto apartado) {
    ApartadoOutput apartadoOutput = parseApartadoOutput(input, apartado);
    apartadoOutput.setApartadosHijos(findApartadosHijosAndRespuestas(input, apartado.getId()));
    return apartadoOutput;
  }

  protected List<ApartadoOutput> findApartadosHijosAndRespuestas(BloquesReportInput input, Long idPadre) {
    List<ApartadoOutput> apartadosHijosResult = new ArrayList<>();

    List<ApartadoDto> apartados = apartadoService.findByPadreId(idPadre);

    if (CollectionUtils.isNotEmpty(apartados)) {
      for (ApartadoDto apartado : apartados) {
        boolean parseApartado = true;
        if (null != input.getApartados()) {
          parseApartado = input.getApartados().contains(apartado.getId());
        }

        if (parseApartado) {
          ApartadoOutput apartadoOutput = parseApartadoAndHijos(input, apartado);
          apartadosHijosResult.add(apartadoOutput);
        }
      }
    }
    return apartadosHijosResult;
  }

  protected ApartadoOutput parseApartadoOutput(BloquesReportInput input, ApartadoDto apartado) {
    ApartadoOutput apartadoOutput = null;

    List<ComentarioDto> comentarios = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(input.getComentarios())) {
      comentarios = input.getComentarios().stream()
          .filter(c -> c.getApartado().getId().compareTo(apartado.getId()) == 0).collect(Collectors.toList());
    }

    RespuestaDto respuestaDto = null;

    if (null != input.getMostrarRespuestas() && input.getMostrarRespuestas()) {
      respuestaDto = respuestaService.findByMemoriaIdAndApartadoId(input.getIdMemoria(), apartado.getId());
    }

    // @formatter:off
    apartadoOutput = ApartadoOutput.builder()
      .id(apartado.getId())
      .nombre(apartado.getNombre())
      .orden(apartado.getOrden())
      .esquema(apartado.getEsquema())
      .respuesta(respuestaDto)
      .comentarios(comentarios)
      .mostrarContenidoApartado(input.getMostrarContenidoApartado())
      .build();
    // @formatter:on

    sgiFormlyService.parseApartadoAndRespuestaAndComentarios(apartadoOutput);

    return apartadoOutput;
  }

  /**
   * Genera el tableModel a partir de un EtiReportOutput
   *
   * @param bloquesReportOutput BloquesReportOutput
   * @return Map con el nombre de la query y el TableModel
   */
  protected Map<String, TableModel> generateTableModelFromReportOutput(BloquesReportOutput bloquesReportOutput) {
    Map<String, TableModel> hmTableModel = new HashMap<>();

    final DefaultTableModel tableModelGeneral = new DefaultTableModel();

    tableModelGeneral.setColumnIdentifiers(COLUMNS_TABLE_MODEL);

    List<ApartadoOutput> apartados = new ArrayList<>();
    for (BloqueOutput bloque : bloquesReportOutput.getBloques()) {
      for (ApartadoOutput apartado : bloque.getApartados()) {
        apartados.clear();
        apartados.add(apartado);
        generateElementTableModelApartado(hmTableModel, tableModelGeneral, bloque, apartados);

        parseApartadoHijoElementTableModel(hmTableModel, tableModelGeneral, bloque, apartados);
      }
    }

    hmTableModel.put(QUERY_TYPE + SEPARATOR_KEY + "general" + SEPARATOR_KEY + "bloques", tableModelGeneral);

    return hmTableModel;
  }

  private void parseApartadoHijoElementTableModel(Map<String, TableModel> hmTableModel,
      final DefaultTableModel tableModelGeneral, BloqueOutput bloque, List<ApartadoOutput> jerarquiaApartados) {
    ApartadoOutput apartado = jerarquiaApartados.get(jerarquiaApartados.size() - 1);
    if (CollectionUtils.isNotEmpty(apartado.getApartadosHijos())) {
      for (ApartadoOutput apartadoHijo : apartado.getApartadosHijos()) {
        List<ApartadoOutput> jerarquiaApartadosHijos = new ArrayList<>();
        jerarquiaApartadosHijos.addAll(jerarquiaApartados);
        jerarquiaApartadosHijos.add(apartadoHijo);
        generateElementTableModelApartado(hmTableModel, tableModelGeneral, bloque, jerarquiaApartadosHijos);
        parseApartadoHijoElementTableModel(hmTableModel, tableModelGeneral, bloque, jerarquiaApartadosHijos);
      }
    }
  }

  private void generateElementTableModelApartado(Map<String, TableModel> hmTableModel,
      final DefaultTableModel tableModelGeneral, BloqueOutput bloque, List<ApartadoOutput> jerarquiaApartados) {
    ApartadoOutput apartado = jerarquiaApartados.get(jerarquiaApartados.size() - 1);
    if (CollectionUtils.isNotEmpty(apartado.getElementos())) {
      for (int i = 0; i < apartado.getElementos().size(); i++) {
        ElementOutput elemento = apartado.getElementos().get(i);

        parseElementTypeFromTableModel(hmTableModel, elemento);
        tableModelGeneral.addRow(generateElementRow(bloque, jerarquiaApartados, elemento, i));
      }
    } else {
      ElementOutput elementoVacio = ElementOutput.builder().content("").tipo(EMPTY_TYPE).nombre("").build();
      tableModelGeneral.addRow(generateElementRow(bloque, jerarquiaApartados, elementoVacio, 0));
    }
  }

  protected Object[] generateElementRow(BloqueOutput bloque, List<ApartadoOutput> jerarquiaApartados,
      ElementOutput elemento, int rowIndex) {
    int i = 0;
    ApartadoOutput apartado = getApartadoOutputFromElementRow(i++, jerarquiaApartados);
    ApartadoOutput apartadoHijo = getApartadoOutputFromElementRow(i++, jerarquiaApartados);
    ApartadoOutput apartadoNieto = getApartadoOutputFromElementRow(i++, jerarquiaApartados);
    ApartadoOutput apartadoBisnieto = getApartadoOutputFromElementRow(i++, jerarquiaApartados);
    ApartadoOutput apartadoTataraNieto = getApartadoOutputFromElementRow(i, jerarquiaApartados);

    // @formatter:off
    return new Object[] { 
      bloque.getId(), bloque.getNombre(), bloque.getOrden(), 
      apartado.getId(), apartado.getTitulo(), apartado.getOrden(), 
      apartado.getId(), apartadoHijo.getId(), apartadoHijo.getTitulo(), apartadoHijo.getOrden(),
      apartadoNieto.getId(), apartadoNieto.getId(), apartadoNieto.getTitulo(), apartadoNieto.getOrden(),
      apartadoBisnieto.getId(), apartadoBisnieto.getId(), apartadoBisnieto.getTitulo(), apartadoBisnieto.getOrden(), 
      apartadoTataraNieto.getId(), apartadoTataraNieto.getId(), apartadoTataraNieto.getTitulo(),apartadoTataraNieto.getOrden(), 
      elemento.getNombre(), elemento.getTipo(), elemento.getContent(), rowIndex
    };
    // @formatter:on  
  }

  private ApartadoOutput getApartadoOutputFromElementRow(int index, List<ApartadoOutput> jerarquiaApartados) {
    ApartadoOutput apartadoOutput = null;
    if (jerarquiaApartados.size() >= index + 1 && null != jerarquiaApartados.get(index)) {
      apartadoOutput = jerarquiaApartados.get(index);
      if (null == apartadoOutput.getOrden()) {
        apartadoOutput.setOrden(0);
      }
      if (null == apartadoOutput.getId()) {
        apartadoOutput.setId(0L);
      }
      if (null == apartadoOutput.getTitulo()) {
        apartadoOutput.setTitulo("");
      }
    } else {
      apartadoOutput = ApartadoOutput.builder().id(0L).titulo("").orden(0).build();
    }

    return apartadoOutput;
  }

  protected void generateKeyTableModelFromTableCrud(Map<String, TableModel> hmTableModel, ElementOutput elemento) {
    elemento.setNombre(elemento.getNombre() + "-" + RandomStringUtils.randomAlphanumeric(3));

    DefaultTableModel tableCrudTableModel = parseTableCrudTableModel(elemento);
    String keyTableModel = QUERY_TYPE + SEPARATOR_KEY + SUBREPORT_TYPE + SEPARATOR_KEY
        + SgiFormlyService.TABLE_CRUD_TYPE + SEPARATOR_KEY + elemento.getNombre();
    hmTableModel.put(keyTableModel, tableCrudTableModel);
  }

  /**
   * Obtiene el DefaultTableModel de un campo de tipo table-crud
   * 
   * @param elemento ElementOutput
   * @return DefaultTableModel
   */
  protected DefaultTableModel parseTableCrudTableModel(ElementOutput elemento) {
    DefaultTableModel tableModel = new DefaultTableModel();
    try {
      ObjectMapper mapper = new ObjectMapper();
      List<List<ElementOutput>> elementsTableCrud = mapper.readValue(elemento.getContent(),
          new TypeReference<List<List<ElementOutput>>>() {
          });

      Vector<Object> columns = new Vector<>();
      Vector<Vector<Object>> elements = new Vector<>();
      for (int i = 0; i < elementsTableCrud.size(); i++) {
        List<ElementOutput> rowElementTableCrud = elementsTableCrud.get(i);

        Vector<Object> elementsRow = new Vector<>();
        for (ElementOutput elementTableCrud : rowElementTableCrud) {
          if (i == 0) {
            String columnName = null != elementTableCrud.getNombre() ? elementTableCrud.getNombre() : "";
            columns.add(columnName);
          }
          String content = null != elementTableCrud.getContent() ? elementTableCrud.getContent() : "";
          elementsRow.add(content);
        }
        elements.add(elementsRow);
      }

      tableModel.setDataVector(elements, columns);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return tableModel;
  }

  protected ApartadoOutput generateApartadoOutputBasic(int apartadoIndex, String apartadoTitle) {
    // @formatter:off
    return ApartadoOutput.builder()
      .id(Long.valueOf(apartadoIndex))
      .nombre(apartadoTitle)
      .titulo(apartadoTitle)
      .orden(apartadoIndex)
      .elementos(new ArrayList<>())
      .apartadosHijos(new ArrayList<>())
      .build();
    // @formatter:on
  }

  protected ElementOutput generateTemplateElementOutput(String question, String answer) {
    // @formatter:off
    return ElementOutput.builder()
      .nombre("")
      .tipo(SgiFormlyService.TEMPLATE_PROPERTY)
      .content(SgiFormlyService.P_HTML + question + SgiFormlyService.I_HTML + answer + SgiFormlyService.I_CLOSE_HTML +  SgiFormlyService.P_CLOSE_HTML )
      .build();
    // @formatter:on
  }

  protected void generateSubreportTableCrud(MasterReport report, String queryKey, TableModel tableModel,
      String elementKey) {

    Map<String, TableModel> hmTableModel = new HashMap<>();
    hmTableModel.put(NAME_GENERAL_TABLE_MODEL, tableModel);

    SgiDynamicReportDto sgiReportDto = SgiDynamicReportDto.builder().dataModel(hmTableModel).customWidth(WIDTH_PORTRAIT)
        .columns(new ArrayList<>()).build();

    for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
      String name = tableModel.getColumnName(columnIndex);
      sgiReportDto.getColumns()
          .add(SgiColumReportDto.builder().type(TypeColumnReportEnum.STRING).name(name).title(name).build());
    }

    SubReport subReportTableCrud = getSubreportTableCrud(sgiReportDto);

    FormulaExpression formulaExpression = new FormulaExpression();
    formulaExpression.setName("expression_" + elementKey);
    formulaExpression.setFormula("=IF(AND([" + COMPONENT_ID + "]=\"" + elementKey + "\";[" + COMPONENT_TYPE + "]=\""
        + SgiFormlyService.TABLE_CRUD_TYPE + "\");\"true\";\"false\")");
    subReportTableCrud.setStyleExpression(ElementStyleKeys.VISIBLE, formulaExpression);

    TableDataFactory dataFactorySubReportTableCrud = new TableDataFactory();
    dataFactorySubReportTableCrud.addTable(queryKey, tableModel);
    subReportTableCrud.setDataFactory(dataFactorySubReportTableCrud);

    subReportTableCrud.setQuery(queryKey);
    report.getItemBand().addSubReport(subReportTableCrud);
  }

  /**
   * Genera un tableModel en función del tipo de elemento que le pasemos
   * 
   * @param hmTableModel Map con el nombre de la query y el TableModel
   * @param elemento     ElementOutput
   */
  abstract void parseElementTypeFromTableModel(Map<String, TableModel> hmTableModel, ElementOutput elemento);
}