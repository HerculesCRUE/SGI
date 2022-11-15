import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ISolicitudRrhhMemoria } from '@core/models/csp/solicitud-rrhh-memoria';
import { FormFragment } from '@core/services/action-service';
import { SolicitudRrhhPublicService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh-public.service';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable, of } from 'rxjs';
import { catchError, switchMap, take } from 'rxjs/operators';

export class SolicitudRrhhMemoriaPublicFragment extends FormFragment<ISolicitudRrhhMemoria> {

  private solicitudRrhhMemoria: ISolicitudRrhhMemoria;

  public readonly userCanEdit: boolean;

  constructor(
    private readonly logger: NGXLogger,
    readonly solicitudRrhhId: string,
    readonly isInvestigador,
    private readonly solicitudRrhhService: SolicitudRrhhPublicService,
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

  protected initializer(key: string): Observable<ISolicitudRrhhMemoria> {
    return this.solicitudRrhhService.findMemoria(key).pipe(
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tituloTrabajo: new FormControl(null, [Validators.required, Validators.maxLength(1000)]),
      resumen: new FormControl(null, [Validators.required, Validators.maxLength(4000)]),
      observaciones: new FormControl(null, Validators.maxLength(4000))
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
