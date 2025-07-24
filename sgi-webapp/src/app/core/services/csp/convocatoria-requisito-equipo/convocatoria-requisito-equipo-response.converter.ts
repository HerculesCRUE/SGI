import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { ISexo } from '@core/models/sgp/sexo';
import { IConvocatoriaRequisitoEquipoResponse } from '@core/services/csp/convocatoria-requisito-equipo/convocatoria-requisito-equipo-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaRequisitoEquipoResponseConverter
  extends SgiBaseConverter<IConvocatoriaRequisitoEquipoResponse, IConvocatoriaRequisitoEquipo> {

  toTarget(value: IConvocatoriaRequisitoEquipoResponse): IConvocatoriaRequisitoEquipo {
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
      otrosRequisitos: value.otrosRequisitos ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.otrosRequisitos) : []
    };
  }

  fromTarget(value: IConvocatoriaRequisitoEquipo): IConvocatoriaRequisitoEquipoResponse {
    if (!value) {
      return value as unknown as IConvocatoriaRequisitoEquipoResponse;
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
      otrosRequisitos: value.otrosRequisitos ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.otrosRequisitos) : []
    };
  }
}

export const CONVOCATORIA_REQUISITO_EQUIPO_RESPONSE_CONVERTER = new ConvocatoriaRequisitoEquipoResponseConverter();
