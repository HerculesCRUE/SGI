import { IPeticionEvaluacionWithIsEliminable } from '@core/models/eti/peticion-evaluacion-with-is-eliminable';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IPeticionEvaluacionWithIsEliminableResponse } from './peticion-evaluacion-with-is-eliminable-response';
import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';

class PeticionEvaluacionWithIsEliminableResponseConverter
  extends SgiBaseConverter<IPeticionEvaluacionWithIsEliminableResponse, IPeticionEvaluacionWithIsEliminable> {
  toTarget(value: IPeticionEvaluacionWithIsEliminableResponse): IPeticionEvaluacionWithIsEliminable {
    if (!value) {
      return value as unknown as IPeticionEvaluacionWithIsEliminable;
    }
    return {
      id: value.id,
      solicitudConvocatoriaRef: value.solicitudConvocatoriaRef,
      codigo: value.codigo,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.titulo) : [],
      tipoActividad: value.tipoActividad,
      tipoInvestigacionTutelada: value.tipoInvestigacionTutelada,
      existeFinanciacion: value.existeFinanciacion,
      fuenteFinanciacion: value.fuenteFinanciacion ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.fuenteFinanciacion) : [],
      estadoFinanciacion: value.estadoFinanciacion,
      importeFinanciacion: value.importeFinanciacion,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      resumen: value.resumen ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.resumen) : [],
      valorSocial: value.valorSocial,
      otroValorSocial: value.otroValorSocial ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.otroValorSocial) : [],
      objetivos: value.objetivos ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.objetivos) : [],
      disMetodologico: value.disMetodologico ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.disMetodologico) : [],
      tieneFondosPropios: value.tieneFondosPropios,
      solicitante: { id: value.personaRef } as IPersona,
      tutor: { id: value.tutorRef } as IPersona,
      activo: value.activo,
      checklistId: value.checklistId,
      eliminable: value.eliminable
    };
  }

  fromTarget(value: IPeticionEvaluacionWithIsEliminable): IPeticionEvaluacionWithIsEliminableResponse {
    if (!value) {
      return value as unknown as IPeticionEvaluacionWithIsEliminableResponse;
    }
    return {
      id: value.id,
      solicitudConvocatoriaRef: value.solicitudConvocatoriaRef,
      codigo: value.codigo,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.titulo) : [],
      tipoActividad: value.tipoActividad,
      tipoInvestigacionTutelada: value.tipoInvestigacionTutelada,
      existeFinanciacion: value.existeFinanciacion,
      fuenteFinanciacion: value.fuenteFinanciacion ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.fuenteFinanciacion) : [],
      estadoFinanciacion: value.estadoFinanciacion,
      importeFinanciacion: value.importeFinanciacion,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      resumen: value.resumen ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.resumen) : [],
      valorSocial: value.valorSocial,
      otroValorSocial: value.otroValorSocial ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.otroValorSocial) : [],
      objetivos: value.objetivos ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.objetivos) : [],
      disMetodologico: value.disMetodologico ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.disMetodologico) : [],
      tieneFondosPropios: value.tieneFondosPropios,
      personaRef: value.solicitante?.id,
      activo: value.activo,
      checklistId: value.checklistId,
      tutorRef: value.tutor?.id,
      eliminable: value.eliminable
    };
  }
}

export const PETICION_EVALUACION_WITH_IS_ELIMINABLE_RESPONSE_CONVERTER = new PeticionEvaluacionWithIsEliminableResponseConverter();
