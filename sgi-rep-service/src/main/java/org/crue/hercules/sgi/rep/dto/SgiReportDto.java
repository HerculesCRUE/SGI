package org.crue.hercules.sgi.rep.dto;

import java.util.Map;

import javax.swing.table.TableModel;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Instancia que contiene un report
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SgiReportDto {
  @NotNull(groups = { Create.class })
  private String name;

  @NotNull(groups = { Create.class })
  private String path;

  // Tamaño máximo de ancho (solo aplica a pdf), Máximos PORTRAIT=470,
  // LANDSCAPE=630
  private Float customWidth;

  // Ancho mínimo de columna, sino lo informamos cogerá el tamaño por
  // defecto o la proporción entre el ancho máximo de la página y el nº de
  // elementos (si es PDF)
  private Float columnMinWidth;

  // Disposición de los campos en el informe: horizontal o vertical
  private FieldOrientation fieldOrientation;

  @JsonIgnore
  private Map<String, TableModel> dataModel;

  @JsonIgnore
  private Map<String, Object> parameters;

  @NotNull
  private OutputType outputType;

  @JsonIgnore
  private byte[] content;

  public enum FieldOrientation {
    HORIZONTAL, VERTICAL
  }

  /**
   * Interfaz para marcar validaciones en los create.
   */
  public interface Create extends Default {
  }

}