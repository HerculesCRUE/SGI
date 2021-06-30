import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipo-origen-fuente-financiacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFuenteFinanciacionRequest } from './fuente-financiacion-request';

class FuenteFinanciacionRequestConverter extends SgiBaseConverter<IFuenteFinanciacionRequest, IFuenteFinanciacion>{
  toTarget(value: IFuenteFinanciacionRequest): IFuenteFinanciacion {
    if (!value) {
      return value as unknown as IFuenteFinanciacion;
    }
    return {
      id: undefined,
      nombre: value.nombre,
      descripcion: value.descripcion,
      fondoEstructural: value.fondoEstructural,
      tipoAmbitoGeografico: { id: value.tipoAmbitoGeograficoId } as ITipoAmbitoGeografico,
      tipoOrigenFuenteFinanciacion: { id: value.tipoOrigenFuenteFinanciacionId } as ITipoOrigenFuenteFinanciacion,
      activo: true
    };
  }
  fromTarget(value: IFuenteFinanciacion): IFuenteFinanciacionRequest {
    if (!value) {
      return value as unknown as IFuenteFinanciacionRequest;
    }
    return {
      nombre: value.nombre,
      descripcion: value.descripcion,
      fondoEstructural: value.fondoEstructural,
      tipoAmbitoGeograficoId: value.tipoAmbitoGeografico?.id,
      tipoOrigenFuenteFinanciacionId: value.tipoOrigenFuenteFinanciacion?.id,
    };
  }
}

export const FUENTE_FINANCIACION_REQUEST_CONVERTER = new FuenteFinanciacionRequestConverter();
