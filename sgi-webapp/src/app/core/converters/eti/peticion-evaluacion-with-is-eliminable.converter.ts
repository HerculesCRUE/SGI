import { IPeticionEvaluacionWithIsEliminableBackend } from '@core/models/eti/backend/peticion-evaluacion-with-is-eliminable-backend';
import { IPeticionEvaluacionWithIsEliminable } from '@core/models/eti/peticion-evaluacion-with-is-eliminable';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class PeticionEvaluacionWithIsEliminableConverter
  extends SgiBaseConverter<IPeticionEvaluacionWithIsEliminableBackend, IPeticionEvaluacionWithIsEliminable> {
  toTarget(value: IPeticionEvaluacionWithIsEliminableBackend): IPeticionEvaluacionWithIsEliminable {
    if (!value) {
      return value as unknown as IPeticionEvaluacionWithIsEliminable;
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
      activo: value.activo,
      eliminable: value.eliminable
    };
  }

  fromTarget(value: IPeticionEvaluacionWithIsEliminable): IPeticionEvaluacionWithIsEliminableBackend {
    if (!value) {
      return value as unknown as IPeticionEvaluacionWithIsEliminableBackend;
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
      activo: value.activo,
      eliminable: value.eliminable
    };
  }
}

export const PETICION_EVALUACION_WITH_IS_ELIMINABLE_CONVERTER = new PeticionEvaluacionWithIsEliminableConverter();
