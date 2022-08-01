import { IEmpresaDocumento } from '@core/models/eer/empresa-documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEmpresaDocumentoRequest } from './empresa-documento-request';

class EmpresaDocumentoRequestConverter
  extends SgiBaseConverter<IEmpresaDocumentoRequest, IEmpresaDocumento> {
  toTarget(value: IEmpresaDocumentoRequest): IEmpresaDocumento {
    throw new Error('Method not implemented');
  }

  fromTarget(value: IEmpresaDocumento): IEmpresaDocumentoRequest {
    if (!value) {
      return value as unknown as IEmpresaDocumentoRequest;
    }
    return {
      comentarios: value.comentarios,
      documentoRef: value.documento.documentoRef,
      empresaId: value.empresa.id,
      nombre: value.nombre,
      tipoDocumentoId: this.getTipoDocumentoId(value)
    };
  }

  private getTipoDocumentoId(value: IEmpresaDocumento): number {
    if (value.subtipoDocumento) {
      return value.subtipoDocumento.id;
    }
    if (value.tipoDocumento) {
      return value.tipoDocumento.id;
    }
    return null;
  }
}

export const EMPRESA_DOCUMENTO_REQUEST_CONVERTER = new EmpresaDocumentoRequestConverter();
