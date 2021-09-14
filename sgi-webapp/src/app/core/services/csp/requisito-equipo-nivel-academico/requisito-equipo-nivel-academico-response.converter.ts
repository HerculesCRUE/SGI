import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRequisitoEquipoNivelAcademicoResponse } from './requisito-equipo-nivel-academico-response';

class RequisitoEquipoNivelAcademicoResponseConverter extends
  SgiBaseConverter<IRequisitoEquipoNivelAcademicoResponse, IRequisitoEquipoNivelAcademico>{
  toTarget(value: IRequisitoEquipoNivelAcademicoResponse): IRequisitoEquipoNivelAcademico {
    if (!value) {
      return value as unknown as IRequisitoEquipoNivelAcademico;
    }
    return {
      id: value.id,
      requisitoEquipo: {
        id: value.requisitoEquipoId
      } as IConvocatoriaRequisitoEquipo,
      nivelAcademico: {
        id: value.nivelAcademicoRef
      } as INivelAcademico
    };
  }
  fromTarget(value: IRequisitoEquipoNivelAcademico): IRequisitoEquipoNivelAcademicoResponse {
    if (!value) {
      return value as unknown as IRequisitoEquipoNivelAcademicoResponse;
    }
    return {
      id: value.id,
      requisitoEquipoId: value.requisitoEquipo?.id,
      nivelAcademicoRef: value.nivelAcademico?.id,
    };
  }
}

export const REQUISITO_EQUIPO_NIVELACADEMICO_RESPONSE_CONVERTER = new RequisitoEquipoNivelAcademicoResponseConverter();
