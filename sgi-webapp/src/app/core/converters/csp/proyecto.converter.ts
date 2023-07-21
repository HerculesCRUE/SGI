import { IProyectoBackend } from '@core/models/csp/backend/proyecto-backend';
import { IProyecto } from '@core/models/csp/proyecto';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiBaseConverter } from '@sgi/framework/core';
import { ESTADO_PROYECTO_CONVERTER } from './estado-proyecto.converter';
import { PROYECTO_IVA_CONVERTER } from './proyecto-iva.converter';

class ProyectoConverter extends SgiBaseConverter<IProyectoBackend, IProyecto> {

  toTarget(value: IProyectoBackend): IProyecto {
    if (!value) {
      return value as unknown as IProyecto;
    }
    return {
      id: value.id,
      estado: ESTADO_PROYECTO_CONVERTER.toTarget(value.estado),
      activo: value.activo,
      titulo: value.titulo,
      acronimo: value.acronimo,
      codigoInterno: value.codigoInterno,
      codigoExterno: value.codigoExterno,
      fechaInicio: LuxonUtils.fromBackend(value.fechaInicio),
      fechaFin: LuxonUtils.fromBackend(value.fechaFin),
      fechaFinDefinitiva: LuxonUtils.fromBackend(value.fechaFinDefinitiva),
      modeloEjecucion: value.modeloEjecucion,
      finalidad: value.finalidad,
      convocatoriaId: value.convocatoriaId,
      convocatoriaExterna: value.convocatoriaExterna,
      solicitudId: value.solicitudId,
      ambitoGeografico: value.ambitoGeografico,
      confidencial: value.confidencial,
      clasificacionCVN: value.clasificacionCVN,
      colaborativo: value.colaborativo,
      excelencia: value.excelencia,
      coordinado: value.coordinado,
      coordinadorExterno: value.coordinadorExterno,
      permitePaquetesTrabajo: value.permitePaquetesTrabajo,
      causaExencion: value.causaExencion,
      iva: PROYECTO_IVA_CONVERTER.toTarget(value.iva),
      anualidades: value.anualidades,
      unidadGestion: { id: +value.unidadGestionRef } as IUnidadGestion,
      observaciones: value.observaciones,
      comentario: value.estado.comentario,
      tipoSeguimiento: value.tipoSeguimiento,
      importeConcedido: value.importeConcedido,
      importePresupuesto: value.importePresupuesto,
      importePresupuestoCostesIndirectos: value.importePresupuestoCostesIndirectos,
      importeConcedidoCostesIndirectos: value.importeConcedidoCostesIndirectos,
      importeConcedidoSocios: value.importeConcedidoSocios,
      importePresupuestoSocios: value.importePresupuestoSocios,
      totalImporteConcedido: value.totalImporteConcedido,
      totalImportePresupuesto: value.totalImportePresupuesto
    };
  }

  fromTarget(value: IProyecto): IProyectoBackend {
    if (!value) {
      return value as unknown as IProyectoBackend;
    }
    return {
      id: value.id,
      estado: ESTADO_PROYECTO_CONVERTER.fromTarget(value.estado),
      titulo: value.titulo,
      acronimo: value.acronimo,
      codigoInterno: value.codigoInterno,
      codigoExterno: value.codigoExterno,
      fechaInicio: LuxonUtils.toBackend(value.fechaInicio),
      fechaFinDefinitiva: LuxonUtils.toBackend(value.fechaFinDefinitiva),
      fechaFin: LuxonUtils.toBackend(value.fechaFin),
      unidadGestionRef: String(value.unidadGestion?.id),
      modeloEjecucion: value.modeloEjecucion,
      finalidad: value.finalidad,
      convocatoriaId: value.convocatoriaId,
      convocatoriaExterna: value.convocatoriaExterna,
      solicitudId: value.solicitudId,
      ambitoGeografico: value.ambitoGeografico,
      confidencial: value.confidencial,
      clasificacionCVN: value.clasificacionCVN,
      coordinado: value.coordinado,
      colaborativo: value.colaborativo,
      excelencia: value.excelencia,
      coordinadorExterno: value.coordinadorExterno,
      permitePaquetesTrabajo: value.permitePaquetesTrabajo,
      causaExencion: value.causaExencion,
      iva: PROYECTO_IVA_CONVERTER.fromTarget(value.iva),
      observaciones: value.observaciones,
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
      totalImportePresupuesto: value.totalImportePresupuesto
    };
  }
}

export const PROYECTO_CONVERTER = new ProyectoConverter();
