import { IConvocatoriaFaseBackend } from '@core/models/csp/backend/convocatoria-fase-backend';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaFaseConverter extends SgiBaseConverter<IConvocatoriaFaseBackend, IConvocatoriaFase> {

  toTarget(value: IConvocatoriaFaseBackend): IConvocatoriaFase {
    if (!value) {
      return value as unknown as IConvocatoriaFase;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      tipoFase: value.tipoFase,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      observaciones: value.observaciones
    };
  }

  fromTarget(value: IConvocatoriaFase): IConvocatoriaFaseBackend {
    if (!value) {
      return value as unknown as IConvocatoriaFaseBackend;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      tipoFase: value.tipoFase,
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      observaciones: value.observaciones
    };
  }
}

export const CONVOCATORIA_FASE_CONVERTER = new ConvocatoriaFaseConverter();
