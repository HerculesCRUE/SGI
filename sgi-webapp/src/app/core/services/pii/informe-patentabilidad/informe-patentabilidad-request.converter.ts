import { IInformePatentabilidad } from "@core/models/pii/informe-patentabilidad";
import { IInvencion } from "@core/models/pii/invencion";
import { IResultadoInformePatentibilidad } from "@core/models/pii/resultado-informe-patentabilidad";
import { IDocumento } from "@core/models/sgdoc/documento";
import { IEmpresa } from "@core/models/sgemp/empresa";
import { LuxonUtils } from "@core/utils/luxon-utils";
import { SgiBaseConverter } from "@sgi/framework/core";
import { IInformePatentabilidadRequest } from "./informe-patentabilidad-request";

class InformePatentabilidadRequestConverter extends SgiBaseConverter<IInformePatentabilidadRequest, IInformePatentabilidad>{

  toTarget(value: IInformePatentabilidadRequest): IInformePatentabilidad {
    if (!value) {
      return value as unknown as IInformePatentabilidad;
    }
    return {
      id: undefined,
      invencion: { id: value.invencionId } as IInvencion,
      fecha: LuxonUtils.fromBackend(value.fecha),
      nombre: value.nombre,
      documento: { documentoRef: value.documentoRef } as IDocumento,
      resultadoInformePatentabilidad: { id: value.resultadoInformePatentabilidadId } as IResultadoInformePatentibilidad,
      entidadCreadora: { id: value.entidadCreadoraRef } as IEmpresa,
      contactoEntidadCreadora: value.contactoEntidadCreadora,
      contactoExaminador: value.contactoExaminador,
      comentarios: value.comentarios
    };
  }

  fromTarget(value: IInformePatentabilidad): IInformePatentabilidadRequest {
    if (!value) {
      return value as unknown as IInformePatentabilidadRequest;
    }
    return {
      invencionId: value.invencion?.id,
      fecha: LuxonUtils.toBackend(value.fecha),
      nombre: value.nombre,
      documentoRef: value.documento?.documentoRef,
      resultadoInformePatentabilidadId: value.resultadoInformePatentabilidad?.id,
      entidadCreadoraRef: value.entidadCreadora?.id,
      contactoEntidadCreadora: value.contactoEntidadCreadora,
      contactoExaminador: value.contactoExaminador,
      comentarios: value.comentarios
    };
  }
}

export const INFORME_PATENTABILIDAD_REQUEST_CONVERTER = new InformePatentabilidadRequestConverter();