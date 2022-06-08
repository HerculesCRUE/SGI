import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { IModulador } from '@core/models/prc/modulador';
import { IAreaConocimiento } from '@core/models/sgo/area-conocimiento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IModuladorRequest } from './modulador-request';

class ModuladorRequestConverter extends SgiBaseConverter<IModuladorRequest, IModulador>{
  toTarget(value: IModuladorRequest): IModulador {
    if (!value) {
      return value as unknown as IModulador;
    }
    return {
      id: undefined,
      area: value.areaRef ? { id: value.areaRef } as IAreaConocimiento : null,
      tipo: value.tipo,
      valor1: value.valor1,
      valor2: value.valor2,
      valor3: value.valor3,
      valor4: value.valor4,
      valor5: value.valor5,
      convocatoriaBaremacion: value.convocatoriaBaremacionId ? { id: value.convocatoriaBaremacionId } as IConvocatoriaBaremacion : null
    };
  }
  fromTarget(value: IModulador): IModuladorRequest {
    if (!value) {
      return value as unknown as IModuladorRequest;
    }
    return {
      areaRef: value.area?.id,
      tipo: value.tipo,
      valor1: value.valor1,
      valor2: value.valor2,
      valor3: value.valor3,
      valor4: value.valor4,
      valor5: value.valor5,
      convocatoriaBaremacionId: value.convocatoriaBaremacion?.id
    };
  }
}

export const MODULADOR_REQUEST_CONVERTER = new ModuladorRequestConverter();
