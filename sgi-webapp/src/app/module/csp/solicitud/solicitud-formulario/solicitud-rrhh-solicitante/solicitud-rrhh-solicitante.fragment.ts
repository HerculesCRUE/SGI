import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ISolicitanteExterno } from '@core/models/csp/solicitante-externo';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudRrhh } from '@core/models/csp/solicitud-rrhh';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { IEmail } from '@core/models/sgp/email';
import { IPersona } from '@core/models/sgp/persona';
import { FormFragment } from '@core/services/action-service';
import { SolicitanteExternoService } from '@core/services/csp/solicitante-externo/solicitante-externo.service';
import { SolicitudRrhhService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { ClasificacionService } from '@core/services/sgo/clasificacion.service';
import { DatosContactoService } from '@core/services/sgp/datos-contacto/datos-contacto.service';
import { DatosPersonalesService } from '@core/services/sgp/datos-personales.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, forkJoin, Observable, of } from 'rxjs';
import { catchError, filter, map, switchMap, take, tap } from 'rxjs/operators';

export interface ISolicitudSolicitanteRrhh extends ISolicitudRrhh {
  solicitante: IPersona;
  solicitanteExterno: ISolicitanteExterno;
  areaAnepListado: SolicitudRrhhAreaAnepListado;
}

export interface SolicitudRrhhAreaAnepListado {
  niveles: IClasificacion[];
  nivelesTexto: string;
  nivelSeleccionado: IClasificacion;
}

export class SolicitudRrhhSolitanteFragment extends FormFragment<ISolicitudSolicitanteRrhh> {

  private solicitudSolicitanteRrhh: ISolicitudSolicitanteRrhh;
  private solicitanteExterno: ISolicitanteExterno;
  private solicitanteForm: IPersona;

  solicitante$ = new BehaviorSubject<IPersona>(null);
  readonly solicitanteEmails$ = new BehaviorSubject<IEmail[]>([]);
  readonly solicitanteTelefonos$ = new BehaviorSubject<string[]>([]);
  readonly areasAnep$ = new BehaviorSubject<SolicitudRrhhAreaAnepListado[]>([]);
  public readonly userCanEdit: boolean;

  get isSolicitanteRequired(): boolean {
    return !!!this.solicitanteExterno || !Object.values(this.solicitanteExterno).some(x => x !== null && x !== undefined && x !== '');
  }

  constructor(
    private readonly logger: NGXLogger,
    readonly solicitud: ISolicitud,
    readonly isInvestigador: boolean,
    private readonly solicitudService: SolicitudService,
    private readonly solicitudRrhhService: SolicitudRrhhService,
    private readonly solicitanteExternoService: SolicitanteExternoService,
    private readonly clasificacionService: ClasificacionService,
    private readonly datosContactoService: DatosContactoService,
    private readonly datosPersonalesService: DatosPersonalesService,
    private readonly personaService: PersonaService,
    private readonly empresaService: EmpresaService,
    private readonly: boolean
  ) {
    super(solicitud?.id, true);
    this.setComplete(true);
    this.solicitudSolicitanteRrhh = {} as ISolicitudSolicitanteRrhh;
    this.userCanEdit = !readonly;

    // Hack edit mode
    this.initialized$.pipe(
      take(2)
    ).subscribe(value => {
      if (value) {
        this.performChecks(true);
      }
    });
  }

  protected initializer(key: string | number): Observable<ISolicitudSolicitanteRrhh> {
    return this.solicitudService.findSolicitudRrhh(key as number).pipe(
      tap(solicitudRrhh => {
        if (!!!solicitudRrhh && this.isInvestigador) {
          this.setChanges(true);
        }
      }),
      map(solicitudRrhh => {
        const solicitanteSolicitudRrhh = (solicitudRrhh ?? {}) as ISolicitudSolicitanteRrhh;
        solicitanteSolicitudRrhh.solicitante = this.solicitud.solicitante;
        this.solicitudSolicitanteRrhh = solicitanteSolicitudRrhh;
        return solicitanteSolicitudRrhh;
      }),
      switchMap(solicitanteSolicitudRrhh => {
        return forkJoin({
          solicitante: this.loadSolicitante(solicitanteSolicitudRrhh?.solicitante?.id),
          solicitanteExterno: this.loadSolicitanteExterno(solicitanteSolicitudRrhh?.id),
          universidad: this.loadUniversidad(solicitanteSolicitudRrhh?.universidad?.id),
          areaAnepListado: this.loadAreaAnep(solicitanteSolicitudRrhh?.areaAnep?.id)
        }).pipe(
          map(({ solicitante, solicitanteExterno, universidad, areaAnepListado }) => {
            solicitanteSolicitudRrhh.solicitante = solicitante;
            solicitanteSolicitudRrhh.solicitanteExterno = solicitanteExterno;
            solicitanteSolicitudRrhh.universidad = universidad;
            solicitanteSolicitudRrhh.areaAnepListado = areaAnepListado;
            return solicitanteSolicitudRrhh;
          })
        );
      }),
      tap(solicitanteSolicitudRrhh => this.solicitudSolicitanteRrhh = solicitanteSolicitudRrhh),
      tap(solicitanteSolicitudRrhh => this.solicitanteExterno = solicitanteSolicitudRrhh.solicitanteExterno),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      solicitante: new FormControl(null, Validators.required),
      solicitanteExterno: new FormGroup({
        nombre: new FormControl(null, Validators.required),
        apellidos: new FormControl(null, Validators.required),
        tipoDocumento: new FormControl(null, Validators.required),
        numeroDocumento: new FormControl(null, Validators.required),
        sexo: new FormControl(null),
        fechaNacimiento: new FormControl(null),
        paisNacimiento: new FormControl(null),
        telefono: new FormControl(null, Validators.required),
        email: new FormControl(null, [Validators.required, Validators.email]),
        direccionContacto: new FormControl(null),
        paisContacto: new FormControl(null),
        comunidadAutonomaContacto: new FormControl(null),
        provinciaContacto: new FormControl(null),
        localidadContacto: new FormControl(null),
        codigoPostalContacto: new FormControl(null)
      }),
      universidadSelect: new FormControl(null),
      universidadText: new FormControl({ value: null, disabled: !this.isInvestigador }),
      areaAnep: new FormControl(null)
    });

    if (this.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  buildPatch(solicitudSolicitanteRrhh: ISolicitudSolicitanteRrhh): { [key: string]: any } {
    const universidadText = this.isInvestigador
      ? solicitudSolicitanteRrhh?.universidad?.nombre ?? solicitudSolicitanteRrhh?.universidadDatos
      : solicitudSolicitanteRrhh?.universidadDatos;

    let formValues: { [key: string]: any } = {
      universidadSelect: solicitudSolicitanteRrhh?.universidad,
      universidadText,
      areaAnep: solicitudSolicitanteRrhh?.areaAnep
    };

    if (!!solicitudSolicitanteRrhh?.solicitante) {
      formValues = {
        ...formValues,
        solicitante: solicitudSolicitanteRrhh.solicitante,
        solicitanteExterno: {
          nombre: solicitudSolicitanteRrhh.solicitante.nombre ?? null,
          apellidos: solicitudSolicitanteRrhh.solicitante.apellidos ?? null,
          tipoDocumento: solicitudSolicitanteRrhh.solicitante.tipoDocumento ?? null,
          numeroDocumento: solicitudSolicitanteRrhh.solicitante.numeroDocumento ?? null,
          sexo: solicitudSolicitanteRrhh.solicitante.sexo ?? null,
          fechaNacimiento: solicitudSolicitanteRrhh.solicitante.datosPersonales?.fechaNacimiento ?? null,
          paisNacimiento: solicitudSolicitanteRrhh.solicitante.datosPersonales?.paisNacimiento ?? null,
          direccionContacto: solicitudSolicitanteRrhh.solicitante.datosContacto?.direccionContacto ?? null,
          paisContacto: solicitudSolicitanteRrhh.solicitante.datosContacto?.paisContacto ?? null,
          comunidadAutonomaContacto: solicitudSolicitanteRrhh.solicitante.datosContacto?.comAutonomaContacto ?? null,
          provinciaContacto: solicitudSolicitanteRrhh.solicitante.datosContacto?.provinciaContacto ?? null,
          localidadContacto: solicitudSolicitanteRrhh.solicitante.datosContacto?.ciudadContacto ?? null,
          codigoPostalContacto: solicitudSolicitanteRrhh.solicitante.datosContacto?.codigoPostalContacto ?? null
        }
      };

      this.getFormGroup().controls.solicitanteExterno.disable({ emitEvent: false });
      this.solicitante$.next(solicitudSolicitanteRrhh.solicitante);
    } else if (!!solicitudSolicitanteRrhh?.solicitanteExterno) {
      formValues = {
        ...formValues,
        solicitanteExterno: {
          nombre: solicitudSolicitanteRrhh.solicitanteExterno.nombre,
          apellidos: solicitudSolicitanteRrhh?.solicitanteExterno.apellidos,
          tipoDocumento: solicitudSolicitanteRrhh?.solicitanteExterno.tipoDocumento,
          numeroDocumento: solicitudSolicitanteRrhh?.solicitanteExterno.numeroDocumento,
          sexo: solicitudSolicitanteRrhh?.solicitanteExterno.sexo,
          fechaNacimiento: solicitudSolicitanteRrhh?.solicitanteExterno.fechaNacimiento,
          paisNacimiento: solicitudSolicitanteRrhh?.solicitanteExterno.paisNacimiento,
          telefono: solicitudSolicitanteRrhh?.solicitanteExterno.telefono,
          email: solicitudSolicitanteRrhh?.solicitanteExterno.email,
          direccionContacto: solicitudSolicitanteRrhh?.solicitanteExterno.direccion,
          paisContacto: solicitudSolicitanteRrhh?.solicitanteExterno.paisContacto,
          comunidadAutonomaContacto: solicitudSolicitanteRrhh?.solicitanteExterno.comunidad,
          provinciaContacto: solicitudSolicitanteRrhh?.solicitanteExterno.provincia,
          localidadContacto: solicitudSolicitanteRrhh?.solicitanteExterno.ciudad,
          codigoPostalContacto: solicitudSolicitanteRrhh?.solicitanteExterno.codigoPostal
        }
      };

      this.setSolicitanteValidators(solicitudSolicitanteRrhh?.solicitanteExterno);
    }

    this.initFormSubscriptions(this.getFormGroup());

    return formValues;
  }


  getValue(): ISolicitudSolicitanteRrhh {
    const form = this.getFormGroup().controls;
    this.solicitudSolicitanteRrhh.solicitante = form.solicitante.value;
    this.solicitudSolicitanteRrhh.solicitanteExterno = this.getDatosFormSolicitanteExterno();
    this.solicitudSolicitanteRrhh.universidad = form.universidadSelect.value;
    this.solicitudSolicitanteRrhh.universidadDatos = form.universidadText.value;
    this.solicitudSolicitanteRrhh.areaAnep = this.areasAnep$.value?.length > 0 ? this.areasAnep$.value[0].nivelSeleccionado : null;

    return this.solicitudSolicitanteRrhh;
  }

  updateAreaAnep(areaAnep: SolicitudRrhhAreaAnepListado): void {
    this.solicitudSolicitanteRrhh.areaAnep = areaAnep.nivelSeleccionado;

    if (this.areasAnep$.value?.length > 0) {
      this.areasAnep$.value[0] = areaAnep;
      this.areasAnep$.next(this.areasAnep$.value);
    } else {
      this.areasAnep$.next([areaAnep]);
    }

    this.setChanges(true);
  }

  deleteAreaAnep(): void {
    this.solicitudSolicitanteRrhh.areaAnep = null;
    this.areasAnep$.next([]);
    this.setChanges(true);
  }

  saveOrUpdate(): Observable<number> {
    const solicitudRrhh = this.getValue();
    const observable$ = this.isEdit() ? this.update(solicitudRrhh) : this.create(solicitudRrhh);
    return observable$.pipe(
      map(value => {
        this.solicitudSolicitanteRrhh.id = value.id;
        return this.solicitudSolicitanteRrhh;
      }),
      switchMap(() => this.saveOrUpdateSolicitante(solicitudRrhh)),
      map(() => {
        this.refreshInitialState();
        return this.solicitudSolicitanteRrhh.id;
      })
    );
  }

  isEdit(): boolean {
    return !!this.solicitudSolicitanteRrhh?.id;
  }

  private create(solicitudRrhh: ISolicitudSolicitanteRrhh): Observable<ISolicitudRrhh> {
    solicitudRrhh.id = this.getKey() as number;
    return this.solicitudRrhhService.create(solicitudRrhh as ISolicitudRrhh);
  }

  private update(grupo: ISolicitudSolicitanteRrhh): Observable<ISolicitudRrhh> {
    return this.solicitudRrhhService.update(grupo.id, grupo as ISolicitudRrhh);
  }

  private saveOrUpdateSolicitante(solicitudSolicitanteRrhh: ISolicitudSolicitanteRrhh): Observable<void> {
    let cascade = of(void 0);
    if (this.solicitud.solicitante?.id !== solicitudSolicitanteRrhh.solicitante?.id) {
      cascade = cascade.pipe(
        switchMap(() => this.saveOrUpdateSolicitanteInterno(solicitudSolicitanteRrhh))
      );
    }
    if (!!!solicitudSolicitanteRrhh.solicitante?.id) {
      cascade = cascade.pipe(
        switchMap(() => this.saveOrUpdateSolicitanteExterno(solicitudSolicitanteRrhh))
      );
    }
    return cascade;
  }

  private saveOrUpdateSolicitanteInterno(solicitudSolicitanteRrhh: ISolicitudSolicitanteRrhh): Observable<void> {
    return this.solicitudService.updateSolicitante(solicitudSolicitanteRrhh.id, solicitudSolicitanteRrhh?.solicitante?.id).pipe(
      tap(() => this.solicitud.solicitante = solicitudSolicitanteRrhh?.solicitante)
    );
  }

  private saveOrUpdateSolicitanteExterno(solicitudSolicitanteRrhh: ISolicitudSolicitanteRrhh): Observable<void> {
    return solicitudSolicitanteRrhh.solicitanteExterno?.id ? this.updateSolicitanteExterno(solicitudSolicitanteRrhh.solicitanteExterno)
      : this.createSolicitanteExterno(this.getDatosFormSolicitanteExterno());
  }

  private createSolicitanteExterno(solicitante: ISolicitanteExterno): Observable<void> {
    return this.solicitanteExternoService.create(solicitante).pipe(
      map(solicitanteExterno => {
        this.solicitanteExterno = solicitante;
        this.solicitanteExterno.id = solicitanteExterno.id;
      })
    );
  }

  private updateSolicitanteExterno(solicitante: ISolicitanteExterno): Observable<void> {
    return this.solicitanteExternoService.update(solicitante.id, solicitante).pipe(
      map(solicitanteExterno => {
        this.solicitanteExterno.id = solicitanteExterno.id;
      })
    );
  }

  private loadSolicitante(id: string): Observable<IPersona> {
    if (!id) {
      return of(null);
    }

    return this.personaService.findById(id).pipe(
      switchMap((solicitante: IPersona) => {
        if (!!!solicitante?.id) {
          return of(solicitante);
        }

        return this.fillDatosContactoAndPersonalesSolicitante(solicitante);
      })
    );
  }

  private fillDatosContactoAndPersonalesSolicitante(solicitante: IPersona): Observable<IPersona> {
    if (!!!solicitante?.id) {
      return of(solicitante);
    }

    return forkJoin({
      datosContacto: this.datosContactoService.findByPersonaId(solicitante.id),
      datosPersonales: this.datosPersonalesService.findByPersonaId(solicitante.id)
    }).pipe(
      map(({ datosContacto, datosPersonales }) => {
        solicitante.datosContacto = datosContacto;
        solicitante.datosPersonales = datosPersonales;
        this.solicitanteForm = solicitante;
        return solicitante;
      }),
      tap(datosSolicitante => {
        if (!!!datosSolicitante) {
          return;
        }

        this.solicitanteEmails$.next(datosSolicitante.datosContacto?.emails);
        const telefonos = (datosSolicitante.datosContacto?.telefonos ?? [])
          .concat(datosSolicitante.datosContacto?.moviles ?? []).filter(telefono => !!telefono);
        this.solicitanteTelefonos$.next(telefonos);
      })
    );
  }

  private loadSolicitanteExterno(solicitudId: number): Observable<ISolicitanteExterno> {
    return solicitudId ? this.solicitudService.findSolicitanteExterno(solicitudId) : of(null);
  }

  private loadUniversidad(id: string): Observable<IEmpresa> {
    return id ? this.empresaService.findById(id) : of(null);
  }

  private loadAreaAnep(id: string): Observable<SolicitudRrhhAreaAnepListado> {
    if (!id) {
      return of(null);
    }

    return this.clasificacionService.findById(id).pipe(
      map((clasificacion) => {
        return {
          niveles: [clasificacion],
          nivelesTexto: '',
          nivelSeleccionado: clasificacion
        };
      }),
      switchMap((solicitudRrhhAreaAnepListado) => {
        return this.getNiveles(solicitudRrhhAreaAnepListado);
      }),
      map(solicitudRrhhAreaAnepListado => {
        solicitudRrhhAreaAnepListado.nivelesTexto = solicitudRrhhAreaAnepListado.niveles
          .slice(1, solicitudRrhhAreaAnepListado.niveles.length - 1)
          .reverse()
          .map(clasificacion => clasificacion.nombre).join(' - ');
        return solicitudRrhhAreaAnepListado;
      })
    ).pipe(
      tap(areaAnepListado => areaAnepListado ? this.areasAnep$.next([areaAnepListado]) : this.areasAnep$.next([]))
    );
  }

  private getNiveles(solicitudRrhhAreaAnep: SolicitudRrhhAreaAnepListado): Observable<SolicitudRrhhAreaAnepListado> {
    const lastLevel = solicitudRrhhAreaAnep.niveles[solicitudRrhhAreaAnep.niveles.length - 1];
    if (!lastLevel.padreId) {
      return of(solicitudRrhhAreaAnep);
    }

    return this.clasificacionService.findById(lastLevel.padreId).pipe(
      switchMap(clasificacion => {
        solicitudRrhhAreaAnep.niveles.push(clasificacion);
        return this.getNiveles(solicitudRrhhAreaAnep);
      })
    );
  }

  private initFormSubscriptions(formGroup: FormGroup): void {
    this.subscriptions.push(
      formGroup.controls.solicitante.valueChanges
        .pipe(
          filter(solicitante => solicitante !== this.solicitanteForm && (!!this.solicitanteForm || !!solicitante)),
          switchMap((solicitante: IPersona) => {
            if (!!solicitante?.id) {
              return this.fillDatosContactoAndPersonalesSolicitante(solicitante);
            }

            return of(solicitante);
          })
        ).subscribe(solicitante => {
          this.solicitanteForm = solicitante;
          this.solicitante$.next(this.solicitanteForm);
          if (!!solicitante) {
            formGroup.controls.solicitanteExterno.disable({ emitEvent: false });
          } else {
            formGroup.controls.solicitanteExterno.enable({ emitEvent: false });
          }

          this.fillDatosSolicitanteForm();
          this.setSolicitanteValidators(this.solicitanteExterno);
        })
    );

    this.subscriptions.push(
      formGroup.controls.solicitanteExterno.valueChanges
        .pipe(
          filter(() => !!this.initialized$.value),
          filter(() => !!!this.solicitanteForm)
        )
        .subscribe(solicitanteExterno => {
          if (!this.hasChanges() && this.isEdit()) {
            return;
          }

          this.setSolicitanteValidators(solicitanteExterno);
        })
    );
  }

  private setSolicitanteValidators(solicitanteExterno: ISolicitanteExterno): void {
    const isEmpty = !!!solicitanteExterno || !Object.values(solicitanteExterno).some(x => x !== null && x !== undefined && x !== '');
    if (isEmpty) {
      this.getFormGroup().controls.solicitante.setValidators([Validators.required]);
    } else {
      this.getFormGroup().controls.solicitante.clearValidators();
    }
    this.getFormGroup().controls.solicitante.updateValueAndValidity({ onlySelf: true });
  }

  private fillDatosSolicitanteForm(): void {
    const solicitante = this.getFormGroup().controls.solicitante.value;
    if (!!solicitante) {
      this.fillDatosSolicitanteUniversidad(solicitante);
    } else {
      this.fillDatosSolicitanteExterno();
    }
  }

  private fillDatosSolicitanteUniversidad(solicitante: IPersona): void {
    const solicitanteExternoFormGroup = this.getFormGroup().controls.solicitanteExterno as FormGroup;
    if (!this.hasChanges() && this.isEdit()) {
      return;
    }
    solicitanteExternoFormGroup.controls.nombre.setValue(solicitante.nombre, { emitEvent: false });
    solicitanteExternoFormGroup.controls.apellidos.setValue(solicitante.apellidos, { emitEvent: false });
    solicitanteExternoFormGroup.controls.tipoDocumento.setValue(solicitante.tipoDocumento, { emitEvent: false });
    solicitanteExternoFormGroup.controls.numeroDocumento.setValue(solicitante.numeroDocumento, { emitEvent: false });
    solicitanteExternoFormGroup.controls.sexo.setValue(solicitante.sexo, { emitEvent: false });

    solicitanteExternoFormGroup.controls.fechaNacimiento.setValue(solicitante.datosPersonales?.fechaNacimiento, { emitEvent: false });
    solicitanteExternoFormGroup.controls.paisNacimiento.setValue(solicitante.datosPersonales?.paisNacimiento, { emitEvent: false });

    solicitanteExternoFormGroup.controls.direccionContacto.setValue(solicitante.datosContacto?.direccionContacto, { emitEvent: false });
    solicitanteExternoFormGroup.controls.paisContacto.setValue(solicitante.datosContacto?.paisContacto, { emitEvent: false });
    solicitanteExternoFormGroup.controls.comunidadAutonomaContacto.setValue(
      solicitante.datosContacto?.comAutonomaContacto,
      { emitEvent: false }
    );
    solicitanteExternoFormGroup.controls.provinciaContacto.setValue(solicitante.datosContacto?.provinciaContacto, { emitEvent: false });
    solicitanteExternoFormGroup.controls.localidadContacto.setValue(solicitante.datosContacto?.ciudadContacto, { emitEvent: false });
    solicitanteExternoFormGroup.controls.codigoPostalContacto.setValue(
      solicitante.datosContacto?.codigoPostalContacto,
      { emitEvent: false }
    );

    solicitanteExternoFormGroup.updateValueAndValidity();
  }

  private fillDatosSolicitanteExterno(): void {
    const solicitanteExternoFormGroup = this.getFormGroup().controls.solicitanteExterno as FormGroup;
    solicitanteExternoFormGroup.controls.nombre.setValue(this.solicitanteExterno?.nombre, { emitEvent: false });
    solicitanteExternoFormGroup.controls.apellidos.setValue(this.solicitanteExterno?.apellidos, { emitEvent: false });
    solicitanteExternoFormGroup.controls.tipoDocumento.setValue(this.solicitanteExterno?.tipoDocumento, { emitEvent: false });
    solicitanteExternoFormGroup.controls.numeroDocumento.setValue(this.solicitanteExterno?.numeroDocumento, { emitEvent: false });
    solicitanteExternoFormGroup.controls.sexo.setValue(this.solicitanteExterno?.sexo, { emitEvent: false });
    solicitanteExternoFormGroup.controls.fechaNacimiento.setValue(this.solicitanteExterno?.fechaNacimiento, { emitEvent: false });
    solicitanteExternoFormGroup.controls.paisNacimiento.setValue(this.solicitanteExterno?.paisNacimiento, { emitEvent: false });
    solicitanteExternoFormGroup.controls.telefono.setValue(this.solicitanteExterno?.telefono, { emitEvent: false });
    solicitanteExternoFormGroup.controls.email.setValue(this.solicitanteExterno?.email, { emitEvent: false });
    solicitanteExternoFormGroup.controls.direccionContacto.setValue(this.solicitanteExterno?.direccion, { emitEvent: false });
    solicitanteExternoFormGroup.controls.paisContacto.setValue(this.solicitanteExterno?.paisContacto, { emitEvent: false });
    solicitanteExternoFormGroup.controls.comunidadAutonomaContacto.setValue(this.solicitanteExterno?.comunidad, { emitEvent: false });
    solicitanteExternoFormGroup.controls.provinciaContacto.setValue(this.solicitanteExterno?.provincia, { emitEvent: false });
    solicitanteExternoFormGroup.controls.localidadContacto.setValue(this.solicitanteExterno?.ciudad, { emitEvent: false });
    solicitanteExternoFormGroup.controls.codigoPostalContacto.setValue(this.solicitanteExterno?.codigoPostal, { emitEvent: false });
  }

  private getDatosFormSolicitanteExterno(): ISolicitanteExterno {
    const solicitanteExternoFormGroup = this.getFormGroup().controls.solicitanteExterno as FormGroup;

    return {
      id: this.solicitanteExterno?.id,
      solicitudId: this.getKey() as number,
      nombre: solicitanteExternoFormGroup.controls.nombre.value,
      apellidos: solicitanteExternoFormGroup.controls.apellidos.value,
      tipoDocumento: solicitanteExternoFormGroup.controls.tipoDocumento.value,
      numeroDocumento: solicitanteExternoFormGroup.controls.numeroDocumento.value,
      sexo: solicitanteExternoFormGroup.controls.sexo.value,
      fechaNacimiento: solicitanteExternoFormGroup.controls.fechaNacimiento.value,
      paisNacimiento: solicitanteExternoFormGroup.controls.paisNacimiento.value,
      telefono: solicitanteExternoFormGroup.controls.telefono.value,
      email: solicitanteExternoFormGroup.controls.email.value,
      direccion: solicitanteExternoFormGroup.controls.direccionContacto.value,
      paisContacto: solicitanteExternoFormGroup.controls.paisContacto.value,
      comunidad: solicitanteExternoFormGroup.controls.comunidadAutonomaContacto.value,
      provincia: solicitanteExternoFormGroup.controls.provinciaContacto.value,
      ciudad: solicitanteExternoFormGroup.controls.localidadContacto.value,
      codigoPostal: solicitanteExternoFormGroup.controls.codigoPostalContacto.value
    };
  }

}
