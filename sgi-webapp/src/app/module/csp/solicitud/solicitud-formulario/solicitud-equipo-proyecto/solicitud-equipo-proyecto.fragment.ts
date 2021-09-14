import { ISolicitudProyectoEquipo } from '@core/models/csp/solicitud-proyecto-equipo';
import { Fragment } from '@core/services/action-service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { SolicitudProyectoEquipoService } from '@core/services/csp/solicitud-proyecto-equipo.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { SolicitudActionService } from '../../solicitud.action.service';

export class SolicitudEquipoProyectoFragment extends Fragment {
  proyectoEquipos$ = new BehaviorSubject<StatusWrapper<ISolicitudProyectoEquipo>[]>([]);

  constructor(
    key: number,
    private solicitudService: SolicitudService,
    private solicitudProyectoEquipoService: SolicitudProyectoEquipoService,
    public actionService: SolicitudActionService,
    public rolProyectoService: RolProyectoService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    const id = this.getKey() as number;
    if (id) {
      this.solicitudService.existSolicitanteInSolicitudProyectoEquipo(id).subscribe(result => {
        if (!result) {
          this.rolProyectoService.findPrincipal().subscribe(rol => {
            const solictudProyectoEquipo = {
              solicitudProyectoId: id,
              rolProyecto: rol,
              persona: this.actionService.solicitante
            } as ISolicitudProyectoEquipo;
            this.addProyectoEquipo(solictudProyectoEquipo);
          });
        }
      });

      const subscription = this.solicitudService.findAllSolicitudProyectoEquipo(id).pipe(
        map(result => result.items.map(solicitudProyectoEquipo =>
          new StatusWrapper<ISolicitudProyectoEquipo>(solicitudProyectoEquipo)
        )),
      ).subscribe(
        (result) => {
          this.proyectoEquipos$.next(result);
        }
      );
      this.subscriptions.push(subscription);
    }
  }

  public deleteProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoEquipo>): void {
    const current = this.proyectoEquipos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      current.splice(index, 1);
      this.proyectoEquipos$.next(current);
      this.setChanges(true);
    }
  }

  addProyectoEquipo(equipoProyectoData: ISolicitudProyectoEquipo): void {
    const wrapped = new StatusWrapper<ISolicitudProyectoEquipo>(equipoProyectoData);
    wrapped.setCreated();
    const current = this.proyectoEquipos$.value;
    current.push(wrapped);
    this.proyectoEquipos$.next(current);
    this.setChanges(true);
  }

  updateProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoEquipo>, index: number): void {
    if (index >= 0) {
      if (!wrapper.created) {
        wrapper.setEdited();
        this.setChanges(true);
      }
      this.proyectoEquipos$.value[index] = wrapper;
      this.proyectoEquipos$.next(this.proyectoEquipos$.value);
    }
  }

  saveOrUpdate(): Observable<void> {
    const solicitudProyectoEquipos = this.proyectoEquipos$.value.map(wrapper => wrapper.value);
    solicitudProyectoEquipos.forEach(solicitudProyectoEquipo => {
      solicitudProyectoEquipo.solicitudProyectoId = this.getKey() as number;
    });

    return (this.solicitudProyectoEquipoService.updateSolicitudProyectoEquipo(this.getKey() as number, solicitudProyectoEquipos)
    ).pipe(
      map((peridosJustificacionActualizados) => {
        this.proyectoEquipos$.next(
          peridosJustificacionActualizados.map((solicitudProyectoEquipo) => {
            solicitudProyectoEquipo.persona = solicitudProyectoEquipos.find(
              equipo => equipo.persona.id === solicitudProyectoEquipo.persona.id).persona;
            return new StatusWrapper<ISolicitudProyectoEquipo>(solicitudProyectoEquipo);
          }));
      }),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.proyectoEquipos$.value.some((wrapper) => wrapper.touched);
    return !touched;
  }
}
