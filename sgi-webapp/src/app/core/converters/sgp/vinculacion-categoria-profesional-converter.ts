import { IVinculacionCategoriaProfesionalBackend } from '@core/models/sgp/backend/vinculacion-categoria-profesional-backend';
import { IVinculacionCategoriaProfesional } from '@core/models/sgp/vinculacion-categoria-profesional';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class VinculacionCategoriaProfesionalConverter extends SgiBaseConverter<IVinculacionCategoriaProfesionalBackend, IVinculacionCategoriaProfesional> {
  toTarget(value: IVinculacionCategoriaProfesionalBackend): IVinculacionCategoriaProfesional {
    if (!value) {
      return value as unknown as IVinculacionCategoriaProfesional;
    }
    return {
      id: value.id,
      categoriaProfesional: value.categoriaProfesional,
      fechaObtencion: LuxonUtils.fromBackend(value.fechaObtencion),
    };
  }

  fromTarget(value: IVinculacionCategoriaProfesional): IVinculacionCategoriaProfesionalBackend {
    if (!value) {
      return value as unknown as IVinculacionCategoriaProfesionalBackend;
    }
    return {
      id: value.id,
      categoriaProfesional: value.categoriaProfesional,
      fechaObtencion: LuxonUtils.toBackend(value.fechaObtencion),
    };
  }
}

export const VINCULACION_CATEGORIA_PROFESIONAL_CONVERTER = new VinculacionCategoriaProfesionalConverter();
