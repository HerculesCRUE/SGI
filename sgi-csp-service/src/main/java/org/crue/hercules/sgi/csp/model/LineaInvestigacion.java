package org.crue.hercules.sgi.csp.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.csp.model.BaseActivableEntity.OnActivar;
import org.crue.hercules.sgi.csp.validation.UniqueNombreLineaInvestigacionActiva;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "linea_investigacion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@UniqueNombreLineaInvestigacionActiva(groups = { BaseEntity.Update.class, BaseEntity.Create.class, OnActivar.class })
@ActivableIsActivo(entityClass = LineaInvestigacion.class, groups = { BaseEntity.Update.class })
public class LineaInvestigacion extends BaseActivableEntity {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "linea_investigacion_seq")
  @SequenceGenerator(name = "linea_investigacion_seq", sequenceName = "linea_investigacion_seq", allocationSize = 1)
  private Long id;

  /** Nombre. */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "linea_investigacion_nombre", joinColumns = @JoinColumn(name = "linea_investigacion_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<LineaInvestigacionNombre> nombre = new HashSet<>();

}