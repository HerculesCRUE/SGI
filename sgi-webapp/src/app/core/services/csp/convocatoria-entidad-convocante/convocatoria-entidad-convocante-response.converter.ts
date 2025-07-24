import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IConvocatoriaEntidadConvocanteResponse } from '@core/services/csp/convocatoria-entidad-convocante/convocatoria-entidad-convocante-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROGRAMA_RESPONSE_CONVERTER } from '../programa/programa-response.converter';

class ConvocatoriaEntidadConvocanteConverter extends
  SgiBaseConverter<IConvocatoriaEntidadConvocanteResponse, IConvocatoriaEntidadConvocante> {

  toTarget(value: IConvocatoriaEntidadConvocanteResponse): IConvocatoriaEntidadConvocante {
    if (!value) {
      return value as unknown as IConvocatoriaEntidadConvocante;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      entidad: { id: value.entidadRef } as IEmpresa,
      programa: PROGRAMA_RESPONSE_CONVERTER.toTarget(value.programa)
    };
  }

  fromTarget(value: IConvocatoriaEntidadConvocante): IConvocatoriaEntidadConvocanteResponse {
    if (!value) {
      return value as unknown as IConvocatoriaEntidadConvocanteResponse;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      entidadRef: value.entidad?.id,
      programa: PROGRAMA_RESPONSE_CONVERTER.fromTarget(value.programa)
    };
  }
}

export const CONVOCATORIA_ENTIDAD_CONVOCANTE_RESPONSE_CONVERTER = new ConvocatoriaEntidadConvocanteConverter();
