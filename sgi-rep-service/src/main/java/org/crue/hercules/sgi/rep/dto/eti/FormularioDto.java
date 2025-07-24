package org.crue.hercules.sgi.rep.dto.eti;

import org.crue.hercules.sgi.rep.dto.BaseRestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FormularioDto extends BaseRestDto {

  public enum Tipo {
    /** Memoria */
    MEMORIA,
    /** Seguimiento Anual */
    SEGUIMIENTO_ANUAL,
    /** Seguimiento Final */
    SEGUIMIENTO_FINAL,
    /** Retrospectiva */
    RETROSPECTIVA;
  }

  /** Titulo de la documentación de Seguimiento Anual */
  public enum SeguimientoAnualDocumentacionTitle {
    /** Memoria */
    TITULO_1,
    /** Seguimiento Anual */
    TITULO_2,
    /** Seguimiento Final */
    TITULO_3;
  }

  private Tipo tipo;
  private String codigo;
  private String descripcion;
  private SeguimientoAnualDocumentacionTitle seguimientoAnualDocumentacionTitle;
}