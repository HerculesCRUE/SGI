import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { MODELO_EJECUCION_RESPONSE_CONVERTER } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_FASE_RESPONSE_CONVERTER } from '../tipo-fase/tipo-fase-response.converter';
import { IModeloTipoFaseResponse } from './modelo-tipo-fase-response';

class ModeloTipoFaseResponseConverter extends SgiBaseConverter<IModeloTipoFaseResponse, IModeloTipoFase> {
  toTarget(value: IModeloTipoFaseResponse): IModeloTipoFase {
    if (!value) {
      return value as unknown as IModeloTipoFase;
    }
    return {
      id: value.id,
      modeloEjecucion: MODELO_EJECUCION_RESPONSE_CONVERTER.toTarget(value.modeloEjecucion),
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.toTarget(value.tipoFase) : null,
      convocatoria: value.convocatoria,
      proyecto: value.proyecto,
      solicitud: value.solicitud,
      activo: value.activo
    };
  }
  fromTarget(value: IModeloTipoFase): IModeloTipoFaseResponse {
    if (!value) {
      return value as unknown as IModeloTipoFaseResponse;
    }
    return {
      id: value.id,
      modeloEjecucion: MODELO_EJECUCION_RESPONSE_CONVERTER.fromTarget(value.modeloEjecucion),
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.fromTarget(value.tipoFase) : null,
      convocatoria: value.convocatoria,
      proyecto: value.proyecto,
      solicitud: value.solicitud,
      activo: value.activo
    };
  }
}

export const MODELO_TIPO_FASE_RESPONSE_CONVERTER = new ModeloTipoFaseResponseConverter();
