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
@Table(name = "solicitud_hito_aviso")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudHitoAviso extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_hito_aviso_seq")
  @SequenceGenerator(name = "solicitud_hito_aviso_seq", sequenceName = "solicitud_hito_aviso_seq", allocationSize = 1)
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
   * Indica si en el aviso se han de incluir como destinatarios los IPs de la
   * Solicitud
   */
  @Column(name = "incluir_ips_solicitud", columnDefinition = "boolean default false", nullable = false)
  @NotNull
  private Boolean incluirIpsSolicitud;

  // Relation mappings for JPA metamodel generation only
  @OneToOne(mappedBy = "solicitudHitoAviso")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final SolicitudHito solicitudHito = null;
}
