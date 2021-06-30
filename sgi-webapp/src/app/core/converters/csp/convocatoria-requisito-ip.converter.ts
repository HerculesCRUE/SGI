import { IConvocatoriaRequisitoIPBackend } from '@core/models/csp/backend/convocatoria-requisito-ip-backend';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
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
      nivelAcademicoRef: value.nivelAcademicoRef,
      aniosNivelAcademico: value.aniosNivelAcademico,
      edadMaxima: value.edadMaxima,
      sexo: value.sexo,
      vinculacionUniversidad: value.vinculacionUniversidad,
      modalidadContratoRef: value.modalidadContratoRef,
      aniosVinculacion: value.aniosVinculacion,
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
      nivelAcademicoRef: value.nivelAcademicoRef,
      aniosNivelAcademico: value.aniosNivelAcademico,
      edadMaxima: value.edadMaxima,
      sexo: value.sexo,
      vinculacionUniversidad: value.vinculacionUniversidad,
      modalidadContratoRef: value.modalidadContratoRef,
      aniosVinculacion: value.aniosVinculacion,
      numMinimoCompetitivos: value.numMinimoCompetitivos,
      numMinimoNoCompetitivos: value.numMinimoNoCompetitivos,
      numMaximoCompetitivosActivos: value.numMaximoCompetitivosActivos,
      numMaximoNoCompetitivosActivos: value.numMaximoNoCompetitivosActivos,
      otrosRequisitos: value.otrosRequisitos
    };
  }
}

export const CONVOCATORIA_REQUISITO_IP_CONVERTER = new ConvocatoriaRequisitoIPConverter();
