package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_retrospectiva")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class EstadoRetrospectiva extends BaseEntity {

  private static final long serialVersionUID = 1L;

  public enum Tipo {
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

    private Tipo(Long id) {
      this.id = id;
    }

    public Long getId() {
      return this.id;
    }

    public static Tipo fromId(Long id) {
      for (Tipo tipo : Tipo.values()) {
        if (Objects.equals(tipo.id, id)) {
          return tipo;
        }
      }
      return null;
    }
  }

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  /** Nombre. */
  @Column(name = "nombre", length = 250, nullable = false)
  private String nombre;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

  @JsonIgnore
  @Transient()
  public Tipo getTipo() {
    return Tipo.fromId(this.id);
  }
}
