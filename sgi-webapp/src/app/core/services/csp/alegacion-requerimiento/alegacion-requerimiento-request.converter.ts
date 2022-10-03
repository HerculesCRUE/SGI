import { IAlegacionRequerimiento } from '@core/models/csp/alegacion-requerimiento';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IAlegacionRequerimientoRequest } from './alegacion-requerimiento-request';

class AlegacionRequerimientoRequestConverter
  extends SgiBaseConverter<IAlegacionRequerimientoRequest, IAlegacionRequerimiento> {
  toTarget(value: IAlegacionRequerimientoRequest): IAlegacionRequerimiento {
    if (!value) {
      return value as unknown as IAlegacionRequerimiento;
    }
    return {
      id: null,
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
      observaciones: value.observaciones,
      requerimientoJustificacion: value.requerimientoJustificacionId ?
        { id: value.requerimientoJustificacionId } as IRequerimientoJustificacion : null
    };
  }

  fromTarget(value: IAlegacionRequerimiento): IAlegacionRequerimientoRequest {
    if (!value) {
      return value as unknown as IAlegacionRequerimientoRequest;
    }
    return {
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
      observaciones: value.observaciones,
      requerimientoJustificacionId: value.requerimientoJustificacion.id
    };
  }
}

export const ALEGACION_REQUERIMIENTO_REQUEST_CONVERTER = new AlegacionRequerimientoRequestConverter();
