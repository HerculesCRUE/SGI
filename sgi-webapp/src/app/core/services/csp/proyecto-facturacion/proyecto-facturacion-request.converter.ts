import { IEstadoValidacionIP } from '@core/models/csp/estado-validacion-ip';
import { IProyectoFacturacion } from '@core/models/csp/proyecto-facturacion';
import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IProyectoFacturacionRequest } from './proyecto-facturacion-request';

class ProyectoFacturacionRequestConverter extends SgiBaseConverter<IProyectoFacturacionRequest, IProyectoFacturacion> {

  toTarget(value: IProyectoFacturacionRequest): IProyectoFacturacion {

    return !value ? value as unknown as IProyectoFacturacion : {
      id: undefined,
      comentario: value.comentario,
      fechaConformidad: LuxonUtils.fromBackend(value.fechaConformidad),
      fechaEmision: LuxonUtils.fromBackend(value.fechaEmision),
      importeBase: value.importeBase,
      numeroPrevision: value.numeroPrevision,
      porcentajeIVA: value.porcentajeIVA,
      proyectoId: value.proyectoId,
      tipoFacturacion: { id: value.tipoFacturacionId } as ITipoFacturacion,
      estadoValidacionIP: {
        id: value.estadoValidacionIP?.id,
        comentario: value.estadoValidacionIP?.comentario,
        estado: value.estadoValidacionIP?.estado,
        fecha: undefined,
        proyectoFacturacionId: undefined
      }
    };
  }

  fromTarget(value: IProyectoFacturacion): IProyectoFacturacionRequest {

    return !value ? value as unknown as IProyectoFacturacionRequest : {
      comentario: value.comentario,
      fechaConformidad: LuxonUtils.toBackend(value.fechaConformidad),
      fechaEmision: LuxonUtils.toBackend(value.fechaEmision),
      importeBase: value.importeBase,
      numeroPrevision: value.numeroPrevision,
      porcentajeIVA: value.porcentajeIVA,
      proyectoId: value.proyectoId,
      tipoFacturacionId: value.tipoFacturacion?.id,
      estadoValidacionIP: {
        id: value.estadoValidacionIP.id,
        comentario: value.estadoValidacionIP.comentario,
        estado: value.estadoValidacionIP.estado,
      }
    };
  }

}

export const PROYECTO_FACTURACION_REQUEST_CONVERTER = new ProyectoFacturacionRequestConverter();
