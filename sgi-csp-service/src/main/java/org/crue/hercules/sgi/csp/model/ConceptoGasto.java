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
import org.crue.hercules.sgi.csp.validation.UniqueNombreConceptoGastoActivo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "concepto_gasto")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@UniqueNombreConceptoGastoActivo(groups = { BaseEntity.Create.class, BaseEntity.Update.class, OnActivar.class })
public class ConceptoGasto extends BaseEntity {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "concepto_gasto_seq")
  @SequenceGenerator(name = "concepto_gasto_seq", sequenceName = "concepto_gasto_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "concepto_gasto_nombre", joinColumns = @JoinColumn(name = "concepto_gasto_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<ConceptoGastoNombre> nombre = new HashSet<>();

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "concepto_gasto_descripcion", joinColumns = @JoinColumn(name = "concepto_gasto_id"))
  @Valid
  @Builder.Default
  private Set<ConceptoGastoDescripcion> descripcion = new HashSet<>();

  /** Costes indirectos */
  @Column(name = "costes_indirectos", columnDefinition = "boolean default false", nullable = false)
  @NotNull(groups = { Update.class })
  private Boolean costesIndirectos;

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull(groups = { Update.class })
  private Boolean activo;

}
