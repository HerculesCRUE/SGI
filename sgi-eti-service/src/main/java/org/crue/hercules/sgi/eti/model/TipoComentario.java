package org.crue.hercules.sgi.eti.model;

import java.beans.Transient;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * TipoComentario
 */

@Entity
@Table(name = "tipo_comentario")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TipoComentario extends BaseEntity {

  public enum Tipo {
    /** GESTOR <code>1L</code> */
    GESTOR(1L),
    /** EVALUADOR <code>2L</code> */
    EVALUADOR(2L),
    /** ACTA_GESTOR <code>3L</code> */
    ACTA_GESTOR(3L),
    /** ACTA_EVALUADOR <code>4L</code> */
    ACTA_EVALUADOR(4L);

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

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

  @JsonIgnore
  @Transient()
  public Tipo getTipo() {
    return Tipo.fromId(this.id);
  }
}