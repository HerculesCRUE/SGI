import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoDocumento } from '@core/models/csp/proyecto-documento';
import { IProyectoDocumentoResponse } from '@core/services/csp/proyecto-documento/proyecto-documento-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from '../tipo-documento/tipo-documento-response.converter';
import { TIPO_FASE_RESPONSE_CONVERTER } from '../tipo-fase/tipo-fase-response.converter';

class ProyectoDocumentoResponseConverter extends SgiBaseConverter<IProyectoDocumentoResponse, IProyectoDocumento> {

  toTarget(value: IProyectoDocumentoResponse): IProyectoDocumento {
    if (!value) {
      return value as unknown as IProyectoDocumento;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.toTarget(value.tipoFase) : null,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.toTarget(value.tipoDocumento) : null,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : [],
      visible: value.visible
    };
  }

  fromTarget(value: IProyectoDocumento): IProyectoDocumentoResponse {
    if (!value) {
      return value as unknown as IProyectoDocumentoResponse;
    }
    return {
      id: value.id,
      proyectoId: value.proyectoId,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      documentoRef: value.documentoRef,
      tipoFase: value.tipoFase ? TIPO_FASE_RESPONSE_CONVERTER.fromTarget(value.tipoFase) : null,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.fromTarget(value.tipoDocumento) : null,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : [],
      visible: value.visible
    };
  }
}

export const PROYECTO_DOCUMENTO_RESPONSE_CONVERTER = new ProyectoDocumentoResponseConverter();
