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
@Table(name = "estado_solicitud")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoSolicitud extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /**
   * Enumerado tipo estado de las solicitudes.
   *
   */
  public enum Estado {
    /** Borrador */
    BORRADOR,
    /** Solicitrada */
    SOLICITADA,
    /** En subsanación */
    SUBSANACION,
    /** Presentada subsanación */
    PRESENTADA_SUBSANACION,
    /** Excluida provisional */
    EXCLUIDA_PROVISIONAL,
    /** Admitida provisional */
    ADMITIDA_PROVISIONAL,
    /** Alegación fase admisión */
    ALEGACION_FASE_ADMISION,
    /** Admitida definitiva */
    ADMITIDA_DEFINITIVA,
    /** DExcluida definitiva */
    EXCLUIDA_DEFINITIVA,
    /** Recurso fase admisión */
    RECURSO_FASE_ADMISION,
    /** Concedida provisional */
    CONCEDIDA_PROVISIONAL,
    /** Denegada provisional */
    DENEGADA_PROVISIONAL,
    /** Alegación fase provisional */
    ALEGACION_FASE_PROVISIONAL,
    /** Concedida provisional alegada */
    CONCEDIDA_PROVISIONAL_ALEGADA,
    /** Concedida provisional no alegada */
    CONCEDIDA_PROVISIONAL_NO_ALEGADA,
    /** Denegada provisional alegada */
    DENEGADA_PROVISIONAL_ALEGADA,
    /** Denegada provisional no alegada */
    DENEGADA_PROVISIONAL_NO_ALEGADA,
    /** Desistida */
    DESISTIDA,
    /** Reserva provisional */
    RESERVA_PROVISIONAL,
    /** En negociación */
    NEGOCIACION,
    /** Concedida */
    CONCEDIDA,
    /** Denegada */
    DENEGADA,
    /** Recurso fase concesión */
    RECURSO_FASE_CONCESION,
    /** Reserva */
    RESERVA,
    /** Firmada */
    FIRMADA,
    /** Renunciada */
    RENUNCIADA,
    /** Rechazada */
    RECHAZADA,
    /** Validada */
    VALIDADA;
  }

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_solicitud_seq")
  @SequenceGenerator(name = "estado_solicitud_seq", sequenceName = "estado_solicitud_seq", allocationSize = 1)
  private Long id;

  /** Solicitud Id */
  @Column(name = "solicitud_id", nullable = false)
  private Long solicitudId;

  /** Tipo estado solicitud */
  @Column(name = "estado", length = 50, nullable = false)
  @Enumerated(EnumType.STRING)
  @NotNull
  private Estado estado;

  /** Fecha. */
  @Column(name = "fecha_estado", nullable = false)
  private Instant fechaEstado;

  /** Comentario */
  @Column(name = "comentario", length = 2000, nullable = true)
  @Size(max = 2000)
  private String comentario;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ESTADOSOLICITUD_SOLICITUD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Solicitud solicitud = null;
}