import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipo-origen-fuente-financiacion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFuenteFinanciacionResponse } from './fuente-financiacion-response';

class FuenteFinanciacionResponseConverter extends SgiBaseConverter<IFuenteFinanciacionResponse, IFuenteFinanciacion>{
  toTarget(value: IFuenteFinanciacionResponse): IFuenteFinanciacion {
    if (!value) {
      return value as unknown as IFuenteFinanciacion;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      descripcion: value.descripcion,
      fondoEstructural: value.fondoEstructural,
      tipoAmbitoGeografico: {
        id: value.tipoAmbitoGeografico.id,
        nombre: value.tipoAmbitoGeografico.nombre
      } as ITipoAmbitoGeografico,
      tipoOrigenFuenteFinanciacion: {
        id: value.tipoOrigenFuenteFinanciacion.id,
        nombre: value.tipoOrigenFuenteFinanciacion.nombre
      } as ITipoOrigenFuenteFinanciacion,
      activo: value.activo
    };
  }
  fromTarget(value: IFuenteFinanciacion): IFuenteFinanciacionResponse {
    if (!value) {
      return value as unknown as IFuenteFinanciacionResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre,
      descripcion: value.descripcion,
      fondoEstructural: value.fondoEstructural,
      tipoAmbitoGeografico: {
        id: value.tipoAmbitoGeografico.id,
        nombre: value.tipoAmbitoGeografico.nombre
      },
      tipoOrigenFuenteFinanciacion: {
        id: value.tipoOrigenFuenteFinanciacion.id,
        nombre: value.tipoOrigenFuenteFinanciacion.nombre
      },
      activo: value.activo
    };
  }
}

export const FUENTE_FINANCIACION_RESPONSE_CONVERTER = new FuenteFinanciacionResponseConverter();
