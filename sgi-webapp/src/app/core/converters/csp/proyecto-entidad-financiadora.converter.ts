import { IProyectoEntidadFinanciadoraBackend } from '@core/models/csp/backend/proyecto-entidad-financiadora-backend';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoEntidadFinanciadoraConverter extends SgiBaseConverter<IProyectoEntidadFinanciadoraBackend, IProyectoEntidadFinanciadora> {

  toTarget(value: IProyectoEntidadFinanciadoraBackend): IProyectoEntidadFinanciadora {
    if (!value) {
      return value as unknown as IProyectoEntidadFinanciadora;
    }
    return {
      id: value.id,
      empresa: { id: value.entidadRef } as IEmpresa,
      proyectoId: value.proyectoId,
      fuenteFinanciacion: value.fuenteFinanciacion,
      tipoFinanciacion: value.tipoFinanciacion,
      porcentajeFinanciacion: value.porcentajeFinanciacion,
      importeFinanciacion: value.importeFinanciacion,
      ajena: value.ajena
    };
  }

  fromTarget(value: IProyectoEntidadFinanciadora): IProyectoEntidadFinanciadoraBackend {
    if (!value) {
      return value as unknown as IProyectoEntidadFinanciadoraBackend;
    }
    return {
      id: value.id,
      entidadRef: value.empresa?.id,
      proyectoId: value.proyectoId,
      fuenteFinanciacion: value.fuenteFinanciacion,
      tipoFinanciacion: value.tipoFinanciacion,
      porcentajeFinanciacion: value.porcentajeFinanciacion,
      importeFinanciacion: value.importeFinanciacion,
      ajena: value.ajena
    };
  }
}

export const PROYECTO_ENTIDAD_FINANCIADORA_CONVERTER = new ProyectoEntidadFinanciadoraConverter();
