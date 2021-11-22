package org.crue.hercules.sgi.pii.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepartoCreateInput {
  @NotNull
  private Long invencionId;

  List<RepartoGastoCreateInput> gastos;

  @NotEmpty
  List<RepartoIngresoCreateInput> ingresos;

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RepartoGastoCreateInput {
    @NotNull
    private InvencionGastoCreateInput invencionGasto;

    @Min(0)
    @NotNull
    private BigDecimal importeADeducir;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class InvencionGastoCreateInput {
    private Long id;

    @NotNull
    private Long invencionId;

    private Long solicitudProteccionId;

    @NotEmpty
    private String gastoRef;

    @Min(0)
    private BigDecimal importePendienteDeducir;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RepartoIngresoCreateInput {
    @NotNull
    private InvencionIngresoCreateInput invencionIngreso;

    @Min(0)
    @NotNull
    private BigDecimal importeARepartir;
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class InvencionIngresoCreateInput {
    private Long id;

    @NotNull
    private Long invencionId;

    @NotEmpty
    private String ingresoRef;

    @Min(0)
    private BigDecimal importePendienteRepartir;
  }
}
