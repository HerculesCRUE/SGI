import { IProyectoSocioBackend } from '@core/models/csp/backend/proyecto-socio-backend';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoSocioConverter extends SgiBaseConverter<IProyectoSocioBackend, IProyectoSocio> {

  toTarget(value: IProyectoSocioBackend): IProyectoSocio {
    if (!value) {
      return value as unknown as IProyectoSocio;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      empresa: { id: value.empresaRef } as IEmpresa,
      rolSocio: value.rolSocio,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      numInvestigadores: value.numInvestigadores,
      importeConcedido: value.importeConcedido,
      importePresupuesto: value.importePresupuesto
    };
  }

  fromTarget(value: IProyectoSocio): IProyectoSocioBackend {
    if (!value) {
      return value as unknown as IProyectoSocioBackend;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      empresaRef: value.empresa.id,
      rolSocio: value.rolSocio,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      numInvestigadores: value.numInvestigadores,
      importeConcedido: value.importeConcedido,
      importePresupuesto: value.importePresupuesto
    };
  }
}

export const PROYECTO_SOCIO_CONVERTER = new ProyectoSocioConverter();
