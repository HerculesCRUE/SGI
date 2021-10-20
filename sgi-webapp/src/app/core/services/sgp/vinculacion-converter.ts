import { IVinculacionBackend } from '@core/models/sgp/backend/vinculacion-backend';
import { IVinculacion } from '@core/models/sgp/vinculacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class VinculacionConverter extends SgiBaseConverter<IVinculacionBackend, IVinculacion> {
  toTarget(value: IVinculacionBackend): IVinculacion {
    if (!value) {
      return value as unknown as IVinculacion;
    }
    return {
      id: value.id,
      categoriaProfesional: value.categoriaProfesional,
      fechaObtencionCategoria: LuxonUtils.fromBackend(value.fechaObtencionCategoria)
    };
  }

  fromTarget(value: IVinculacion): IVinculacionBackend {
    if (!value) {
      return value as unknown as IVinculacionBackend;
    }
    return {
      id: value.id,
      categoriaProfesional: value.categoriaProfesional,
      fechaObtencionCategoria: LuxonUtils.toBackend(value.fechaObtencionCategoria)
    };
  }
}

export const VINCULACION_CONVERTER = new VinculacionConverter();
