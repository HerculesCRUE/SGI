import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { ITipoAmbitoGeografico, ITipoOrigenFuenteFinanciacion } from '@core/models/csp/tipos-configuracion';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IFuenteFinanciacionRequest } from './fuente-financiacion-request';

class FuenteFinanciacionRequestConverter extends SgiBaseConverter<IFuenteFinanciacionRequest, IFuenteFinanciacion> {
  toTarget(value: IFuenteFinanciacionRequest): IFuenteFinanciacion {
    if (!value) {
      return value as unknown as IFuenteFinanciacion;
    }
    return {
      id: undefined,
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.descripcion) : [],
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
      nombre: value.nombre ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.descripcion) : [],
      fondoEstructural: value.fondoEstructural,
      tipoAmbitoGeograficoId: value.tipoAmbitoGeografico?.id,
      tipoOrigenFuenteFinanciacionId: value.tipoOrigenFuenteFinanciacion?.id,
    };
  }
}

export const FUENTE_FINANCIACION_REQUEST_CONVERTER = new FuenteFinanciacionRequestConverter();
