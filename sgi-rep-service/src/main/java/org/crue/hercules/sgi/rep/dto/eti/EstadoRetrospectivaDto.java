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
public class EstadoRetrospectivaDto extends BaseRestDto {

  public enum TipoEstadoRetrospectiva {
    /** Pendiente <code>1L</code> */
    PENDIENTE(1L),
    /** Completada <code>2L</code> */
    COMPLETADA(2L),
    /** En secretaría <code>3L</code> */
    EN_SECRETARIA(3L),
    /** En evaluación <code>4L</code> */
    EN_EVALUACION(4L),
    /** Fin evaluación <code>5L</code> */
    FIN_EVALUACION(5L);

    private final Long id;

    private TipoEstadoRetrospectiva(Long id) {
      this.id = id;
    }

    public Long getId() {
      return this.id;
    }

    public static TipoEstadoRetrospectiva fromId(Long id) {
      for (TipoEstadoRetrospectiva tipo : TipoEstadoRetrospectiva.values()) {
        if (tipo.id.compareTo(id) == 0) {
          return tipo;
        }
      }
      return null;
    }
  }

  private Long id;
  private String nombre;
  private Boolean activo;

  @JsonIgnore
  @Transient()
  public TipoEstadoRetrospectiva getTipo() {
    return TipoEstadoRetrospectiva.fromId(this.id);
  }
}
