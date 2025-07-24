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
import javax.validation.constraints.NotNull;

import org.crue.hercules.sgi.csp.model.BaseActivableEntity.OnActivar;
import org.crue.hercules.sgi.csp.validation.UniqueNombreModeloEjecucionActivo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "modelo_ejecucion")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueNombreModeloEjecucionActivo(groups = { BaseEntity.Create.class, BaseEntity.Update.class, OnActivar.class })
public class ModeloEjecucion extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modelo_ejecucion_seq")
  @SequenceGenerator(name = "modelo_ejecucion_seq", sequenceName = "modelo_ejecucion_seq", allocationSize = 1)
  private Long id;

  /** Nombre. */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "modelo_ejecucion_nombre", joinColumns = @JoinColumn(name = "modelo_ejecucion_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<ModeloEjecucionNombre> nombre = new HashSet<>();

  /** Descripcion. */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "modelo_ejecucion_descripcion", joinColumns = @JoinColumn(name = "modelo_ejecucion_id"))
  @Valid
  @Builder.Default
  private Set<ModeloEjecucionDescripcion> descripcion = new HashSet<>();

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull(groups = { Update.class })
  private Boolean activo;

  /** Externo */
  @Column(name = "externo", columnDefinition = "boolean default false", nullable = false)
  private Boolean externo;

  /** Contrato */
  @Column(name = "contrato", columnDefinition = "boolean default false", nullable = false)
  private Boolean contrato;

  /** Permite crear solicitudes sin convocatoria */
  @Column(name = "solicitud_sin_convocatoria", columnDefinition = "boolean default false", nullable = false)
  private Boolean solicitudSinConvocatoria;

  @OneToMany(mappedBy = "modeloEjecucion")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<ModeloUnidad> modelosUnidad = null;

}
