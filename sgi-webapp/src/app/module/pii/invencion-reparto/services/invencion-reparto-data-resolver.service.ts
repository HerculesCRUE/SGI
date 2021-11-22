import { Injectable } from '@angular/core';
import { IRelacion, TipoEntidad } from '@core/models/rel/relacion';
import { IDatoEconomico } from '@core/models/sgepii/dato-economico';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RelacionService } from '@core/services/rel/relaciones/relacion.service';
import { GastosInvencionService, TipoOperacion } from '@core/services/sgepii/gastos-invencion.service';
import { IngresosInvencionService } from '@core/services/sgepii/ingresos-invencion.service';
import { from, Observable } from 'rxjs';
import { map, mergeMap, reduce, switchMap } from 'rxjs/operators';
import { IColumnDefinition } from 'src/app/module/csp/ejecucion-economica/ejecucion-economica-formulario/desglose-economico.fragment';

@Injectable()
export class InvencionRepartoDataResolverService {

  constructor(
    private readonly ingresosInvencionService: IngresosInvencionService,
    private readonly relacionService: RelacionService,
    private readonly proyectoService: ProyectoService,
    private readonly gastosInvencionService: GastosInvencionService,
  ) { }

  getGastosReparto(invencionId: string): Observable<IDatoEconomico[]> {
    return this.gastosInvencionService.getGastos(invencionId, TipoOperacion.REPARTO);
  }

  getGastosColumns(invencionId: string): Observable<IColumnDefinition[]> {
    return this.gastosInvencionService.getColumnas(invencionId)
      .pipe(
        map(columnas => columnas.map(columna => {
          return {
            id: columna.id,
            name: columna.nombre,
            compute: columna.acumulable,
            importeReparto: columna.importeReparto
          };
        })
        )
      );
  }

  /**
   * Obtiene los Ingresos asociados a la Invencion
   * @param invencionId id de la Inencion
   * @returns Lista de Ingresos asociados a la Invención.
   */
  getIngresosByInvencionId(invencionId: number): Observable<IDatoEconomico[]> {
    return this.relacionService.findInvencionRelaciones(invencionId).pipe(
      map(relaciones => this.convertRelacionesToArrayProyectoIds(relaciones)),
      switchMap(proyectoIds => this.getProyectosSgeId(proyectoIds)),
      switchMap(proyectoSgeIds => this.getIngresosProyectosSge(proyectoSgeIds)));
  }

  private convertRelacionesToArrayProyectoIds(relaciones: IRelacion[]): number[] {
    return relaciones.map(relacion => this.getProyectoIdFromRelacion(relacion));
  }

  private getProyectoIdFromRelacion(relacion: IRelacion): number {
    return relacion.tipoEntidadOrigen === TipoEntidad.PROYECTO ? +relacion.entidadOrigen.id : +relacion.entidadDestino.id;
  }

  private getProyectosSgeId(proyectoIds: number[]): Observable<string[]> {
    return from(proyectoIds).pipe(
      mergeMap(proyectoId => this.getProyectoSgeId(proyectoId)),
      // flat array
      reduce((acc, val) => acc.concat(val), [])
    );
  }

  private getProyectoSgeId(proyectoId: number): Observable<string[]> {
    return this.proyectoService.findAllProyectosSgeProyecto(proyectoId).pipe(
      map(({ items }) => items.map(proyectoSge => proyectoSge.proyectoSge.id))
    );
  }

  private getIngresosProyectosSge(proyectoSgeIds: string[]): Observable<IDatoEconomico[]> {
    return from(proyectoSgeIds).pipe(
      mergeMap(proyectoSgeId => this.getIngresosProyectoSge(proyectoSgeId)),
      // flat array
      reduce((acc, val) => acc.concat(val), [])
    );
  }

  private getIngresosProyectoSge(proyectoSgeId: string): Observable<IDatoEconomico[]> {
    return this.ingresosInvencionService.getIngresos(proyectoSgeId);
  }

  getIngresosColumns(): Observable<IColumnDefinition[]> {
    return this.ingresosInvencionService.getColumnas().pipe(
      map((columnas) =>
        columnas.map((columna) => {
          return {
            id: columna.id,
            name: columna.nombre,
            compute: columna.acumulable,
            importeReparto: columna.importeReparto
          };
        })
      )
    );
  }
}
