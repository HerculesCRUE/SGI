import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { IEmail } from '@core/models/sgp/email';
import { IPersona } from '@core/models/sgp/persona';
import { FormFragment } from '@core/services/action-service';
import { SolicitudRrhhService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh.service';
import { DatosContactoService } from '@core/services/sgp/datos-contacto/datos-contacto.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, forkJoin, Observable, of } from 'rxjs';
import { catchError, map, switchMap, take, tap } from 'rxjs/operators';

export class SolicitudRrhhTutorFragment extends FormFragment<ISolicitudRrhhTutor> {

  private solicitudRrhhTutor: ISolicitudRrhhTutor;
  readonly tutorEmails$ = new BehaviorSubject<IEmail[]>([]);
  readonly tutorTelefonos$ = new BehaviorSubject<string[]>([]);
  tutor$ = new BehaviorSubject<IPersona>(null);

  public readonly userCanEdit: boolean;

  constructor(
    private readonly logger: NGXLogger,
    readonly solicitudRrhhId: number,
    readonly isInvestigador,
    private readonly solicitudRrhhService: SolicitudRrhhService,
    private readonly datosContactoService: DatosContactoService,
    private readonly vinculacionService: VinculacionService,
    private readonly personaService: PersonaService,
    private readonly: boolean
  ) {
    super(solicitudRrhhId, true);
    this.setComplete(true);
    this.userCanEdit = !readonly;
    this.solicitudRrhhTutor = {} as ISolicitudRrhhTutor;

    // Hack edit mode
    this.initialized$.pipe(
      take(2)
    ).subscribe(value => {
      if (value) {
        this.performChecks(true);
      }
    });
  }

  protected initializer(key: string | number): Observable<ISolicitudRrhhTutor> {
    return this.solicitudRrhhService.findTutor(key as number).pipe(
      map(tutorSolicitudRrhh => tutorSolicitudRrhh ?? {} as ISolicitudRrhhTutor),
      switchMap(tutorSolicitudRrhh => this.getDatosTutor(tutorSolicitudRrhh?.tutor)
        .pipe(
          map(tutor => {
            tutorSolicitudRrhh.tutor = tutor;
            return tutorSolicitudRrhh;
          }))
      ),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tutor: new FormControl(null, Validators.required),
      nombre: new FormControl({ value: null, disabled: true }),
      apellidos: new FormControl({ value: null, disabled: true }),
      categoria: new FormControl({ value: null, disabled: true }),
      departamento: new FormControl({ value: null, disabled: true }),
      centro: new FormControl({ value: null, disabled: true })
    });

    if (this.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  buildPatch(solicitudRrhhTutor: ISolicitudRrhhTutor): { [key: string]: any } {
    this.initFormSubscriptions(this.getFormGroup());

    return {
      tutor: solicitudRrhhTutor?.tutor ?? null,
      nombre: solicitudRrhhTutor.tutor?.nombre ?? null,
      apellidos: solicitudRrhhTutor.tutor?.apellidos ?? null,
      categoria: solicitudRrhhTutor.tutor?.vinculacion?.categoriaProfesional?.nombre ?? null,
      departamento: solicitudRrhhTutor.tutor?.vinculacion?.departamento?.nombre ?? null,
      centro: solicitudRrhhTutor.tutor?.vinculacion?.centro?.nombre ?? null
    };
  }


  getValue(): ISolicitudRrhhTutor {
    const form = this.getFormGroup().controls;
    this.solicitudRrhhTutor.tutor = form.tutor.value;

    return this.solicitudRrhhTutor;
  }


  saveOrUpdate(): Observable<void> {
    const solicitudRrhhTutor = this.getValue();
    return this.solicitudRrhhService.updateTutor(this.solicitudRrhhId, solicitudRrhhTutor).pipe(
      switchMap(() => of(void 0))
    );
  }

  private getDatosTutor(tutor: IPersona): Observable<IPersona> {
    if (!!!tutor?.id) {
      return of(tutor);
    }

    return this.personaService.findById(tutor.id).pipe(
      catchError((err) => {
        this.logger.error(err);
        return of(tutor);
      }),
      switchMap((persona: IPersona) => {
        if (!!!persona?.id) {
          return of(persona);
        }

        return this.fillDatosContactoAndVinculacionesTutor(persona);
      }),
      tap(persona => {
        if (!!!persona) {
          return;
        }

        this.fillDatosTutorForm(persona);
        this.tutor$.next(persona);
        this.tutorEmails$.next(persona.datosContacto?.emails);
        const telefonos = (persona.datosContacto?.telefonos ?? []).concat(persona.datosContacto?.moviles ?? [])
          .filter(telefono => !!telefono);
        this.tutorTelefonos$.next(telefonos);
      })
    );
  }

  private fillDatosContactoAndVinculacionesTutor(tutor: IPersona): Observable<IPersona> {
    if (!!!tutor?.id) {
      return of(tutor);
    }

    return forkJoin({
      datosContacto: this.datosContactoService.findByPersonaId(tutor.id),
      vinculacion: this.vinculacionService.findByPersonaId(tutor.id)
    }).pipe(
      map(({ datosContacto, vinculacion }) => {
        tutor.datosContacto = datosContacto;
        tutor.vinculacion = vinculacion;
        return tutor;
      }),
      catchError((err) => {
        this.logger.error(err);
        return of(tutor);
      })
    );
  }

  private initFormSubscriptions(formGroup: FormGroup): void {
    this.subscriptions.push(
      formGroup.controls.tutor.valueChanges
        .pipe(
          switchMap(tutor => this.getDatosTutor(tutor)),
        ).subscribe(tutor => {
          this.solicitudRrhhTutor.tutor = tutor ?? null;
        })
    );
  }

  private fillDatosTutorForm(tutor: IPersona): void {
    this.getFormGroup().controls.nombre.setValue(tutor.nombre ?? null, { emitEvent: false });
    this.getFormGroup().controls.apellidos.setValue(tutor.apellidos ?? null, { emitEvent: false });
    this.getFormGroup().controls.categoria.setValue(tutor.vinculacion?.categoriaProfesional?.nombre ?? null, { emitEvent: false });
    this.getFormGroup().controls.departamento.setValue(tutor.vinculacion?.departamento?.nombre ?? null, { emitEvent: false });
    this.getFormGroup().controls.centro.setValue(tutor.vinculacion?.centro?.nombre ?? null, { emitEvent: false });
  }

}
