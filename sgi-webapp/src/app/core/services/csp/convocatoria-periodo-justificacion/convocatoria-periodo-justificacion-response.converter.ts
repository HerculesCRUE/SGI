import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { IConvocatoriaPeriodoJustificacionResponse } from '@core/services/csp/convocatoria-periodo-justificacion/convocatoria-periodo-justificacion-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaPeriodoJustificacionResponseConverter extends
  SgiBaseConverter<IConvocatoriaPeriodoJustificacionResponse, IConvocatoriaPeriodoJustificacion> {

  toTarget(value: IConvocatoriaPeriodoJustificacionResponse): IConvocatoriaPeriodoJustificacion {
    if (!value) {
      return value as unknown as IConvocatoriaPeriodoJustificacion;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicioPresentacion: LuxonUtils.fromBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.fromBackend(value.fechaFinPresentacion),
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      tipo: value.tipo
    };
  }

  fromTarget(value: IConvocatoriaPeriodoJustificacion): IConvocatoriaPeriodoJustificacionResponse {
    if (!value) {
      return value as unknown as IConvocatoriaPeriodoJustificacionResponse;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicioPresentacion: LuxonUtils.toBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.toBackend(value.fechaFinPresentacion),
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      tipo: value.tipo
    };
  }
}

export const CONVOCATORIA_PERIODO_JUSTIFICACION_RESPONSE_CONVERTER = new ConvocatoriaPeriodoJustificacionResponseConverter();
