package org.crue.hercules.sgi.rep.report.data.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenCosteIndirectoOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenInvestigadorOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenProduccionCientificaOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenSexenioOutput;
import org.crue.hercules.sgi.rep.dto.prc.DetalleGrupoInvestigacionOutput.ResumenTotalOutput;

import lombok.Getter;

@Getter
public class DetalleGrupoInvestigacionObject {

  private String grupo;
  private Integer anio;
  private List<ResumenInvestigadorObject> investigadores;
  private BigDecimal precioPuntoProduccion;
  private BigDecimal precioPuntoCostesIndirectos;
  private BigDecimal precioPuntoSexenio;

  private ResumenSexenioObject sexenios;
  private List<ResumenProduccionCientificaObject> produccionesCientificas;
  private ResumenCosteIndirectoObject costesIndirectos;
  private List<ResumenTotalObject> totales = new ArrayList<>();

  public DetalleGrupoInvestigacionObject(DetalleGrupoInvestigacionOutput dto) {
    this.grupo = dto.getGrupo();
    this.anio = dto.getAnio();
    if (!CollectionUtils.isEmpty(dto.getInvestigadores())) {
      this.investigadores = dto.getInvestigadores().stream().map(ResumenInvestigadorObject::new).toList();
    }
    this.precioPuntoProduccion = dto.getPrecioPuntoProduccion();
    this.precioPuntoCostesIndirectos = dto.getPrecioPuntoCostesIndirectos();
    this.precioPuntoSexenio = dto.getPrecioPuntoSexenio();
    if (!CollectionUtils.isEmpty(dto.getTotales())) {
      this.totales.addAll(dto.getTotales().stream().map(ResumenTotalObject::new).toList());
    }
    if (!CollectionUtils.isEmpty(dto.getProduccionesCientificas())) {
      this.produccionesCientificas = dto.getProduccionesCientificas().stream()
          .map(ResumenProduccionCientificaObject::new).toList();
      if (CollectionUtils.isEmpty(this.totales)) {
        this.totales = new ArrayList<>();
      }
      ListUtils.emptyIfNull(dto.getProduccionesCientificas()).stream().forEach(prc -> {
        ResumenTotalOutput dineroTotal = new ResumenTotalOutput();
        dineroTotal.setTipo(prc.getTipo());
        dineroTotal.setImporte(prc.getImporte());
        this.totales.add(new ResumenTotalObject(dineroTotal));
      });
    }
  }

  @Getter
  public static class ResumenInvestigadorObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String investigador;
    private BigDecimal puntosProduccion;
    private BigDecimal puntosCostesIndirectos;

    public ResumenInvestigadorObject(ResumenInvestigadorOutput dto) {
      this.investigador = dto.getInvestigador();
      this.puntosProduccion = dto.getPuntosProduccion();
      this.puntosCostesIndirectos = dto.getPuntosCostesIndirectos();
    }
  }

  @Getter
  public static class ResumenSexenioObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer numero;
    private BigDecimal puntos;
    private BigDecimal importe;

    public ResumenSexenioObject(ResumenSexenioOutput dto) {
      this.numero = dto.getNumero();
      this.puntos = dto.getPuntos();
      this.importe = dto.getImporte();
    }
  }

  @Getter
  public static class ResumenProduccionCientificaObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tipo;
    private Integer numero;
    private BigDecimal puntos;
    private BigDecimal importe;

    public ResumenProduccionCientificaObject(ResumenProduccionCientificaOutput dto) {
      this.tipo = dto.getTipo();
      this.numero = dto.getNumero();
      this.puntos = dto.getPuntos();
      this.importe = dto.getImporte();
    }
  }

  @Getter
  public static class ResumenCosteIndirectoObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer numero;
    private BigDecimal puntos;
    private BigDecimal importe;

    public ResumenCosteIndirectoObject(ResumenCosteIndirectoOutput dto) {
      this.numero = dto.getNumero();
      this.puntos = dto.getPuntos();
      this.importe = dto.getImporte();
    }
  }

  @Getter
  public static class ResumenTotalObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tipo;
    private BigDecimal importe;

    public ResumenTotalObject(ResumenTotalOutput dto) {
      this.tipo = dto.getTipo();
      this.importe = dto.getImporte();
    }
  }
}
