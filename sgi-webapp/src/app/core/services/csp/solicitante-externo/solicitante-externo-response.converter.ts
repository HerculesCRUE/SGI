import { ISolicitanteExterno } from '@core/models/csp/solicitante-externo';
import { IComunidadAutonoma } from '@core/models/sgo/comunidad-autonoma';
import { IPais } from '@core/models/sgo/pais';
import { IProvincia } from '@core/models/sgo/provincia';
import { ISexo } from '@core/models/sgp/sexo';
import { ITipoDocumento } from '@core/models/sgp/tipo-documento';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ISolicitanteExternoResponse } from './solicitante-externo-response';

class SolicitanteExternoResponseConverter
  extends SgiBaseConverter<ISolicitanteExternoResponse, ISolicitanteExterno> {
  toTarget(value: ISolicitanteExternoResponse): ISolicitanteExterno {
    if (!value) {
      return value as unknown as ISolicitanteExterno;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      nombre: value.nombre,
      apellidos: value.apellidos,
      tipoDocumento: value.tipoDocumentoRef ? { id: value.tipoDocumentoRef } as ITipoDocumento : null,
      numeroDocumento: value.numeroDocumento,
      sexo: value.sexoRef ? { id: value.sexoRef } as ISexo : null,
      fechaNacimiento: LuxonUtils.fromBackend(value.fechaNacimiento),
      paisNacimiento: value.paisNacimientoRef ? { id: value.paisNacimientoRef } as IPais : null,
      telefono: value.telefono,
      email: value.email,
      direccion: value.direccion,
      paisContacto: value.paisContactoRef ? { id: value.paisContactoRef } as IPais : null,
      comunidad: value.comunidadRef ? { id: value.comunidadRef } as IComunidadAutonoma : null,
      provincia: value.provinciaRef ? { id: value.provinciaRef } as IProvincia : null,
      ciudad: value.ciudad,
      codigoPostal: value.codigoPostal
    };
  }

  fromTarget(value: ISolicitanteExterno): ISolicitanteExternoResponse {
    if (!value) {
      return value as unknown as ISolicitanteExternoResponse;
    }
    return {
      id: value.id,
      solicitudId: value.solicitudId,
      nombre: value.nombre,
      apellidos: value.apellidos,
      tipoDocumentoRef: value.tipoDocumento?.id,
      numeroDocumento: value.numeroDocumento,
      sexoRef: value.sexo?.id,
      fechaNacimiento: LuxonUtils.toBackend(value.fechaNacimiento),
      paisNacimientoRef: value.paisNacimiento?.id,
      telefono: value.telefono,
      email: value.email,
      direccion: value.direccion,
      paisContactoRef: value.paisContacto?.id,
      comunidadRef: value.comunidad?.id,
      provinciaRef: value.provincia?.id,
      ciudad: value.ciudad,
      codigoPostal: value.codigoPostal
    };
  }
}

export const SOLICITANTE_EXTERNO_RESPONSE_CONVERTER = new SolicitanteExternoResponseConverter();
