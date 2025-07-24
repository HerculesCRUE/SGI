import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyectoFacturacion } from '@core/models/csp/proyecto-facturacion';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { TIPO_FACTURACION_RESPONSE_CONVERTER } from '../tipo-facturacion/tipo-facturacion-response.converter';
import { IProyectoFacturacionResponse } from './proyecto-facturacion-response';

class ProyectoFacturacionResponseConverter extends
  SgiBaseConverter<IProyectoFacturacionResponse, IProyectoFacturacion> {

  toTarget(value: IProyectoFacturacionResponse): IProyectoFacturacion {

    return !value ? value as unknown as IProyectoFacturacion : {
      id: value.id,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentario) : [],
      estadoValidacionIP: value.estadoValidacionIP,
      fechaConformidad: LuxonUtils.fromBackend(value.fechaConformidad),
      fechaEmision: LuxonUtils.fromBackend(value.fechaEmision),
      importeBase: value.importeBase,
      numeroFacturaSge: value.numeroFacturaSge,
      numeroPrevision: value.numeroPrevision,
      porcentajeIVA: value.porcentajeIVA,
      proyectoId: value.proyectoId,
      proyectoProrroga: value.proyectoProrrogaId ? { id: value.proyectoProrrogaId } as IProyectoProrroga : null,
      proyectoSgeRef: value.proyectoSgeRef,
      tipoFacturacion: TIPO_FACTURACION_RESPONSE_CONVERTER.toTarget(value.tipoFacturacion),
    };
  }

  fromTarget(value: IProyectoFacturacion): IProyectoFacturacionResponse {
    return !value ? value as unknown as IProyectoFacturacionResponse : {
      id: value.id,
      comentario: value.comentario ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentario) : [],
      estadoValidacionIP: value.estadoValidacionIP,
      fechaConformidad: LuxonUtils.toBackend(value.fechaConformidad),
      fechaEmision: LuxonUtils.toBackend(value.fechaEmision),
      importeBase: value.importeBase,
      numeroFacturaSge: value.numeroFacturaSge,
      numeroPrevision: value.numeroPrevision,
      porcentajeIVA: value.porcentajeIVA,
      proyectoId: value.proyectoId,
      proyectoProrrogaId: value.proyectoProrroga?.id,
      proyectoSgeRef: value.proyectoSgeRef,
      tipoFacturacion: TIPO_FACTURACION_RESPONSE_CONVERTER.fromTarget(value.tipoFacturacion),
    };
  }

}

export const PROYECTO_FACTURACION_RESPONSE_CONVERTER = new ProyectoFacturacionResponseConverter();
