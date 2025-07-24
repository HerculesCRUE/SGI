import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IAlegacionRequerimiento } from '@core/models/csp/alegacion-requerimiento';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAlegacionRequerimientoResponse } from './alegacion-requerimiento-response';

class AlegacionRequerimientoResponseConverter
  extends SgiBaseConverter<IAlegacionRequerimientoResponse, IAlegacionRequerimiento> {
  toTarget(value: IAlegacionRequerimientoResponse): IAlegacionRequerimiento {
    if (!value) {
      return value as unknown as IAlegacionRequerimiento;
    }
    return {
      id: value.id,
      fechaAlegacion: LuxonUtils.fromBackend(value.fechaAlegacion),
      fechaReintegro: LuxonUtils.fromBackend(value.fechaReintegro),
      importeAlegado: value.importeAlegado,
      importeAlegadoCd: value.importeAlegadoCd,
      importeAlegadoCi: value.importeAlegadoCi,
      importeReintegrado: value.importeReintegrado,
      importeReintegradoCd: value.importeReintegradoCd,
      importeReintegradoCi: value.importeReintegradoCi,
      interesesReintegrados: value.interesesReintegrados,
      justificanteReintegro: value.justificanteReintegro,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      requerimientoJustificacion: value.requerimientoJustificacionId ?
        { id: value.requerimientoJustificacionId } as IRequerimientoJustificacion : null
    };
  }

  fromTarget(value: IAlegacionRequerimiento): IAlegacionRequerimientoResponse {
    if (!value) {
      return value as unknown as IAlegacionRequerimientoResponse;
    }
    return {
      id: value.id,
      fechaAlegacion: LuxonUtils.toBackend(value.fechaAlegacion),
      fechaReintegro: LuxonUtils.toBackend(value.fechaReintegro),
      importeAlegado: value.importeAlegado,
      importeAlegadoCd: value.importeAlegadoCd,
      importeAlegadoCi: value.importeAlegadoCi,
      importeReintegrado: value.importeReintegrado,
      importeReintegradoCd: value.importeReintegradoCd,
      importeReintegradoCi: value.importeReintegradoCi,
      interesesReintegrados: value.interesesReintegrados,
      justificanteReintegro: value.justificanteReintegro,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      requerimientoJustificacionId: value.requerimientoJustificacion.id
    };
  }
}

export const ALEGACION_REQUERIMIENTO_RESPONSE_CONVERTER = new AlegacionRequerimientoResponseConverter();
