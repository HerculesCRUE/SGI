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
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.csp.model.BaseActivableEntity.OnActivar;
import org.crue.hercules.sgi.csp.validation.UniqueNombreTipoFaseActivo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_fase")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueNombreTipoFaseActivo(groups = { BaseEntity.Create.class, BaseEntity.Update.class, OnActivar.class })
public class TipoFase extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;
  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_fase_seq")
  @SequenceGenerator(name = "tipo_fase_seq", sequenceName = "tipo_fase_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_fase_nombre", joinColumns = @JoinColumn(name = "tipo_fase_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<TipoFaseNombre> nombre = new HashSet<>();

  /** Descripcion */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_fase_descripcion", joinColumns = @JoinColumn(name = "tipo_fase_id"))
  @Valid
  @Builder.Default
  private Set<TipoFaseDescripcion> descripcion = new HashSet<>();

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull(groups = { Update.class })
  private Boolean activo;

}
