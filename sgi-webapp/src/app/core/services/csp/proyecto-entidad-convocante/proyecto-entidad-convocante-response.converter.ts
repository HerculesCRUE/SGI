import { IProyectoEntidadConvocante } from '@core/models/csp/proyecto-entidad-convocante';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IProyectoEntidadConvocanteResponse } from '@core/services/csp/proyecto-entidad-convocante/proyecto-entidad-convocante-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROGRAMA_RESPONSE_CONVERTER } from '../programa/programa-response.converter';

class ProyectoEntidadConvocanteResponseConverter extends SgiBaseConverter<IProyectoEntidadConvocanteResponse, IProyectoEntidadConvocante> {

  toTarget(value: IProyectoEntidadConvocanteResponse): IProyectoEntidadConvocante {
    if (!value) {
      return value as unknown as IProyectoEntidadConvocante;
    }
    return {
      id: value.id,
      entidad: { id: value.entidadRef } as IEmpresa,
      programaConvocatoria: PROGRAMA_RESPONSE_CONVERTER.toTarget(value.programaConvocatoria),
      programa: PROGRAMA_RESPONSE_CONVERTER.toTarget(value.programa)
    };
  }

  fromTarget(value: IProyectoEntidadConvocante): IProyectoEntidadConvocanteResponse {
    if (!value) {
      return value as unknown as IProyectoEntidadConvocanteResponse;
    }
    return {
      id: value.id,
      entidadRef: value.entidad?.id,
      programaConvocatoria: PROGRAMA_RESPONSE_CONVERTER.fromTarget(value.programaConvocatoria),
      programa: PROGRAMA_RESPONSE_CONVERTER.fromTarget(value.programa)
    };
  }
}

export const PROYECTO_ENTIDAD_CONVOCANTE_RESPONSE_CONVERTER = new ProyectoEntidadConvocanteResponseConverter();
