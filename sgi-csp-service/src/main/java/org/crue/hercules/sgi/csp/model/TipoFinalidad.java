package org.crue.hercules.sgi.csp.model;

import java.util.HashSet;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.crue.hercules.sgi.csp.model.BaseActivableEntity.OnActivar;
import org.crue.hercules.sgi.csp.validation.UniqueNombreTipoFinalidadActivo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_finalidad")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueNombreTipoFinalidadActivo(groups = { BaseEntity.Create.class, BaseEntity.Update.class, OnActivar.class })
public class TipoFinalidad extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_finalidad_seq")
  @SequenceGenerator(name = "tipo_finalidad_seq", sequenceName = "tipo_finalidad_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_finalidad_nombre", joinColumns = @JoinColumn(name = "tipo_finalidad_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<TipoFinalidadNombre> nombre = new HashSet<>();

  /** Descripción */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_finalidad_descripcion", joinColumns = @JoinColumn(name = "tipo_finalidad_id"))
  @Valid
  @Builder.Default
  private Set<TipoFinalidadDescripcion> descripcion = new HashSet<>();

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  private Boolean activo;

  @OneToMany(mappedBy = "tipoFinalidad")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ModeloTipoFinalidad> modelosTipoFinalidad = null;
}