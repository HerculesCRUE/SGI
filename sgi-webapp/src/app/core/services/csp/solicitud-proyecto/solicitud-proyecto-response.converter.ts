import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { AREA_TEMATICA_RESPONSE_CONVERTER } from '@core/services/csp/area-tematica/area-tematica-response.converter';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitudProyectoResponse } from './solicitud-proyecto-response';

class SolicitudProyectoResponseConverter extends SgiBaseConverter<ISolicitudProyectoResponse, ISolicitudProyecto> {

  toTarget(value: ISolicitudProyectoResponse): ISolicitudProyecto {
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
      rolUniversidad: !!value.rolUniversidadId ? { id: value.rolUniversidadId } as IRolSocio : null,
      objetivos: value.objetivos ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.objetivos) : [],
      intereses: value.intereses ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.intereses) : [],
      resultadosPrevistos: value.resultadosPrevistos ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.resultadosPrevistos) : [],
      areaTematica: AREA_TEMATICA_RESPONSE_CONVERTER.toTarget(value.areaTematica),
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

  fromTarget(value: ISolicitudProyecto): ISolicitudProyectoResponse {
    if (!value) {
      return value as unknown as ISolicitudProyectoResponse;
    }
    return {
      id: value.id,
      acronimo: value.acronimo,
      codExterno: value.codExterno,
      duracion: value.duracion,
      colaborativo: value.colaborativo,
      coordinado: value.coordinado,
      rolUniversidadId: value.rolUniversidad?.id,
      objetivos: value.objetivos ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.objetivos) : [],
      intereses: value.intereses ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.intereses) : [],
      resultadosPrevistos: value.resultadosPrevistos ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.resultadosPrevistos) : [],
      areaTematica: AREA_TEMATICA_RESPONSE_CONVERTER.fromTarget(value.areaTematica),
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

export const SOLICITUD_PROYECTO_RESPONSE_CONVERTER = new SolicitudProyectoResponseConverter();
