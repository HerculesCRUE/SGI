import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { CONCEPTO_GASTO_RESPONSE_CONVERTER } from '@core/services/csp/concepto-gasto/concepto-gasto-response.converter';
import { IProyectoConceptoGastoResponse } from '@core/services/csp/proyecto-concepto-gasto/proyecto-concepto-gasto-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoConceptoGastoResponseConverter extends SgiBaseConverter<IProyectoConceptoGastoResponse, IProyectoConceptoGasto> {

  toTarget(value: IProyectoConceptoGastoResponse): IProyectoConceptoGasto {
    if (!value) {
      return value as unknown as IProyectoConceptoGasto;
    }
    return {
      id: value.id,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.toTarget(value.conceptoGasto) : null,
      proyectoId: value.proyectoId,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      importeMaximo: value.importeMaximo,
      permitido: value.permitido,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      convocatoriaConceptoGastoId: value.convocatoriaConceptoGastoId
    };
  }

  fromTarget(value: IProyectoConceptoGasto): IProyectoConceptoGastoResponse {
    if (!value) {
      return value as unknown as IProyectoConceptoGastoResponse;
    }
    return {
      id: value.id,
      conceptoGasto: value.conceptoGasto ? CONCEPTO_GASTO_RESPONSE_CONVERTER.fromTarget(value.conceptoGasto) : null,
      proyectoId: value.proyectoId,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      importeMaximo: value.importeMaximo,
      permitido: value.permitido,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      convocatoriaConceptoGastoId: value.convocatoriaConceptoGastoId
    };
  }
}

export const PROYECTO_CONCEPTO_GASTO_RESPONSE_CONVERTER = new ProyectoConceptoGastoResponseConverter();
