package org.crue.hercules.sgi.prc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.crue.hercules.sgi.prc.enums.EpigrafeCVN;
import org.crue.hercules.sgi.prc.model.converter.EpigrafeCVNConverter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = ConfiguracionBaremo.TABLE_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConfiguracionBaremo extends BaseActivableEntity implements Serializable {

  protected static final String TABLE_NAME = "configuracion_baremo";
  private static final String SEQUENCE_NAME = TABLE_NAME + "_seq";

  public static final List<TipoNodo> TIPO_NODOS_PESO = Collections
      .unmodifiableList(new ArrayList<>(Arrays.asList(TipoNodo.PESO, TipoNodo.PESO_CUANTIA, TipoNodo.PESO_PUNTOS)));
  public static final List<TipoNodo> TIPO_NODOS_PUNTOS = Collections
      .unmodifiableList(new ArrayList<>(Arrays.asList(TipoNodo.PUNTOS, TipoNodo.PESO_PUNTOS)));

  public enum TipoBaremo {
    PRODUCCION_CIENTIFICA,
    SEXENIO,
    COSTE_INDIRECTO,
    // LIBROS
    LIBROS,
    AUTORIA_BCI_EDITORIAL_EXTRANJERA,
    CAP_LIBRO_BCI_EDITORIAL_EXTRANJERA,
    EDICION_BCI_EDITORIAL_EXTRANJERA,
    COMENTARIO_BCI_EDITORIAL_EXTRANJERA,
    AUTORIA_BCI_EDITORIAL_NACIONAL,
    CAP_LIBRO_BCI_EDITORIAL_NACIONAL,
    EDICION_BCI_EDITORIAL_NACIONAL,
    COMENTARIO_BCI_EDITORIAL_NACIONAL,
    AUTORIA_ICEE_Q1,
    CAP_LIBRO_ICEE_Q1,
    EDICION_ICEE_Q1,
    COMENTARIO_ICEE_Q1,
    AUTORIA_ICEE_RESTO_CUARTILES,
    CAP_LIBRO_ICEE_RESTO_CUARTILES,
    EDICION_ICEE_RESTO_CUARTILES,
    COMENTARIO_ICEE_RESTO_CUARTILES,
    AUTORIA_DIALNET,
    CAP_LIBRO_DIALNET,
    EDICION_DIALNET,
    COMENTARIO_DIALNET,
    AUTORIA_OTRAS,
    CAP_LIBRO_OTRAS,
    EDICION_OTRAS,
    COMENTARIO_OTRAS,
    LIBRO_NUMERO_AUTORES,
    LIBRO_EDITORIAL_PRESTIGIO,
    // ARTICULOS
    ARTICULOS,
    ARTICULO_JCR_Q1,
    ARTICULO_JCR_Q2,
    ARTICULO_JCR_Q3,
    ARTICULO_JCR_Q4,
    ARTICULO_CITEC_Q1,
    ARTICULO_CITEC_Q2,
    ARTICULO_CITEC_Q3,
    ARTICULO_CITEC_Q4,
    ARTICULO_SCOPUS_Q1,
    ARTICULO_SCOPUS_Q2,
    ARTICULO_SCOPUS_Q3,
    ARTICULO_SCOPUS_Q4,
    ARTICULO_SCIMAGO_Q1,
    ARTICULO_SCIMAGO_Q2,
    ARTICULO_SCIMAGO_Q3,
    ARTICULO_SCIMAGO_Q4,
    ARTICULO_ERIH_Q1,
    ARTICULO_ERIH_Q2,
    ARTICULO_ERIH_Q3,
    ARTICULO_ERIH_Q4,
    ARTICULO_DIALNET_Q1,
    ARTICULO_DIALNET_Q2,
    ARTICULO_DIALNET_Q3,
    ARTICULO_DIALNET_Q4,
    ARTICULO_MIAR_Q1,
    ARTICULO_MIAR_Q2,
    ARTICULO_MIAR_Q3,
    ARTICULO_MIAR_Q4,
    ARTICULO_FECYT_Q1,
    ARTICULO_FECYT_Q2,
    ARTICULO_FECYT_Q3,
    ARTICULO_FECYT_Q4,
    ARTICULO,
    ARTICULO_NATURE_O_SCIENCE,
    ARTICULO_INDICE_NORMALIZADO,
    ARTICULO_LIDERAZGO,
    ARTICULO_EXCELENCIA,
    ARTICULO_OPEN_ACCESS_ALL,
    ARTICULO_OPEN_ACCESS_GOLD,
    ARTICULO_OPEN_ACCESS_HYBRID_GOLD,
    ARTICULO_OPEN_ACCESS_BRONZE,
    ARTICULO_OPEN_ACCESS_GREEN,
    ARTICULO_INTERNACIONALIZACION,
    ARTICULO_INTERDISCIPLINARIEDAD,
    ARTICULO_JCR_Q1_DECIL1,
    ARTICULO_NUMERO_AUTORES,
    ARTICULO_AREAS,
    // Comites editoriales
    COMITES_EDITORIALES,
    COMITE_EDITORIAL_JCR_Q1,
    COMITE_EDITORIAL_JCR_Q2,
    COMITE_EDITORIAL_JCR_Q3,
    COMITE_EDITORIAL_JCR_Q4,
    COMITE_EDITORIAL_CITEC_Q1,
    COMITE_EDITORIAL_CITEC_Q2,
    COMITE_EDITORIAL_CITEC_Q3,
    COMITE_EDITORIAL_CITEC_Q4,
    COMITE_EDITORIAL_SCOPUS_Q1,
    COMITE_EDITORIAL_SCOPUS_Q2,
    COMITE_EDITORIAL_SCOPUS_Q3,
    COMITE_EDITORIAL_SCOPUS_Q4,
    COMITE_EDITORIAL_SCIMAGO_Q1,
    COMITE_EDITORIAL_SCIMAGO_Q2,
    COMITE_EDITORIAL_SCIMAGO_Q3,
    COMITE_EDITORIAL_SCIMAGO_Q4,
    COMITE_EDITORIAL_ERIH_Q1,
    COMITE_EDITORIAL_ERIH_Q2,
    COMITE_EDITORIAL_ERIH_Q3,
    COMITE_EDITORIAL_ERIH_Q4,
    COMITE_EDITORIAL_DIALNET_Q1,
    COMITE_EDITORIAL_DIALNET_Q2,
    COMITE_EDITORIAL_DIALNET_Q3,
    COMITE_EDITORIAL_DIALNET_Q4,
    COMITE_EDITORIAL_MIAR_Q1,
    COMITE_EDITORIAL_MIAR_Q2,
    COMITE_EDITORIAL_MIAR_Q3,
    COMITE_EDITORIAL_MIAR_Q4,
    COMITE_EDITORIAL_FECYT_Q1,
    COMITE_EDITORIAL_FECYT_Q2,
    COMITE_EDITORIAL_FECYT_Q3,
    COMITE_EDITORIAL_FECYT_Q4,
    // Comites editoriales EDITOR
    COMITE_EDITORIAL_JCR_Q1_EDITOR,
    COMITE_EDITORIAL_JCR_Q2_EDITOR,
    COMITE_EDITORIAL_JCR_Q3_EDITOR,
    COMITE_EDITORIAL_JCR_Q4_EDITOR,
    COMITE_EDITORIAL_CITEC_Q1_EDITOR,
    COMITE_EDITORIAL_CITEC_Q2_EDITOR,
    COMITE_EDITORIAL_CITEC_Q3_EDITOR,
    COMITE_EDITORIAL_CITEC_Q4_EDITOR,
    COMITE_EDITORIAL_SCOPUS_Q1_EDITOR,
    COMITE_EDITORIAL_SCOPUS_Q2_EDITOR,
    COMITE_EDITORIAL_SCOPUS_Q3_EDITOR,
    COMITE_EDITORIAL_SCOPUS_Q4_EDITOR,
    COMITE_EDITORIAL_SCIMAGO_Q1_EDITOR,
    COMITE_EDITORIAL_SCIMAGO_Q2_EDITOR,
    COMITE_EDITORIAL_SCIMAGO_Q3_EDITOR,
    COMITE_EDITORIAL_SCIMAGO_Q4_EDITOR,
    COMITE_EDITORIAL_ERIH_Q1_EDITOR,
    COMITE_EDITORIAL_ERIH_Q2_EDITOR,
    COMITE_EDITORIAL_ERIH_Q3_EDITOR,
    COMITE_EDITORIAL_ERIH_Q4_EDITOR,
    COMITE_EDITORIAL_DIALNET_Q1_EDITOR,
    COMITE_EDITORIAL_DIALNET_Q2_EDITOR,
    COMITE_EDITORIAL_DIALNET_Q3_EDITOR,
    COMITE_EDITORIAL_DIALNET_Q4_EDITOR,
    COMITE_EDITORIAL_MIAR_Q1_EDITOR,
    COMITE_EDITORIAL_MIAR_Q2_EDITOR,
    COMITE_EDITORIAL_MIAR_Q3_EDITOR,
    COMITE_EDITORIAL_MIAR_Q4_EDITOR,
    COMITE_EDITORIAL_FECYT_Q1_EDITOR,
    COMITE_EDITORIAL_FECYT_Q2_EDITOR,
    COMITE_EDITORIAL_FECYT_Q3_EDITOR,
    COMITE_EDITORIAL_FECYT_Q4_EDITOR,
    COMITE_EDITORIAL,

    // Congresos
    CONGRESOS,
    CONGRESO_GRUPO1_O_CORE_A_POR,
    CONGRESO_GRUPO1_O_CORE_A,
    CONGRESO_INTERNACIONAL_POSTER_O_CARTEL,
    CONGRESO_INTERNACIONAL_PONENCIA_ORAL_O_ESCRITA,
    CONGRESO_INTERNACIONAL_PLENARIA_O_KEYNOTE,
    CONGRESO_NACIONAL_POSTER_O_CARTEL,
    CONGRESO_NACIONAL_PONENCIA_ORAL_O_ESCRITA,
    CONGRESO_NACIONAL_PLENARIA_O_KEYNOTE,
    CONGRESO_RESUMEN_O_ABSTRACT,
    CONGRESO_INTERNACIONAL_OBRA_COLECTIVA,
    CONGRESO_NACIONAL_OBRA_COLECTIVA,

    // Direccion tesis
    DIRECCION_TESIS,
    DIRECCION_TESIS_TESIS,
    DIRECCION_TESIS_TESINA_O_DEA_O_TFM,
    DIRECCION_TESIS_MENCION_INDUSTRIAL,
    DIRECCION_TESIS_MENCION_INTERNACIONAL,

    // Obra artística
    OBRAS_ARTISTICAS,
    OBRA_ARTISTICA_EXP_GRUPO1_INDIVIDUAL,
    OBRA_ARTISTICA_EXP_GRUPO1_COLECTIVA,
    OBRA_ARTISTICA_EXP_GRUPO2_INDIVIDUAL,
    OBRA_ARTISTICA_EXP_GRUPO2_COLECTIVA,
    OBRA_ARTISTICA_EXP_GRUPO3,
    OBRA_ARTISTICA_EXP_GRUPO4,
    OBRA_ARTISTICA_DISENIO_GRUPO1,
    OBRA_ARTISTICA_DISENIO_GRUPO2,
    OBRA_ARTISTICA_DISENIO_GRUPO3,

    // Organización de actividades I+D+i
    ORGANIZACION_ACTIVIDADES,
    ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_NACIONAL,
    ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_INTERNACIONAL,
    ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_NACIONAL_PRESIDENTE,
    ORG_ACT_COMITE_CIENTIFICO_ORGANIZ_INTERNACIONAL_PRESIDENTE,

    // CONTRATOS
    CONTRATOS,
    CONTRATO_CUANTIA,

    // PROYECTOS_INVESTIGACION
    PROYECTOS_INVESTIGACION,
    PROYECTO_INVESTIGACION_REGIONAL,
    PROYECTO_INVESTIGACION_NACIONAL,
    PROYECTO_INVESTIGACION_EUROPEO,
    PROYECTO_INVESTIGACION_RESTO,
    PROYECTO_INVESTIGACION_REGIONAL_EXCELENCIA,
    PROYECTO_INVESTIGACION_NACIONAL_EXCELENCIA,
    PROYECTO_INVESTIGACION_REGIONAL_IP,
    PROYECTO_INVESTIGACION_NACIONAL_IP,
    PROYECTO_INVESTIGACION_EUROPEO_IP,
    PROYECTO_INVESTIGACION_RESTO_IP,

    // INVENCIONES
    INVENCIONES,
    INVENCION_PATENTE_NACIONAL,
    INVENCION_PATENTE_INTERNACIONAL,
    INVENCION_OTRO_NACIONAL,
    INVENCION_OTRO_INTERNACIONAL,
    INVENCION_LICENCIA_EXPLOTACION;

  }

  public enum TipoFuente {
    SGI,
    CVN,
    OTRO_SISTEMA,
    CVN_OTRO_SISTEMA;
  }

  public enum TipoPuntos {
    PRINCIPAL,
    EXTRA,
    MODULADOR;
  }

  public enum TipoNodo {
    PESO_PUNTOS,
    PESO_CUANTIA,
    PESO,
    NO_BAREMABLE,
    PUNTOS,
    SIN_PUNTOS;
  }

  private static final long serialVersionUID = 1L;

  /** Id */
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME)
  @SequenceGenerator(name = SEQUENCE_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
  private Long id;

  @Column(name = "nombre", length = BaseEntity.NOMBRE_CONF_BAREMOS_LENGTH, nullable = false)
  private String nombre;

  @Column(name = "prioridad", nullable = true)
  private Integer prioridad;

  @Column(name = "tipo_baremo", length = BaseEntity.TIPO_BAREMO_LENGTH, nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoBaremo tipoBaremo;

  @Column(name = "tipo_fuente", length = BaseEntity.TIPO_FUENTE_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private TipoFuente tipoFuente;

  @Column(name = "tipo_puntos", length = BaseEntity.TIPO_PUNTOS_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private TipoPuntos tipoPuntos;

  @Column(name = "tipo_nodo", length = BaseEntity.TIPO_NODO_LENGTH, nullable = true)
  @Enumerated(EnumType.STRING)
  private TipoNodo tipoNodo;

  /** EpigrafeCVN */
  @Column(name = "epigrafe_cvn", length = BaseEntity.EPIGRAFE_LENGTH, nullable = true)
  @Convert(converter = EpigrafeCVNConverter.class)
  private EpigrafeCVN epigrafeCVN;

  /** ConfiguracionBaremo padre. */
  @Column(name = "configuracion_baremo_padre_id", nullable = true)
  private Long padreId;

  // Relation mappings for JPA metamodel generation only
  @ManyToOne
  @JoinColumn(name = "configuracion_baremo_padre_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_CONFIGURACIONBAREMO_PADRE"))
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final ConfiguracionBaremo padre = null;

  @OneToMany(mappedBy = "configuracionBaremo")
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final List<Baremo> baremos = null;

}
