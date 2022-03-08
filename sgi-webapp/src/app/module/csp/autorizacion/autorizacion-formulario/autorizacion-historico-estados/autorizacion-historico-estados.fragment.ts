import { IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { Fragment } from '@core/services/action-service';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

export class AutorizacionHistoricoEstadosFragment extends Fragment {

  historicoEstado$: BehaviorSubject<StatusWrapper<IEstadoAutorizacion>[]>;

  constructor(
    key: number,
    private autorizacionService: AutorizacionService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
    this.historicoEstado$ = new BehaviorSubject<StatusWrapper<IEstadoAutorizacion>[]>([]);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.autorizacionService.findEstadosAutorizacion(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((historicoEstados) => {
        this.historicoEstado$.next(historicoEstados.map(
          historicoEstado => new StatusWrapper<IEstadoAutorizacion>(historicoEstado))
        );
      });
    }
  }

  saveOrUpdate(): Observable<string | number | void> {
    return of(void 0);
  }

  reload(): void {
    this.onInitialize();
  }
}
