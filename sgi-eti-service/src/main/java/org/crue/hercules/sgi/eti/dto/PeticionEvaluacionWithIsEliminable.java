package org.crue.hercules.sgi.eti.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import org.crue.hercules.sgi.eti.model.PeticionEvaluacion;
import org.crue.hercules.sgi.eti.model.PeticionEvaluacion.TipoValorSocial;
import org.crue.hercules.sgi.eti.model.TipoActividad;
import org.crue.hercules.sgi.eti.model.TipoInvestigacionTutelada;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeticionEvaluacionWithIsEliminable implements Serializable {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** identificador */
  private Long id;

  /** Referencia solicitud convocatoria */
  private String solicitudConvocatoriaRef;

  /** Código */
  private String codigo;

  /** Título */
  private String titulo;

  /** Tipo Actividad */
  private TipoActividad tipoActividad;

  /** Tipo Investigacion Tutelada */
  private TipoInvestigacionTutelada tipoInvestigacionTutelada;

  /** Fuente financiacion */
  private String fuenteFinanciacion;

  /** Fecha Inicio. */
  private Instant fechaInicio;

  /** Fecha Fin. */
  private Instant fechaFin;

  /** Resumen */
  private String resumen;

  /** Valor social */
  private TipoValorSocial valorSocial;

  /** Objetivos */
  private String objetivos;

  /** Diseño metodológico */
  private String disMetodologico;

  /** Tiene fondos propios */
  private Boolean tieneFondosPropios;

  /** Referencia usuario */
  private String personaRef;

  /** Activo */
  private Boolean activo;

  /** Is eliminable */
  private Boolean eliminable;

  /** Importe Financiación */
  private BigDecimal importeFinanciacion;

  public PeticionEvaluacionWithIsEliminable(PeticionEvaluacion peticionEvaluacion, Boolean eliminable) {
    this.id = peticionEvaluacion.getId();
    this.activo = peticionEvaluacion.getActivo();
    this.codigo = peticionEvaluacion.getCodigo();
    this.disMetodologico = peticionEvaluacion.getDisMetodologico();
    this.eliminable = eliminable;
    this.fechaFin = peticionEvaluacion.getFechaFin();
    this.fechaInicio = peticionEvaluacion.getFechaInicio();
    this.fuenteFinanciacion = peticionEvaluacion.getFuenteFinanciacion();
    this.objetivos = peticionEvaluacion.getObjetivos();
    this.personaRef = peticionEvaluacion.getPersonaRef();
    this.resumen = peticionEvaluacion.getResumen();
    this.solicitudConvocatoriaRef = peticionEvaluacion.getSolicitudConvocatoriaRef();
    this.tieneFondosPropios = peticionEvaluacion.getTieneFondosPropios();
    this.tipoActividad = peticionEvaluacion.getTipoActividad();
    this.tipoInvestigacionTutelada = peticionEvaluacion.getTipoInvestigacionTutelada();
    this.titulo = peticionEvaluacion.getTitulo();
    this.valorSocial = peticionEvaluacion.getValorSocial();
    this.importeFinanciacion = peticionEvaluacion.getImporteFinanciacion();
  }
}