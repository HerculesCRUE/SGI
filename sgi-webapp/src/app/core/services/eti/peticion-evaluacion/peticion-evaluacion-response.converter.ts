import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { IPeticionEvaluacionResponse } from '@core/services/eti/peticion-evaluacion/peticion-evaluacion-response';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class PeticionEvaluacionResponseConverter extends SgiBaseConverter<IPeticionEvaluacionResponse, IPeticionEvaluacion> {
  toTarget(value: IPeticionEvaluacionResponse): IPeticionEvaluacion {
    if (!value) {
      return value as unknown as IPeticionEvaluacion;
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
      checklistId: value.checklistId,
      tutor: { id: value.tutorRef } as IPersona,
      activo: value.activo
    };
  }

  fromTarget(value: IPeticionEvaluacion): IPeticionEvaluacionResponse {
    if (!value) {
      return value as unknown as IPeticionEvaluacionResponse;
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
      checklistId: value.checklistId,
      tutorRef: value.tutor?.id,
      activo: value.activo
    };
  }
}

export const PETICION_EVALUACION_RESPONSE_CONVERTER = new PeticionEvaluacionResponseConverter();
