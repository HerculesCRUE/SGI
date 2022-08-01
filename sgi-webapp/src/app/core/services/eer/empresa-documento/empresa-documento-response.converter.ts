import { IEmpresaDocumento } from '@core/models/eer/empresa-documento';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { ITipoDocumento } from '@core/models/eer/tipo-documento';
import { IDocumento } from '@core/models/sgdoc/documento';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IEmpresaDocumentoResponse } from './empresa-documento-response';

class EmpresaDocumentoResponseConverter
  extends SgiBaseConverter<IEmpresaDocumentoResponse, IEmpresaDocumento> {
  toTarget(value: IEmpresaDocumentoResponse): IEmpresaDocumento {
    if (!value) {
      return value as unknown as IEmpresaDocumento;
    }
    return {
      id: value.id,
      comentarios: value.comentarios,
      documento: value.documentoRef ? { documentoRef: value.documentoRef } as IDocumento : undefined,
      empresa: value.empresaId ? { id: value.empresaId } as IEmpresaExplotacionResultados : undefined,
      nombre: value.nombre,
      subtipoDocumento: this.getSubtipoDocumento(value),
      tipoDocumento: this.getTipoDocumento(value)
    };
  }

  fromTarget(value: IEmpresaDocumento): IEmpresaDocumentoResponse {
    if (!value) {
      return value as unknown as IEmpresaDocumentoResponse;
    }
    return {
      id: value.id,
      comentarios: value.comentarios,
      documentoRef: value.documento.documentoRef,
      empresaId: value.empresa.id,
      nombre: value.nombre,
      tipoDocumento: value.subtipoDocumento ?
        {
          id: value.subtipoDocumento.id,
          descripcion: value.subtipoDocumento.descripcion,
          nombre: value.subtipoDocumento.nombre,
          activo: value.subtipoDocumento.activo,
          padre: value.tipoDocumento
        } :
        {
          id: value.tipoDocumento.id,
          descripcion: value.tipoDocumento.descripcion,
          nombre: value.tipoDocumento.nombre,
          activo: value.tipoDocumento.activo,
          padre: undefined
        }
    };
  }

  private getTipoDocumento({ tipoDocumento }: IEmpresaDocumentoResponse): ITipoDocumento {
    if (!tipoDocumento) {
      return undefined;
    }
    const { padre, ...data } = tipoDocumento;
    return padre ? padre : data;
  }

  private getSubtipoDocumento({ tipoDocumento }: IEmpresaDocumentoResponse): ITipoDocumento {
    if (!tipoDocumento) {
      return undefined;
    }
    const { padre, ...data } = tipoDocumento;
    return padre ? data : undefined;
  }
}

export const EMPRESA_DOCUMENTO_RESPONSE_CONVERTER = new EmpresaDocumentoResponseConverter();
