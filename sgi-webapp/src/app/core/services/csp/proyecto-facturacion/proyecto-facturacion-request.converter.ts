import { I18N_FIELD_REQUEST_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoFacturacion } from '@core/models/csp/proyecto-facturacion';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoFacturacionRequest } from './proyecto-facturacion-request';

class ProyectoFacturacionRequestConverter extends SgiBaseConverter<IProyectoFacturacionRequest, IProyectoFacturacion> {

  toTarget(value: IProyectoFacturacionRequest): IProyectoFacturacion {

    return !value ? value as unknown as IProyectoFacturacion : {
      id: undefined,
      comentario: value.comentario ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.comentario) : [],
      estadoValidacionIP: {
        comentario: value.estadoValidacionIP.comentario ? I18N_FIELD_REQUEST_CONVERTER.toTargetArray(value.estadoValidacionIP.comentario) : [],
        estado: value.estadoValidacionIP?.estado,
        fecha: undefined,
        id: value.estadoValidacionIP?.id,
        proyectoFacturacionId: undefined
      },
      fechaConformidad: LuxonUtils.fromBackend(value.fechaConformidad),
      fechaEmision: LuxonUtils.fromBackend(value.fechaEmision),
      importeBase: value.importeBase,
      numeroFacturaSge: value.numeroFacturaSge,
      numeroPrevision: value.numeroPrevision,
      porcentajeIVA: value.porcentajeIVA,
      proyectoId: value.proyectoId,
      proyectoProrroga: value.proyectoProrrogaId ? { id: value.proyectoProrrogaId } as IProyectoProrroga : null,
      proyectoSgeRef: value.proyectoSgeRef,
      tipoFacturacion: { id: value.tipoFacturacionId } as ITipoFacturacion,
    };
  }

  fromTarget(value: IProyectoFacturacion): IProyectoFacturacionRequest {

    return !value ? value as unknown as IProyectoFacturacionRequest : {
      comentario: value.comentario ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.comentario) : [],
      estadoValidacionIP: {
        id: value.estadoValidacionIP.id,
        comentario: value.estadoValidacionIP.comentario ? I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(value.estadoValidacionIP.comentario) : [],
        estado: value.estadoValidacionIP.estado,
      },
      fechaConformidad: LuxonUtils.toBackend(value.fechaConformidad),
      fechaEmision: LuxonUtils.toBackend(value.fechaEmision),
      importeBase: value.importeBase,
      numeroFacturaSge: value.numeroFacturaSge,
      numeroPrevision: value.numeroPrevision,
      porcentajeIVA: value.porcentajeIVA,
      proyectoId: value.proyectoId,
      proyectoProrrogaId: value.proyectoProrroga?.id,
      proyectoSgeRef: value.proyectoSgeRef,
      tipoFacturacionId: value.tipoFacturacion?.id,
    };
  }

}

export const PROYECTO_FACTURACION_REQUEST_CONVERTER = new ProyectoFacturacionRequestConverter();
