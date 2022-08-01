import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ISolicitudRrhhMemoria } from '@core/models/csp/solicitud-rrhh-memoria';
import { FormFragment } from '@core/services/action-service';
import { SolicitudRrhhService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh.service';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable, of } from 'rxjs';
import { catchError, switchMap, take } from 'rxjs/operators';

export class SolicitudRrhhMemoriaFragment extends FormFragment<ISolicitudRrhhMemoria> {

  private solicitudRrhhMemoria: ISolicitudRrhhMemoria;

  public readonly userCanEdit: boolean;

  constructor(
    private readonly logger: NGXLogger,
    readonly solicitudRrhhId: number,
    readonly isInvestigador,
    private readonly solicitudRrhhService: SolicitudRrhhService,
    private readonly: boolean
  ) {
    super(solicitudRrhhId, true);
    this.setComplete(true);
    this.userCanEdit = !readonly;
    this.solicitudRrhhMemoria = {} as ISolicitudRrhhMemoria;

    // Hack edit mode
    this.initialized$.pipe(
      take(2)
    ).subscribe(value => {
      if (value) {
        this.performChecks(true);
      }
    });
  }

  protected initializer(key: string | number): Observable<ISolicitudRrhhMemoria> {
    return this.solicitudRrhhService.findMemoria(key as number).pipe(
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tituloTrabajo: new FormControl(null, Validators.required),
      resumen: new FormControl(null, Validators.required),
      observaciones: new FormControl(null)
    });

    if (this.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  buildPatch(solicitudRrhhMemoria: ISolicitudRrhhMemoria): { [key: string]: any } {
    return {
      tituloTrabajo: solicitudRrhhMemoria?.tituloTrabajo,
      resumen: solicitudRrhhMemoria?.resumen,
      observaciones: solicitudRrhhMemoria?.observaciones
    };
  }

  getValue(): ISolicitudRrhhMemoria {
    const form = this.getFormGroup().controls;
    this.solicitudRrhhMemoria.tituloTrabajo = form.tituloTrabajo.value;
    this.solicitudRrhhMemoria.resumen = form.resumen.value;
    this.solicitudRrhhMemoria.observaciones = form.observaciones.value;

    return this.solicitudRrhhMemoria;
  }


  saveOrUpdate(): Observable<void> {
    const solicitudRrhhMemoria = this.getValue();
    return this.solicitudRrhhService.updateMemoria(this.solicitudRrhhId, solicitudRrhhMemoria).pipe(
      switchMap(() => of(void 0))
    );
  }

}
