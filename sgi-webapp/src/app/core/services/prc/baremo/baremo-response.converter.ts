import { IBaremo } from '@core/models/prc/baremo';
import { IConfiguracionBaremo } from '@core/models/prc/configuracion-baremo';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IBaremoResponse } from './baremo-response';

class BaremoResponseConverter extends SgiBaseConverter<IBaremoResponse, IBaremo>{
  toTarget(value: IBaremoResponse): IBaremo {
    if (!value) {
      return value as unknown as IBaremo;
    }
    return {
      id: value.id,
      configuracionBaremo: value.configuracionBaremoId !== null ?
        { id: value.configuracionBaremoId } as IConfiguracionBaremo : null,
      convocatoriaBaremacion: value.convocatoriaBaremacionId !== null ?
        { id: value.convocatoriaBaremacionId } as IConvocatoriaBaremacion : null,
      cuantia: value.cuantia,
      peso: value.peso,
      puntos: value.puntos,
      tipoCuantia: value.tipoCuantia
    };
  }
  fromTarget(value: IBaremo): IBaremoResponse {
    if (!value) {
      return value as unknown as IBaremoResponse;
    }
    return {
      id: value.id,
      configuracionBaremoId: value.configuracionBaremo?.id,
      convocatoriaBaremacionId: value.convocatoriaBaremacion?.id,
      cuantia: value.cuantia,
      peso: value.peso,
      puntos: value.puntos,
      tipoCuantia: value.tipoCuantia
    };
  }
}

export const BAREMO_RESPONSE_CONVERTER = new BaremoResponseConverter();
