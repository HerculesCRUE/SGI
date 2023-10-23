package org.crue.hercules.sgi.eti.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dictamen")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Dictamen extends BaseEntity {

  public enum Tipo {
    /** Favorable <code>1L</code> */
    FAVORABLE(1L),
    /** Favorable pendiente revision minima <code>2L</code> */
    FAVORABLE_PENDIENTE_REVISION_MINIMA(2L),
    /** Pendiente correcciones <code>3L</code> */
    PENDIENTE_CORRECCIONES(3L),
    /** No procede evaluar <code>4L</code> */
    NO_PROCEDE_EVALUAR(4L),
    /** Favorable seguimiento anual <code>5L</code> */
    FAVORABLE_SEGUIMIENTO_ANUAL(5L),
    /** Solicitud modificaciones <code>6L</code> */
    SOLICITUD_MODIFICACIONES(6L),
    /** Favorable seguimiento final <code>7L</code> */
    FAVORABLE_SEGUIMIENTO_FINAL(7L),
    /** Solicitud aclaraciones seguimiento final <code>8L</code> */
    SOLICITUD_ACLARACIONES_SEGUIMIENTO_FINAL(8L),
    /** Favorable retrospectiva <code>9L</code> */
    FAVORABLE_RETROSPECTIVA(9L),
    /** Desfavorable retrospectiva <code>10L</code> */
    DESFAVORABLE_RETROSPECTIVA(10L),
    /** Desfavorable <code>11L</code> */
    DESFAVORABLE(11L);

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

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  /** Nombre. */
  @Column(name = "nombre", length = 250, nullable = false)
  private String nombre;

  /** Tipo evaluacion. */
  @ManyToOne
  @JoinColumn(name = "tipo_evaluacion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_DICTAMEN_TIPOEVALUACION"))
  private TipoEvaluacion tipoEvaluacion;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

  @JsonIgnore
  @Transient()
  public Tipo getTipo() {
    return Tipo.fromId(this.id);
  }

}