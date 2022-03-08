import { IConvocatoria } from '@core/models/csp/convocatoria';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IConvocatoriaTituloResponse } from './convocatoria-titulo-response';

class ConvocatoriaTituloResponseConverter
  extends SgiBaseConverter<IConvocatoriaTituloResponse, IConvocatoria> {
  toTarget(value: IConvocatoriaTituloResponse): IConvocatoria {
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
      titulo: value.titulo,
      unidadGestion: undefined
    };
  }

  fromTarget(value: IConvocatoria): IConvocatoriaTituloResponse {
    if (!value) {
      return value as unknown as IConvocatoriaTituloResponse;
    }
    return {
      id: value.id,
      titulo: value.titulo
    };
  }
}

export const CONVOCATORIA_TITULO_RESPONSE_CONVERTER = new ConvocatoriaTituloResponseConverter();
