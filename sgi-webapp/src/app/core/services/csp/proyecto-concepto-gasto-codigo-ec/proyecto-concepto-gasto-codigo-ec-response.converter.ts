
import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { ICodigoEconomicoGasto } from '@core/models/sge/codigo-economico-gasto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoConceptoGastoCodigoEcResponse } from './proyecto-concepto-gasto-codigo-ec-response';

class ProyectoConceptoGastoCodigoEcResponseConverter extends
  SgiBaseConverter<IProyectoConceptoGastoCodigoEcResponse, IProyectoConceptoGastoCodigoEc> {

  toTarget(value: IProyectoConceptoGastoCodigoEcResponse): IProyectoConceptoGastoCodigoEc {
    if (!value) {
      return value as unknown as IProyectoConceptoGastoCodigoEc;
    }
    return {
      id: value.id,
      proyectoConceptoGasto: { id: value.proyectoConceptoGastoId } as IProyectoConceptoGasto,
      codigoEconomico: { id: value.codigoEconomicoRef } as ICodigoEconomicoGasto,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      convocatoriaConceptoGastoCodigoEcId: value.convocatoriaConceptoGastoCodigoEcId
    };
  }

  fromTarget(value: IProyectoConceptoGastoCodigoEc): IProyectoConceptoGastoCodigoEcResponse {
    if (!value) {
      return value as unknown as IProyectoConceptoGastoCodigoEcResponse;
    }
    return {
      id: value.id,
      proyectoConceptoGastoId: value.proyectoConceptoGasto?.id,
      codigoEconomicoRef: value.codigoEconomico.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      convocatoriaConceptoGastoCodigoEcId: value.convocatoriaConceptoGastoCodigoEcId
    };
  }
}

export const PROYECTO_CONCEPTO_GASTO_CODIGO_EC_RESPONSE_CONVERTER = new ProyectoConceptoGastoCodigoEcResponseConverter();
