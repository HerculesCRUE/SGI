package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "solicitud_modalidad")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudModalidad extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_modalidad_seq")
  @SequenceGenerator(name = "solicitud_modalidad_seq", sequenceName = "solicitud_modalidad_seq", allocationSize = 1)
  private Long id;

  /** Solicitud Id */
  @Column(name = "solicitud_id", nullable = false)
  @NotNull
  private Long solicitudId;

  /** EntidadRef */
  @Column(name = "entidad_ref", length = 50, nullable = false)
  @Size(max = 50)
  @NotNull
  private String entidadRef;

  /** Programa */
  @ManyToOne
  @JoinColumn(name = "programa_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDMODALIDAD_PROGRAMA"))
  @NotNull
  private Programa programa;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "solicitud_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDMODALIDAD_SOLICITUD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Solicitud solicitud = null;
}