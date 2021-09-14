package org.crue.hercules.sgi.pii.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.pii.validation.EntidadActiva;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "informe_patentabilidad")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InformePatentabilidad extends BaseEntity {
  public static final int NOMBRE_LENGTH = 50;
  public static final int CONTACTO_LENGTH = 50;
  public static final int REF_LENGTH = 50;
  public static final int LONG_TEXT_LENGTH = 250;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "informe_patentabilidad_seq")
  @SequenceGenerator(name = "informe_patentabilidad_seq", sequenceName = "informe_patentabilidad_seq", allocationSize = 1)
  private Long id;

  /** Invencion Id */
  @Column(name = "invencion_id", nullable = false)
  private Long invencionId;

  /** Fecha. */
  @Column(name = "fecha", nullable = false)
  private Instant fecha;

  /** Nombre. */
  @Column(name = "nombre", length = NOMBRE_LENGTH, nullable = false)
  private String nombre;

  /** Documento ref. */
  @Column(name = "documento_ref", length = REF_LENGTH, nullable = false)
  private String documentoRef;

  /** Tipo de protección. */
  @ManyToOne
  @JoinColumn(name = "resultado_informe_patentabilidad_id", nullable = false, foreignKey = @ForeignKey(name = "FK_INFORMEPATENTABILIDAD_RESULTADOINFORMEPATENTABILIDAD"))
  @EntidadActiva(entityClass = ResultadoInformePatentabilidad.class, groups = { OnCrear.class,
      OnActualizarResultadoInformePatentabilidad.class })
  private ResultadoInformePatentabilidad resultadoInformePatentabilidad;

  /** Entidad creadora ref. */
  @Column(name = "entidad_creadora_ref", length = REF_LENGTH, nullable = false)
  private String entidadCreadoraRef;

  @Column(name = "contacto_entidad_creadora", length = CONTACTO_LENGTH, nullable = false)
  private String contactoEntidadCreadora;

  @Column(name = "contacto_examinador", length = CONTACTO_LENGTH, nullable = false)
  private String contactoExaminador;

  /** Comentarios */
  @Column(name = "comentarios", length = LONG_TEXT_LENGTH, nullable = true)
  private String comentarios;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "invencion_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INFORMEPATENTABILIDAD_INVENCION"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Invencion invencion = null;

  /**
   * Interfaz para marcar validaciones en la creación de la entidad.
   */
  public interface OnCrear {
  }

  public interface OnActualizarResultadoInformePatentabilidad {
  }
}
