package org.crue.hercules.sgi.eti.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_estado_memoria")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TipoEstadoMemoria extends BaseEntity {

  public enum Tipo {
    /** En elaboración <code>1L</code> */
    EN_ELABORACION(1L),
    /** Completada <code>2L</code> */
    COMPLETADA(2L),
    /** En secretaría <code>3L</code> */
    EN_SECRETARIA(3L),
    /** En secretaría revisión mínima <code>4L</code> */
    EN_SECRETARIA_REVISION_MINIMA(4L),
    /** En evaluación <code>5L</code> */
    EN_EVALUACION(5L),
    /** En secretaría revisión mínima <code>6L</code> */
    FAVORABLE_PENDIENTE_MODIFICACIONES_MINIMAS(6L),
    /** Pendiente correcciones <code>7L</code> */
    PENDIENTE_CORRECCIONES(7L),
    /** No procede evaluar <code>8L</code> */
    NO_PROCEDE_EVALUAR(8L),
    /** Fin evaluación <code>9L</code> */
    FIN_EVALUACION(9L),
    /** Archivada <code>10L</code> */
    ARCHIVADA(10L),
    /** Completada seguimiento anual <code>11L</code> */
    COMPLETADA_SEGUIMIENTO_ANUAL(11L),
    /** En secretaría seguimiento anual <code>12L</code> */
    EN_SECRETARIA_SEGUIMIENTO_ANUAL(12L),
    /** En evaluación seguimiento anual <code>13L</code> */
    EN_EVALUACION_SEGUIMIENTO_ANUAL(13L),
    /** Fin evaluación seguimiento anual <code>14L</code> */
    FIN_EVALUACION_SEGUIMIENTO_ANUAL(14L),
    /** Solicitud modificación <code>15L</code> */
    SOLICITUD_MODIFICACION(15L),
    /** Completada seguimiento final <code>16L</code> */
    COMPLETADA_SEGUIMIENTO_FINAL(16L),
    /** En secretaría seguimiento final <code>17L</code> */
    EN_SECRETARIA_SEGUIMIENTO_FINAL(17L),
    /** En secretaría seguimiento final aclaraciones <code>18L</code> */
    EN_SECRETARIA_SEGUIMIENTO_FINAL_ACLARACIONES(18L),
    /** En evaluación seguimiento final <code>19L</code> */
    EN_EVALUACION_SEGUIMIENTO_FINAL(19L),
    /** Fin evaluación seguimiento final <code>20L</code> */
    FIN_EVALUACION_SEGUIMIENTO_FINAL(20L),
    /** En aclaración seguimiento final <code>21L</code> */
    EN_ACLARACION_SEGUIMIENTO_FINAL(21L);

    private final Long id;

    private Tipo(Long id) {
      this.id = id;
    }

    public Long getId() {
      return this.id;
    }

    public static Tipo fromId(Long id) {
      for (Tipo tipo : Tipo.values()) {
        if (tipo.id == id) {
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

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

  @JsonIgnore
  @Transient()
  public Tipo getTipo() {
    return Tipo.fromId(this.id);
  }
}