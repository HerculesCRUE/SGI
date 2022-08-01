package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "convocatoria_fase_aviso")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ConvocatoriaFaseAviso extends BaseEntity {

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "convocatoria_fase_aviso_seq")
  @SequenceGenerator(name = "convocatoria_fase_aviso_seq", sequenceName = "convocatoria_fase_aviso_seq", allocationSize = 1)
  private Long id;

  /** Referencia externa del aviso a enviar, procedente del servicio COM */
  @Column(name = "comunicado_ref", nullable = false)
  @NotNull
  private String comunicadoRef;

  /**
   * Referencia externa de la tarea programada de envío del comunicado, procedente
   * de TP
   */
  @Column(name = "tarea_programada_ref", nullable = false)
  @NotNull
  private String tareaProgramadaRef;

  /**
   * Indica si en el aviso se han de incluir como destinatarios los IPs de las
   * Solicitudes relacionadas con la Convocatoria
   */
  @Column(name = "incluir_ips_solicitud", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean incluirIpsSolicitud;

  /**
   * Indica si en el aviso se han de incluir como destinatarios los IPs de los
   * Proyectos relacionadas con la Convocatoria
   */
  @Column(name = "incluir_ips_proyecto", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean incluirIpsProyecto;

  // Relation mappings for JPA metamodel generation only
  @OneToOne(mappedBy = "convocatoriaFaseAviso1")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConvocatoriaFase convocatoriaFase = null;

}
