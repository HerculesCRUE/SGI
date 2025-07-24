package org.crue.hercules.sgi.csp.model;

import java.math.BigDecimal;
import java.time.Instant;
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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = ProyectoFacturacion.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProyectoFacturacion extends BaseEntity {

  protected static final String TABLE_NAME = "proyecto_facturacion";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "proyecto_facturacion_comentario", joinColumns = @JoinColumn(name = "proyecto_facturacion_id"))
  @Valid
  @Builder.Default
  private Set<ProyectoFacturacionComentario> comentario = new HashSet<>();

  @Column(name = "fecha_conformidad", nullable = true)
  private Instant fechaConformidad;

  @NotNull
  @Column(name = "fecha_emision", nullable = false)
  private Instant fechaEmision;

  @NotNull
  @Column(name = "importe_base", nullable = false)
  private BigDecimal importeBase;

  @Column(name = "numero_factura_sge", nullable = true)
  private String numeroFacturaSge;

  @Column(name = "numero_prevision", nullable = false)
  private Integer numeroPrevision;

  @Max(100)
  @Column(name = "porcentaje_iva", nullable = false)
  private Integer porcentajeIVA;

  @NotNull
  @Column(name = "proyecto_id", nullable = false)
  private Long proyectoId;

  @OneToOne
  @JoinColumn(name = "estado_validacion_ip_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTOFACTURACION_ESTADOVALIDACIONIP"))
  private EstadoValidacionIP estadoValidacionIP;

  @ManyToOne
  @JoinColumn(name = "tipo_facturacion_id", nullable = true, foreignKey = @ForeignKey(name = "FK_PROYECTOFACTURACION_TIPOFACTURACION"))
  private TipoFacturacion tipoFacturacion;

  @Column(name = "proyecto_prorroga_id", nullable = true)
  private Long proyectoProrrogaId;

  @Column(name = "proyecto_sge_ref", nullable = true)
  private String proyectoSgeRef;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "proyecto_id", nullable = false, insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOFACTURACION_PROYECTO"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Proyecto proyecto = null;

  @ManyToOne
  @JoinColumn(name = "proyecto_prorroga_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROYECTOFACTURACION_PROYECTOPRORROGA"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ProyectoProrroga prorroga = null;

}
