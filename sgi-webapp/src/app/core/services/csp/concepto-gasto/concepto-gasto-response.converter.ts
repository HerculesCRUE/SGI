import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConceptoGastoResponse } from './concepto-gasto-response';

class ConceptoGastoResponseConverter extends SgiBaseConverter<IConceptoGastoResponse, IConceptoGasto> {
  toTarget(value: IConceptoGastoResponse): IConceptoGasto {
    if (!value) {
      return value as unknown as IConceptoGasto;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.descripcion) : [],
      costesIndirectos: value.costesIndirectos,
      activo: value.activo
    };
  }
  fromTarget(value: IConceptoGasto): IConceptoGastoResponse {
    if (!value) {
      return value as unknown as IConceptoGastoResponse;
    }
    return {
      id: value.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      descripcion: value.descripcion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.descripcion) : [],
      costesIndirectos: value.costesIndirectos,
      activo: value.activo
    };
  }
}

export const CONCEPTO_GASTO_RESPONSE_CONVERTER = new ConceptoGastoResponseConverter();
