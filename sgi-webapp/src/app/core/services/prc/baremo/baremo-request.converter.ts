import { IBaremo } from '@core/models/prc/baremo';
import { IConfiguracionBaremo } from '@core/models/prc/configuracion-baremo';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IBaremoRequest } from './baremo-request';

class BaremoRequestConverter extends SgiBaseConverter<IBaremoRequest, IBaremo>{
  toTarget(value: IBaremoRequest): IBaremo {
    if (!value) {
      return value as unknown as IBaremo;
    }
    return {
      id: null,
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
  fromTarget(value: IBaremo): IBaremoRequest {
    if (!value) {
      return value as unknown as IBaremoRequest;
    }
    return {
      configuracionBaremoId: value.configuracionBaremo?.id,
      convocatoriaBaremacionId: value.convocatoriaBaremacion?.id,
      cuantia: value.cuantia,
      peso: value.peso,
      puntos: value.puntos,
      tipoCuantia: value.tipoCuantia
    };
  }
}

export const BAREMO_REQUEST_CONVERTER = new BaremoRequestConverter();
