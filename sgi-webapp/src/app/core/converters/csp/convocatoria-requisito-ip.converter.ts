import { IConvocatoriaRequisitoIPBackend } from '@core/models/csp/backend/convocatoria-requisito-ip-backend';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { ISexo } from '@core/models/sgp/sexo';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaRequisitoIPConverter
  extends SgiBaseConverter<IConvocatoriaRequisitoIPBackend, IConvocatoriaRequisitoIP> {

  toTarget(value: IConvocatoriaRequisitoIPBackend): IConvocatoriaRequisitoIP {
    if (!value) {
      return value as unknown as IConvocatoriaRequisitoIP;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      numMaximoIP: value.numMaximoIP,
      fechaMaximaNivelAcademico: LuxonUtils.fromBackend(value.fechaMaximaNivelAcademico),
      fechaMinimaNivelAcademico: LuxonUtils.fromBackend(value.fechaMinimaNivelAcademico),
      edadMaxima: value.edadMaxima,
      sexo: {
        id: value.sexoRef,
      } as ISexo,
      vinculacionUniversidad: value.vinculacionUniversidad,
      fechaMaximaCategoriaProfesional: LuxonUtils.fromBackend(value.fechaMaximaCategoriaProfesional),
      fechaMinimaCategoriaProfesional: LuxonUtils.fromBackend(value.fechaMinimaCategoriaProfesional),
      numMinimoCompetitivos: value.numMinimoCompetitivos,
      numMinimoNoCompetitivos: value.numMinimoNoCompetitivos,
      numMaximoCompetitivosActivos: value.numMaximoCompetitivosActivos,
      numMaximoNoCompetitivosActivos: value.numMaximoNoCompetitivosActivos,
      otrosRequisitos: value.otrosRequisitos
    };
  }

  fromTarget(value: IConvocatoriaRequisitoIP): IConvocatoriaRequisitoIPBackend {
    if (!value) {
      return value as unknown as IConvocatoriaRequisitoIPBackend;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      numMaximoIP: value.numMaximoIP,
      fechaMaximaNivelAcademico: LuxonUtils.toBackend(value.fechaMaximaNivelAcademico),
      fechaMinimaNivelAcademico: LuxonUtils.toBackend(value.fechaMinimaNivelAcademico),
      edadMaxima: value.edadMaxima,
      sexoRef: value.sexo?.id,
      vinculacionUniversidad: value.vinculacionUniversidad,
      fechaMaximaCategoriaProfesional: LuxonUtils.toBackend(value.fechaMaximaCategoriaProfesional),
      fechaMinimaCategoriaProfesional: LuxonUtils.toBackend(value.fechaMinimaCategoriaProfesional),
      numMinimoCompetitivos: value.numMinimoCompetitivos,
      numMinimoNoCompetitivos: value.numMinimoNoCompetitivos,
      numMaximoCompetitivosActivos: value.numMaximoCompetitivosActivos,
      numMaximoNoCompetitivosActivos: value.numMaximoNoCompetitivosActivos,
      otrosRequisitos: value.otrosRequisitos
    };
  }
}

export const CONVOCATORIA_REQUISITO_IP_CONVERTER = new ConvocatoriaRequisitoIPConverter();
