package org.crue.hercules.sgi.rep.dto.eti;

import javax.persistence.Transient;

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
public class FormularioDto extends BaseRestDto {

  public enum TipoFormulario {
    /** M10 <code>1L</code> */
    M10(1L),
    /** M20 <code>2L</code> */
    M20(2L),
    /** M30 <code>3L</code> */
    M30(3L),
    /** Seguimiento Anual <code>4L</code> */
    SEGUIMIENTO_ANUAL(4L),
    /** Seguimiento Final <code>5L</code> */
    SEGUIMIENTO_FINAL(5L),
    /** Retrospectiva <code>6L</code> */
    RETROSPECTIVA(6L);

    private final Long id;

    private TipoFormulario(Long id) {
      this.id = id;
    }

    public Long getId() {
      return this.id;
    }

    public static TipoFormulario fromId(Long id) {
      for (TipoFormulario tipo : TipoFormulario.values()) {
        if (tipo.id.compareTo(id) == 0) {
          return tipo;
        }
      }
      return null;
    }
  }

  private String nombre;
  private String descripcion;

  @JsonIgnore
  @Transient()
  public TipoFormulario getTipo() {
    return TipoFormulario.fromId(this.getId());
  }
}