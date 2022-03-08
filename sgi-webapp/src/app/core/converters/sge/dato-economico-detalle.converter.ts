import { IDatoEconomicoDetalleBackend } from '@core/models/sge/backend/dato-economico-detalle-backend';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class DatoEconomicoDetalleConverter extends SgiBaseConverter<IDatoEconomicoDetalleBackend, IDatoEconomicoDetalle> {
  toTarget(value: IDatoEconomicoDetalleBackend): IDatoEconomicoDetalle {
    if (!value) {
      return value as unknown as IDatoEconomicoDetalle;
    }
    return {
      id: value.id,
      anualidad: value.anualidad,
      campos: value.campos,
      clasificacionSGE: value.clasificacionSGE,
      codigoEconomico: value.codigoEconomico,
      documentos: value.documentos,
      fechaDevengo: LuxonUtils.fromBackend(value.fechaDevengo),
      partidaPresupuestaria: value.partidaPresupuestaria,
      proyectoId: value.proyectoId,
    };
  }

  fromTarget(value: IDatoEconomicoDetalle): IDatoEconomicoDetalleBackend {
    if (!value) {
      return value as unknown as IDatoEconomicoDetalleBackend;
    }
    return {
      id: value.id,
      anualidad: value.anualidad,
      campos: value.campos,
      clasificacionSGE: value.clasificacionSGE,
      codigoEconomico: value.codigoEconomico,
      documentos: value.documentos,
      fechaDevengo: LuxonUtils.toBackend(value.fechaDevengo),
      partidaPresupuestaria: value.partidaPresupuestaria,
      proyectoId: value.proyectoId,
    };
  }
}

export const DATO_ECONOMICO_DETALLE_CONVERTER = new DatoEconomicoDetalleConverter();
