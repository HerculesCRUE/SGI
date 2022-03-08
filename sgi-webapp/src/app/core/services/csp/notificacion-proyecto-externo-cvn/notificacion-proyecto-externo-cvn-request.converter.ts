import { IAutorizacion } from '@core/models/csp/autorizacion';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { IProyecto } from '@core/models/csp/proyecto';
import { IDocumento } from '@core/models/sgdoc/documento';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IPersona } from '@core/models/sgp/persona';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { INotificacionProyectoExternoCVNRequest } from './notificacion-proyecto-externo-cvn-request';

class INotificacionProyectoExternoCVNRequestConverter
  extends SgiBaseConverter<INotificacionProyectoExternoCVNRequest, INotificacionProyectoExternoCVN> {
  toTarget(value: INotificacionProyectoExternoCVNRequest): INotificacionProyectoExternoCVN {
    if (!value) {
      return value as unknown as INotificacionProyectoExternoCVN;
    }
    return {
      id: undefined,
      titulo: value.titulo,
      autorizacion: { id: value.autorizacionId } as IAutorizacion,
      proyecto: { id: value.proyectoId } as IProyecto,
      ambitoGeografico: value.ambitoGeografico,
      codExterno: value.codExterno,
      datosEntidadParticipacion: value.datosEntidadParticipacion,
      datosResponsable: value.datosResponsable,
      documento: { documentoRef: value.documentoRef } as IDocumento,
      entidadParticipacion: { id: value.entidadParticipacionRef } as IEmpresa,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      gradoContribucion: value.gradoContribucion,
      importeTotal: value.importeTotal,
      nombrePrograma: value.nombrePrograma,
      porcentajeSubvencion: value.porcentajeSubvencion,
      proyectoCVNId: value.proyectoCVNId,
      responsable: { id: value.responsableRef } as IPersona,
      urlDocumentoAcreditacion: value.urlDocumentoAcreditacion,
      solicitante: { id: value.solicitanteRef } as IPersona,
    };
  }

  fromTarget(value: INotificacionProyectoExternoCVN): INotificacionProyectoExternoCVNRequest {
    if (!value) {
      return value as unknown as INotificacionProyectoExternoCVNRequest;
    }
    return {
      titulo: value.titulo,
      autorizacionId: value.autorizacion?.id,
      proyectoId: value.proyecto?.id,
      ambitoGeografico: value.ambitoGeografico,
      codExterno: value.codExterno,
      datosEntidadParticipacion: value.datosEntidadParticipacion,
      datosResponsable: value.datosResponsable,
      documentoRef: value.documento.documentoRef,
      entidadParticipacionRef: value.entidadParticipacion?.id,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      gradoContribucion: value.gradoContribucion,
      importeTotal: value.importeTotal,
      nombrePrograma: value.nombrePrograma,
      porcentajeSubvencion: value.porcentajeSubvencion,
      proyectoCVNId: value.proyectoCVNId,
      responsableRef: value.responsable?.id,
      urlDocumentoAcreditacion: value.urlDocumentoAcreditacion,
      solicitanteRef: value.solicitante?.id,
    };
  }
}

export const NOTIFICACION_PROYECTO_EXTERNO_CVN_REQUEST_CONVERTER = new INotificacionProyectoExternoCVNRequestConverter();
