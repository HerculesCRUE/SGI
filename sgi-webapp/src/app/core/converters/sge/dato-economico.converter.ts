import { IDatoEconomicoBackend } from '@core/models/sge/backend/dato-economico-backend';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class DatoEconomicoConverter extends SgiBaseConverter<IDatoEconomicoBackend, IDatoEconomico> {
  toTarget(value: IDatoEconomicoBackend): IDatoEconomico {
    if (!value) {
      return value as unknown as IDatoEconomico;
    }
    return {
      id: value.id,
      anualidad: value.anualidad,
      clasificacionSGE: value.clasificacionSGE,
      codigoEconomico: value.codigoEconomico,
      columnas: value.columnas,
      fechaDevengo: LuxonUtils.fromBackend(value.fechaDevengo),
      partidaPresupuestaria: value.partidaPresupuestaria,
      proyectoId: value.proyectoId,
      tipo: value.tipo
    };
  }

  fromTarget(value: IDatoEconomico): IDatoEconomicoBackend {
    if (!value) {
      return value as unknown as IDatoEconomicoBackend;
    }
    return {
      id: value.id,
      anualidad: value.anualidad,
      clasificacionSGE: value.clasificacionSGE,
      codigoEconomico: value.codigoEconomico,
      columnas: value.columnas,
      fechaDevengo: LuxonUtils.toBackend(value.fechaDevengo),
      partidaPresupuestaria: value.partidaPresupuestaria,
      proyectoId: value.proyectoId,
      tipo: value.tipo
    };
  }
}

export const DATO_ECONOMICO_CONVERTER = new DatoEconomicoConverter();
