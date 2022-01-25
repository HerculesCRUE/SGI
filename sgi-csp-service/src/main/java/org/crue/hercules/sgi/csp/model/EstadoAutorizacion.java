package org.crue.hercules.sgi.csp.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = EstadoAutorizacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoAutorizacion extends BaseEntity {

  protected static final String TABLE_NAME = "estado_autorizacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";
  public static final int MAX_LENGTH = 250;

  public enum Estado {

    /** Borrador */
    BORRADOR,
    /** Presentada */
    PRESENTADA,
    /** Autorizada */
    AUTORIZADA,
    /** Revisión */
    REVISION
  }

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EstadoAutorizacion.SEQUENCE_NAME)
  @SequenceGenerator(name = EstadoAutorizacion.SEQUENCE_NAME, sequenceName = EstadoAutorizacion.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Autorizacion */
  @Column(name = "autorizacion_id", nullable = false)
  @NotNull
  private Long autorizacionId;

  /** Comentario */
  @Column(name = "comentario", length = EstadoAutorizacion.MAX_LENGTH, nullable = true)
  @Size(max = EstadoAutorizacion.MAX_LENGTH)
  private String comentario;

  /** Fecha */
  @Column(name = "fecha", nullable = false)
  @NotNull
  private Instant fecha;

  /** Estado */
  @Column(name = "estado", nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Estado estado;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "autorizacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ESTADOAUTORIZACION_AUTORIZACION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private static final Autorizacion autorizacion = null;
}
