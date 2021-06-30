package org.crue.hercules.sgi.pii.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad.OnActivar;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad.OnActualizar;
import org.crue.hercules.sgi.pii.model.ResultadoInformePatentabilidad.OnCrear;
import org.crue.hercules.sgi.pii.validation.EntidadActiva;
import org.crue.hercules.sgi.pii.validation.UniqueNombreResultadoInformePatentabilidad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "resultado_informe_patentabilidad")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@UniqueNombreResultadoInformePatentabilidad(groups = { OnActualizar.class, OnActivar.class, OnCrear.class })
@EntidadActiva(entityClass = ResultadoInformePatentabilidad.class, groups = { OnActualizar.class })
public class ResultadoInformePatentabilidad extends BaseActivableEntity {

  public static final int NOMBRE_LENGTH = 50;
  public static final int DESCRIPCION_LENGTH = 250;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resultado_informe_patentabilidad_seq")
  @SequenceGenerator(name = "resultado_informe_patentabilidad_seq", sequenceName = "resultado_informe_patentabilidad_seq", allocationSize = 1)
  private Long id;

  /** Nombre */
  @Column(name = "nombre", length = NOMBRE_LENGTH, nullable = false)
  private String nombre;

  /** Descripcion */
  @Column(name = "descripcion", length = DESCRIPCION_LENGTH, nullable = true)
  private String descripcion;

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

  /**
   * Interfaz para marcar validaciones en las activaciones de la entidad.
   */
  public interface OnActivar {
  }

}