import { IEstadoValidacionIP, TipoEstadoValidacion } from '@core/models/csp/estado-validacion-ip';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoFacturacion } from '@core/models/csp/proyecto-facturacion';
import { Fragment } from '@core/services/action-service';
import { ProyectoFacturacionService } from '@core/services/csp/proyecto-facturacion/proyecto-facturacion.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { FacturaPrevistaEmitidaService } from '@core/services/sge/factura-prevista-emitida/factura-prevista-emitida.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { BehaviorSubject, concat, from, Observable, of, Subscription } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';

export interface IProyectoFacturacionData extends IProyectoFacturacion {
  numeroFacturaEmitida: string;
}
export class ProyectoCalendarioFacturacionFragment extends Fragment {

  public proyectosFacturacion$: BehaviorSubject<StatusWrapper<IProyectoFacturacionData>[]> =
    new BehaviorSubject<StatusWrapper<IProyectoFacturacionData>[]>([]);
  private proyectosFacturacionDeleted: StatusWrapper<IProyectoFacturacionData>[] = [];
  private loadProyectosFacturacionByProyectoIdSubscription: Subscription;

  public proyectoIVA: number = null;

  constructor(
    key: number,
    public proyecto: IProyecto,
    private proyectoService: ProyectoService,
    private proyectoFacturacionService: ProyectoFacturacionService,
    private facturaPrevistaEmitidaService: FacturaPrevistaEmitidaService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (!this.getKey()) {
      return;
    }
    this.loadProyectosFacturacionByProyectoId();
  }

  private loadProyectosFacturacionByProyectoId(): void {
    this.loadProyectosFacturacionByProyectoIdSubscription =
      this.proyectoService.findProyectosFacturacionByProyectoId(this.getKey() as number).pipe(
        map(response => response.items.map(item => new StatusWrapper(item as IProyectoFacturacionData))),
        switchMap(response =>
          from(response).pipe(
            mergeMap(data => {
              if (data.value.fechaConformidad && data.value.numeroPrevision) {
                const filter = new RSQLSgiRestFilter('proyectoIdSGI', SgiRestFilterOperator.EQUALS, data.value.proyectoId?.toString())
                  .and('numeroPrevision', SgiRestFilterOperator.EQUALS, data.value.numeroPrevision.toString());
                return this.facturaPrevistaEmitidaService.findAll({ filter }).pipe(
                  map(facturasEmitidas => {
                    if (facturasEmitidas.items.length === 1) {
                      data.value.numeroFacturaEmitida = facturasEmitidas.items[0]?.numeroFactura;
                    }
                    return data;
                  })
                );
              } else {
                return of(data);
              }
            }), toArray(),
            map(() => {
              return response;
            })
          ))
      ).subscribe((data) => this.proyectosFacturacion$.next(data));

    this.subscriptions.push(this.loadProyectosFacturacionByProyectoIdSubscription);
  }

  saveOrUpdate(): Observable<void> {
    return concat(
      this.deleteProyectosFacturacion(),
      this.updateProyectosFacturacion(),
      this.createProyectosFacturacion()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  public addProyectoFacturacion(proyectoFacturacion: IProyectoFacturacionData): void {
    proyectoFacturacion.estadoValidacionIP = { estado: TipoEstadoValidacion.PENDIENTE } as IEstadoValidacionIP;
    const newItem = new StatusWrapper<IProyectoFacturacionData>(proyectoFacturacion);
    newItem.setCreated();

    const current = this.proyectosFacturacion$.value;
    current.push(newItem);

    this.proyectosFacturacion$.next(current);

    this.setChanges(true);
  }

  public updateProyectoFacturacion(toUpdate: StatusWrapper<IProyectoFacturacionData>, index?: number): void {

    if (index < 0) {
      return;
    }

    if (toUpdate.value.id) {
      toUpdate.setEdited();
    } else {
      toUpdate.setCreated();
    }

    const current = this.proyectosFacturacion$.value;
    current[index] = toUpdate;
    this.proyectosFacturacion$.next(current);

    this.setChanges(true);
  }

  public deleteProyectoFacturacion(toDelete: StatusWrapper<IProyectoFacturacionData>): void {

    const current = this.proyectosFacturacion$.value;
    const index = current.findIndex((item: StatusWrapper<IProyectoFacturacionData>) => item === toDelete);

    if (index < 0) {
      return;
    }

    if (!toDelete.created) {
      this.proyectosFacturacionDeleted.push(current[index]);
    }

    current.splice(index, 1);
    this.proyectosFacturacion$.next(current);

    this.setChanges(true);
  }

  private deleteProyectosFacturacion(): Observable<void> {

    if (this.proyectosFacturacionDeleted.length === 0) {
      return of(void (0));
    }
    return from(this.proyectosFacturacionDeleted).pipe(
      mergeMap((toDelete: StatusWrapper<IProyectoFacturacionData>) => {
        return this.proyectoFacturacionService.deleteById(toDelete.value.id)
          .pipe(
            tap(() => {
              this.proyectosFacturacionDeleted = this.proyectosFacturacionDeleted
                .filter(deletedItem => deletedItem.value.id !== toDelete.value.id)
            })
          );
      }));
  }

  private updateProyectosFacturacion(): Observable<void> {

    const toUpdateProyetosFacturacionItems = this.proyectosFacturacion$.value.filter(item => item.edited);
    if (toUpdateProyetosFacturacionItems.length === 0) {
      return of(void 0);
    }

    return from(toUpdateProyetosFacturacionItems).pipe(
      mergeMap((toUpdate: StatusWrapper<IProyectoFacturacionData>) => {
        return this.proyectoFacturacionService.update(toUpdate.value.id, toUpdate.value)
          .pipe(
            map((updatedItem: IProyectoFacturacionData) => {
              const index = this.proyectosFacturacion$.value.findIndex(currentItem => currentItem.value.id === updatedItem.id);
              const proyectoFacturacionListado = toUpdate.value;
              proyectoFacturacionListado.id = updatedItem.id;
              this.proyectosFacturacion$.value[index] = new StatusWrapper<IProyectoFacturacionData>(proyectoFacturacionListado);
              this.proyectosFacturacion$.next(this.proyectosFacturacion$.value);
            })
          );
      })
    );
  }

  private createProyectosFacturacion(): Observable<void> {

    const toCreateProyectosFacuracion = this.proyectosFacturacion$.value.filter(item => item.created);

    if (toCreateProyectosFacuracion.length === 0) {
      return of(void 0);
    }

    toCreateProyectosFacuracion.map(newItem => newItem.value.proyectoId = this.getKey() as number);

    return from(toCreateProyectosFacuracion).pipe(
      mergeMap((toCreate: StatusWrapper<IProyectoFacturacionData>) => {
        return this.proyectoFacturacionService.create(toCreate.value)
          .pipe(
            map((createdProyectoFacturacion: IProyectoFacturacionData) => {
              const index = this.proyectosFacturacion$.value.findIndex(currentItem => currentItem.value === toCreate.value);
              const proyectoFacturacionListado = toCreate.value;
              proyectoFacturacionListado.id = createdProyectoFacturacion.id;
              this.proyectosFacturacion$.value[index] = new StatusWrapper<IProyectoFacturacionData>(proyectoFacturacionListado);
              this.proyectosFacturacion$.next(this.proyectosFacturacion$.value);
            }));
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.proyectosFacturacion$.value.some((item) =>
      item.touched);
    return !(this.proyectosFacturacionDeleted.length > 0 || touched);
  }

  public getNextNumeroPrevision(): number {

    return (this.proyectosFacturacion$?.value.length > 0) ? this.getMaxNumeroPrevision(this.proyectosFacturacion$?.value) + 1 : 1;
  }

  private getMaxNumeroPrevision(newItems: StatusWrapper<IProyectoFacturacionData>[]) {
    return newItems.map(proyectoFacturacion => proyectoFacturacion.value.numeroPrevision).reduce((a, b) => a > b ? a : b);
  }

}
