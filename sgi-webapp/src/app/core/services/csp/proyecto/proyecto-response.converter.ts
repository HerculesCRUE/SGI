import { I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
import { IProyecto } from '@core/models/csp/proyecto';
import { IRolSocio } from '@core/models/csp/rol-socio';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { MODELO_EJECUCION_RESPONSE_CONVERTER } from '@core/services/csp/modelo-ejecucion/modelo-ejecucion-response.converter';
import { IProyectoResponse } from '@core/services/csp/proyecto/proyecto-response';
import { TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER } from '@core/services/csp/tipo-ambito-geografico/tipo-ambito-geografico-response.converter';
import { TIPO_FINALIDAD_RESPONSE_CONVERTER } from '@core/services/csp/tipo-finalidad/tipo-finalidad-response.converter';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { PROYECTO_IVA_CONVERTER } from '../../../converters/csp/proyecto-iva.converter';
import { ESTADO_PROYECTO_RESPONSE_CONVERTER } from '../estado-proyecto/estado-proyecto-response.converter';

class ProyectoResponseConverter extends SgiBaseConverter<IProyectoResponse, IProyecto> {

  toTarget(value: IProyectoResponse): IProyecto {
    if (!value) {
      return value as unknown as IProyecto;
    }
    return {
      id: value.id,
      estado: ESTADO_PROYECTO_RESPONSE_CONVERTER.toTarget(value.estado),
      activo: value.activo,
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.titulo) : [],
      acronimo: value.acronimo,
      codigoInterno: value.codigoInterno,
      codigoExterno: value.codigoExterno,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaInicioStarted: value.fechaInicioStarted,
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaFinDefinitiva: LuxonUtils.fromBackend(value.fechaFinDefinitiva),
      modeloEjecucion: MODELO_EJECUCION_RESPONSE_CONVERTER.toTarget(value.modeloEjecucion),
      finalidad: TIPO_FINALIDAD_RESPONSE_CONVERTER.toTarget(value.finalidad),
      convocatoriaId: value.convocatoriaId,
      convocatoriaExterna: value.convocatoriaExterna,
      solicitudId: value.solicitudId,
      ambitoGeografico: TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER.toTarget(value.ambitoGeografico),
      confidencial: value.confidencial,
      clasificacionCVN: value.clasificacionCVN,
      colaborativo: value.colaborativo,
      excelencia: value.excelencia,
      coordinado: value.coordinado,
      rolUniversidad: value.rolUniversidadId ? { id: value.rolUniversidadId } as IRolSocio : null,
      permitePaquetesTrabajo: value.permitePaquetesTrabajo,
      causaExencion: value.causaExencion,
      iva: PROYECTO_IVA_CONVERTER.toTarget(value.iva),
      ivaDeducible: value.ivaDeducible,
      anualidades: value.anualidades,
      unidadGestion: { id: +value.unidadGestionRef } as IUnidadGestion,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.toTargetArray(value.observaciones) : [],
      tipoSeguimiento: value.tipoSeguimiento,
      importeConcedido: value.importeConcedido,
      importePresupuesto: value.importePresupuesto,
      importePresupuestoCostesIndirectos: value.importePresupuestoCostesIndirectos,
      importeConcedidoCostesIndirectos: value.importeConcedidoCostesIndirectos,
      importeConcedidoSocios: value.importeConcedidoSocios,
      importePresupuestoSocios: value.importePresupuestoSocios,
      totalImporteConcedido: value.totalImporteConcedido,
      totalImportePresupuesto: value.totalImportePresupuesto,
      anio: value.anio
    };
  }

  fromTarget(value: IProyecto): IProyectoResponse {
    if (!value) {
      return value as unknown as IProyectoResponse;
    }
    return {
      id: value.id,
      estado: ESTADO_PROYECTO_RESPONSE_CONVERTER.fromTarget(value.estado),
      titulo: value.titulo ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.titulo) : [],
      acronimo: value.acronimo,
      codigoInterno: value.codigoInterno,
      codigoExterno: value.codigoExterno,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaInicioStarted: value.fechaInicioStarted,
      fechaFinDefinitiva: LuxonUtils.toBackend(value.fechaFinDefinitiva),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      unidadGestionRef: String(value.unidadGestion?.id),
      modeloEjecucion: MODELO_EJECUCION_RESPONSE_CONVERTER.fromTarget(value.modeloEjecucion),
      finalidad: TIPO_FINALIDAD_RESPONSE_CONVERTER.fromTarget(value.finalidad),
      convocatoriaId: value.convocatoriaId,
      convocatoriaExterna: value.convocatoriaExterna,
      solicitudId: value.solicitudId,
      ambitoGeografico: TIPO_AMBITO_GEOGRAFICO_RESPONSE_CONVERTER.fromTarget(value.ambitoGeografico),
      confidencial: value.confidencial,
      clasificacionCVN: value.clasificacionCVN,
      coordinado: value.coordinado,
      colaborativo: value.colaborativo,
      excelencia: value.excelencia,
      rolUniversidadId: value.rolUniversidad?.id,
      permitePaquetesTrabajo: value.permitePaquetesTrabajo,
      causaExencion: value.causaExencion,
      iva: PROYECTO_IVA_CONVERTER.fromTarget(value.iva),
      ivaDeducible: value.ivaDeducible,
      observaciones: value.observaciones ? I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(value.observaciones) : [],
      anualidades: value.anualidades,
      activo: value.activo,
      tipoSeguimiento: value.tipoSeguimiento,
      importeConcedido: value.importeConcedido,
      importePresupuesto: value.importePresupuesto,
      importePresupuestoCostesIndirectos: value.importePresupuestoCostesIndirectos,
      importeConcedidoCostesIndirectos: value.importeConcedidoCostesIndirectos,
      importeConcedidoSocios: value.importeConcedidoSocios,
      importePresupuestoSocios: value.importePresupuestoSocios,
      totalImporteConcedido: value.totalImporteConcedido,
      totalImportePresupuesto: value.totalImportePresupuesto,
      anio: value.anio
    };
  }
}

export const PROYECTO_RESPONSE_CONVERTER = new ProyectoResponseConverter();
