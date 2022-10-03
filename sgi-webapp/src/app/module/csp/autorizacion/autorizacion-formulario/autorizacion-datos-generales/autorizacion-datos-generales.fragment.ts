import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { Estado } from '@core/models/csp/estado-autorizacion';
import { FormFragment } from '@core/services/action-service';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { EstadoAutorizacionService } from '@core/services/csp/estado-autorizacion/estado-autorizacion.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, merge, Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

export interface IAutorizacionDatosGeneralesData extends IAutorizacion {
  fechaFirstEstado: DateTime;
}

export class AutorizacionDatosGeneralesFragment extends FormFragment<IAutorizacionDatosGeneralesData> {

  public autorizacionData: IAutorizacionDatosGeneralesData;

  public investigadorRequired: boolean;
  public entidadRequired: boolean;

  readonly disableCambioEstado$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private autorizacionService: AutorizacionService,
    private personaService: PersonaService,
    private empresaService: EmpresaService,
    private estadoAutorizacionService: EstadoAutorizacionService,
    private convocatoriaService: ConvocatoriaService,
    public readonly isInvestigador: boolean,
    public readonly isVisor: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.autorizacionData = {} as IAutorizacionDatosGeneralesData;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      estado: new FormControl({ value: null, disabled: true }, Validators.required),
      fechaSolicitud: new FormControl({ value: null, disabled: true }),
      solicitante: new FormControl({ value: null, disabled: true }),
      tituloProyecto: new FormControl(null, [Validators.maxLength(250), Validators.required]),
      convocatoria: new FormControl(null),
      datosConvocatoria: new FormControl(null, Validators.maxLength(250)),
      entidadParticipa: new FormControl(null, Validators.required),
      datosEntidad: new FormControl(null, Validators.maxLength(250)),
      investigadorPrincipalProyecto: new FormControl(null, [Validators.required, Validators.maxLength(250)]),
      datosIpProyecto: new FormControl(null),
      horasDedicacion: new FormControl(null),
      observaciones: new FormControl(null, Validators.maxLength(2000))
    });

    this.subscriptions.push(form.controls.convocatoria.valueChanges.subscribe(
      (convocatoria) => {
        if (this.autorizacionData?.estado?.estado === 'BORRADOR' || !this.isEdit()) {
          if (convocatoria) {
            form.controls.datosConvocatoria.disable();
            form.controls.datosConvocatoria.setValue(null);
          } else if (!this.isVisor) {
            form.controls.datosConvocatoria.enable();
          }
        }
      }
    ));

    // Se setean los validadores condicionales y
    // se hace una subscripcion a los campos que provocan
    // cambios en los validadores del formulario
    this.setConditionalValidatorsEntidad(form);
    this.setConditionalValidatorsIP(form);

    this.subscriptions.push(
      merge(
        form.controls.entidadParticipa.valueChanges,
        form.controls.datosEntidad.valueChanges
      ).subscribe(_ => {
        this.setConditionalValidatorsEntidad(form);
      })
    );

    this.subscriptions.push(
      merge(
        form.controls.investigadorPrincipalProyecto.valueChanges,
        form.controls.datosIpProyecto.valueChanges
      ).subscribe(_ => {
        this.setConditionalValidatorsIP(form);
      })
    );
    return form;
  }

  buildPatch(autorizacionData: IAutorizacionDatosGeneralesData): { [key: string]: any } {
    this.autorizacionData = autorizacionData;
    return {
      estado: autorizacionData.estado?.estado,
      fechaSolicitud: autorizacionData.fechaFirstEstado,
      solicitante: autorizacionData.solicitante,
      tituloProyecto: autorizacionData.tituloProyecto,
      convocatoria: autorizacionData.convocatoria,
      datosConvocatoria: autorizacionData.datosConvocatoria,
      entidadParticipa: autorizacionData.entidad,
      datosEntidad: autorizacionData.datosEntidad,
      investigadorPrincipalProyecto: autorizacionData.responsable,
      datosIpProyecto: autorizacionData.datosResponsable,
      horasDedicacion: autorizacionData.horasDedicacion,
      observaciones: autorizacionData.observaciones
    };
  }

  protected initializer(key: string | number): Observable<IAutorizacionDatosGeneralesData> {
    return this.autorizacionService.findById(key as number).pipe(
      map(autorizacion => autorizacion as IAutorizacionDatosGeneralesData),
      switchMap((autorizacionData) => {
        this.autorizacionData = autorizacionData;
        this.disableCambioEstado$.next(!autorizacionData.tituloProyecto
          || !autorizacionData.responsable
          || !autorizacionData.entidad);
        if (autorizacionData.responsable?.id) {
          return this.personaService.findById(autorizacionData.responsable?.id).pipe(
            map(responsable => {
              autorizacionData.responsable = responsable;
              return autorizacionData;
            })
          );
        } else {
          return of(autorizacionData);
        }
      }),
      switchMap(autorizacionData => {
        if (autorizacionData.convocatoria?.id) {
          const convocatoria$ = this.isInvestigador ?
            this.autorizacionService.findConvocatoria(autorizacionData.id)
            : this.convocatoriaService.findById(autorizacionData.convocatoria.id);
          return convocatoria$.pipe(
            map(convocatoria => {
              autorizacionData.convocatoria = convocatoria;
              return autorizacionData;
            })
          );
        } else {
          return of(autorizacionData);
        }
      }),
      switchMap(autorizacionData => {
        if (autorizacionData.entidad?.id) {
          return this.empresaService.findById(autorizacionData.entidad.id).pipe(
            map(entidad => {
              autorizacionData.entidad = entidad;
              return autorizacionData;
            })
          );
        } else {
          return of(autorizacionData);
        }
      }),
      switchMap(autorizacionData => {
        if (autorizacionData.estado.id) {
          return this.estadoAutorizacionService.findById(autorizacionData.estado.id).pipe(
            map(estado => {
              autorizacionData.estado = estado;
              if (this.isEdit()) {
                this.disableNotEditableFieldsEstado(this.getFormGroup(), this.autorizacionData);
              }
              return autorizacionData;
            })
          );
        } else {
          return of(autorizacionData);
        }
      }),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      }),
      switchMap(autorizacionData => {
        if (autorizacionData?.solicitante?.id) {
          return this.personaService.findById(autorizacionData?.solicitante?.id).pipe(
            map((persona) => {
              autorizacionData.solicitante = persona;
              return autorizacionData;
            }),
            catchError((error) => {
              this.logger.error(error);
              return EMPTY;
            }));
        } else {
          return of(autorizacionData);
        }
      }),
      switchMap(autorizacionData => {
        if (autorizacionData.id) {
          return this.autorizacionService.findFirstEstado(autorizacionData.id).pipe(
            map(firstEstado => {
              autorizacionData.fechaFirstEstado = firstEstado.fecha;
              return autorizacionData;
            })
          );
        }
        return of(autorizacionData);
      }),
    );
  }

  getValue(): IAutorizacionDatosGeneralesData {
    const form = this.getFormGroup().controls;
    this.autorizacionData.tituloProyecto = form.tituloProyecto.value;
    this.autorizacionData.convocatoria = form.convocatoria?.value;
    this.autorizacionData.datosConvocatoria = form.datosConvocatoria?.value;
    this.autorizacionData.entidad = form.entidadParticipa.value;
    this.autorizacionData.datosEntidad = form.datosEntidad.value;
    this.autorizacionData.responsable = form.investigadorPrincipalProyecto.value;
    this.autorizacionData.datosResponsable = form.datosIpProyecto.value;
    this.autorizacionData.horasDedicacion = form.horasDedicacion?.value;
    this.autorizacionData.observaciones = form.observaciones?.value;
    this.autorizacionData.fechaFirstEstado = form.fechaSolicitud?.value;

    return this.autorizacionData;
  }

  saveOrUpdate(): Observable<number> {
    const autorizacionData = this.getValue();
    const observable$ = this.isEdit() ? this.update(autorizacionData as IAutorizacion) :
      this.create(autorizacionData as IAutorizacion);
    return observable$.pipe(
      map(value => {
        this.autorizacionData.id = value.id;
        this.disableCambioEstado$.next(!value.tituloProyecto
          || !value.responsable
          || !value.entidad);
        return this.autorizacionData.id;
      })
    );
  }

  private create(autorizacion: IAutorizacion): Observable<IAutorizacion> {
    return this.autorizacionService.create(autorizacion).pipe(
      tap(result => this.autorizacionData = result as IAutorizacionDatosGeneralesData));
  }

  private update(autorizacion: IAutorizacion): Observable<IAutorizacion> {
    return this.autorizacionService.update(autorizacion.id, autorizacion).pipe(
      tap(result => { this.autorizacionData = result as IAutorizacionDatosGeneralesData; }));

  }

  private setConditionalValidatorsEntidad(form: FormGroup): void {
    const entidadParticipaControl = form.controls.entidadParticipa;
    const datosEntidadControl = form.controls.datosEntidad;
    const entidadParticipa = entidadParticipaControl.value;
    if (entidadParticipa) {
      datosEntidadControl.disable({ emitEvent: false });
      if (this.isInvestigador) {
        datosEntidadControl.setValue(null, { emitEvent: false });
      }
      entidadParticipaControl.setValidators(Validators.required);
    } else {
      entidadParticipaControl.clearValidators();
      datosEntidadControl.enable({ emitEvent: false });
      this.disableNotEditableFieldsEstado(form, this.autorizacionData);
    }

    const datosEntidad = datosEntidadControl.value;
    if (datosEntidad) {
      entidadParticipaControl.clearValidators();
      datosEntidadControl.setValue(datosEntidad, { emitEvent: false });
    } else {
      entidadParticipaControl.setValidators(Validators.required);
    }

    entidadParticipaControl.updateValueAndValidity({ emitEvent: false });
    datosEntidadControl.updateValueAndValidity({ emitEvent: false });

    this.entidadRequired = entidadParticipa;
  }

  private setConditionalValidatorsIP(form: FormGroup): void {
    const investigadorPrincipalControl = form.controls.investigadorPrincipalProyecto;
    const datosInvestigadorPrincipalControl = form.controls.datosIpProyecto;
    const investigadorPrincipal = investigadorPrincipalControl.value;
    if (investigadorPrincipal) {
      datosInvestigadorPrincipalControl.disable({ emitEvent: false });
      if (this.isInvestigador) {
        datosInvestigadorPrincipalControl.setValue(null, { emitEvent: false });
      }
      datosInvestigadorPrincipalControl.setValidators(Validators.required);
    } else {
      investigadorPrincipalControl.clearValidators();
      datosInvestigadorPrincipalControl.enable({ emitEvent: false });
      this.disableNotEditableFieldsEstado(form, this.autorizacionData);
    }

    const datosInvestigadorPrincipal = datosInvestigadorPrincipalControl.value;
    if (datosInvestigadorPrincipal) {
      investigadorPrincipalControl.clearValidators();
      datosInvestigadorPrincipalControl.setValue(datosInvestigadorPrincipal, { emitEvent: false });
    } else {
      investigadorPrincipalControl.setValidators(Validators.required);
    }

    investigadorPrincipalControl.updateValueAndValidity({ emitEvent: false });
    datosInvestigadorPrincipalControl.updateValueAndValidity({ emitEvent: false });

    this.investigadorRequired = investigadorPrincipal;

  }

  private disableNotEditableFieldsEstado(formgroup: FormGroup, autorizacion: IAutorizacionDatosGeneralesData): void {
    if (autorizacion?.estado?.estado
      && (autorizacion?.estado?.estado !== Estado.BORRADOR && this.isInvestigador)
      || (!this.isInvestigador && autorizacion?.estado?.estado === Estado.AUTORIZADA) || this.isVisor) {
      formgroup.controls.tituloProyecto.disable();
      formgroup.controls.convocatoria.disable({ emitEvent: false });
      formgroup.controls.datosConvocatoria.disable({ emitEvent: false });
      formgroup.controls.entidadParticipa.disable({ emitEvent: false });
      formgroup.controls.datosEntidad.disable({ emitEvent: false });
      formgroup.controls.investigadorPrincipalProyecto.disable({ emitEvent: false });
      formgroup.controls.datosIpProyecto.disable({ emitEvent: false });
      formgroup.controls.horasDedicacion.disable();
      formgroup.controls.observaciones.disable();
    } else {
      formgroup.controls.tituloProyecto.enable();
      formgroup.controls.convocatoria.enable({ emitEvent: false });
      formgroup.controls.datosConvocatoria.enable({ emitEvent: false });
      formgroup.controls.entidadParticipa.enable({ emitEvent: false });
      formgroup.controls.datosEntidad.enable({ emitEvent: false });
      formgroup.controls.investigadorPrincipalProyecto.enable({ emitEvent: false });
      formgroup.controls.datosIpProyecto.enable({ emitEvent: false });
      formgroup.controls.horasDedicacion.enable();
      formgroup.controls.observaciones.enable();
    }
    if (!this.isInvestigador) {
      formgroup.controls.datosEntidad.disable({ emitEvent: false });
    }
  }

  reload(): void {
    this.initializer(this.getKey()).subscribe((initialValue) => {
      this.getFormGroup().patchValue(this.buildPatch(initialValue));
      this.refreshInitialState(true);
    });
  }
}
