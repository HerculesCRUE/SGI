import { IProyectoEntidadFinanciadoraBackend } from '@core/models/csp/backend/proyecto-entidad-financiadora-backend';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FUENTE_FINANCIACION_RESPONSE_CONVERTER } from '@core/services/csp/fuente-financiacion/fuente-financiacion-response.converter';
import { TIPO_FINANCIACION_RESPONSE_CONVERTER } from '@core/services/csp/tipo-financiacion/tipo-financiacion-response.converter';
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
      fuenteFinanciacion: FUENTE_FINANCIACION_RESPONSE_CONVERTER.toTarget(value.fuenteFinanciacion),
      tipoFinanciacion: TIPO_FINANCIACION_RESPONSE_CONVERTER.toTarget(value.tipoFinanciacion),
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
      fuenteFinanciacion: FUENTE_FINANCIACION_RESPONSE_CONVERTER.fromTarget(value.fuenteFinanciacion),
      tipoFinanciacion: TIPO_FINANCIACION_RESPONSE_CONVERTER.fromTarget(value.tipoFinanciacion),
      porcentajeFinanciacion: value.porcentajeFinanciacion,
      importeFinanciacion: value.importeFinanciacion,
      ajena: value.ajena
    };
  }
}

export const PROYECTO_ENTIDAD_FINANCIADORA_CONVERTER = new ProyectoEntidadFinanciadoraConverter();
