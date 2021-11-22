package org.crue.hercules.sgi.rep.dto;

import java.io.Serializable;
import java.util.List;

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

  @NotNull
  private String title;

  private List<SgiFilterReportDto> filters;

  // Solo se puede agrupar cuando la orientación de los campos es vertical
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

    private String name;
    @NotNull
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
    @NotNull
    private String title;
    @NotNull
    private String name;
    @NotNull
    private TypeColumnReportEnum type;

    private String format;

    private FieldOrientationType fieldOrientationType;

    private List<SgiColumReportDto> columns;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SgiGroupReportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private String name;

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

  public enum TypeColumnReportEnum {
    STRING, DATE, NUMBER, BOOLEAN, FORMULA, SUBREPORT
  }
}