import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ROL_SOCIO_RESPONSE_CONVERTER } from '../rol-socio/rol-socio-response.converter';
import { IProyectoSocioResponse } from './proyecto-socio-response';

class ProyectoSocioConverter extends SgiBaseConverter<IProyectoSocioResponse, IProyectoSocio> {

  toTarget(value: IProyectoSocioResponse): IProyectoSocio {
    if (!value) {
      return value as unknown as IProyectoSocio;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      empresa: { id: value.empresaRef } as IEmpresa,
      rolSocio: ROL_SOCIO_RESPONSE_CONVERTER.toTarget(value.rolSocio),
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      numInvestigadores: value.numInvestigadores,
      importeConcedido: value.importeConcedido,
      importePresupuesto: value.importePresupuesto
    };
  }

  fromTarget(value: IProyectoSocio): IProyectoSocioResponse {
    if (!value) {
      return value as unknown as IProyectoSocioResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      empresaRef: value.empresa.id,
      rolSocio: ROL_SOCIO_RESPONSE_CONVERTER.fromTarget(value.rolSocio),
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      numInvestigadores: value.numInvestigadores,
      importeConcedido: value.importeConcedido,
      importePresupuesto: value.importePresupuesto
    };
  }
}

export const PROYECTO_SOCIO_RESPONSE_CONVERTER = new ProyectoSocioConverter();
