import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoPeriodoSeguimientoDocumento } from '@core/models/csp/proyecto-periodo-seguimiento-documento';
import { IProyectoPeriodoSeguimientoDocumentoResponse } from '@core/services/csp/proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-documento-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from '../tipo-documento/tipo-documento-response.converter';

class ProyectoPeriodoSeguimientoDocumentoResponseConverter extends
  SgiBaseConverter<IProyectoPeriodoSeguimientoDocumentoResponse, IProyectoPeriodoSeguimientoDocumento> {

  toTarget(value: IProyectoPeriodoSeguimientoDocumentoResponse): IProyectoPeriodoSeguimientoDocumento {
    if (!value) {
      return value as unknown as IProyectoPeriodoSeguimientoDocumento;
    }
    return {
      id: value.id,
      proyectoPeriodoSeguimientoId: value.proyectoPeriodoSeguimientoId,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.toTarget(value.tipoDocumento) : null,
      visible: value.visible,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : []
    };
  }

  fromTarget(value: IProyectoPeriodoSeguimientoDocumento): IProyectoPeriodoSeguimientoDocumentoResponse {
    if (!value) {
      return value as unknown as IProyectoPeriodoSeguimientoDocumentoResponse;
    }
    return {
      id: value.id,
      proyectoPeriodoSeguimientoId: value.proyectoPeriodoSeguimientoId,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.fromTarget(value.tipoDocumento) : null,
      visible: value.visible,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : []
    };
  }
}

export const PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_RESPONSE_CONVERTER = new ProyectoPeriodoSeguimientoDocumentoResponseConverter();
