import { IConvocatoriaPeriodoSeguimientoCientificoBackend } from '@core/models/csp/backend/convocatoria-periodo-seguimiento-cientifico-backend';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaSeguimientoCientificoConverter extends
  SgiBaseConverter<IConvocatoriaPeriodoSeguimientoCientificoBackend, IConvocatoriaPeriodoSeguimientoCientifico> {

  toTarget(value: IConvocatoriaPeriodoSeguimientoCientificoBackend): IConvocatoriaPeriodoSeguimientoCientifico {
    if (!value) {
      return value as unknown as IConvocatoriaPeriodoSeguimientoCientifico;
    }
    return {
      id: value.id,
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicioPresentacion: LuxonUtils.fromBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.fromBackend(value.fechaFinPresentacion),
      tipoSeguimiento: value.tipoSeguimiento,
      observaciones: value.observaciones,
      convocatoriaId: value.convocatoriaId
    };
  }

  fromTarget(value: IConvocatoriaPeriodoSeguimientoCientifico): IConvocatoriaPeriodoSeguimientoCientificoBackend {
    if (!value) {
      return value as unknown as IConvocatoriaPeriodoSeguimientoCientificoBackend;
    }
    return {
      id: value.id,
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicioPresentacion: LuxonUtils.toBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.toBackend(value.fechaFinPresentacion),
      tipoSeguimiento: value.tipoSeguimiento,
      observaciones: value.observaciones,
      convocatoriaId: value.convocatoriaId
    };
  }
}

export const CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_CONVERTER = new ConvocatoriaSeguimientoCientificoConverter();
