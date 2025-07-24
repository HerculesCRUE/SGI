import { I18N_FIELD_RESPONSE_CONVERTER } from "@core/i18n/i18n-field.converter";
import { IInformePatentabilidad } from "@core/models/pii/informe-patentabilidad";
import { IInvencion } from "@core/models/pii/invencion";
import { IDocumento } from "@core/models/sgdoc/documento";
import { IEmpresa } from "@core/models/sgemp/empresa";
import { LuxonUtils } from "@core/utils/luxon-utils";
import { SgiBaseConverter } from "@sgi/framework/core";
import { RESULTADO_INFORME_PATENTABILIDAD_RESPONSE_CONVERTER } from "../resultado-informe-patentabilidad/resultado-informe-patentabilidad-response.converter";
import { IInformePatentabilidadResponse } from "./informe-patentabilidad-response";

class InformePatentabilidadResponseConverter extends SgiBaseConverter<IInformePatentabilidadResponse, IInformePatentabilidad> {

  toTarget(value: IInformePatentabilidadResponse): IInformePatentabilidad {
    if (!value) {
      return value as unknown as IInformePatentabilidad;
    }
    return {
      id: value.id,
      invencion: { id: value.invencionId } as IInvencion,
      fecha: LuxonUtils.fromBackend(value.fecha),
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      documento: { documentoRef: value.documentoRef } as IDocumento,
      resultadoInformePatentabilidad: value.resultadoInformePatentabilidad ? RESULTADO_INFORME_PATENTABILIDAD_RESPONSE_CONVERTER.toTarget(value.resultadoInformePatentabilidad) : null,
      entidadCreadora: { id: value.entidadCreadoraRef } as IEmpresa,
      contactoEntidadCreadora: value.contactoEntidadCreadora,
      contactoExaminador: value.contactoExaminador,
      comentarios: value.comentarios ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.comentarios) : []
    };
  }

  fromTarget(value: IInformePatentabilidad): IInformePatentabilidadResponse {
    if (!value) {
      return value as unknown as IInformePatentabilidadResponse;
    }
    return {
      id: value.id,
      invencionId: value.invencion?.id,
      fecha: LuxonUtils.toBackend(value.fecha),
      nombre: value.nombre ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      documentoRef: value.documento?.documentoRef,
      resultadoInformePatentabilidad: value.resultadoInformePatentabilidad ? RESULTADO_INFORME_PATENTABILIDAD_RESPONSE_CONVERTER.fromTarget(value.resultadoInformePatentabilidad) : null,
      entidadCreadoraRef: value.entidadCreadora.id,
      contactoEntidadCreadora: value.contactoEntidadCreadora,
      contactoExaminador: value.contactoExaminador,
      comentarios: value.comentarios ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.comentarios) : []
    };
  }
}

export const INFORME_PATENTABILIDAD_RESPONSE_CONVERTER = new InformePatentabilidadResponseConverter();