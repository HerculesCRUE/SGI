import { IConfiguracionBaremo } from '@core/models/prc/configuracion-baremo';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConfiguracionBaremoResponse } from './configuracion-baremo-response';

class ConfiguracionBaremoResponseConverter extends SgiBaseConverter<IConfiguracionBaremoResponse, IConfiguracionBaremo>{
  toTarget(value: IConfiguracionBaremoResponse): IConfiguracionBaremo {
    if (!value) {
      return value as unknown as IConfiguracionBaremo;
    }
    return {
      id: value.id,
      epigrafe: value.epigrafe,
      nombre: value.nombre,
      padre: value.padreId !== null ? { id: value.padreId } as IConfiguracionBaremo : null,
      prioridad: value.prioridad,
      tipoBaremo: value.tipoBaremo,
      tipoFuente: value.tipoFuente,
      tipoNodo: value.tipoNodo,
      tipoPuntos: value.tipoPuntos
    };
  }
  fromTarget(value: IConfiguracionBaremo): IConfiguracionBaremoResponse {
    if (!value) {
      return value as unknown as IConfiguracionBaremoResponse;
    }
    return {
      id: value.id,
      epigrafe: value.epigrafe,
      nombre: value.nombre,
      padreId: value.padre?.id,
      prioridad: value.prioridad,
      tipoBaremo: value.tipoBaremo,
      tipoFuente: value.tipoFuente,
      tipoNodo: value.tipoNodo,
      tipoPuntos: value.tipoPuntos
    };
  }
}

export const CONFIGURACION_BAREMO_RESPONSE_CONVERTER = new ConfiguracionBaremoResponseConverter();
