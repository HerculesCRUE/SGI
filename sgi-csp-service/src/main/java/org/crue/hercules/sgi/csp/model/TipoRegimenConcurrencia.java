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

import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia.OnActualizar;
import org.crue.hercules.sgi.csp.model.TipoRegimenConcurrencia.OnCrear;
import org.crue.hercules.sgi.csp.validation.UniqueNombreTipoRegimenConcurrenciaActiva;
import org.crue.hercules.sgi.framework.validation.ActivableIsActivo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tipo_regimen_concurrencia")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
@UniqueNombreTipoRegimenConcurrenciaActiva(groups = { OnActualizar.class, BaseActivableEntity.OnActivar.class,
    OnCrear.class })
@ActivableIsActivo(entityClass = TipoRegimenConcurrencia.class, groups = { OnActualizar.class })
public class TipoRegimenConcurrencia extends BaseActivableEntity {

  public static final int NOMBRE_LENGTH = 50;

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_regimen_concurrencia_seq")
  @SequenceGenerator(name = "tipo_regimen_concurrencia_seq", sequenceName = "tipo_regimen_concurrencia_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tipo_regimen_concurrencia_nombre", joinColumns = @JoinColumn(name = "tipo_regimen_concurrencia_id"))
  @NotEmpty
  @Valid
  private Set<TipoRegimenConcurrenciaNombre> nombre = new HashSet<>();

  /**
   * Interfaz para marcar validaciones en la creación de la entidad.
   */
  public interface OnCrear {
  }

  /**
   * Interfaz para marcar validaciones en la actualizacion de la entidad.
   */
  public interface OnActualizar {
  }

}