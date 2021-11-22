package org.crue.hercules.sgi.rep.dto.eti;

import java.beans.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class TipoEvaluacionDto extends BaseRestDto {

  public enum TipoEvaluacion {
    /** Retrospectiva <code>1L</code> */
    RETROSPECTIVA(1L),
    /** Memoria <code>2L</code> */
    MEMORIA(2L),
    /** Seguimiento Anual <code>3L</code> */
    SEGUIMIENTO_ANUAL(3L),
    /** Seguimiento Final <code>4L</code> */
    SEGUIMIENTO_FINAL(4L);

    private final Long id;

    private TipoEvaluacion(Long id) {
      this.id = id;
    }

    public Long getId() {
      return this.id;
    }

    public static TipoEvaluacion fromId(Long id) {
      for (TipoEvaluacion tipo : TipoEvaluacion.values()) {
        if (tipo.id.compareTo(id) == 0) {
          return tipo;
        }
      }
      return null;
    }
  }

  private String nombre;
  private Boolean activo;

  @JsonIgnore
  @Transient()
  public TipoEvaluacion getTipo() {
    return TipoEvaluacion.fromId(this.getId());
  }

}