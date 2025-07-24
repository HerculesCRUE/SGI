import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoProrrogaDocumento } from '@core/models/csp/proyecto-prorroga-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from '../tipo-documento/tipo-documento-response.converter';
import { IProyectoProrrogaDocumentoResponse } from './proyecto-prorroga-documento-response';

class ProyectoProrrogaDocumentoResponseConverter extends SgiBaseConverter<IProyectoProrrogaDocumentoResponse, IProyectoProrrogaDocumento> {

  toTarget(value: IProyectoProrrogaDocumentoResponse): IProyectoProrrogaDocumento {
    if (!value) {
      return value as unknown as IProyectoProrrogaDocumento;
    }
    return {
      id: value.id,
      proyectoProrrogaId: value.proyectoProrrogaId,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.toTarget(value.tipoDocumento) : null,
      visible: value.visible,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : []
    };
  }

  fromTarget(value: IProyectoProrrogaDocumento): IProyectoProrrogaDocumentoResponse {
    if (!value) {
      return value as unknown as IProyectoProrrogaDocumentoResponse;
    }
    return {
      id: value.id,
      proyectoProrrogaId: value.proyectoProrrogaId,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.fromTarget(value.tipoDocumento) : null,
      visible: value.visible,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : []
    };
  }
}

export const PROYECTO_PRORROGA_DOCUMENTO_RESPONSE_CONVERTER = new ProyectoProrrogaDocumentoResponseConverter();
