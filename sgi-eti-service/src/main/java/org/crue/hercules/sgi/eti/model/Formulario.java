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

/**
 * Formulario
 */

@Entity
@Table(name = "formulario")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Formulario extends BaseEntity {

  public enum Tipo {
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
  @Column(name = "nombre", length = 50, nullable = false)
  private String nombre;

  /** Descripción. */
  @Column(name = "descripcion", length = 250, nullable = false)
  private String descripcion;

  @JsonIgnore
  @Transient()
  public Tipo getTipo() {
    return Tipo.fromId(this.id);
  }
}