import { IConfiguracion } from '@core/models/csp/configuracion';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IProyectoPeriodoJustificacionSeguimiento } from '@core/models/csp/proyecto-periodo-justificacion-seguimiento';
import { IProyectoPeriodoSeguimiento } from '@core/models/csp/proyecto-periodo-seguimiento';
import { IProyectoSeguimientoEjecucionEconomica } from '@core/models/csp/proyecto-seguimiento-ejecucion-economica';
import { IProyectoSeguimientoJustificacion } from '@core/models/csp/proyecto-seguimiento-justificacion';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { ISeguimientoJustificacionAnualidad } from '@core/models/csp/seguimiento-justificacion-anualidad';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment } from '@core/services/action-service';
import { ProyectoPeriodoJustificacionSeguimientoService } from '@core/services/csp/proyecto-periodo-justificacion-seguimiento/proyecto-periodo-justificacion-seguimiento.service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { ProyectoSeguimientoEjecucionEconomicaService } from '@core/services/csp/proyecto-seguimiento-ejecucion-economica/proyecto-seguimiento-ejecucion-economica.service';
import { ProyectoSeguimientoJustificacionService } from '@core/services/csp/proyecto-seguimiento-justificacion/proyecto-seguimiento-justificacion.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { BehaviorSubject, forkJoin, from, merge, Observable, of } from 'rxjs';
import { concatMap, map, mergeMap, tap, toArray } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';

export interface IProyectoSeguimientoEjecucionEconomicaData extends IProyectoSeguimientoEjecucionEconomica {
  responsables: IPersona[];
  entidadesFinanciadoras: IEntidadFinanciadora[];
}

export interface IProyectoSeguimientoJustificacionWithFechaJustificacion extends IProyectoSeguimientoJustificacion {
  fechaUltimaJustificacion: DateTime;
}

export interface IProyectoPeriodoJustificacionWithTituloProyecto extends IProyectoPeriodoJustificacion {
  tituloProyecto: string;
}

export interface IProyectoPeriodoSeguimientoWithTituloProyecto extends IProyectoPeriodoSeguimiento {
  tituloProyecto: string;
}

export class SeguimientoJustificacionResumenFragment extends Fragment {
  private responsablesMap: Map<number, IPersona[]>;
  private proyectoTituloMap: Map<number, string>;
  private proyectosSGI$ = new BehaviorSubject<IProyectoSeguimientoEjecucionEconomicaData[]>([]);
  private seguimientosJustificacion$ = new BehaviorSubject<StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>[]>([]);
  private seguimientosJustificacionAnualidad$ = new BehaviorSubject<StatusWrapper<ISeguimientoJustificacionAnualidad>[]>([]);
  private periodosJustificacion$ = new BehaviorSubject<StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[]>([]);
  private periodoJustificacionChanged$ = new BehaviorSubject<IProyectoPeriodoJustificacion>(null);
  private periodosSeguimiento$ = new BehaviorSubject<StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>[]>([]);

  get configuracion(): IConfiguracion {
    return this._configuracion;
  }

  get proyectoSgeRef(): string {
    return this.proyectoSge.id;
  }

  constructor(
    key: number,
    private readonly proyectoSge: IProyectoSge,
    readonly relacionesProyectos: IRelacionEjecucionEconomicaWithResponsables[],
    // tslint:disable-next-line: variable-name
    private readonly _configuracion: IConfiguracion,
    private readonly proyectoService: ProyectoService,
    private readonly proyectoSeguimientoEjecucionEconomicaService: ProyectoSeguimientoEjecucionEconomicaService,
    private readonly empresaService: EmpresaService,
    private readonly proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService,
    private readonly proyectoPeriodoSeguimientoService: ProyectoPeriodoSeguimientoService,
    private readonly proyectoSeguimientoJustificacionService: ProyectoSeguimientoJustificacionService,
    private readonly proyectoPeriodoJustificacionSeguimientoService: ProyectoPeriodoJustificacionSeguimientoService
  ) {
    super(key);
    this.responsablesMap = new Map(
      relacionesProyectos.map(relacion => ([relacion.id, relacion.responsables]))
    );
    this.proyectoTituloMap = new Map(
      relacionesProyectos.map(relacion => ([relacion.id, relacion.nombre]))
    );
  }

  protected onInitialize(): void | Observable<any> {
    return forkJoin({
      proyectosSGI: this.findProyectosSGI(this.proyectoSge),
      seguimientosJustificacion: this.findSeguimientosJustificacion(this.proyectoSge),
      seguimientosJustificacionAnualidad: this.findSeguimientosJustificacionAnualidad(this.proyectoSge),
      periodosJustificacion: this.findPeriodosJustificacion(this.proyectoSge),
      periodosSeguimiento: this.findPeriodosSeguimiento(this.proyectoSge),
    }).pipe(
      map(data => ({
        ...data,
        seguimientosJustificacion: this.refreshSeguimientosJustificacionUltimaFechaJustificacion(
          // Puede que en la BBDD no exista Seguimiento Justificacion para alguno o todos los proyectosSGI
          this.createSeguimientosJustificacionIfNotExists(data.proyectosSGI, data.seguimientosJustificacion),
          data.periodosJustificacion
        )
      })),
      tap(data => {
        this.proyectosSGI$.next(data.proyectosSGI);
        this.seguimientosJustificacion$.next(data.seguimientosJustificacion);
        this.seguimientosJustificacionAnualidad$.next(data.seguimientosJustificacionAnualidad);
        this.periodosJustificacion$.next(data.periodosJustificacion);
        this.periodosSeguimiento$.next(data.periodosSeguimiento);
      })
    );
  }

  private findProyectosSGI(proyectoSge: IProyectoSge): Observable<IProyectoSeguimientoEjecucionEconomicaData[]> {
    return this.proyectoSeguimientoEjecucionEconomicaService.findProyectosSeguimientoEjecucionEconomica(proyectoSge.id)
      .pipe(
        map(({ items }) => this.transformToProyectoSeguimientoEjecucionEconomicaData(items)),
        concatMap(proyectos => this.fillProyectoSeguimientoEjecucionEconomicaDataRelatedEntities(proyectos))
      );
  }

  private transformToProyectoSeguimientoEjecucionEconomicaData(proyectos: IProyectoSeguimientoEjecucionEconomica[]):
    IProyectoSeguimientoEjecucionEconomicaData[] {
    return proyectos.map(proyecto => (
      {
        ...proyecto,
        responsables: this.responsablesMap.get(proyecto.proyectoId) ?? [],
        entidadesFinanciadoras: []
      })
    );
  }

  private fillProyectoSeguimientoEjecucionEconomicaDataRelatedEntities(proyectos: IProyectoSeguimientoEjecucionEconomicaData[]):
    Observable<IProyectoSeguimientoEjecucionEconomicaData[]> {
    return from(proyectos).pipe(
      concatMap(proyecto => this.fillEntidadesFinanciadoras(proyecto)),
      concatMap(proyecto => this.calculateImportesConcedidos(proyecto)),
      toArray()
    );
  }

  private fillEntidadesFinanciadoras(proyecto: IProyectoSeguimientoEjecucionEconomicaData):
    Observable<IProyectoSeguimientoEjecucionEconomicaData> {
    return this.proyectoService.findEntidadesFinanciadoras(proyecto.proyectoId)
      .pipe(
        concatMap(({ items }) => this.fillEmpresa(items)),
        map(entidadesFinanciadoras => {
          proyecto.entidadesFinanciadoras = entidadesFinanciadoras ?? [];
          return proyecto;
        })
      );
  }

  private fillEmpresa(entidades: IProyectoEntidadFinanciadora[]): Observable<IProyectoEntidadFinanciadora[]> {
    return from(entidades).pipe(
      concatMap(entidad => this.empresaService.findById(entidad.empresa.id)
        .pipe(
          map(empresa => {
            entidad.empresa = empresa;
            return entidad;
          })
        )
      ),
      toArray()
    );
  }

  private calculateImportesConcedidos(proyecto: IProyectoSeguimientoEjecucionEconomicaData):
    Observable<IProyectoSeguimientoEjecucionEconomicaData> {
    if (proyecto && (proyecto.importeConcedido == null || proyecto.importeConcedidoCostesIndirectos == null)) {
      return this.proyectoService.getProyectoPresupuestoTotales(proyecto.proyectoId).pipe(
        map(presupuestosTotales => {
          proyecto.importeConcedido = proyecto.importeConcedido ?? presupuestosTotales.importeTotalConcedidoUniversidad;
          proyecto.importeConcedidoCostesIndirectos = proyecto.importeConcedidoCostesIndirectos ??
            presupuestosTotales.importeTotalConcedidoUniversidadCostesIndirectos;
          return proyecto;
        })
      );
    } else {
      return of(proyecto);
    }
  }

  private findSeguimientosJustificacion(proyectoSge: IProyectoSge): Observable<IProyectoSeguimientoJustificacion[]> {
    return this.proyectoSeguimientoEjecucionEconomicaService.findSeguimientosJustificacion(proyectoSge.id)
      .pipe(
        map(({ items }) => items)
      );
  }

  private findSeguimientosJustificacionAnualidad(proyectoSge: IProyectoSge):
    Observable<StatusWrapper<ISeguimientoJustificacionAnualidad>[]> {
    return this.proyectoSeguimientoEjecucionEconomicaService.findSeguimientosJustificacionAnualidad(proyectoSge.id)
      .pipe(
        concatMap(seguimientosJustificacionAnualidad =>
          this.fillSeguimientoJustificacionAnualidadRelatedEntities(seguimientosJustificacionAnualidad)
        ),
        map(seguimientosJustificacionAnualidad =>
          seguimientosJustificacionAnualidad.map(seguimientoJustificacionAnualidad => new StatusWrapper(seguimientoJustificacionAnualidad))
        )
      );
  }

  private fillSeguimientoJustificacionAnualidadRelatedEntities(seguimientosJustificacionAnualidad: ISeguimientoJustificacionAnualidad[]):
    Observable<ISeguimientoJustificacionAnualidad[]> {
    return from(seguimientosJustificacionAnualidad).pipe(
      concatMap(proyecto => this.fillProyectoPeriodoJustificacionSeguimiento(proyecto)),
      toArray()
    );
  }

  private fillProyectoPeriodoJustificacionSeguimiento(seguimientoJustificacionAnualidad: ISeguimientoJustificacionAnualidad):
    Observable<ISeguimientoJustificacionAnualidad> {
    if (!seguimientoJustificacionAnualidad?.proyectoPeriodoJustificacionSeguimiento?.id) {
      return of(seguimientoJustificacionAnualidad);
    }

    return this.proyectoPeriodoJustificacionSeguimientoService.findById(
      seguimientoJustificacionAnualidad.proyectoPeriodoJustificacionSeguimiento.id
    ).pipe(
      map(proyectoPeriodoJustificacionSeguimiento => {
        seguimientoJustificacionAnualidad.proyectoPeriodoJustificacionSeguimiento = proyectoPeriodoJustificacionSeguimiento;
        return seguimientoJustificacionAnualidad;
      })
    );
  }

  private createSeguimientosJustificacionIfNotExists(
    proyectosSGI: IProyectoSeguimientoEjecucionEconomica[],
    seguimientosJustificacion: IProyectoSeguimientoJustificacion[]
  ): StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>[] {
    return proyectosSGI.map(proyectoSGI => {
      const seguimientoJustificacionToWrapper = seguimientosJustificacion.find(
        seguimientoJustificacion =>
          seguimientoJustificacion.proyectoProyectoSge.id === proyectoSGI.id) ??
        this.createProyectoSeguimientoJustificacionFromProyectoSGIData(proyectoSGI);
      return new StatusWrapper({ ...seguimientoJustificacionToWrapper, fechaUltimaJustificacion: null });
    }
    );
  }

  private createProyectoSeguimientoJustificacionFromProyectoSGIData(
    proyectoSGI: IProyectoSeguimientoEjecucionEconomica): IProyectoSeguimientoJustificacion {
    return {
      proyectoProyectoSge: {
        id: proyectoSGI.id,
        proyecto: {
          id: proyectoSGI.proyectoId
        },
        proyectoSge: {
          id: proyectoSGI.proyectoSgeRef
        }
      }
    } as IProyectoSeguimientoJustificacion;
  }

  private refreshSeguimientosJustificacionUltimaFechaJustificacion(
    seguimientosJustificacion: StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>[],
    periodosJustificacion: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[]):
    StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>[] {
    return seguimientosJustificacion.map(seguimientoJustificacion => {
      seguimientoJustificacion.value.fechaUltimaJustificacion = this.getFechaUltimaJustificacionByProyecto(
        periodosJustificacion.filter(periodoJustificacion =>
          periodoJustificacion.value.proyecto.id === seguimientoJustificacion.value.proyectoProyectoSge.proyecto.id
        )
      );
      return seguimientoJustificacion;
    });
  }

  private getFechaUltimaJustificacionByProyecto(
    periodosJustificacion: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[]): DateTime | null {
    if (periodosJustificacion.length === 0) {
      return null;
    }
    periodosJustificacion.sort((a, b) =>
      this.transformDateToMillis(b.value.fechaPresentacionJustificacion) -
      this.transformDateToMillis(a.value.fechaPresentacionJustificacion)
    );
    const [ultimoPeriodoJustificacion] = periodosJustificacion;
    return ultimoPeriodoJustificacion.value.fechaPresentacionJustificacion;
  }

  private transformDateToMillis(date: DateTime): number {
    return date?.toMillis() ?? 0;
  }

  private findPeriodosJustificacion(proyectoSge: IProyectoSge):
    Observable<StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[]> {
    return this.proyectoSeguimientoEjecucionEconomicaService.findProyectoPeriodosJustificacion(proyectoSge.id)
      .pipe(
        map(({ items }) => items.map(item =>
          (new StatusWrapper({ ...item, tituloProyecto: this.proyectoTituloMap.get(item.proyecto.id) })))
        )
      );
  }

  private findPeriodosSeguimiento(proyectoSge: IProyectoSge):
    Observable<StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>[]> {
    return this.proyectoSeguimientoEjecucionEconomicaService.findProyectoPeriodosSeguimiento(proyectoSge.id)
      .pipe(
        map(({ items }) => items.map(item =>
          (new StatusWrapper({ ...item, tituloProyecto: this.proyectoTituloMap.get(item.proyectoId) })))
        )
      );
  }

  getProyectosSGI$(): Observable<IProyectoSeguimientoEjecucionEconomicaData[]> {
    return this.proyectosSGI$.asObservable();
  }

  getSeguimientosJustificacion$(): Observable<StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>[]> {
    return this.seguimientosJustificacion$.asObservable();
  }

  getSeguimientosJustificacionAnualidad$(): Observable<StatusWrapper<ISeguimientoJustificacionAnualidad>[]> {
    return this.seguimientosJustificacionAnualidad$.asObservable();
  }

  getPeriodosJustificacion$(): Observable<StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[]> {
    return this.periodosJustificacion$.asObservable();
  }

  getPeriodoJustificacionChanged$(): Observable<IProyectoPeriodoJustificacion> {
    return this.periodoJustificacionChanged$.asObservable();
  }

  getPeriodosSeguimiento$(): Observable<StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>[]> {
    return this.periodosSeguimiento$.asObservable();
  }

  createSeguimientoJustificacion(seguimientoJustificacion: StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>): void {
    seguimientoJustificacion.setCreated();
    this.setChanges(true);
  }

  updateSeguimientoJustificacion(seguimientoJustificacion: StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>): void {
    if (!seguimientoJustificacion.created) {
      seguimientoJustificacion.setEdited();
      this.setChanges(true);
    }
  }

  createSeguimientoJustificacionAnualidad(seguimientoJustificacionAnualidad: StatusWrapper<ISeguimientoJustificacionAnualidad>): void {
    seguimientoJustificacionAnualidad.setCreated();
    this.setChanges(true);
  }

  updateSeguimientoJustificacionAnualidad(seguimientoJustificacionAnualidad: StatusWrapper<ISeguimientoJustificacionAnualidad>): void {
    if (!seguimientoJustificacionAnualidad.created) {
      seguimientoJustificacionAnualidad.setEdited();
      this.setChanges(true);
    }
  }

  onRequerimientosJustificacionChanged(currentRequerimientosJustificacion: IRequerimientoJustificacion[]): void {
    // Si se elimina un Requerimiento de Justificacion y no hay otro asociado al mismo Periodo de Justificacion
    // desaparece el Seguimiento de Justificacion de Anualidad asociado a dicho Periodo de Justificacion
    this.seguimientosJustificacionAnualidad$.next(
      this.seguimientosJustificacionAnualidad$.value.filter(seguimientoJustificacionAnualidad =>
        currentRequerimientosJustificacion.some(requerimientoJustificacion =>
          requerimientoJustificacion?.proyectoPeriodoJustificacion?.identificadorJustificacion ===
          seguimientoJustificacionAnualidad.value.identificadorJustificacion))
    );
    this.setChanges(this.hasFragmentChangesPending());
  }

  updatePeriodoJustificacion(periodoJustificacion: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>): void {
    periodoJustificacion.setEdited();
    this.seguimientosJustificacion$.next(
      this.refreshSeguimientosJustificacionUltimaFechaJustificacion(
        this.seguimientosJustificacion$.value, this.periodosJustificacion$.value
      )
    );
    this.seguimientosJustificacionAnualidad$.next(
      this.refreshSeguimientosJustificacionIdentificadorJustificacion(this.seguimientosJustificacionAnualidad$.value, periodoJustificacion)
    );
    this.periodoJustificacionChanged$.next(this.getProyectoPeriodoJustificacion(periodoJustificacion.value));
    this.setChanges(true);
  }

  private refreshSeguimientosJustificacionIdentificadorJustificacion(
    seguimientosJustificacionAnualidad: StatusWrapper<ISeguimientoJustificacionAnualidad>[],
    periodoJustificacion: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>
  ): StatusWrapper<ISeguimientoJustificacionAnualidad>[] {
    return seguimientosJustificacionAnualidad.map(seguimientoJustificacionAnualidad => {
      if (seguimientoJustificacionAnualidad.value.proyectoPeriodoJustificacionId === periodoJustificacion.value.id) {
        seguimientoJustificacionAnualidad.value.identificadorJustificacion = periodoJustificacion.value.identificadorJustificacion;
      }
      return seguimientoJustificacionAnualidad;
    });
  }

  updatePeriodoSeguimiento(periodoSeguimiento: StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>): void {
    periodoSeguimiento.setEdited();
    this.setChanges(true);
  }

  saveOrUpdate(action?: any): Observable<string | number | void> {
    return merge(
      this.updatePeriodosJustificacion(),
      this.updatePeriodosSeguimiento(),
      this.createSeguimientosJustificacion(),
      this.updateSeguimientosJustificacion(),
      this.createSeguimientosJustificacionAnualidad(),
      this.updateSeguimientosJustificacionAnualidad()
    ).pipe(
      tap(() => {
        this.setChanges(this.hasFragmentChangesPending());
      })
    );
  }

  private hasFragmentChangesPending(): boolean {
    return this.periodosJustificacion$.value.some(wrapper => wrapper.edited)
      || this.periodosSeguimiento$.value.some(wrapper => wrapper.edited)
      || this.seguimientosJustificacion$.value.some(wrapper => wrapper.edited || wrapper.created)
      || this.seguimientosJustificacionAnualidad$.value.some(wrapper => wrapper.edited || wrapper.created);
  }

  private createSeguimientosJustificacion(): Observable<void> {
    const current = this.seguimientosJustificacion$.value;
    return from(current.filter(wrapper => wrapper.created)).pipe(
      mergeMap((wrapper) =>
        this.proyectoSeguimientoJustificacionService.create(
          this.getProyectoSeguimientoJustificacion(wrapper.value))
          .pipe(
            map((seguimientoJustificacionResponse) =>
              this.refreshSeguimientosJustificacionTableData(seguimientoJustificacionResponse, wrapper, current)
            ),
          )
      )
    );
  }

  private updateSeguimientosJustificacion(): Observable<void> {
    const current = this.seguimientosJustificacion$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap((wrapper) =>
        this.proyectoSeguimientoJustificacionService.update(
          wrapper.value.id,
          this.getProyectoSeguimientoJustificacion(wrapper.value))
          .pipe(
            map((seguimientoJustificacionResponse) =>
              this.refreshSeguimientosJustificacionTableData(seguimientoJustificacionResponse, wrapper, current)
            ),
          )
      )
    );
  }

  private getProyectoSeguimientoJustificacion(
    seguimientoJustificacionWithFechaJustificacion: IProyectoSeguimientoJustificacionWithFechaJustificacion
  ): IProyectoSeguimientoJustificacion {
    const { fechaUltimaJustificacion, ...periodoSeguimiento } = seguimientoJustificacionWithFechaJustificacion;
    return periodoSeguimiento;
  }

  private refreshSeguimientosJustificacionTableData(
    seguimientoJustificacionResponse: IProyectoSeguimientoJustificacion,
    wrapper: StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>,
    current: StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>[]
  ): void {
    const seguimientoJustificacionWithFechaJustificacion: IProyectoSeguimientoJustificacionWithFechaJustificacion =
    {
      ...seguimientoJustificacionResponse,
      fechaUltimaJustificacion: null
    };
    this.copySeguimientoJustificacionRelatedAttributes(wrapper.value, seguimientoJustificacionWithFechaJustificacion);
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<IProyectoSeguimientoJustificacionWithFechaJustificacion>(
      seguimientoJustificacionWithFechaJustificacion);
    this.seguimientosJustificacion$.next(current);
  }

  private copySeguimientoJustificacionRelatedAttributes(
    source: IProyectoSeguimientoJustificacionWithFechaJustificacion,
    target: IProyectoSeguimientoJustificacionWithFechaJustificacion
  ): void {
    target.fechaUltimaJustificacion = source.fechaUltimaJustificacion;
  }

  private createSeguimientosJustificacionAnualidad(): Observable<void> {
    const current = this.seguimientosJustificacionAnualidad$.value;
    return from(current.filter(wrapper => wrapper.created)).pipe(
      mergeMap((wrapper) =>
        this.proyectoPeriodoJustificacionSeguimientoService.create(
          wrapper.value.proyectoPeriodoJustificacionSeguimiento)
          .pipe(
            map((proyectoPeriodoJustificacionSeguimientoResponse) =>
              this.refreshSeguimientosJustificacionAnualidadTableData(proyectoPeriodoJustificacionSeguimientoResponse, wrapper, current)
            ),
          )
      )
    );
  }

  private updateSeguimientosJustificacionAnualidad(): Observable<void> {
    const current = this.seguimientosJustificacionAnualidad$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap((wrapper) =>
        this.proyectoPeriodoJustificacionSeguimientoService.update(
          wrapper.value.proyectoPeriodoJustificacionSeguimiento.id,
          wrapper.value.proyectoPeriodoJustificacionSeguimiento)
          .pipe(
            map((proyectoPeriodoJustificacionSeguimientoResponse) =>
              this.refreshSeguimientosJustificacionAnualidadTableData(proyectoPeriodoJustificacionSeguimientoResponse, wrapper, current)
            ),
          )
      )
    );
  }

  private refreshSeguimientosJustificacionAnualidadTableData(
    proyectoPeriodoJustificacionSeguimientoResponse: IProyectoPeriodoJustificacionSeguimiento,
    wrapper: StatusWrapper<ISeguimientoJustificacionAnualidad>,
    current: StatusWrapper<ISeguimientoJustificacionAnualidad>[]
  ): void {
    const seguimientoJustificacionToCreate: ISeguimientoJustificacionAnualidad = {
      proyectoPeriodoJustificacionSeguimiento: proyectoPeriodoJustificacionSeguimientoResponse
    } as ISeguimientoJustificacionAnualidad;
    this.copySeguimientoJustificacionAnualidadRelatedAttributes(
      wrapper.value, seguimientoJustificacionToCreate
    );
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<ISeguimientoJustificacionAnualidad>(
      seguimientoJustificacionToCreate);
    this.seguimientosJustificacionAnualidad$.next(current);
  }

  private copySeguimientoJustificacionAnualidadRelatedAttributes(
    source: ISeguimientoJustificacionAnualidad,
    target: ISeguimientoJustificacionAnualidad
  ): void {
    target.proyectoPeriodoJustificacionSeguimiento.proyectoAnualidad = source.proyectoPeriodoJustificacionSeguimiento.proyectoAnualidad;
    target.fechaPresentacionJustificacion = source.fechaPresentacionJustificacion;
    target.identificadorJustificacion = source.identificadorJustificacion;
    target.proyectoId = source.proyectoId;
    target.proyectoPeriodoJustificacionId = source.proyectoPeriodoJustificacionId;
  }

  private updatePeriodosJustificacion(): Observable<void> {
    const current = this.periodosJustificacion$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap((wrapper => {
        return this.proyectoPeriodoJustificacionService.updateIdentificadorJustificacion(
          this.getProyectoPeriodoJustificacion(wrapper.value))
          .pipe(
            map((periodoJustificacionResponse) =>
              this.refreshPeriodosJustificacionTableData(periodoJustificacionResponse, wrapper, current)
            ),
          );
      }))
    );
  }

  private getProyectoPeriodoJustificacion(periodoJustificacionWithTituloProyecto: IProyectoPeriodoJustificacionWithTituloProyecto):
    IProyectoPeriodoJustificacion {
    const { tituloProyecto, ...periodoJustificacion } = periodoJustificacionWithTituloProyecto;
    return periodoJustificacion;
  }

  private refreshPeriodosJustificacionTableData(
    periodoJustificacionResponse: IProyectoPeriodoJustificacion,
    wrapper: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>,
    current: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[]
  ): void {
    const periodoJustificacionWithTituloProyecto: IProyectoPeriodoJustificacionWithTituloProyecto =
    {
      ...periodoJustificacionResponse,
      tituloProyecto: null
    };
    this.copyPeriodoJustificacionRelatedAttributes(wrapper.value, periodoJustificacionWithTituloProyecto);
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>(
      periodoJustificacionWithTituloProyecto);
    this.periodosJustificacion$.next(current);
  }

  private copyPeriodoJustificacionRelatedAttributes(
    source: IProyectoPeriodoJustificacionWithTituloProyecto,
    target: IProyectoPeriodoJustificacionWithTituloProyecto
  ): void {
    target.tituloProyecto = source.tituloProyecto;
  }

  private updatePeriodosSeguimiento(): Observable<void> {
    const current = this.periodosSeguimiento$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap((wrapper => {
        return this.proyectoPeriodoSeguimientoService.updateFechaPresentacionDocumentacion(
          this.getProyectoPeriodoSeguimiento(wrapper.value)
        )
          .pipe(
            map((periodoSeguimientoResponse) =>
              this.refreshPeriodosSeguimientoTableData(periodoSeguimientoResponse, wrapper, current)
            ),
          );
      }))
    );
  }

  private getProyectoPeriodoSeguimiento(periodoSeguimientoWithTituloProyecto: IProyectoPeriodoSeguimientoWithTituloProyecto):
    IProyectoPeriodoSeguimiento {
    const { tituloProyecto, ...periodoSeguimiento } = periodoSeguimientoWithTituloProyecto;
    return periodoSeguimiento;
  }

  private refreshPeriodosSeguimientoTableData(
    periodoSeguimientoResponse: IProyectoPeriodoSeguimiento,
    wrapper: StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>,
    current: StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>[]
  ): void {
    const periodoSeguimientoWithTituloProyecto: IProyectoPeriodoSeguimientoWithTituloProyecto =
    {
      ...periodoSeguimientoResponse,
      tituloProyecto: null
    };
    this.copyPeriodoSeguimientoRelatedAttributes(wrapper.value, periodoSeguimientoWithTituloProyecto);
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<IProyectoPeriodoSeguimientoWithTituloProyecto>(
      periodoSeguimientoWithTituloProyecto);
    this.periodosSeguimiento$.next(current);
  }

  private copyPeriodoSeguimientoRelatedAttributes(
    source: IProyectoPeriodoSeguimientoWithTituloProyecto,
    target: IProyectoPeriodoSeguimientoWithTituloProyecto
  ): void {
    target.tituloProyecto = source.tituloProyecto;
  }
}
