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

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = TipoRequerimiento.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class TipoRequerimiento extends BaseActivableEntity {

  protected static final String TABLE_NAME = "tipo_requerimiento";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TipoRequerimiento.SEQUENCE_NAME)
  @SequenceGenerator(name = TipoRequerimiento.SEQUENCE_NAME, sequenceName = TipoRequerimiento.SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_requerimiento_nombre", joinColumns = @JoinColumn(name = "tipo_requerimiento_id"))
  @NotEmpty
  @Valid
  @Builder.Default
  private Set<TipoRequerimientoNombre> nombre = new HashSet<>();
}
