import { IPeticionEvaluacionBackend } from '@core/models/eti/backend/peticion-evaluacion-backend';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class PeticionEvaluacionConverter extends SgiBaseConverter<IPeticionEvaluacionBackend, IPeticionEvaluacion> {
  toTarget(value: IPeticionEvaluacionBackend): IPeticionEvaluacion {
    if (!value) {
      return value as unknown as IPeticionEvaluacion;
    }
    return {
      id: value.id,
      solicitudConvocatoriaRef: value.solicitudConvocatoriaRef,
      codigo: value.codigo,
      titulo: value.titulo,
      tipoActividad: value.tipoActividad,
      tipoInvestigacionTutelada: value.tipoInvestigacionTutelada,
      existeFinanciacion: value.existeFinanciacion,
      fuenteFinanciacion: value.fuenteFinanciacion,
      estadoFinanciacion: value.estadoFinanciacion,
      importeFinanciacion: value.importeFinanciacion,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      resumen: value.resumen,
      valorSocial: value.valorSocial,
      otroValorSocial: value.otroValorSocial,
      objetivos: value.objetivos,
      disMetodologico: value.disMetodologico,
      externo: value.externo,
      tieneFondosPropios: value.tieneFondosPropios,
      solicitante: { id: value.personaRef } as IPersona,
      activo: value.activo
    };
  }

  fromTarget(value: IPeticionEvaluacion): IPeticionEvaluacionBackend {
    if (!value) {
      return value as unknown as IPeticionEvaluacionBackend;
    }
    return {
      id: value.id,
      solicitudConvocatoriaRef: value.solicitudConvocatoriaRef,
      codigo: value.codigo,
      titulo: value.titulo,
      tipoActividad: value.tipoActividad,
      tipoInvestigacionTutelada: value.tipoInvestigacionTutelada,
      existeFinanciacion: value.existeFinanciacion,
      fuenteFinanciacion: value.fuenteFinanciacion,
      estadoFinanciacion: value.estadoFinanciacion,
      importeFinanciacion: value.importeFinanciacion,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      resumen: value.resumen,
      valorSocial: value.valorSocial,
      otroValorSocial: value.otroValorSocial,
      objetivos: value.objetivos,
      disMetodologico: value.disMetodologico,
      externo: value.externo,
      tieneFondosPropios: value.tieneFondosPropios,
      personaRef: value.solicitante?.id,
      activo: value.activo
    };
  }
}

export const PETICION_EVALUACION_CONVERTER = new PeticionEvaluacionConverter();
