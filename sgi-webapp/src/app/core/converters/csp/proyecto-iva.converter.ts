import { IProyectoIVABackend } from '@core/models/csp/backend/proyecto-iva-backend';
import { IProyectoIVA } from '@core/models/csp/proyecto-iva';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoIVAConverter extends SgiBaseConverter<IProyectoIVABackend, IProyectoIVA> {

  toTarget(value: IProyectoIVABackend): IProyectoIVA {
    if (!value) {
      return value as unknown as IProyectoIVA;
    }
    return {
      id: value.id,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      proyectoId: value.proyectoId,
      iva: value.iva
    };
  }

  fromTarget(value: IProyectoIVA): IProyectoIVABackend {
    if (!value) {
      return value as unknown as IProyectoIVABackend;
    }
    return {
      id: value.id,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      proyectoId: value.proyectoId,
      iva: value.iva
    };
  }
}

export const PROYECTO_IVA_CONVERTER = new ProyectoIVAConverter();
