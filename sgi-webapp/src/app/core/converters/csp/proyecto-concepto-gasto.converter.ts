import { IProyectoConceptoGastoBackend } from '@core/models/csp/backend/proyecto-concepto-gasto-backend';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoConceptoGastoConverter extends SgiBaseConverter<IProyectoConceptoGastoBackend, IProyectoConceptoGasto> {

  toTarget(value: IProyectoConceptoGastoBackend): IProyectoConceptoGasto {
    if (!value) {
      return value as unknown as IProyectoConceptoGasto;
    }
    return {
      id: value.id,
      conceptoGasto: value.conceptoGasto,
      proyectoId: value.proyectoId,
      observaciones: value.observaciones,
      importeMaximo: value.importeMaximo,
      permitido: value.permitido,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      convocatoriaConceptoGastoId: value.convocatoriaConceptoGastoId
    };
  }

  fromTarget(value: IProyectoConceptoGasto): IProyectoConceptoGastoBackend {
    if (!value) {
      return value as unknown as IProyectoConceptoGastoBackend;
    }
    return {
      id: value.id,
      conceptoGasto: value.conceptoGasto,
      proyectoId: value.proyectoId,
      observaciones: value.observaciones,
      importeMaximo: value.importeMaximo,
      permitido: value.permitido,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      convocatoriaConceptoGastoId: value.convocatoriaConceptoGastoId
    };
  }
}

export const PROYECTO_CONCEPTO_GASTO_CONVERTER = new ProyectoConceptoGastoConverter();
