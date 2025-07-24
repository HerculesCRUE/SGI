package org.crue.hercules.sgi.csp.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.csp.model.BaseActivableEntity.OnActivar;
import org.crue.hercules.sgi.csp.validation.UniqueNombreProgramaActivo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "programa")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueNombreProgramaActivo(groups = { BaseEntity.Create.class, BaseEntity.Update.class, OnActivar.class })
public class Programa extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programa_seq")
  @SequenceGenerator(name = "programa_seq", sequenceName = "programa_seq", allocationSize = 1)
  private Long id;

  /** Nombre. */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "programa_nombre", joinColumns = @JoinColumn(name = "programa_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<ProgramaNombre> nombre = new HashSet<>();

  /** Descripcion. */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "programa_descripcion", joinColumns = @JoinColumn(name = "programa_id"))
  @Valid
  @Builder.Default
  private Set<ProgramaDescripcion> descripcion = new HashSet<>();

  /** Programa padre. */
  @ManyToOne
  @JoinColumn(name = "programa_padre_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROGRAMA_PADRE"))
  private Programa padre;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull(groups = { Update.class })
  private Boolean activo;

}
