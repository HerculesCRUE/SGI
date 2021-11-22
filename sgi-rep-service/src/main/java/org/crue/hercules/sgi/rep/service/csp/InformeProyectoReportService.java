package org.crue.hercules.sgi.rep.service.csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.crue.hercules.sgi.rep.config.SgiConfigProperties;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiColumReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiGroupReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.SgiRowReportDto;
import org.crue.hercules.sgi.rep.dto.SgiDynamicReportDto.TypeColumnReportEnum;
import org.crue.hercules.sgi.rep.dto.SgiReportDto.FieldOrientationType;
import org.crue.hercules.sgi.rep.dto.csp.ProyectoDto;
import org.crue.hercules.sgi.rep.dto.csp.ProyectoEquipoDto;
import org.crue.hercules.sgi.rep.dto.csp.ReportProyecto;
import org.crue.hercules.sgi.rep.dto.sgemp.EmpresaDto;
import org.crue.hercules.sgi.rep.exceptions.GetDataReportException;
import org.crue.hercules.sgi.rep.service.SgiDynamicReportService;
import org.crue.hercules.sgi.rep.service.sgemp.EmpresaService;
import org.crue.hercules.sgi.rep.service.sgp.PersonaService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de generación de informe de proyectos
 */
@Service
@Slf4j
public class InformeProyectoReportService extends SgiDynamicReportService {

  protected static final String NAME_DETAILS_TABLE_MODEL = "details";
  private static final String[] COLUMNS_NAME = new String[] { "Id#id#NUMBER", "Convocatoria#convocatoria#NUMBER",
      "Solicitud#solicitud#NUMBER", "Estado#estado#STRING", "Título#titulo#STRING", "Acrónimo#acronimo#STRING",
      "Cod. externo#codExterno#STRING", "Fecha inicio#fechaInicio#DATE#dd/MM/yy", "Fecha fin#fechaFin#DATE",
      "Fecha fin definitiva#fechaFinDefinitiva#DATE" };

  private ProyectoService proyectoService;
  private PersonaService personaService;
  private EmpresaService empresaService;

  public InformeProyectoReportService(SgiConfigProperties sgiConfigProperties, ProyectoService proyectoService,
      PersonaService personaService, EmpresaService empresaService) {
    super(sgiConfigProperties);
    this.proyectoService = proyectoService;
    this.personaService = personaService;
    this.empresaService = empresaService;
  }

  public SgiDynamicReportDto getReport(@Valid ReportProyecto reportProyecto) {
    log.debug("getReport(sgiReport) - start");

    SgiDynamicReportDto sgiDynamicReportDto = SgiDynamicReportDto.builder()
        .outputReportType(reportProyecto.getOutputReportType()).title("Listado de proyectos").build();

    try {

      initConfigurationSubReport(sgiDynamicReportDto);

      List<ProyectoDto> proyectos = getDataProyectos(reportProyecto);

      fillDynamicReportDto(sgiDynamicReportDto, proyectos, reportProyecto.getExportAsSubReport());

      sgiDynamicReportDto.setFieldOrientationType(FieldOrientationType.VERTICAL);
      sgiDynamicReportDto.setCustomWidth(WIDTH_PORTRAIT);
      sgiDynamicReportDto.setColumnMinWidth(120f);
      sgiDynamicReportDto.setGroupBy(SgiGroupReportDto.builder().name("titulo").visible(Boolean.TRUE).build());

      generateDynamicReport(sgiDynamicReportDto);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new GetDataReportException();
    }

    return sgiDynamicReportDto;
  }

  private void fillDynamicReportDto(SgiDynamicReportDto sgiDynamicReportDto, List<ProyectoDto> proyectos,
      boolean exportAsSubReport) {
    sgiDynamicReportDto.setFilters(new ArrayList<>());

    List<SgiColumReportDto> columns = getColumnsDynamicReportDto(proyectos, exportAsSubReport);
    sgiDynamicReportDto.setColumns(columns);

    List<SgiRowReportDto> rows = getRowsDynamicReportDto(proyectos, exportAsSubReport);
    sgiDynamicReportDto.setRows(rows);

    toJsonFile(sgiDynamicReportDto);
  }

  private List<SgiRowReportDto> getRowsDynamicReportDto(List<ProyectoDto> proyectos, boolean exportAsSubReport) {
    List<SgiRowReportDto> rows = new ArrayList<>();

    proyectos.forEach(proyecto -> {

      List<Object> elementsRow = new ArrayList<>();
      elementsRow.add(proyecto.getId());
      elementsRow.add(ObjectUtils.defaultIfNull(proyecto.getConvocatoriaId(), ""));
      elementsRow.add(ObjectUtils.defaultIfNull(proyecto.getSolicitudId(), ""));
      elementsRow.add(proyecto.getEstado().getEstado().name());
      elementsRow.add(ObjectUtils.defaultIfNull(proyecto.getTitulo(), ""));
      elementsRow.add(ObjectUtils.defaultIfNull(proyecto.getAcronimo(), ""));
      elementsRow.add(ObjectUtils.defaultIfNull(proyecto.getCodigoExterno(), ""));
      String fechaInicio = "";
      if (null != proyecto.getFechaInicio()) {
        fechaInicio = formatInstantToString(proyecto.getFechaInicio());
      }
      elementsRow.add(fechaInicio);
      String fechaFin = "";
      if (null != proyecto.getFechaFin()) {
        fechaFin = formatInstantToString(proyecto.getFechaFin());
      }
      elementsRow.add(fechaFin);
      String fechaFinDefinitiva = "";
      if (null != proyecto.getFechaFinDefinitiva()) {
        fechaFinDefinitiva = formatInstantToString(proyecto.getFechaFinDefinitiva());
      }
      elementsRow.add(fechaFinDefinitiva);

      getRowsTableModelEquipo(proyecto, elementsRow, exportAsSubReport);
      getRowsTableModelEntidadConvocante(proyecto, elementsRow, exportAsSubReport);
      getRowsTableModelEntidadFinanciadora(proyecto, elementsRow, exportAsSubReport);
      rows.add(SgiRowReportDto.builder().elements(elementsRow).build());
    });
    return rows;
  }

  private List<SgiColumReportDto> getColumnsDynamicReportDto(List<ProyectoDto> proyectos, boolean exportAsSubReport) {
    List<SgiColumReportDto> columns = initColumnsFromArray();

    if (exportAsSubReport) {
      getColumnsDynamicAsSubReport(columns);
    } else {
      getColumnsDynamicAsList(proyectos, columns);
    }
    return columns;
  }

  private void getColumnsDynamicAsSubReport(List<SgiColumReportDto> columns) {
    SgiColumReportDto investigador = SgiColumReportDto.builder().title("Investigador").name("investigador")
        .type(TypeColumnReportEnum.STRING).build();
    SgiColumReportDto equipo = SgiColumReportDto.builder().title("Equipo").name("equipo")
        .type(TypeColumnReportEnum.SUBREPORT).fieldOrientationType(FieldOrientationType.VERTICAL)
        .columns(Arrays.asList(investigador)).build();

    SgiColumReportDto entidadConvocante = SgiColumReportDto.builder().title("Razón social").name("razonSocial")
        .type(TypeColumnReportEnum.STRING).build();
    SgiColumReportDto cifEntidadConvocante = SgiColumReportDto.builder().title("CIF").name("cif")
        .type(TypeColumnReportEnum.STRING).build();
    SgiColumReportDto entidadesConvocantes = SgiColumReportDto.builder().title("Entidad convocante")
        .name("entidadConvocante").type(TypeColumnReportEnum.SUBREPORT)
        .fieldOrientationType(FieldOrientationType.VERTICAL)
        .columns(Arrays.asList(entidadConvocante, cifEntidadConvocante)).build();
    SgiColumReportDto entidadesFinanciadoras = SgiColumReportDto.builder().title("Entidad financiadora")
        .name("entidadFinanciadora").type(TypeColumnReportEnum.SUBREPORT)
        .fieldOrientationType(FieldOrientationType.VERTICAL)
        .columns(Arrays.asList(entidadConvocante, cifEntidadConvocante)).build();

    columns.add(equipo);
    columns.add(entidadesConvocantes);
    columns.add(entidadesFinanciadoras);
  }

  private void getColumnsDynamicAsList(List<ProyectoDto> proyectos, List<SgiColumReportDto> columns) {
    Integer maxNumEquipos = proyectos.stream().mapToInt(p -> p.getEquipo().size()).max().orElseGet(null);
    Integer maxNumEntidasConvocantes = proyectos.stream().mapToInt(p -> p.getEntidadesConvocantes().size()).max()
        .orElseGet(null);
    Integer maxNumEntidasFinanciadoras = proyectos.stream().mapToInt(p -> p.getEntidadesFinanciadoras().size()).max()
        .orElseGet(null);

    for (int i = 0; i < maxNumEquipos; i++) {
      columns.add(SgiColumReportDto.builder().title("Investigador " + (i + 1)).name("investigador" + (i + 1))
          .type(TypeColumnReportEnum.STRING).build());
    }
    for (int i = 0; i < maxNumEntidasConvocantes; i++) {
      columns.add(SgiColumReportDto.builder().title("Entidad convocante " + (i + 1)).name("entidadConvocante" + (i + 1))
          .type(TypeColumnReportEnum.STRING).build());
      columns.add(SgiColumReportDto.builder().title("Cif entidad convocante " + (i + 1))
          .name("cifEntidadConvocante" + (i + 1)).type(TypeColumnReportEnum.STRING).build());
    }
    for (int i = 0; i < maxNumEntidasFinanciadoras; i++) {
      columns.add(SgiColumReportDto.builder().title("Entidad financiadora " + (i + 1))
          .name("entidadFinanciadora" + (i + 1)).type(TypeColumnReportEnum.STRING).build());
      columns.add(SgiColumReportDto.builder().title("Cif entidad financiadora " + (i + 1))
          .name("cifEntidadFinanciadora" + (i + 1)).type(TypeColumnReportEnum.STRING).build());
    }
  }

  private List<SgiColumReportDto> initColumnsFromArray() {
    List<SgiColumReportDto> columns = new ArrayList<>();
    for (String columnName : COLUMNS_NAME) {
      String[] arrColumnName = columnName.split("#");
      String format = arrColumnName.length == 4 ? arrColumnName[3] : null;
      String title = arrColumnName[0];
      String name = arrColumnName[1];
      String type = arrColumnName[2];
      columns.add(SgiColumReportDto.builder().title(title).name(name).type(TypeColumnReportEnum.valueOf(type))
          .format(format).build());
    }
    return columns;
  }

  private List<ProyectoDto> getDataProyectos(ReportProyecto reportProyecto) {
    List<ProyectoDto> proyectos = proyectoService.findAll(reportProyecto.getQuery(), reportProyecto.getPaging(),
        new ParameterizedTypeReference<List<ProyectoDto>>() {
        });

    proyectos.forEach(proyecto -> {
      proyecto.setEquipo(proyectoService.findAllProyectoEquipo(proyecto.getId()));
      proyecto.getEquipo().forEach(personaEquipo -> {
        try {
          personaEquipo.setPersona(personaService.findById(personaEquipo.getPersonaRef()));
        } catch (Exception e) {
          log.debug(e.getMessage());
        }
      });
      proyecto.setEntidadesConvocantes(proyectoService.findAllProyectoEntidadConvocante(proyecto.getId()));
      proyecto.getEntidadesConvocantes().forEach(entidad -> {
        try {
          entidad.setEmpresa(empresaService.findById(entidad.getEntidadRef()));
        } catch (Exception e) {
          log.debug(e.getMessage());
        }
      });
      proyecto.setEntidadesFinanciadoras(proyectoService.findAllProyectoEntidadFinanciadora(proyecto.getId()));
      proyecto.getEntidadesFinanciadoras().forEach(entidad -> {
        try {

          entidad.setEmpresa(empresaService.findById(entidad.getEntidadRef()));

        } catch (Exception e) {
          log.debug(e.getMessage());
        }
      });
    });
    return proyectos;
  }

  private void getRowsTableModelEquipo(ProyectoDto proyecto, Collection<Object> elementsRow,
      boolean exportAsSubReport) {

    if (exportAsSubReport) {

      List<SgiRowReportDto> rows = new ArrayList<>();
      proyecto.getEquipo().forEach(equipo -> {
        String investigador = getInvestigador(equipo);
        List<Object> rowsSubReport = new ArrayList<>();
        rowsSubReport.add(investigador);

        rows.add(SgiRowReportDto.builder().elements(rowsSubReport).build());
      });

      elementsRow.add(SgiDynamicReportDto.builder().rows(rows).build());

    } else {
      proyecto.getEquipo().forEach(equipo -> {
        String investigador = getInvestigador(equipo);
        elementsRow.add(investigador);
      });
    }

  }

  private String getInvestigador(ProyectoEquipoDto equipo) {
    String investigador = "";
    if (null != equipo.getPersona()) {
      try {
        investigador = equipo.getPersona().getNombre() + " " + equipo.getPersona().getApellidos();
      } catch (Exception e) {
        investigador = getErrorMessageToReport(e);
      }
    }
    return investigador;
  }

  private void getRowsTableModelEntidadConvocante(ProyectoDto proyecto, Collection<Object> elementsRow,
      boolean exportAsSubReport) {
    if (exportAsSubReport) {
      List<SgiRowReportDto> rows = new ArrayList<>();
      proyecto.getEntidadesConvocantes().forEach(entidad -> {
        List<Object> rowsSubReport = new ArrayList<>();
        getColumnsTableModelEntidad(rowsSubReport, entidad.getEmpresa());

        rows.add(SgiRowReportDto.builder().elements(rowsSubReport).build());
      });

      elementsRow.add(SgiDynamicReportDto.builder().rows(rows).build());

    } else {
      proyecto.getEntidadesConvocantes()
          .forEach(entidad -> getColumnsTableModelEntidad(elementsRow, entidad.getEmpresa()));
    }

  }

  private void getRowsTableModelEntidadFinanciadora(ProyectoDto proyecto, Collection<Object> elementsRow,
      boolean exportAsSubReport) {

    if (exportAsSubReport) {
      List<SgiRowReportDto> rows = new ArrayList<>();
      proyecto.getEntidadesFinanciadoras().forEach(entidad -> {
        List<Object> rowsSubReport = new ArrayList<>();
        getColumnsTableModelEntidad(rowsSubReport, entidad.getEmpresa());

        rows.add(SgiRowReportDto.builder().elements(rowsSubReport).build());
      });

      elementsRow.add(SgiDynamicReportDto.builder().rows(rows).build());

    } else {
      proyecto.getEntidadesFinanciadoras()
          .forEach(entidad -> getColumnsTableModelEntidad(elementsRow, entidad.getEmpresa()));
    }
  }

  private void getColumnsTableModelEntidad(Collection<Object> elementsRow, EmpresaDto empresa) {
    String nombreEntidad = "";
    if (null != empresa) {
      try {
        nombreEntidad = empresa.getNombre();
      } catch (Exception e) {
        nombreEntidad = getErrorMessageToReport(e);
      }
    }
    elementsRow.add(nombreEntidad);
    String numeroIdentificacion = "";
    if (null != empresa) {
      try {
        numeroIdentificacion = empresa.getNumeroIdentificacion();
      } catch (Exception e) {
        numeroIdentificacion = getErrorMessageToReport(e);
      }
    }
    elementsRow.add(numeroIdentificacion);
  }

}