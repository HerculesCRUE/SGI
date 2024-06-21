import { IEstadoValidacionIP, TipoEstadoValidacion } from '@core/models/csp/estado-validacion-ip';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoFacturacion } from '@core/models/csp/proyecto-facturacion';
import { IFacturaPrevista } from '@core/models/sge/factura-prevista';
import { IFacturaPrevistaEmitida } from '@core/models/sge/factura-prevista-emitida';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { ConfigService } from '@core/services/csp/config.service';
import { ProyectoFacturacionService } from '@core/services/csp/proyecto-facturacion/proyecto-facturacion.service';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { FacturaPrevistaEmitidaService } from '@core/services/sge/factura-prevista-emitida/factura-prevista-emitida.service';
import { FacturaPrevistaService } from '@core/services/sge/factura-prevista/factura-prevista.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { BehaviorSubject, Observable, concat, forkJoin, from, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap, toArray } from 'rxjs/operators';

export interface IProyectoFacturacionData extends IProyectoFacturacion {
  numeroFacturaEmitida: string;
  facturasEmitidas: IFacturaPrevistaEmitida[];
  estadosNotSaved: IEstadoValidacionIP[];
}
export class ProyectoCalendarioFacturacionFragment extends Fragment {

  public proyectosFacturacion$: BehaviorSubject<StatusWrapper<IProyectoFacturacionData>[]> =
    new BehaviorSubject<StatusWrapper<IProyectoFacturacionData>[]>([]);
  proyectosSGE$ = new BehaviorSubject<IProyectoSge[]>([]);
  private proyectosFacturacionDeleted: StatusWrapper<IProyectoFacturacionData>[] = [];
  private _isCalendarioFacturacionSgeEnabled = false;

  public proyectoIVA: number = null;

  get isCalendarioFacturacionSgeEnabled(): boolean {
    return this._isCalendarioFacturacionSgeEnabled;
  }

  constructor(
    key: number,
    public proyecto: IProyecto,
    private proyectoService: ProyectoService,
    private proyectoFacturacionService: ProyectoFacturacionService,
    private facturaPrevistaService: FacturaPrevistaService,
    private facturaPrevistaEmitidaService: FacturaPrevistaEmitidaService,
    private proyectoProrrogaService: ProyectoProrrogaService,
    private configService: ConfigService,
    private readonly isInvestigador: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (!this.getKey()) {
      return;
    }

    this.subscriptions.push(
      this.configService.isCalendarioFacturacionSgeEnabled().subscribe(isEnabled => this._isCalendarioFacturacionSgeEnabled = isEnabled)
    );

    this.loadItemsFacturacion(this.getKey() as number);
  }

  private proyectoFacturacionToIProyectoFacturacionData(proyectoFacturacion: IProyectoFacturacion): IProyectoFacturacionData {
    const proyectoFacturacionData = proyectoFacturacion as IProyectoFacturacionData;
    proyectoFacturacionData.estadosNotSaved = [];
    return proyectoFacturacionData;
  }


  private loadItemsFacturacion(proyectoId: number) {
    this.subscriptions.push(
      forkJoin({
        itemsFacturacion: this.getProyectosFacturacionByProyectoId(proyectoId),
        facturasPrevistasEmitidas: this.getFacturasPrevistasEmitidas(proyectoId)
      }).subscribe(({ itemsFacturacion, facturasPrevistasEmitidas }) => {

        const itemsFacturacionWrapped = itemsFacturacion.map(item => {
          item.facturasEmitidas = facturasPrevistasEmitidas.filter(factura => factura.numeroPrevision === item.numeroPrevision?.toString());
          item.numeroFacturaEmitida = item.facturasEmitidas?.map(factura => factura.numeroFactura).join('*');
          return new StatusWrapper(item);
        })

        this.proyectosFacturacion$.next(itemsFacturacionWrapped);
      })
    );
  }

  private getFacturasPrevistasEmitidas(proyectoId: number): Observable<IFacturaPrevistaEmitida[]> {
    const filter = new RSQLSgiRestFilter('proyectoIdSGI', SgiRestFilterOperator.EQUALS, proyectoId?.toString());
    return this.facturaPrevistaEmitidaService.findAll({ filter }).pipe(map(response => response.items));
  }


  private getProyectosFacturacionByProyectoId(proyectoId: number): Observable<IProyectoFacturacionData[]> {
    return this.proyectoService.findProyectosFacturacionByProyectoId(proyectoId).pipe(
      map(response => response.items.map(item => this.proyectoFacturacionToIProyectoFacturacionData(item))),
      switchMap(response =>
        from(response).pipe(
          mergeMap(data => {
            if (!data.proyectoProrroga) {
              return of(data);
            }

            return this.proyectoProrrogaService.findById(data.proyectoProrroga.id).pipe(
              map(proyectoProrroga => {
                data.proyectoProrroga = proyectoProrroga;
                return data;
              })
            );
          }),
          toArray(),
          map(() => {
            return response;
          })
        ))
    );
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
    newItem.value.estadosNotSaved = [];

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

    toUpdate.value.estadosNotSaved.push(toUpdate.value.estadoValidacionIP);

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
        const update$ = this.isInvestigador
          ? this.proyectoFacturacionService.updateValidacionIP(toUpdate.value.id, toUpdate.value)
          : this.proyectoFacturacionService.update(toUpdate.value.id, toUpdate.value);

        // elimina el ultimo estado que ya se guardara con el update
        const currentEstado = toUpdate.value.estadosNotSaved.pop();

        let obs$: Observable<IProyectoFacturacion>;

        if (toUpdate.value.estadosNotSaved.length > 0) {
          obs$ = this.updateEstadosNotSaved(toUpdate.value).pipe(
            switchMap(() => {
              toUpdate.value.estadoValidacionIP = currentEstado;
              return update$;
            })
          );
        } else {
          obs$ = update$;
        }

        if (this.isCalendarioFacturacionSgeEnabled && currentEstado?.estado === TipoEstadoValidacion.VALIDADA) {
          obs$ = obs$.pipe(
            switchMap((itemFacturacion: IProyectoFacturacionData) => {
              itemFacturacion.facturasEmitidas = toUpdate.value.facturasEmitidas;
              itemFacturacion.tipoFacturacion = toUpdate.value.tipoFacturacion;

              return this.createOrUpdateFacturaPrevista(itemFacturacion);
            })
          )
        }

        return obs$
          .pipe(
            map((updatedItem: IProyectoFacturacionData) => {
              const index = this.proyectosFacturacion$.value.findIndex(currentItem => currentItem.value.id === updatedItem.id);
              const proyectoFacturacionListado = toUpdate.value;
              proyectoFacturacionListado.id = updatedItem.id;

              if (this.isCalendarioFacturacionSgeEnabled) {
                proyectoFacturacionListado.facturasEmitidas = updatedItem.facturasEmitidas;
              }

              this.proyectosFacturacion$.value[index] = new StatusWrapper<IProyectoFacturacionData>(proyectoFacturacionListado);
              this.proyectosFacturacion$.next(this.proyectosFacturacion$.value);
            })
          );
      })
    );
  }


  private createOrUpdateFacturaPrevista(itemFacturacion: IProyectoFacturacionData): Observable<IProyectoFacturacionData> {
    let facturaPrevista$: Observable<IFacturaPrevista>;

    const facturaPrevista: IFacturaPrevista = {
      id: itemFacturacion?.facturasEmitidas && itemFacturacion?.facturasEmitidas.length > 0 ? itemFacturacion.facturasEmitidas[0]?.id : null,
      comentario: itemFacturacion.comentario,
      fechaEmision: itemFacturacion.fechaEmision,
      importeBase: itemFacturacion.importeBase,
      numeroPrevision: itemFacturacion.numeroPrevision,
      porcentajeIVA: itemFacturacion.porcentajeIVA,
      proyectoIdSGI: itemFacturacion.proyectoId,
      proyectoSgeId: itemFacturacion.proyectoSgeRef,
      tipoFacturacion: itemFacturacion.tipoFacturacion?.nombre
    };

    if (!facturaPrevista.id) {
      facturaPrevista$ = this.facturaPrevistaService.create(facturaPrevista);
    } else {
      facturaPrevista$ = this.facturaPrevistaService.update(facturaPrevista.id, facturaPrevista);
    }

    return facturaPrevista$.pipe(
      switchMap(facturaPrevistaResponse => {
        if (!itemFacturacion.facturasEmitidas) {
          itemFacturacion.facturasEmitidas = [{ id: facturaPrevistaResponse.id } as IFacturaPrevistaEmitida];
        }
        return of(itemFacturacion);
      })
    );
  }

  private createProyectosFacturacion(): Observable<void> {

    const toCreateProyectosFacuracion = this.proyectosFacturacion$.value.filter(item => item.created);

    if (toCreateProyectosFacuracion.length === 0) {
      return of(void 0);
    }

    toCreateProyectosFacuracion.forEach(newItem => newItem.value.proyectoId = this.getKey() as number);

    return from(toCreateProyectosFacuracion).pipe(
      mergeMap((toCreate: StatusWrapper<IProyectoFacturacionData>) => {
        return this.proyectoFacturacionService.create(toCreate.value)
          .pipe(
            switchMap((createdProyectoFacturacion: IProyectoFacturacionData) => {
              if (this.isCalendarioFacturacionSgeEnabled && toCreate.value.estadoValidacionIP?.estado === TipoEstadoValidacion.VALIDADA) {
                createdProyectoFacturacion.tipoFacturacion = toCreate.value.tipoFacturacion;
                return this.createOrUpdateFacturaPrevista(createdProyectoFacturacion);
              }

              return of(createdProyectoFacturacion);
            }),
            map(createdProyectoFacturacion => {
              const index = this.proyectosFacturacion$.value.findIndex(currentItem => currentItem.value === toCreate.value);
              const proyectoFacturacionListado = toCreate.value;
              proyectoFacturacionListado.id = createdProyectoFacturacion.id;

              if (this.isCalendarioFacturacionSgeEnabled) {
                proyectoFacturacionListado.facturasEmitidas = createdProyectoFacturacion.facturasEmitidas;
              }

              this.proyectosFacturacion$.value[index] = new StatusWrapper<IProyectoFacturacionData>(proyectoFacturacionListado);
              this.proyectosFacturacion$.next(this.proyectosFacturacion$.value);
              return proyectoFacturacionListado;
            }),
            switchMap(createdProyectoFacturacion => this.updateEstadosNotSaved(createdProyectoFacturacion).pipe(map(() => void 0)))
          );
      })
    );
  }

  private updateEstadosNotSaved(proyectoFacturacion: IProyectoFacturacionData): Observable<IProyectoFacturacionData> {
    if (proyectoFacturacion.estadosNotSaved.length === 0) {
      return of(proyectoFacturacion);
    }

    proyectoFacturacion.estadoValidacionIP = proyectoFacturacion.estadosNotSaved.shift();

    return this.proyectoFacturacionService.update(proyectoFacturacion.id, proyectoFacturacion).pipe(
      switchMap(() => {

        if (proyectoFacturacion.estadosNotSaved.length === 0) {
          return of(proyectoFacturacion);
        }

        return this.updateEstadosNotSaved(proyectoFacturacion);
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
