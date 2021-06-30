import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipo-origen-fuente-financiacion';
import { IResultadoInformePatentibilidad } from '@core/models/pii/resultado-informe-patentabilidad';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IResultadoInformePatentibilidadRequest } from './resultado-informe-patentabilidad-request';

class ResultadoInformePatentibilidadRequestConverter extends SgiBaseConverter<IResultadoInformePatentibilidadRequest, IResultadoInformePatentibilidad>{
  toTarget(value: IResultadoInformePatentibilidadRequest): IResultadoInformePatentibilidad {
    if (!value) {
      return value as unknown as IResultadoInformePatentibilidad;
    }
    return {
      id: undefined,
      nombre: value.nombre,
      descripcion: value.descripcion,
      activo: true
    };
  }
  fromTarget(value: IResultadoInformePatentibilidad): IResultadoInformePatentibilidadRequest {
    if (!value) {
      return value as unknown as IResultadoInformePatentibilidadRequest;
    }
    return {
      nombre: value.nombre,
      descripcion: value.descripcion,
    };
  }
}

export const RESULTADO_INFORME_PATENTABILIDAD_REQUEST_CONVERTER = new ResultadoInformePatentibilidadRequestConverter();
