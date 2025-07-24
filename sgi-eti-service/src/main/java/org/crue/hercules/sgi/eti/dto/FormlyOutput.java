package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Representación de salida del API REST de un Formly.
 * <p>
 * La entidad Formly representa un formulario configurable.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormlyOutput implements Serializable {
  private Long id;
  private String nombre;
  private Integer version;
  private List<FormlyDefinicionOutput> definicion;

}
