import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { ITipoAmbitoGeografico, ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFuenteFinanciacionResponse } from './fuente-financiacion-response';

class FuenteFinanciacionResponseConverter extends SgiBaseConverter<IFuenteFinanciacionResponse, IFuenteFinanciacion> {
  toTarget(value: IFuenteFinanciacionResponse): IFuenteFinanciacion {
    if (!value) {
      return value as unknown as IFuenteFinanciacion;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      fondoEstructural: value.fondoEstructural,
      tipoAmbitoGeografico: {
        id: value.tipoAmbitoGeografico.id,
        nombre: value.tipoAmbitoGeografico.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.tipoAmbitoGeografico.nombre) : [],
      } as ITipoAmbitoGeografico,
      tipoOrigenFuenteFinanciacion: {
        id: value.tipoOrigenFuenteFinanciacion.id,
        nombre: value.tipoOrigenFuenteFinanciacion.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.tipoOrigenFuenteFinanciacion.nombre) : [],
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
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      fondoEstructural: value.fondoEstructural,
      tipoAmbitoGeografico: {
        id: value.tipoAmbitoGeografico.id,
        nombre: value.tipoAmbitoGeografico.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.tipoAmbitoGeografico.nombre) : [],
      },
      tipoOrigenFuenteFinanciacion: {
        id: value.tipoOrigenFuenteFinanciacion.id,
        nombre: value.tipoOrigenFuenteFinanciacion.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.tipoOrigenFuenteFinanciacion.nombre) : [],
      },
      activo: value.activo
    };
  }
}

export const FUENTE_FINANCIACION_RESPONSE_CONVERTER = new FuenteFinanciacionResponseConverter();
