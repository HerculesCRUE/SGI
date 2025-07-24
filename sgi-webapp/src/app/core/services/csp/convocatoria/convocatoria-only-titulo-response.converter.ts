import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaOnlyTituloResponse } from '@core/services/csp/convocatoria/convocatoria-only-titulo-response';
import { SgiBaseConverter } from '@sgi/framework/core';

class ConvocatoriaOnlyTituloResponseConverter
  extends SgiBaseConverter<IConvocatoriaOnlyTituloResponse, IConvocatoria> {
  toTarget(value: IConvocatoriaOnlyTituloResponse): IConvocatoria {
    if (!value) {
      return value as unknown as IConvocatoria;
    }
    return {
      id: value.id,
      observaciones: undefined,
      abiertoPlazoPresentacionSolicitud: undefined,
      activo: undefined,
      ambitoGeografico: undefined,
      clasificacionCVN: undefined,
      codigo: undefined,
      duracion: undefined,
      estado: undefined,
      excelencia: undefined,
      fechaConcesion: undefined,
      fechaProvisional: undefined,
      fechaPublicacion: undefined,
      finalidad: undefined,
      formularioSolicitud: undefined,
      modeloEjecucion: undefined,
      objeto: undefined,
      regimenConcurrencia: undefined,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.titulo) : [],
      unidadGestion: undefined,
      codigoInterno: undefined,
      anio: undefined
    };
  }

  fromTarget(value: IConvocatoria): IConvocatoriaOnlyTituloResponse {
    if (!value) {
      return value as unknown as IConvocatoriaOnlyTituloResponse;
    }
    return {
      id: value.id,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.titulo) : [],
    };
  }
}

export const CONVOCATORIA_ONLY_TITULO_RESPONSE_CONVERTER = new ConvocatoriaOnlyTituloResponseConverter();
