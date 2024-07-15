import { CardinalidadRelacionSgiSge } from '@core/models/csp/configuracion';
import { IProyecto } from '@core/models/csp/proyecto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { Estado, ISolicitudProyectoSge } from '@core/models/sge/solicitud-proyecto-sge';
import { Fragment } from '@core/services/action-service';
import { ConfigService } from '@core/services/csp/configuracion/config.service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { SolicitudProyectoSgeService } from '@core/services/sge/solicitud-proyecto-sge/solicitud-proyecto-sge.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, forkJoin, from, merge, of } from 'rxjs';
import { map, mergeMap, switchMap, tap } from 'rxjs/operators';

export class ProyectoProyectosSgeFragment extends Fragment {
  proyectosSge$ = new BehaviorSubject<StatusWrapper<IProyectoProyectoSge>[]>([]);
  isSectorIvaSgeEnabled$ = new BehaviorSubject<boolean>(false);

  private _cardinalidadRelacionSgiSge: CardinalidadRelacionSgiSge;
  private _disableAddIdentificadorSge$ = new BehaviorSubject<boolean>(false);
  private _isModificacionProyectoSgeEnabled: boolean;
  private _isSolicitudProyectoAltaPendiente$ = new BehaviorSubject<boolean>(false);
  private _solicitudesProyectoPendientes$ = new BehaviorSubject<ISolicitudProyectoSge[]>([]);

  get disableAddIdentificadorSge$(): Observable<boolean> {
    return this._disableAddIdentificadorSge$;
  }

  get isModificacionProyectoSgeEnabled(): boolean {
    return this._isModificacionProyectoSgeEnabled;
  }

  get showInfoSolicitudProyectoAltaPendiente$(): Observable<boolean> {
    return this._isSolicitudProyectoAltaPendiente$;
  }

  get showInfoSolicitudProyectoModificacionPendiente$(): Observable<boolean> {
    return this.solicitudesProyectoModificacionPendientes$.pipe(
      map(modificacionesPendientes => !!modificacionesPendientes?.length)
    );
  }

  get solicitudesProyectoAltaPendientes$(): Observable<ISolicitudProyectoSge[]> {
    return this._solicitudesProyectoPendientes$.pipe(
      map(solicitudesProyectoPendientes => solicitudesProyectoPendientes?.filter(solicitud => this.isSolicitudProyectoAltaPendiente(solicitud)) ?? [])
    );
  }

  get solicitudesProyectoModificacionPendientes$(): Observable<ISolicitudProyectoSge[]> {
    return this._solicitudesProyectoPendientes$.pipe(
      map(solicitudesProyectoPendientes => solicitudesProyectoPendientes.filter(solicitud => this.isSolicitudProyectoModificacionPendiente(solicitud)) ?? [])
    );
  }

  constructor(
    key: number,
    private service: ProyectoProyectoSgeService,
    private proyectoService: ProyectoService,
    private proyectoSgeService: ProyectoSgeService,
    private solicitudProyectoSgeService: SolicitudProyectoSgeService,
    private configService: ConfigService,
    public readonly: boolean,
    public isVisor: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {

      this.subscriptions.push(
        this.configService.isSectorIvaSgeEnabled().pipe(
          tap(isSectorIvaSgeEnabled => {
            this.isSectorIvaSgeEnabled$.next(isSectorIvaSgeEnabled);
          }),
          switchMap(() =>
            forkJoin({
              proyectosSge: this.proyectoService.findAllProyectosSgeProyecto(this.getKey() as number).pipe(
                map(response => response.items.map(proyectoProyectoSge => new StatusWrapper<IProyectoProyectoSge>(proyectoProyectoSge))),
                switchMap(response => {
                  if (!this.isSectorIvaSgeEnabled$.value) {
                    return of(response);
                  }

                  const requestsProyectoSge: Observable<StatusWrapper<IProyectoProyectoSge>>[] = [];
                  response.forEach(proyectoProyectoSge => {
                    requestsProyectoSge.push(this.proyectoSgeService.findById(proyectoProyectoSge.value.proyectoSge.id).pipe(
                      map((proyectoSge) => {
                        proyectoProyectoSge.value.proyectoSge = proyectoSge;
                        return proyectoProyectoSge;
                      })
                    ));
                  });
                  return of(response).pipe(
                    tap(() => merge(...requestsProyectoSge).subscribe())
                  );
                })
              ),
              cardinalidadRelacionSgiSge: this.configService.getCardinalidadRelacionSgiSge(),
              isModificacionProyectoSgeEnabled: this.configService.isModificacionProyectoSgeEnabled(),
              solicitudesPendientes: this.getSolicitudesProyectoPendientes(this.getKey() as number)
            })
          )
        ).subscribe(({ cardinalidadRelacionSgiSge, isModificacionProyectoSgeEnabled, proyectosSge, solicitudesPendientes }) => {
          this._cardinalidadRelacionSgiSge = cardinalidadRelacionSgiSge;
          this._isModificacionProyectoSgeEnabled = isModificacionProyectoSgeEnabled;
          this._solicitudesProyectoPendientes$.next(solicitudesPendientes);
          this._isSolicitudProyectoAltaPendiente$.next(this.containsSolicitudProyectoAltaPendiente(solicitudesPendientes));
          this.proyectosSge$.next(proyectosSge);
        })
      );

      this.subscriptions.push(
        this.proyectosSge$.subscribe(proyectosSge => {
          this.fillDisableAddIdentificadorSge(proyectosSge);
        })
      )
    }
  }

  public addProyectoSge(proyectoSge: IProyectoSge) {
    const proyectoProyectoSge: IProyectoProyectoSge = {
      id: undefined,
      proyecto: { id: this.getKey() as number } as IProyecto,
      proyectoSge
    };

    const wrapped = new StatusWrapper<IProyectoProyectoSge>(proyectoProyectoSge);
    wrapped.setCreated();
    const current = this.proyectosSge$.value;
    current.push(wrapped);
    this.proyectosSge$.next(current);
    this.setChanges(true);
  }

  public refreshSolicitudesProyectoPendientes(): void {
    this.subscriptions.push(
      this.getSolicitudesProyectoPendientes(this.getKey() as number).subscribe(solicitudesPendientes => {
        this._solicitudesProyectoPendientes$.next(solicitudesPendientes);
        this._isSolicitudProyectoAltaPendiente$.next(this.containsSolicitudProyectoAltaPendiente(solicitudesPendientes));
        this.fillDisableAddIdentificadorSge(this.proyectosSge$.value);
      })
    );
  }

  saveOrUpdate(): Observable<void> {
    return this.createProyectosSge().pipe(
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private createProyectosSge(): Observable<void> {
    const createdProyectosSge = this.proyectosSge$.value.filter((proyectoSge) => proyectoSge.created);
    if (createdProyectosSge.length === 0) {
      return of(void 0);
    }

    return from(createdProyectosSge).pipe(
      mergeMap((wrappedProyectoSge) => {
        return this.service.create(wrappedProyectoSge.value).pipe(
          map((createdProyectoSge) => {
            const index = this.proyectosSge$.value.findIndex((currentProyectoSge) =>
              currentProyectoSge === wrappedProyectoSge);
            const proyectoSge = wrappedProyectoSge.value;
            proyectoSge.id = createdProyectoSge.id;
            this.proyectosSge$.value[index] = new StatusWrapper<IProyectoProyectoSge>(proyectoSge);
            this.proyectosSge$.next(this.proyectosSge$.value);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.proyectosSge$.value.some((wrapper) => wrapper.touched);
    return !(touched);
  }

  private getSolicitudesProyectoPendientes(proyectoId: number): Observable<ISolicitudProyectoSge[]> {
    return forkJoin({
      altaAsync: this.configService.isProyectoSgeAltaModoEjecucionAsync(),
      modificacionAsync: this.configService.isProyectoSgeModificacionModoEjecucionAsync()
    }).pipe(
      switchMap(({ altaAsync, modificacionAsync }) => {
        if (!altaAsync && !modificacionAsync) {
          return of([]);
        }

        return this.solicitudProyectoSgeService.findPendientes(proyectoId);
      })
    );
  }

  private isSolicitudProyectoAltaPendiente(solicitud: ISolicitudProyectoSge): boolean {
    return [
      Estado.ALTA_ACEPTADA_SGE,
      Estado.ALTA_ERROR_SGI,
      Estado.ALTA_RECHAZADA_SGE,
      Estado.ALTA_SOLICITUD_SGI
    ].includes(solicitud.estado);
  }

  private isSolicitudProyectoModificacionPendiente(solicitud: ISolicitudProyectoSge): boolean {
    return [
      Estado.MODIFICACION_ACEPTADA_SGE,
      Estado.MODIFICACION_RECHAZADA_SGE,
      Estado.MODIFICACION_SOLICITUD_SGI,
      Estado.REASIGNACION_ACEPTADA_SGE,
      Estado.REASIGNACION_ERROR_SGI
    ].includes(solicitud.estado);
  }

  private containsSolicitudProyectoAltaPendiente(solicitudes: ISolicitudProyectoSge[]): boolean {
    return solicitudes?.some(solicitud => this.isSolicitudProyectoAltaPendiente(solicitud));
  }

  private fillDisableAddIdentificadorSge(proyectosSge: StatusWrapper<IProyectoProyectoSge>[]): void {
    this._disableAddIdentificadorSge$.next(
      ((proyectosSge?.length ?? 0) > 0 || this._isSolicitudProyectoAltaPendiente$.value)
      && (this._cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_1_SGE_1
        || this._cardinalidadRelacionSgiSge === CardinalidadRelacionSgiSge.SGI_N_SGE_1));
  }

}
