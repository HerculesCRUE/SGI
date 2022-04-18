package org.crue.hercules.sgi.csp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "solicitud_grupo")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudGrupo extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "solicitud_grupo_seq")
  @SequenceGenerator(name = "solicitud_grupo_seq", sequenceName = "solicitud_grupo_seq", allocationSize = 1)
  private Long id;

  /** Grupo */
  @Column(name = "grupo_id", nullable = false)
  @NotNull
  private Long grupoId;

  /** Solicitud Id */
  @Column(name = "solicitud_id", nullable = false)
  @NotNull
  private Long solicitudId;

  // Relation mappings for JPA metamodel generation only
  @OneToOne
  @JoinColumn(name = "solicitud_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDGRUPO_SOLICITUD"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Solicitud solicitud = null;

  @ManyToOne
  @JoinColumn(name = "grupo_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_SOLICITUDGRUPO_GRUPO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Grupo grupo = null;

}
