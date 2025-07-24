import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoSocioPeriodoJustificacionDocumento } from '@core/models/csp/proyecto-socio-periodo-justificacion-documento';
import { IProyectoSocioPeriodoJustificacionDocumentoResponse } from '@core/services/csp/proyecto-socio-periodo-justificacion-documento/proyecto-socio-periodo-justificacion-documento-response';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from '../tipo-documento/tipo-documento-response.converter';

class ProyectoSocioPeriodoJustificacionDocumentoResponseConverter extends
  SgiBaseConverter<IProyectoSocioPeriodoJustificacionDocumentoResponse, IProyectoSocioPeriodoJustificacionDocumento> {

  toTarget(value: IProyectoSocioPeriodoJustificacionDocumentoResponse): IProyectoSocioPeriodoJustificacionDocumento {
    if (!value) {
      return value as unknown as IProyectoSocioPeriodoJustificacionDocumento;
    }
    return {
      id: value.id,
      proyectoSocioPeriodoJustificacionId: value.proyectoSocioPeriodoJustificacionId,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.toTarget(value.tipoDocumento) : null,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : [],
      visible: value.visible
    };
  }

  fromTarget(value: IProyectoSocioPeriodoJustificacionDocumento): IProyectoSocioPeriodoJustificacionDocumentoResponse {
    if (!value) {
      return value as unknown as IProyectoSocioPeriodoJustificacionDocumentoResponse;
    }
    return {
      id: value.id,
      proyectoSocioPeriodoJustificacionId: value.proyectoSocioPeriodoJustificacionId,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      documentoRef: value.documentoRef,
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.fromTarget(value.tipoDocumento) : null,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : [],
      visible: value.visible
    };
  }
}

export const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DOCUMENTO_RESPONSE_CONVERTER = new ProyectoSocioPeriodoJustificacionDocumentoResponseConverter();
