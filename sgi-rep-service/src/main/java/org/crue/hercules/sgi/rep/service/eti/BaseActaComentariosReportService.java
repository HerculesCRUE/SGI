package org.crue.hercules.sgi.rep.service.eti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.commons.collections4.CollectionUtils;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.eti.ActaComentariosMemoriaReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ActaComentariosReportOutput;
import org.crue.hercules.sgi.rep.dto.eti.ApartadoOutput;
import org.crue.hercules.sgi.rep.dto.eti.BloqueOutput;
import org.crue.hercules.sgi.rep.dto.eti.ElementOutput;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Validated
public class BaseActaComentariosReportService extends BaseApartadosRespuestasReportService {

  // @formatter:off
  protected static final String[] COLUMNS_TABLE_MODEL_ACTA = new String[] { 
    "num_referencia_memoria", "num_comentarios",
    "bloque_id", "bloque_nombre", "bloque_orden", 
    "apartado_id", "apartado_nombre", "apartado_orden", 
    "apartado_hijo_padre_id", "apartado_hijo_id", "apartado_hijo_nombre", "apartado_hijo_orden",
    "apartado_nieto_padre_id", "apartado_nieto_id", "apartado_nieto_nombre", "apartado_nieto_orden",
    "apartado_bisnieto_padre_id", "apartado_bisnieto_id", "apartado_bisnieto_nombre", "apartado_bisnieto_orden",
    "apartado_tataranieto_padre_id", "apartado_tataranieto_id", "apartado_tataranieto_nombre", "apartado_tataranieto_orden",
    COMPONENT_ID, COMPONENT_TYPE, "content", "content_orden" };
  // @formatter:on

  private final BloqueService bloqueService;
  private final ApartadoService apartadoService;
  private final SgiFormlyService sgiFormlyService;
  private final RespuestaService respuestaService;

  protected BaseActaComentariosReportService(SgiConfigProperties sgiConfigProperties,
      BloqueService bloqueService, ApartadoService apartadoService, SgiFormlyService sgiFormlyService,
      RespuestaService respuestaService) {

    super(sgiConfigProperties, bloqueService, apartadoService, sgiFormlyService, respuestaService);

    this.bloqueService = bloqueService;
    this.apartadoService = apartadoService;
    this.sgiFormlyService = sgiFormlyService;
    this.respuestaService = respuestaService;
  }

  protected Map<String, TableModel> generateTableModelFromReportOutput(ActaComentariosReportOutput actaComentarios) {
    Map<String, TableModel> hmTableModel = new HashMap<>();

    final DefaultTableModel tableModelGeneral = new DefaultTableModel();

    tableModelGeneral.setColumnIdentifiers(COLUMNS_TABLE_MODEL_ACTA);

    List<ApartadoOutput> apartados = new ArrayList<>();
    for (ActaComentariosMemoriaReportOutput comentariosMemoria : actaComentarios.getComentariosMemoria()) {
      for (BloqueOutput bloque : comentariosMemoria.getBloques()) {
        for (ApartadoOutput apartado : bloque.getApartados()) {
          apartados.clear();
          apartados.add(apartado);
          generateElementTableModelApartado(hmTableModel, tableModelGeneral, bloque, apartados,
              comentariosMemoria.getNumReferenciaMemoria(), comentariosMemoria.getNumComentarios());

          parseApartadoHijoElementTableModel(hmTableModel, tableModelGeneral, bloque, apartados,
              comentariosMemoria.getNumReferenciaMemoria(), comentariosMemoria.getNumComentarios());
        }
      }
    }

    hmTableModel.put(QUERY_TYPE + SEPARATOR_KEY + "general" + SEPARATOR_KEY + "bloques", tableModelGeneral);

    return hmTableModel;
  }

  private void parseApartadoHijoElementTableModel(Map<String, TableModel> hmTableModel,
      final DefaultTableModel tableModelGeneral, BloqueOutput bloque, List<ApartadoOutput> jerarquiaApartados,
      String numReferenciaMemoria, Integer numComentarios) {
    ApartadoOutput apartado = jerarquiaApartados.get(jerarquiaApartados.size() - 1);
    if (CollectionUtils.isNotEmpty(apartado.getApartadosHijos())) {
      for (ApartadoOutput apartadoHijo : apartado.getApartadosHijos()) {
        List<ApartadoOutput> jerarquiaApartadosHijos = new ArrayList<>();
        jerarquiaApartadosHijos.addAll(jerarquiaApartados);
        jerarquiaApartadosHijos.add(apartadoHijo);
        generateElementTableModelApartado(hmTableModel, tableModelGeneral, bloque, jerarquiaApartadosHijos,
            numReferenciaMemoria, numComentarios);
        parseApartadoHijoElementTableModel(hmTableModel, tableModelGeneral, bloque, jerarquiaApartadosHijos,
            numReferenciaMemoria, numComentarios);
      }
    }
  }

  private void generateElementTableModelApartado(Map<String, TableModel> hmTableModel,
      final DefaultTableModel tableModelGeneral, BloqueOutput bloque, List<ApartadoOutput> jerarquiaApartados,
      String numReferenciaMemoria, Integer numComentarios) {
    ApartadoOutput apartado = jerarquiaApartados.get(jerarquiaApartados.size() - 1);
    if (CollectionUtils.isNotEmpty(apartado.getElementos())) {
      for (int i = 0; i < apartado.getElementos().size(); i++) {
        ElementOutput elemento = apartado.getElementos().get(i);

        parseElementTypeFromTableModel(hmTableModel, elemento);
        tableModelGeneral.addRow(generateElementRow(bloque, jerarquiaApartados, elemento, i, numReferenciaMemoria,
            numComentarios));
      }
    } else {
      ElementOutput elementoVacio = ElementOutput.builder().content("").tipo(EMPTY_TYPE).nombre("").build();
      tableModelGeneral.addRow(generateElementRow(bloque, jerarquiaApartados, elementoVacio, 0, numReferenciaMemoria,
          numComentarios));
    }
  }

  protected Object[] generateElementRow(BloqueOutput bloque, List<ApartadoOutput> jerarquiaApartados,
      ElementOutput elemento, int rowIndex, String numReferenciaMemoria, Integer numComentarios) {
    int i = 0;
    ApartadoOutput apartado = getApartadoOutputFromElementRow(i++, jerarquiaApartados);
    ApartadoOutput apartadoHijo = getApartadoOutputFromElementRow(i++, jerarquiaApartados);
    ApartadoOutput apartadoNieto = getApartadoOutputFromElementRow(i++, jerarquiaApartados);
    ApartadoOutput apartadoBisnieto = getApartadoOutputFromElementRow(i++, jerarquiaApartados);
    ApartadoOutput apartadoTataraNieto = getApartadoOutputFromElementRow(i, jerarquiaApartados);

    // @formatter:off
    return new Object[] { 
      numReferenciaMemoria, numComentarios,
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

  protected void parseElementTypeFromTableModel(Map<String, TableModel> hmTableModel, ElementOutput elemento) {
    try {
      if (elemento.getTipo().equals(SgiFormlyService.TABLE_CRUD_TYPE)) {

        generateKeyTableModelFromTableCrud(hmTableModel, elemento);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

}