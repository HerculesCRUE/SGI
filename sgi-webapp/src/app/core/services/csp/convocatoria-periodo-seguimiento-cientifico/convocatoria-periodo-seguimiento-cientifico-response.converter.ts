import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { IConvocatoriaPeriodoSeguimientoCientificoResponse } from '@core/services/csp/convocatoria-periodo-seguimiento-cientifico/convocatoria-periodo-seguimiento-cientifico-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaSeguimientoCientificoResponseConverter extends
  SgiBaseConverter<IConvocatoriaPeriodoSeguimientoCientificoResponse, IConvocatoriaPeriodoSeguimientoCientifico> {

  toTarget(value: IConvocatoriaPeriodoSeguimientoCientificoResponse): IConvocatoriaPeriodoSeguimientoCientifico {
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
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      convocatoriaId: value.convocatoriaId
    };
  }

  fromTarget(value: IConvocatoriaPeriodoSeguimientoCientifico): IConvocatoriaPeriodoSeguimientoCientificoResponse {
    if (!value) {
      return value as unknown as IConvocatoriaPeriodoSeguimientoCientificoResponse;
    }
    return {
      id: value.id,
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicioPresentacion: LuxonUtils.toBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.toBackend(value.fechaFinPresentacion),
      tipoSeguimiento: value.tipoSeguimiento,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      convocatoriaId: value.convocatoriaId
    };
  }
}

export const CONVOCATORIA_PERIODO_SEGUIMIENTO_CIENTIFICO_RESPONSE_CONVERTER = new ConvocatoriaSeguimientoCientificoResponseConverter();
