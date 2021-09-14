import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IRequisitoEquipoNivelAcademicoRequest } from './requisito-equipo-nivel-academico-request';

class RequisitoEquipoNivelAcademicoRequestConverter
  extends SgiBaseConverter<IRequisitoEquipoNivelAcademicoRequest, IRequisitoEquipoNivelAcademico>{
  toTarget(value: IRequisitoEquipoNivelAcademicoRequest): IRequisitoEquipoNivelAcademico {
    if (!value) {
      return value as unknown as IRequisitoEquipoNivelAcademico;
    }
    return {
      id: undefined,
      requisitoEquipo: {
        id: value.requisitoEquipoId
      } as IConvocatoriaRequisitoEquipo,
      nivelAcademico: {
        id: value.nivelAcademicoRef
      } as INivelAcademico
    };
  }
  fromTarget(value: IRequisitoEquipoNivelAcademico): IRequisitoEquipoNivelAcademicoRequest {
    if (!value) {
      return value as unknown as IRequisitoEquipoNivelAcademicoRequest;
    }
    return {
      requisitoEquipoId: value.requisitoEquipo?.id,
      nivelAcademicoRef: value.nivelAcademico?.id,
    };
  }
}

export const REQUISITO_EQUIPO_NIVELACADEMICO_REQUEST_CONVERTER = new RequisitoEquipoNivelAcademicoRequestConverter();
