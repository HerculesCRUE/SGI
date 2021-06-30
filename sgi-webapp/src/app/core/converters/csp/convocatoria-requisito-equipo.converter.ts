import { IConvocatoriaRequisitoEquipoBackend } from '@core/models/csp/backend/convocatoria-requisito-equipo-backend';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
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
      nivelAcademicoRef: value.nivelAcademicoRef,
      aniosNivelAcademico: value.aniosNivelAcademico,
      edadMaxima: value.edadMaxima,
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

  fromTarget(value: IConvocatoriaRequisitoEquipo): IConvocatoriaRequisitoEquipoBackend {
    if (!value) {
      return value as unknown as IConvocatoriaRequisitoEquipoBackend;
    }
    return {
      id: value.id,
      convocatoriaId: value.convocatoriaId,
      nivelAcademicoRef: value.nivelAcademicoRef,
      aniosNivelAcademico: value.aniosNivelAcademico,
      edadMaxima: value.edadMaxima,
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

export const CONVOCATORIA_REQUISITO_EQUIPO_CONVERTER = new ConvocatoriaRequisitoEquipoConverter();
