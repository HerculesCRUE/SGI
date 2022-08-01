import { IConfiguracion } from '@core/models/csp/configuracion';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import { IProyectoEntidadFinanciadora } from '@core/models/csp/proyecto-entidad-financiadora';
import { IProyectoPeriodoJustificacion } from '@core/models/csp/proyecto-periodo-justificacion';
import { IProyectoSeguimientoEjecucionEconomica } from '@core/models/csp/proyecto-seguimiento-ejecucion-economica';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment } from '@core/services/action-service';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoSeguimientoEjecucionEconomicaService } from '@core/services/csp/proyecto-seguimiento-ejecucion-economica/proyecto-seguimiento-ejecucion-economica.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, forkJoin, from, Observable, of } from 'rxjs';
import { concatMap, map, mergeMap, tap, toArray } from 'rxjs/operators';
import { IRelacionEjecucionEconomicaWithResponsables } from '../../ejecucion-economica.action.service';

export interface IProyectoSeguimientoEjecucionEconomicaData extends IProyectoSeguimientoEjecucionEconomica {
  responsables: IPersona[];
  entidadesFinanciadoras: IEntidadFinanciadora[];
}

export interface IProyectoPeriodoJustificacionWithTituloProyecto extends IProyectoPeriodoJustificacion {
  tituloProyecto: string;
}

export class SeguimientoJustificacionResumenFragment extends Fragment {
  private responsablesMap: Map<number, IPersona[]>;
  private proyectoTituloMap: Map<number, string>;
  private proyectosSGI$ = new BehaviorSubject<IProyectoSeguimientoEjecucionEconomicaData[]>([]);
  private periodosJustificacion$ = new BehaviorSubject<StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[]>([]);

  get configuracion(): IConfiguracion {
    return this._configuracion;
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
    private readonly proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService
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
    forkJoin({
      proyectosSGI: this.findProyectosSGI(this.proyectoSge),
      calendarioJustificacion: this.findCalendarioJustificacion(this.proyectoSge)
    }).subscribe(data => {
      this.proyectosSGI$.next(data.proyectosSGI);
      this.periodosJustificacion$.next(data.calendarioJustificacion);
    });
  }

  private findProyectosSGI(proyectoSge: IProyectoSge): Observable<IProyectoSeguimientoEjecucionEconomicaData[]> {
    return this.proyectoSeguimientoEjecucionEconomicaService.findProyectosSeguimientoEjecucionEconomica(proyectoSge.id)
      .pipe(
        map(({ items }) => this.transformToProyectoSeguimientoEjecucionEconomicaData(items)),
        concatMap(proyectos => this.fillRelatedEntities(proyectos))
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

  private fillRelatedEntities(proyectos: IProyectoSeguimientoEjecucionEconomicaData[]):
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

  private findCalendarioJustificacion(proyectoSge: IProyectoSge):
    Observable<StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[]> {
    return this.proyectoSeguimientoEjecucionEconomicaService.findProyectoPeriodosJustificacion(proyectoSge.id)
      .pipe(
        map(({ items }) => items.map(item =>
          (new StatusWrapper({ ...item, tituloProyecto: this.proyectoTituloMap.get(item.proyecto.id) })))
        )
      );
  }

  getProyectosSGI$(): Observable<IProyectoSeguimientoEjecucionEconomicaData[]> {
    return this.proyectosSGI$.asObservable();
  }

  getPeriodosJustificacion$(): Observable<StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[]> {
    return this.periodosJustificacion$.asObservable();
  }

  updatePeriodoJustificacion(periodoJustificacion: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>): void {
    periodoJustificacion.setEdited();
    this.setChanges(true);
  }

  saveOrUpdate(action?: any): Observable<string | number | void> {
    return this.updatePeriodosJustificacion().pipe(
      tap(() => {
        this.setChanges(this.hasFragmentChangesPending());
      })
    );
  }

  private hasFragmentChangesPending(): boolean {
    return this.periodosJustificacion$.value.some(wrapper => wrapper.edited);
  }

  private updatePeriodosJustificacion(): Observable<void> {
    const current = this.periodosJustificacion$.value;
    return from(current.filter(wrapper => wrapper.edited)).pipe(
      mergeMap((wrapper => {
        return this.proyectoPeriodoJustificacionService.updateIdentificadorJustificacion(
          this.getProyectoPeriodoJustificacion(wrapper.value))
          .pipe(
            map((periodoJustificacionResponse) => this.refreshProyectoRelacionesTableData(periodoJustificacionResponse, wrapper, current)),
          );
      }))
    );
  }

  private getProyectoPeriodoJustificacion(periodoJustificacionWithTituloProyecto: IProyectoPeriodoJustificacionWithTituloProyecto):
    IProyectoPeriodoJustificacion {
    const { tituloProyecto, ...periodoJustificacion } = periodoJustificacionWithTituloProyecto;
    return periodoJustificacion;
  }

  private refreshProyectoRelacionesTableData(
    periodoJustificacionResponse: IProyectoPeriodoJustificacion,
    wrapper: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>,
    current: StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>[]
  ): void {
    const periodoJustificacionWithTituloProyecto: IProyectoPeriodoJustificacionWithTituloProyecto =
    {
      ...periodoJustificacionResponse,
      tituloProyecto: null
    };
    this.copyRelatedAttributes(wrapper.value, periodoJustificacionWithTituloProyecto);
    current[current.findIndex(c => c === wrapper)] = new StatusWrapper<IProyectoPeriodoJustificacionWithTituloProyecto>(
      periodoJustificacionWithTituloProyecto);
    this.periodosJustificacion$.next(current);
  }

  private copyRelatedAttributes(
    source: IProyectoPeriodoJustificacionWithTituloProyecto,
    target: IProyectoPeriodoJustificacionWithTituloProyecto
  ): void {
    target.tituloProyecto = source.tituloProyecto;
  }
}
