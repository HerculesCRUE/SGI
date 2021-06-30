import { ISolicitudProyectoAreaConocimiento } from '@core/models/csp/solicitud-proyecto-area-conocimiento';
import { IAreaConocimiento } from '@core/models/sgo/area-conocimiento';
import { Fragment } from '@core/services/action-service';
import { SolicitudProyectoAreaConocimientoService } from '@core/services/csp/solicitud-proyecto-area-conocimiento.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { AreaConocimientoService } from '@core/services/sgo/area-conocimiento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, concat, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface SolicitudProyectoAreaConocimientoListado {
  id: number;
  solicitudProyectoId: number;
  niveles: IAreaConocimiento[];
  nivelesTexto: string;
  nivelSeleccionado: IAreaConocimiento;
}

export class SolicitudProyectoAreaConocimientoFragment extends Fragment {
  areasConocimiento$ = new BehaviorSubject<StatusWrapper<SolicitudProyectoAreaConocimientoListado>[]>([]);
  private areasConocimientoEliminadas: StatusWrapper<SolicitudProyectoAreaConocimientoListado>[] = [];

  constructor(
    key: number,
    private service: SolicitudProyectoAreaConocimientoService,
    private solicitudService: SolicitudService,
    private areaConocimientoService: AreaConocimientoService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.solicitudService.findAllSolicitudProyectoAreaConocimiento(this.getKey() as number).pipe(
        map(response => response.items.map(solicitudProyectoAreaConocimiento => {
          const areasConocimientoListado: SolicitudProyectoAreaConocimientoListado = {
            id: solicitudProyectoAreaConocimiento.id,
            solicitudProyectoId: solicitudProyectoAreaConocimiento.solicitudProyectoId,
            nivelSeleccionado: solicitudProyectoAreaConocimiento.areaConocimiento,
            niveles: undefined,
            nivelesTexto: ''
          };
          return areasConocimientoListado;
        })),
        switchMap((result) => {
          return from(result).pipe(
            mergeMap((solicitudProyectoAreaConocimiento) => {
              return this.areaConocimientoService.findById(solicitudProyectoAreaConocimiento.nivelSeleccionado.id).pipe(
                map((areaConocimiento) => {
                  solicitudProyectoAreaConocimiento.nivelSeleccionado = areaConocimiento;
                  solicitudProyectoAreaConocimiento.niveles = [areaConocimiento];
                }),
                switchMap(() => {
                  return this.getNiveles(solicitudProyectoAreaConocimiento);
                })
              );
            })
          );
        })
      ).subscribe((solicitudProyectoAreaConocimiento) => {
        solicitudProyectoAreaConocimiento.nivelesTexto = solicitudProyectoAreaConocimiento.niveles
          .slice(1, solicitudProyectoAreaConocimiento.niveles.length)
          .reverse()
          .map(area => area.nombre).join(' - ');
        this.areasConocimiento$.value.push(new StatusWrapper<SolicitudProyectoAreaConocimientoListado>(solicitudProyectoAreaConocimiento));
        this.areasConocimiento$.next(this.areasConocimiento$.value);
      });

      this.subscriptions.push(subscription);
    }
  }

  public addAreas(areas: SolicitudProyectoAreaConocimientoListado[]) {
    if (areas && areas.length > 0) {
      areas.forEach((area) => {
        area.solicitudProyectoId = this.getKey() as number;
        const wrapped = new StatusWrapper<SolicitudProyectoAreaConocimientoListado>(area);
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
        const solicitudProyectoAreaConocimiento: ISolicitudProyectoAreaConocimiento = {
          id: undefined,
          solicitudProyectoId: wrappedAreaConocimiento.value.solicitudProyectoId,
          areaConocimiento: wrappedAreaConocimiento.value.nivelSeleccionado,
        };
        return this.service.create(solicitudProyectoAreaConocimiento).pipe(
          map((createdAreaConocimiento) => {
            const index = this.areasConocimiento$.value.findIndex((currentAreasConocimiento) =>
              currentAreasConocimiento === wrappedAreaConocimiento);
            const areaConocimientoListado = wrappedAreaConocimiento.value;
            areaConocimientoListado.id = createdAreaConocimiento.id;
            this.areasConocimiento$.value[index] = new StatusWrapper<SolicitudProyectoAreaConocimientoListado>(areaConocimientoListado);
          })
        );
      })
    );
  }

  public deleteArea(wrapper: StatusWrapper<SolicitudProyectoAreaConocimientoListado>) {
    const current = this.areasConocimiento$.value;
    const index = current.findIndex(
      (value) => value === wrapper
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
    return (this.areasConocimientoEliminadas.length > 0 || touched);
  }

  private getNiveles(solicitudProyectoAreaConocimiento: SolicitudProyectoAreaConocimientoListado):
    Observable<SolicitudProyectoAreaConocimientoListado> {
    const lastLevel = solicitudProyectoAreaConocimiento.niveles[solicitudProyectoAreaConocimiento.niveles.length - 1];
    if (!lastLevel.padreId) {
      return of(solicitudProyectoAreaConocimiento);
    }

    return this.areaConocimientoService.findById(lastLevel.padreId).pipe(
      switchMap(area => {
        solicitudProyectoAreaConocimiento.niveles.push(area);
        return this.getNiveles(solicitudProyectoAreaConocimiento);
      })
    );
  }

}
