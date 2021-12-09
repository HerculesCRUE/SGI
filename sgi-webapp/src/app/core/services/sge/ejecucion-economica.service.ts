import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IColumna } from '@core/models/sge/columna';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { environment } from '@env';
import {
  RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSort, SgiRestSortDirection
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

enum TipoOperacion {
  EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL = 'EPA',
  EJECUCION_PRESUPUESTARIA_GASTOS = 'EPG',
  EJECUCION_PRESUPUESTARIA_INGRESOS = 'EPI',
  FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS = 'FJF',
  FACTURAS_JUSTIFICANTES_VIAJES_DIETAS = 'FJV',
  FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO = 'FJP',
  DETALLE_OPERACIONES_GASTOS = 'DOG',
  DETALLE_OPERACIONES_INGRESOS = 'DOI',
  DETALLE_OPERACIONES_MODIFICACIONES = 'DOM'
}

@Injectable({
  providedIn: 'root'
})
export class EjecucionEconomicaService extends SgiRestBaseService {
  private static readonly MAPPING = '/ejecucion-economica';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.sge}${EjecucionEconomicaService.MAPPING}`,
      http
    );
  }

  private getColumnas(
    proyectoEconomicoId: string,
    tipoOperacion: TipoOperacion,
    reducido = false
  ): Observable<IColumna[]> {
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoEconomicoId)
      .and('tipoOperacion', SgiRestFilterOperator.EQUALS, tipoOperacion).and('reducida', SgiRestFilterOperator.EQUALS, `${reducido}`);
    const options: SgiRestFindOptions = {
      filter
    };
    return this.find<IColumna, IColumna>(
      `${this.endpointUrl}/columnas`,
      options
    ).pipe(
      map(response => response.items)
    );
  }

  getColumnasEjecucionPresupuestariaEstadoActual(proyectoEconomicoId: string): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL);
  }

  getColumnasEjecucionPresupuestariaGastos(proyectoEconomicoId: string): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.EJECUCION_PRESUPUESTARIA_GASTOS);
  }

  getColumnasEjecucionPresupuestariaIngresos(proyectoEconomicoId: string): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.EJECUCION_PRESUPUESTARIA_INGRESOS);
  }

  getColumnasDetalleOperacionesGastos(proyectoEconomicoId: string): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.DETALLE_OPERACIONES_GASTOS);
  }

  getColumnasDetalleOperacionesIngresos(proyectoEconomicoId: string): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.DETALLE_OPERACIONES_INGRESOS);
  }

  getColumnasDetalleOperacionesModificaciones(proyectoEconomicoId: string): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.DETALLE_OPERACIONES_MODIFICACIONES);
  }

  getColumnasFacturasGastos(proyectoEconomicoId: string, reducida = true): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS, reducida);
  }

  getColumnasViajesDietas(proyectoEconomicoId: string, reducida = true): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.FACTURAS_JUSTIFICANTES_VIAJES_DIETAS, reducida);
  }

  getColumnasPersonalContratado(proyectoEconomicoId: string, reducida = true): Observable<IColumna[]> {
    return this.getColumnas(proyectoEconomicoId, TipoOperacion.FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO, reducida);
  }

  private getDatosEconomicos(
    sort: SgiRestSort,
    proyectoEconomicoId: string,
    tipoOperacion: TipoOperacion,
    anualidades: string[] = [],
    reducido = false,
    fechaPagoRange?: { desde: string, hasta: string },
    fechaDevengoRange?: { desde: string, hasta: string },
    fechaContabilizacionRange?: { desde: string, hasta: string }
  ): Observable<IDatoEconomico[]> {
    const filter = new RSQLSgiRestFilter('proyectoId', SgiRestFilterOperator.EQUALS, proyectoEconomicoId)
      .and('tipoOperacion', SgiRestFilterOperator.EQUALS, tipoOperacion)
      .and('reducida', SgiRestFilterOperator.EQUALS, `${reducido}`);
    if (anualidades.length) {
      filter.and('anualidad', SgiRestFilterOperator.IN, anualidades);
    }
    if (fechaPagoRange?.desde && fechaPagoRange?.hasta) {
      filter.and('fechaPago', SgiRestFilterOperator.GREATHER_OR_EQUAL, fechaPagoRange.desde)
        .and('fechaPago', SgiRestFilterOperator.LOWER_OR_EQUAL, fechaPagoRange.hasta);
    }
    if (fechaDevengoRange?.desde && fechaDevengoRange?.hasta) {
      filter.and('fechaDevengo', SgiRestFilterOperator.GREATHER_OR_EQUAL, fechaDevengoRange.desde)
        .and('fechaDevengo', SgiRestFilterOperator.LOWER_OR_EQUAL, fechaDevengoRange.hasta);
    }
    if (fechaContabilizacionRange?.desde && fechaContabilizacionRange?.hasta) {
      filter.and('fechaContabilizacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, fechaContabilizacionRange.desde)
        .and('fechaContabilizacion', SgiRestFilterOperator.LOWER_OR_EQUAL, fechaContabilizacionRange.hasta);
    }
    const options: SgiRestFindOptions = {
      filter,
      sort
    };
    return this.find<IDatoEconomico, IDatoEconomico>(
      `${this.endpointUrl}`,
      options
    ).pipe(
      map(response => response.items)
    );
  }

  getEjecucionPresupuestariaEstadoActual(proyectoEconomicoId: string, anualidades: string[] = []): Observable<IDatoEconomico[]> {
    const sort = new RSQLSgiRestSort('tipo', SgiRestSortDirection.ASC).and('anualidad', SgiRestSortDirection.DESC);
    return this.getDatosEconomicos(sort, proyectoEconomicoId, TipoOperacion.EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL, anualidades);
  }

  getEjecucionPresupuestariaGastos(proyectoEconomicoId: string, anualidades: string[] = []): Observable<IDatoEconomico[]> {
    const sort = new RSQLSgiRestSort('anualidad', SgiRestSortDirection.DESC);
    return this.getDatosEconomicos(sort, proyectoEconomicoId, TipoOperacion.EJECUCION_PRESUPUESTARIA_GASTOS, anualidades);
  }

  getEjecucionPresupuestariaIngresos(proyectoEconomicoId: string, anualidades: string[] = []): Observable<IDatoEconomico[]> {
    const sort = new RSQLSgiRestSort('anualidad', SgiRestSortDirection.DESC);
    return this.getDatosEconomicos(sort, proyectoEconomicoId, TipoOperacion.EJECUCION_PRESUPUESTARIA_INGRESOS, anualidades);
  }

  getDetalleOperacionesGastos(proyectoEconomicoId: string, anualidades: string[] = []): Observable<IDatoEconomico[]> {
    const sort = new RSQLSgiRestSort('anualidad', SgiRestSortDirection.DESC)
      .and('partidaPresupuestaria', SgiRestSortDirection.ASC)
      .and('codigoEconomico.id', SgiRestSortDirection.ASC);
    return this.getDatosEconomicos(sort, proyectoEconomicoId, TipoOperacion.DETALLE_OPERACIONES_GASTOS, anualidades);
  }

  getDetalleOperacionesIngresos(proyectoEconomicoId: string, anualidades: string[] = []): Observable<IDatoEconomico[]> {
    const sort = new RSQLSgiRestSort('anualidad', SgiRestSortDirection.DESC)
      .and('partidaPresupuestaria', SgiRestSortDirection.ASC)
      .and('codigoEconomico.id', SgiRestSortDirection.ASC);
    return this.getDatosEconomicos(sort, proyectoEconomicoId, TipoOperacion.DETALLE_OPERACIONES_INGRESOS, anualidades);
  }

  getDetalleOperacionesModificaciones(proyectoEconomicoId: string, anualidades: string[] = []): Observable<IDatoEconomico[]> {
    const sort = new RSQLSgiRestSort('anualidad', SgiRestSortDirection.DESC)
      .and('partidaPresupuestaria', SgiRestSortDirection.ASC);
    return this.getDatosEconomicos(sort, proyectoEconomicoId, TipoOperacion.DETALLE_OPERACIONES_MODIFICACIONES, anualidades);
  }

  getFacturasGastos(
    proyectoEconomicoId: string,
    anualidades: string[] = [],
    reducida = true,
    fechaPagoRange?: { desde: string, hasta: string },
    fechaDevengoRange?: { desde: string, hasta: string },
    fechaContabilizacionRange?: { desde: string, hasta: string },
  ): Observable<IDatoEconomico[]> {
    return this.getDatosEconomicos(
      null,
      proyectoEconomicoId,
      TipoOperacion.FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS,
      anualidades,
      reducida,
      fechaPagoRange,
      fechaDevengoRange,
      fechaContabilizacionRange
    );
  }

  getViajesDietas(
    proyectoEconomicoId: string,
    anualidades: string[] = [],
    reducida = true,
    fechaPagoRange?: { desde: string, hasta: string },
    fechaDevengoRange?: { desde: string, hasta: string },
    fechaContabilizacionRange?: { desde: string, hasta: string }
  ): Observable<IDatoEconomico[]> {
    return this.getDatosEconomicos(
      null,
      proyectoEconomicoId,
      TipoOperacion.FACTURAS_JUSTIFICANTES_VIAJES_DIETAS,
      anualidades,
      reducida,
      fechaPagoRange,
      fechaDevengoRange,
      fechaContabilizacionRange
    );
  }

  getPersonalContratado(
    proyectoEconomicoId: string,
    anualidades: string[] = [],
    reducida = true,
    fechaPagoRange?: { desde: string, hasta: string },
    fechaDevengoRange?: { desde: string, hasta: string },
    fechaContabilizacionRange?: { desde: string, hasta: string }
  ): Observable<IDatoEconomico[]> {
    return this.getDatosEconomicos(
      null,
      proyectoEconomicoId,
      TipoOperacion.FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO,
      anualidades,
      reducida,
      fechaPagoRange,
      fechaDevengoRange,
      fechaContabilizacionRange
    );
  }

  private getDatoEconomicoDetalle(id: string, tipoOperacion: TipoOperacion): Observable<IDatoEconomicoDetalle> {
    return this.http.get<IDatoEconomicoDetalle>(`${this.endpointUrl}/${id}`, { params: { tipoOperacion } });
  }

  getFacturaGasto(id: string): Observable<IDatoEconomicoDetalle> {
    return this.getDatoEconomicoDetalle(id, TipoOperacion.FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS);
  }

  getViajeDieta(id: string): Observable<IDatoEconomicoDetalle> {
    return this.getDatoEconomicoDetalle(id, TipoOperacion.FACTURAS_JUSTIFICANTES_VIAJES_DIETAS);
  }

  getPersonaContratada(id: string): Observable<IDatoEconomicoDetalle> {
    return this.getDatoEconomicoDetalle(id, TipoOperacion.FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO);
  }
}
