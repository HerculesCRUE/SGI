import { IProyectoAreaConocimiento } from '@core/models/csp/proyecto-area-conocimiento';
import { IAreaConocimiento } from '@core/models/sgo/area-conocimiento';
import { Fragment } from '@core/services/action-service';
import { ProyectoAreaConocimientoService } from '@core/services/csp/proyecto-area-conocimiento.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, concat, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface ProyectoAreaConocimientoListado {
  id: number;
  proyectoId: number;
  niveles: IAreaConocimiento[];
  nivelesTexto: string;
  nivelSeleccionado: IAreaConocimiento;
}

export class ProyectoAreaConocimientoFragment extends Fragment {
  areasConocimiento$ = new BehaviorSubject<StatusWrapper<ProyectoAreaConocimientoListado>[]>([]);
  private areasConocimientoEliminadas: StatusWrapper<ProyectoAreaConocimientoListado>[] = [];

  constructor(
    key: number,
    private service: ProyectoAreaConocimientoService,
    private proyectoService: ProyectoService,
    private areaConocimientoService: AreaConocimientoService,
    public readonly: boolean,
    public isVisor: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.proyectoService.findAllProyectoAreaConocimiento(this.getKey() as number).pipe(
        map(response => response.items.map(proyectoAreaConocimiento => {
          const areasConocimientoListado: ProyectoAreaConocimientoListado = {
            id: proyectoAreaConocimiento.id,
            proyectoId: proyectoAreaConocimiento.proyectoId,
            nivelSeleccionado: proyectoAreaConocimiento.areaConocimiento,
            niveles: undefined,
            nivelesTexto: ''
          };
          return areasConocimientoListado;
        })),
        switchMap((result) => {
          return from(result).pipe(
            mergeMap((proyectoAreaConocimiento) => {
              return this.areaConocimientoService.findById(proyectoAreaConocimiento.nivelSeleccionado.id).pipe(
                map((areaConocimiento) => {
                  proyectoAreaConocimiento.nivelSeleccionado = areaConocimiento;
                  proyectoAreaConocimiento.niveles = [areaConocimiento];
                }),
                switchMap(() => {
                  return this.getNiveles(proyectoAreaConocimiento);
                })
              );
            })
          );
        })
      ).subscribe((proyectoAreaConocimiento) => {
        proyectoAreaConocimiento.nivelesTexto = proyectoAreaConocimiento.niveles
          .slice(1, proyectoAreaConocimiento.niveles.length)
          .reverse()
          .map(area => area.nombre).join(' - ');
        this.areasConocimiento$.value.push(new StatusWrapper<ProyectoAreaConocimientoListado>(proyectoAreaConocimiento));
        this.areasConocimiento$.next(this.areasConocimiento$.value);
      });

      this.subscriptions.push(subscription);
    }
  }

  public addAreas(areas: ProyectoAreaConocimientoListado[]) {
    if (areas && areas.length > 0) {
      areas.forEach((area) => {
        area.proyectoId = this.getKey() as number;
        const wrapped = new StatusWrapper<ProyectoAreaConocimientoListado>(area);
        wrapped.setCreated();
        const current = this.areasConocimiento$.value;
        current.push(wrapped);
        this.areasConocimiento$.next(current);
      });

      this.setChanges(true);
    }
  }

  private createAreas(): Observable<void> {
    const createdAreasConocimiento = this.areasConocimiento$.value.filter((area) => area.created);
    if (createdAreasConocimiento.length === 0) {
      return of(void 0);
    }

    return from(createdAreasConocimiento).pipe(
      mergeMap((wrappedAreaConocimiento) => {
        const proyectoAreaConocimiento: IProyectoAreaConocimiento = {
          id: undefined,
          proyectoId: wrappedAreaConocimiento.value.proyectoId,
          areaConocimiento: wrappedAreaConocimiento.value.nivelSeleccionado,
        };
        return this.service.create(proyectoAreaConocimiento).pipe(
          map((createdAreaConocimiento) => {
            const index = this.areasConocimiento$.value.findIndex((currentClasificaciones) =>
              currentClasificaciones === wrappedAreaConocimiento);
            const areaConocimientoListado = wrappedAreaConocimiento.value;
            areaConocimientoListado.id = createdAreaConocimiento.id;
            this.areasConocimiento$.value[index] = new StatusWrapper<ProyectoAreaConocimientoListado>(areaConocimientoListado);
            this.areasConocimiento$.next(this.areasConocimiento$.value);
          })
        );
      })
    );
  }

  public deleteArea(wrapper: StatusWrapper<ProyectoAreaConocimientoListado>) {
    const current = this.areasConocimiento$.value;
    const index = current.findIndex(
      (value) => value.value === wrapper.value
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.areasConocimientoEliminadas.push(current[index]);
      }
      current.splice(index, 1);
      this.areasConocimiento$.next(current);
      this.setChanges(true);
    }
  }

  private deleteAreas(): Observable<void> {
    if (this.areasConocimientoEliminadas.length === 0) {
      return of(void 0);
    }
    return from(this.areasConocimientoEliminadas).pipe(
      mergeMap((wrapped) => {
        return this.service.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.areasConocimientoEliminadas = this.areasConocimientoEliminadas.filter(deletedArea =>
                deletedArea.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  saveOrUpdate(): Observable<void> {
    return concat(
      this.deleteAreas(),
      this.createAreas()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.areasConocimiento$.value.some((wrapper) => wrapper.touched);
    return !(this.areasConocimientoEliminadas.length > 0 || touched);
  }

  private getNiveles(proyectoAreaConocimiento: ProyectoAreaConocimientoListado):
    Observable<ProyectoAreaConocimientoListado> {
    const lastLevel = proyectoAreaConocimiento.niveles[proyectoAreaConocimiento.niveles.length - 1];
    if (!lastLevel.padreId) {
      return of(proyectoAreaConocimiento);
    }

    return this.areaConocimientoService.findById(lastLevel.padreId).pipe(
      switchMap(area => {
        proyectoAreaConocimiento.niveles.push(area);
        return this.getNiveles(proyectoAreaConocimiento);
      })
    );
  }

}
