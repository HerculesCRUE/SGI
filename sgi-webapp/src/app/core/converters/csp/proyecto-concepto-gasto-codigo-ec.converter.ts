import { IProyectoConceptoGastoCodigoEcBackend } from '@core/models/csp/backend/proyecto-concepto-gasto-codigo-ec-backend';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoConceptoGastoCodigoEcConverter extends
  SgiBaseConverter<IProyectoConceptoGastoCodigoEcBackend, IProyectoConceptoGastoCodigoEc> {

  toTarget(value: IProyectoConceptoGastoCodigoEcBackend): IProyectoConceptoGastoCodigoEc {
    if (!value) {
      return value as unknown as IProyectoConceptoGastoCodigoEc;
    }
    return {
      id: value.id,
      proyectoConceptoGasto: { id: value.proyectoConceptoGastoId } as IProyectoConceptoGasto,
      codigoEconomicoRef: value.codigoEconomicoRef,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      observaciones: value.observaciones,
      convocatoriaConceptoGastoCodigoEcId: value.convocatoriaConceptoGastoCodigoEcId
    };
  }

  fromTarget(value: IProyectoConceptoGastoCodigoEc): IProyectoConceptoGastoCodigoEcBackend {
    if (!value) {
      return value as unknown as IProyectoConceptoGastoCodigoEcBackend;
    }
    return {
      id: value.id,
      proyectoConceptoGastoId: value.proyectoConceptoGasto?.id,
      codigoEconomicoRef: value.codigoEconomicoRef,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      observaciones: value.observaciones,
      convocatoriaConceptoGastoCodigoEcId: value.convocatoriaConceptoGastoCodigoEcId
    };
  }
}

export const PROYECTO_CONCEPTO_GASTO_CODIGO_EC_CONVERTER = new ProyectoConceptoGastoCodigoEcConverter();
