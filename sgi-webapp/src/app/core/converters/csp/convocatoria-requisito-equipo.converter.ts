import { I } from '@angular/cdk/keycodes';
import { IConvocatoriaRequisitoEquipoBackend } from '@core/models/csp/backend/convocatoria-requisito-equipo-backend';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { ISexo } from '@core/models/sgp/sexo';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaRequisitoEquipoConverter
  extends SgiBaseConverter<IConvocatoriaRequisitoEquipoBackend, IConvocatoriaRequisitoEquipo> {

  toTarget(value: IConvocatoriaRequisitoEquipoBackend): IConvocatoriaRequisitoEquipo {
    if (!value) {
      return value as unknown as IConvocatoriaRequisitoEquipo;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      fechaMaximaNivelAcademico: LuxonUtils.fromBackend(value.fechaMaximaNivelAcademico),
      fechaMinimaNivelAcademico: LuxonUtils.fromBackend(value.fechaMinimaNivelAcademico),
      edadMaxima: value.edadMaxima,
      sexo: {
        id: value.sexoRef,
      } as ISexo,
      ratioSexo: value.ratioSexo,
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

  fromTarget(value: IConvocatoriaRequisitoEquipo): IConvocatoriaRequisitoEquipoBackend {
    if (!value) {
      return value as unknown as IConvocatoriaRequisitoEquipoBackend;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      fechaMaximaNivelAcademico: LuxonUtils.toBackend(value.fechaMaximaNivelAcademico),
      fechaMinimaNivelAcademico: LuxonUtils.toBackend(value.fechaMinimaNivelAcademico),
      edadMaxima: value.edadMaxima,
      sexoRef: value.sexo?.id,
      ratioSexo: value.ratioSexo,
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

export const CONVOCATORIA_REQUISITO_EQUIPO_CONVERTER = new ConvocatoriaRequisitoEquipoConverter();
