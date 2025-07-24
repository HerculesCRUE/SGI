import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IEmpresaDocumento } from '@core/models/eer/empresa-documento';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_DOCUMENTO_RESPONSE_CONVERTER } from '../tipo-documento/tipo-documento-response.converter';
import { IEmpresaDocumentoResponse } from './empresa-documento-response';

class EmpresaDocumentoResponseConverter
  extends SgiBaseConverter<IEmpresaDocumentoResponse, IEmpresaDocumento> {
  toTarget(value: IEmpresaDocumentoResponse): IEmpresaDocumento {
    if (!value) {
      return value as unknown as IEmpresaDocumento;
    }
    return {
      id: value.id,
      comentarios: value.comentarios ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentarios) : [],
      documento: value.documentoRef ? { documentoRef: value.documentoRef } as IDocumento : undefined,
      empresa: value.empresaId ? { id: value.empresaId } as IEmpresaExplotacionResultados : undefined,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.getTipoDocumento(value.tipoDocumento) : null,
      subtipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.getSubtipoDocumento(value.tipoDocumento) : null
    };
  }

  fromTarget(value: IEmpresaDocumento): IEmpresaDocumentoResponse {
    if (!value) {
      return value as unknown as IEmpresaDocumentoResponse;
    }
    return {
      id: value.id,
      comentarios: value.comentarios ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentarios) : [],
      documentoRef: value.documento.documentoRef,
      empresaId: value.empresa.id,
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      tipoDocumento: value.tipoDocumento ? TIPO_DOCUMENTO_RESPONSE_CONVERTER.fromTarget(value.tipoDocumento) : null
    };
  }
}

export const EMPRESA_DOCUMENTO_RESPONSE_CONVERTER = new EmpresaDocumentoResponseConverter();
