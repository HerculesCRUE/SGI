import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IComite, IComiteNombre } from '@core/models/eti/comite';
import { SgiBaseConverter } from '@sgi/framework/core';
import { IComiteNombreResponse, IComiteResponse } from './comite-response';

class ComiteResponseConverter extends SgiBaseConverter<IComiteResponse, IComite> {
  toTarget(value: IComiteResponse): IComite {
    if (!value) {
      return value as unknown as IComite;
    }
    return {
      id: value.id,
      codigo: value.codigo,
      nombre: value.nombre ? COMITE_NOMBRE_RESPONSE_CONVERTER.toTargetArray(value.nombre) : [],
      formularioMemoriaId: value.formularioMemoriaId,
      formularioSeguimientoAnualId: value.formularioSeguimientoAnualId,
      formularioSeguimientoFinalId: value.formularioSeguimientoFinalId,
      formularioRetrospectivaId: value.formularioRetrospectivaId,
      requiereRetrospectiva: value.requiereRetrospectiva,
      prefijoReferencia: value.prefijoReferencia,
      permitirRatificacion: value.permitirRatificacion,
      tareaNombreLibre: value.tareaNombreLibre,
      tareaExperienciaLibre: value.tareaExperienciaLibre,
      tareaExperienciaDetalle: value.tareaExperienciaDetalle,
      memoriaTituloLibre: value.memoriaTituloLibre,
      activo: value.activo
    };
  }

  fromTarget(value: IComite): IComiteResponse {
    if (!value) {
      return value as unknown as IComiteResponse;
    }
    return {
      id: value.id,
      codigo: value.codigo,
      nombre: value.nombre ? COMITE_NOMBRE_RESPONSE_CONVERTER.fromTargetArray(value.nombre) : [],
      formularioMemoriaId: value.formularioMemoriaId,
      formularioSeguimientoAnualId: value.formularioSeguimientoAnualId,
      formularioSeguimientoFinalId: value.formularioSeguimientoFinalId,
      formularioRetrospectivaId: value.formularioRetrospectivaId,
      requiereRetrospectiva: value.requiereRetrospectiva,
      prefijoReferencia: value.prefijoReferencia,
      permitirRatificacion: value.permitirRatificacion,
      tareaNombreLibre: value.tareaNombreLibre,
      tareaExperienciaLibre: value.tareaExperienciaLibre,
      tareaExperienciaDetalle: value.tareaExperienciaDetalle,
      memoriaTituloLibre: value.memoriaTituloLibre,
      activo: value.activo
    };
  }
}
export const COMITE_RESPONSE_CONVERTER = new ComiteResponseConverter();

class ComiteNombreResponseConverter extends SgiBaseConverter<IComiteNombreResponse, IComiteNombre> {
  toTarget(value: IComiteNombreResponse): IComiteNombre {
    if (!value) {
      return value as unknown as IComiteNombre;
    }
    return {
      ...I18N_FIELD_RESPONSE_CONVERTER.toTarget(value),
      genero: value.genero
    }
  }

  fromTarget(value: IComiteNombre): IComiteNombreResponse {
    if (!value) {
      return value as unknown as IComiteNombreResponse;
    }
    return {
      ...I18N_FIELD_RESPONSE_CONVERTER.fromTarget(value),
      genero: value.genero
    };
  }
}

const COMITE_NOMBRE_RESPONSE_CONVERTER = new ComiteNombreResponseConverter();
