import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { ISexo } from '@core/models/sgp/sexo';
import { IConvocatoriaRequisitoIPResponse } from '@core/services/csp/convocatoria-requisito-ip/convocatoria-requisito-ip-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaRequisitoIPResponseConverter
  extends SgiBaseConverter<IConvocatoriaRequisitoIPResponse, IConvocatoriaRequisitoIP> {

  toTarget(value: IConvocatoriaRequisitoIPResponse): IConvocatoriaRequisitoIP {
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
      otrosRequisitos: value.otrosRequisitos ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.otrosRequisitos) : []
    };
  }

  fromTarget(value: IConvocatoriaRequisitoIP): IConvocatoriaRequisitoIPResponse {
    if (!value) {
      return value as unknown as IConvocatoriaRequisitoIPResponse;
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
      otrosRequisitos: value.otrosRequisitos ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.otrosRequisitos) : []
    };
  }
}

export const CONVOCATORIA_REQUISITO_IP_RESPONSE_CONVERTER = new ConvocatoriaRequisitoIPResponseConverter();
