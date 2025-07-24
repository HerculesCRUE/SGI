import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { IConvocatoriaResponse } from '@core/services/csp/convocatoria/convocatoria-response';
import { MODELO_EJECUCION_RESPONSE_CONVERTER } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response.converter';
import { TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER } from '@core/services/csp/tipo-ambito-geografico/tipo-ambito-geografico-response.converter';
import { TIPO_FINALIDAD_RESPONSE_CONVERTER } from '@core/services/csp/tipo-finalidad/tipo-finalidad-response.converter';
import { TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER } from '@core/services/csp/tipo-regimen-concurrencia/tipo-regimen-concurrencia-response.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaResponseConverter extends SgiBaseConverter<IConvocatoriaResponse, IConvocatoria> {

  toTarget(value: IConvocatoriaResponse): IConvocatoria {
    if (!value) {
      return value as unknown as IConvocatoria;
    }
    return {
      id: value.id,
      unidadGestion: { id: +value.unidadGestionRef } as IUnidadGestion,
      modeloEjecucion: MODELO_EJECUCION_RESPONSE_CONVERTER.toTarget(value.modeloEjecucion),
      codigo: value.codigo,
      fechaPublicacion: LuxonUtils.fromBackend(value.fechaPublicacion),
      fechaProvisional: LuxonUtils.fromBackend(value.fechaProvisional),
      fechaConcesion: LuxonUtils.fromBackend(value.fechaConcesion),
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.titulo) : [],
      objeto: value.objeto ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.objeto) : [],
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      finalidad: TIPO_FINALIDAD_RESPONSE_CONVERTER.toTarget(value.finalidad),
      regimenConcurrencia: TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER.toTarget(value.regimenConcurrencia),
      formularioSolicitud: value.formularioSolicitud,
      estado: value.estado,
      duracion: value.duracion,
      abiertoPlazoPresentacionSolicitud: value.abiertoPlazoPresentacionSolicitud,
      ambitoGeografico: TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER.toTarget(value.ambitoGeografico),
      clasificacionCVN: value.clasificacionCVN,
      activo: value.activo,
      excelencia: value.excelencia,
      codigoInterno: value.codigoInterno,
      anio: value.anio
    };
  }

  fromTarget(value: IConvocatoria): IConvocatoriaResponse {
    if (!value) {
      return value as unknown as IConvocatoriaResponse;
    }
    return {
      id: value.id,
      unidadGestionRef: String(value.unidadGestion?.id),
      modeloEjecucion: MODELO_EJECUCION_RESPONSE_CONVERTER.fromTarget(value.modeloEjecucion),
      codigo: value.codigo,
      fechaPublicacion: LuxonUtils.toBackend(value.fechaPublicacion),
      fechaProvisional: LuxonUtils.toBackend(value.fechaProvisional),
      fechaConcesion: LuxonUtils.toBackend(value.fechaConcesion),
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.titulo) : [],
      objeto: value.objeto ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.objeto) : [],
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      finalidad: TIPO_FINALIDAD_RESPONSE_CONVERTER.fromTarget(value.finalidad),
      regimenConcurrencia: TIPO_REGIMEN_CONCURRENCIA_RESPONSE_CONVERTER.fromTarget(value.regimenConcurrencia),
      formularioSolicitud: value.formularioSolicitud,
      estado: value.estado,
      duracion: value.duracion,
      abiertoPlazoPresentacionSolicitud: value.abiertoPlazoPresentacionSolicitud,
      ambitoGeografico: TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER.fromTarget(value.ambitoGeografico),
      clasificacionCVN: value.clasificacionCVN,
      activo: value.activo,
      excelencia: value.excelencia,
      codigoInterno: value.codigoInterno,
      anio: value.anio
    };
  }
}

export const CONVOCATORIA_RESPONSE_CONVERTER = new ConvocatoriaResponseConverter();
