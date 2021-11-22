package org.crue.hercules.sgi.csp.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = EstadoValidacionIP.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoValidacionIP extends BaseEntity {

  protected static final String TABLE_NAME = "estado_validacion_ip";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final int COMENTARIO_MAX_LENGTH = 1024;

  public enum TipoEstadoValidacion {
    PENDIENTE, NOTIFICADA, VALIDADA, RECHAZADA
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "comentario", length = COMENTARIO_MAX_LENGTH)
  private String comentario;

  @Column(name = "estado", nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoEstadoValidacion estado;

  @Column(name = "fecha")
  private Instant fecha;

  @Column(name = "proyecto_facturacion_id", nullable = false)
  private Long proyectoFacturacionId;

  @ManyToOne
  @JoinColumn(name = "proyecto_facturacion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ESTADOVALIDACIONIP_PROYECTOFACTURACION"), nullable = false)
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private ProyectoFacturacion proyectoFacturacion;

  @PrePersist
  public void initialize() {
    this.fecha = Instant.now();
  }
}
