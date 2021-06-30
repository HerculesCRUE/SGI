import { IConvocatoriaPeriodoJustificacionBackend } from '@core/models/csp/backend/convocatoria-periodo-justificacion-backend';
import { IConvocatoriaPeriodoJustificacion } from '@core/models/csp/convocatoria-periodo-justificacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaPeriodoJustificacionConverter extends
  SgiBaseConverter<IConvocatoriaPeriodoJustificacionBackend, IConvocatoriaPeriodoJustificacion> {

  toTarget(value: IConvocatoriaPeriodoJustificacionBackend): IConvocatoriaPeriodoJustificacion {
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
      observaciones: value.observaciones,
      tipo: value.tipo
    };
  }

  fromTarget(value: IConvocatoriaPeriodoJustificacion): IConvocatoriaPeriodoJustificacionBackend {
    if (!value) {
      return value as unknown as IConvocatoriaPeriodoJustificacionBackend;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      numPeriodo: value.numPeriodo,
      mesInicial: value.mesInicial,
      mesFinal: value.mesFinal,
      fechaInicioPresentacion: LuxonUtils.toBackend(value.fechaInicioPresentacion),
      fechaFinPresentacion: LuxonUtils.toBackend(value.fechaFinPresentacion),
      observaciones: value.observaciones,
      tipo: value.tipo
    };
  }
}

export const CONVOCATORIA_PERIODO_JUSTIFICACION_CONVERTER = new ConvocatoriaPeriodoJustificacionConverter();
