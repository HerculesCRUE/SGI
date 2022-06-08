package org.crue.hercules.sgi.rep.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * Instancia que contiene un report dinámico
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Slf4j
public class SgiDynamicReportDto extends SgiReportDto {
  // TODO validar toda la estructura completamente: nº columnas = nº de elementos
  // en cada fila, tamaño de columnas correcto, etc

  private String title;

  @Valid
  private List<SgiFilterReportDto> filters;

  // Solo se puede agrupar cuando la orientación de los campos es vertical
  @Valid
  private SgiGroupReportDto groupBy;

  @NotNull
  @NotEmpty
  private List<SgiColumReportDto> columns;

  @NotNull
  @NotEmpty
  private List<SgiRowReportDto> rows;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SgiFilterReportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String name;
    @NotBlank
    private String filter;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SgiRowReportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @NotEmpty
    private transient List<Object> elements;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SgiColumReportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String title;

    /**
     * Cabeceras adicionales en el título solo aplicables si el report es pdf con
     * orientación vertical
     */
    private List<String> additionalTitle;

    @NotBlank
    private String name;

    @NotBlank
    private ColumnType type;

    /**
     * Indica si es visible cuando el subreport no contiene elementos
     */
    private Boolean visibleIfSubReportEmpty;

    private String format;

    /**
     * Indica si es visible el campo en el report (se suele utilizar para visualizar
     * dicho campo en la cabecera additionalGroupNames sin mostrarlo en el listado)
     */
    private Boolean visible;

    @Valid
    private SgiColumnHorizontalOptionsReportDto horizontalOptions;

    @Valid
    private FieldOrientation fieldOrientation;

    @Valid
    private List<SgiColumReportDto> columns;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SgiColumnHorizontalOptionsReportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Permite definir el ancho de la columna cuando se trata de disposición
     * horizontal de columnas en función de un porcentaje sobre el ancho máximo
     * posible.
     * 
     * reportDto.customWidth = 630
     * customHorizontalWidth = 20
     * 
     * Ancho de columna = 630*0.20 = 126
     * 
     * Si no lo informamos cogerá el valor por defecto customWidth/nº de columnas
     * por lo que podría darse el caso de que no cojan todas las columnas, en ese
     * caso, lo ideal, sería darle un "porcentaje" a todas las columnas, es decir,
     * informar este campo para cada columna sin que sobrepase la suma de todos
     * ellos en un 100%
     * 
     * TODO añadir mas flexibilidad a este campo para que permita redistribuir todas
     * las columnas aunque los porcentajes sobrepasen el 100% y que permita mas
     * tipos de cálculos de columnas (px, peso, etc)
     * 
     */
    private Float customWidth;

    /**
     * Alineación de la celda
     */
    private ElementAlignmentType elementAlignmentType;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SgiGroupReportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Nombre del campo que se va a mostrar en la cabecera del registro
     * , necesario cuando la disposición de registros es en vertical
     */
    @NotBlank
    private String name;

    /**
     * Campos adicionales de agrupacion que se mostrarán en la cabecera
     * (solo válido para disposición vertical)
     */
    private List<@NotEmpty String> additionalGroupNames;

    /**
     * Indica si se visualiza o no el campo en la cabecera
     */
    @NotNull
    private Boolean visible;
  }

  public String toJson() {
    String result = "";
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.setSerializationInclusion(Include.NON_NULL);
      result = mapper.writeValueAsString(this);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return result;
  }

  public enum ColumnType {
    STRING, DATE, NUMBER, BOOLEAN, FORMULA, SUBREPORT
  }

  public enum ElementAlignmentType {
    RIGHT, LEFT, JUSTIFY, BOTTOM, MIDDLE, TOP, CENTER
  }
}