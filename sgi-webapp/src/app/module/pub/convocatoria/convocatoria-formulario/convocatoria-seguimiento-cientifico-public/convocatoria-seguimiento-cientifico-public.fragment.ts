import { IConvocatoriaPeriodoSeguimientoCientifico } from '@core/models/csp/convocatoria-periodo-seguimiento-cientifico';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export class ConvocatoriaSeguimientoCientificoPublicFragment extends Fragment {
  seguimientosCientificos$ = new BehaviorSubject<StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>[]>([]);

  constructor(
    key: number,
    private convocatoriaService: ConvocatoriaPublicService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      this.convocatoriaService.findSeguimientosCientificos(this.getKey() as number).pipe(
        map((response) => response.items)
      ).subscribe((seguimientosCientificos) => {
        this.seguimientosCientificos$.next(seguimientosCientificos.map(
          seguimientoCientifico => new StatusWrapper<IConvocatoriaPeriodoSeguimientoCientifico>(seguimientoCientifico))
        );
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    throw new Error('Method not implemented.');
  }

}
