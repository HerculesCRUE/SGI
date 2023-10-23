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
@Table(name = "tipo_memoria")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TipoMemoria extends BaseEntity {

  public enum Tipo {
    /** Nueva <code>1L</code> */
    NUEVA(1L),
    /** Modificacion <code>2L</code> */
    MODIFICACION(2L),
    /** Ratificacion <code>3L</code> */
    RATIFICACION(3L);

    private final Long id;

    private Tipo(Long id) {
      this.id = id;
    }

    public Long getId() {
      return this.id;
    }

    public static Tipo fromId(Long id) {
      for (Tipo tipo : Tipo.values()) {
        if (tipo.id.equals(id)) {
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
