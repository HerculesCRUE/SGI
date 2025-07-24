package org.crue.hercules.sgi.eti.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_tarea")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TipoTarea extends BaseEntity {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id. */
  @Id
  @Column(name = "id", nullable = false)
  @NotNull
  private Long id;

  /** Nombre. */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_tarea_nombre", joinColumns = @JoinColumn(name = "tipo_tarea_id"))
  @Valid
  @NotEmpty
  private Set<TipoTareaNombre> nombre = new HashSet<>();

  /** Activo */
  @Column(name = "activo", columnDefinition = "boolean default true", nullable = false)
  @NotNull
  private Boolean activo;
}