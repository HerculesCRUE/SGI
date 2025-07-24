import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoContexto } from '@core/models/csp/proyecto-contexto';
import { AREA_TEMATICA_RESPONSE_CONVERTER } from '@core/services/csp/area-tematica/area-tematica-response.converter';
import { IProyectoContextoResponse } from '@core/services/csp/proyecto-contexto/proyecto-contexto-response';
import { SgiBaseConverter } from '@sgi/framework/core';

class ProyectoContextoResponseConverter extends SgiBaseConverter<IProyectoContextoResponse, IProyectoContexto> {

  toTarget(value: IProyectoContextoResponse): IProyectoContexto {
    if (!value) {
      return value as unknown as IProyectoContexto;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      objetivos: value.objetivos ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.objetivos) : [],
      intereses: value.intereses ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.intereses) : [],
      resultadosPrevistos: value.resultadosPrevistos ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.resultadosPrevistos) : [],
      propiedadResultados: value.propiedadResultados,
      areaTematica: AREA_TEMATICA_RESPONSE_CONVERTER.toTarget(value.areaTematica),
    };
  }

  fromTarget(value: IProyectoContexto): IProyectoContextoResponse {
    if (!value) {
      return value as unknown as IProyectoContextoResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      objetivos: value.objetivos ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.objetivos) : [],
      intereses: value.intereses ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.intereses) : [],
      resultadosPrevistos: value.resultadosPrevistos ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.resultadosPrevistos) : [],
      propiedadResultados: value.propiedadResultados,
      areaTematica: AREA_TEMATICA_RESPONSE_CONVERTER.fromTarget(value.areaTematica),
    };
  }
}

export const PROYECTO_CONTEXTO_RESPONSE_CONVERTER = new ProyectoContextoResponseConverter();
