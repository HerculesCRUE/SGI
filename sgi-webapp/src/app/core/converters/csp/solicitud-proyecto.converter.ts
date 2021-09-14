import { ISolicitudProyectoBackend } from '@core/models/csp/backend/solicitud-proyecto-backend';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { SgiBaseConverter } from '@sgi/framework/core';

class SolicitudProyectoConverter extends SgiBaseConverter<ISolicitudProyectoBackend, ISolicitudProyecto> {

  toTarget(value: ISolicitudProyectoBackend): ISolicitudProyecto {
    if (!value) {
      return value as unknown as ISolicitudProyecto;
    }
    return {
      id: value.id,
      acronimo: value.acronimo,
      codExterno: value.codExterno,
      duracion: value.duracion,
      colaborativo: value.colaborativo,
      coordinado: value.coordinado,
      coordinadorExterno: value.coordinadorExterno,
      objetivos: value.objetivos,
      intereses: value.intereses,
      resultadosPrevistos: value.resultadosPrevistos,
      areaTematica: value.areaTematica,
      checklistRef: value.checklistRef,
      peticionEvaluacionRef: value.peticionEvaluacionRef,
      tipoPresupuesto: value.tipoPresupuesto,
      importeSolicitado: value.importeSolicitado,
      importePresupuestado: value.importePresupuestado,
      importePresupuestadoCostesIndirectos: value.importePresupuestadoCostesIndirectos,
      importeSolicitadoCostesIndirectos: value.importeSolicitadoCostesIndirectos,
      importeSolicitadoSocios: value.importeSolicitadoSocios,
      importePresupuestadoSocios: value.importePresupuestadoSocios,
      totalImporteSolicitado: value.totalImporteSolicitado,
      totalImportePresupuestado: value.totalImportePresupuestado
    };
  }

  fromTarget(value: ISolicitudProyecto): ISolicitudProyectoBackend {
    if (!value) {
      return value as unknown as ISolicitudProyectoBackend;
    }
    return {
      id: value.id,
      acronimo: value.acronimo,
      codExterno: value.codExterno,
      duracion: value.duracion,
      colaborativo: value.colaborativo,
      coordinado: value.coordinado,
      coordinadorExterno: value.coordinadorExterno,
      objetivos: value.objetivos,
      intereses: value.intereses,
      resultadosPrevistos: value.resultadosPrevistos,
      areaTematica: value.areaTematica,
      checklistRef: value.checklistRef,
      peticionEvaluacionRef: value.peticionEvaluacionRef,
      tipoPresupuesto: value.tipoPresupuesto,
      importeSolicitado: value.importeSolicitado,
      importePresupuestado: value.importePresupuestado,
      importePresupuestadoCostesIndirectos: value.importePresupuestadoCostesIndirectos,
      importeSolicitadoCostesIndirectos: value.importeSolicitadoCostesIndirectos,
      importeSolicitadoSocios: value.importeSolicitadoSocios,
      importePresupuestadoSocios: value.importePresupuestadoSocios,
      totalImporteSolicitado: value.totalImporteSolicitado,
      totalImportePresupuestado: value.totalImportePresupuestado
    };
  }
}

export const SOLICITUD_PROYECTO_CONVERTER = new SolicitudProyectoConverter();
