import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { Estado } from '@core/models/csp/estado-autorizacion';
import { FormFragment } from '@core/services/action-service';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { EstadoAutorizacionService } from '@core/services/csp/estado-autorizacion/estado-autorizacion.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, merge, Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

export class AutorizacionDatosGeneralesFragment extends FormFragment<IAutorizacion> {

  private autorizacion: IAutorizacion;

  public investigadorRequired: boolean;
  public entidadRequired: boolean;
  public isInvestigador: boolean;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private autorizacionService: AutorizacionService,
    private personaService: PersonaService,
    private empresaService: EmpresaService,
    private estadoAutorizacionService: EstadoAutorizacionService,
    private convocatoriaService: ConvocatoriaService,
    public authService: SgiAuthService
  ) {
    super(key, true);
    this.setComplete(true);
    this.autorizacion = {} as IAutorizacion;
    this.isInvestigador = this.authService.hasAnyAuthority(['CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR']);
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      estado: new FormControl({ value: null, disabled: true }, Validators.required),
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
        if (this.autorizacion?.estado?.estado === 'BORRADOR' || !this.isEdit()) {
          if (convocatoria) {
            form.controls.datosConvocatoria.disable();
            form.controls.datosConvocatoria.setValue(null);
          } else {
            form.controls.datosConvocatoria.setValue(null);
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

  buildPatch(autorizacion: IAutorizacion): { [key: string]: any } {
    this.autorizacion = autorizacion;
    return {
      estado: autorizacion.estado?.estado,
      tituloProyecto: autorizacion.tituloProyecto,
      convocatoria: autorizacion.convocatoria,
      datosConvocatoria: autorizacion.datosConvocatoria,
      entidadParticipa: autorizacion.entidad,
      datosEntidad: autorizacion.datosEntidad,
      investigadorPrincipalProyecto: autorizacion.responsable,
      datosIpProyecto: autorizacion.datosResponsable,
      horasDedicacion: autorizacion.horasDedicacion,
      observaciones: autorizacion.observaciones
    };
  }

  protected initializer(key: string | number): Observable<IAutorizacion> {
    return this.autorizacionService.findById(key as number).pipe(
      switchMap((autorizacion) => {
        this.autorizacion = autorizacion;
        if (autorizacion.responsable?.id) {
          return this.personaService.findById(autorizacion.responsable?.id).pipe(
            map(responsable => {
              autorizacion.responsable = responsable;
              return autorizacion;
            })
          );
        } else {
          return of(autorizacion);
        }
      }),
      switchMap(autorizacion => {
        if (autorizacion.convocatoria?.id) {
          const convocatoria$ = this.isInvestigador ? this.autorizacionService.findConvocatoria(autorizacion.id) : this.convocatoriaService.findById(autorizacion.convocatoria.id);
          return convocatoria$.pipe(
            map(convocatoria => {
              autorizacion.convocatoria = convocatoria;
              return autorizacion;
            })
          );
        } else {
          return of(autorizacion);
        }
      }),
      switchMap(autorizacion => {
        if (autorizacion.entidad?.id) {
          return this.empresaService.findById(autorizacion.entidad.id).pipe(
            map(entidad => {
              autorizacion.entidad = entidad;
              return autorizacion;
            })
          );
        } else {
          return of(autorizacion);
        }
      }),
      switchMap(autorizacion => {
        if (autorizacion.estado.id) {
          return this.estadoAutorizacionService.findById(autorizacion.estado.id).pipe(
            map(estado => {
              autorizacion.estado = estado;
              if (this.isEdit()) {
                this.disableNotEditableFieldsEstado(this.getFormGroup(), this.autorizacion);
              }
              return autorizacion;
            })
          );
        } else {
          return of(autorizacion);
        }
      }),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  getValue(): IAutorizacion {
    const form = this.getFormGroup().controls;
    this.autorizacion.tituloProyecto = form.tituloProyecto.value;
    this.autorizacion.convocatoria = form.convocatoria?.value;
    this.autorizacion.datosConvocatoria = form.datosConvocatoria?.value;
    this.autorizacion.entidad = form.entidadParticipa.value;
    this.autorizacion.datosEntidad = form.datosEntidad.value;
    this.autorizacion.responsable = form.investigadorPrincipalProyecto.value;
    this.autorizacion.datosResponsable = form.datosIpProyecto.value;
    this.autorizacion.horasDedicacion = form.horasDedicacion?.value;
    this.autorizacion.observaciones = form.observaciones?.value;

    return this.autorizacion;
  }

  saveOrUpdate(): Observable<number> {
    const autorizacion = this.getValue();
    const observable$ = this.isEdit() ? this.update(autorizacion) :
      this.create(autorizacion);
    return observable$.pipe(
      map(value => {
        this.autorizacion.id = value.id;
        return this.autorizacion.id;
      })
    );
  }

  private create(autorizacion: IAutorizacion): Observable<IAutorizacion> {
    return this.autorizacionService.create(autorizacion).pipe(
      tap(result => this.autorizacion = result));
  }

  private update(autorizacion: IAutorizacion): Observable<IAutorizacion> {
    return this.autorizacionService.update(autorizacion.id, autorizacion).pipe(
      tap(result => this.autorizacion = result));
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
      this.disableNotEditableFieldsEstado(form, this.autorizacion);
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
      this.disableNotEditableFieldsEstado(form, this.autorizacion);
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

  private disableNotEditableFieldsEstado(formgroup: FormGroup, autorizacion: IAutorizacion): void {
    if (autorizacion?.estado?.estado
      && (autorizacion?.estado?.estado !== Estado.BORRADOR && this.isInvestigador)
      || (!this.isInvestigador && autorizacion?.estado?.estado === Estado.AUTORIZADA)) {
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

}
