import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { Estado } from '@core/models/csp/estado-solicitud';
import { IPrograma } from '@core/models/csp/programa';
import { ISolicitud, OrigenSolicitud, TipoSolicitudGrupo } from '@core/models/csp/solicitud';
import { ISolicitudGrupo } from '@core/models/csp/solicitud-grupo';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IPersona } from '@core/models/sgp/persona';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudGrupoService } from '@core/services/csp/solicitud-grupo/solicitud-grupo.service';
import { SolicitudModalidadService } from '@core/services/csp/solicitud-modalidad.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { anioValidator } from '@core/validators/anio-validator';
import { I18nValidators } from '@core/validators/i18n-validator';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, Observable, Subject, from, merge, of } from 'rxjs';
import { catchError, filter, map, mergeMap, pairwise, startWith, switchMap, takeLast, tap } from 'rxjs/operators';

export interface SolicitudModalidadEntidadConvocanteListado {
  entidadConvocante: IConvocatoriaEntidadConvocante;
  plan: IPrograma;
  modalidad: StatusWrapper<ISolicitudModalidad>;
}

export interface SolicitudDatosGenerales extends ISolicitud {
  convocatoria: IConvocatoria;
}

export class SolicitudDatosGeneralesFragment extends FormFragment<ISolicitud> {

  public solicitud: ISolicitud;
  private solicitudGrupo: ISolicitudGrupo;
  public solicitanteRef: string;

  entidadesConvocantes = [] as IConvocatoriaEntidadConvocante[];

  entidadesConvocantesModalidad$ = new BehaviorSubject<SolicitudModalidadEntidadConvocanteListado[]>([]);
  origenSolicitud$ = new BehaviorSubject<OrigenSolicitud>(null);
  hasDocumentosOrHitos$ = new Subject<boolean>();

  public showComentariosEstado$ = new BehaviorSubject<boolean>(false);
  public convocatoria$: Subject<IConvocatoria> = new BehaviorSubject(null);

  tipoFormularioSolicitud: FormularioSolicitud;
  hasDocumentosOrHitos: boolean;
  hasEstadoUnidadGestionModificable: boolean;
  showInfoUnidadGestionDisabledByEstadoUO: boolean;

  get isSolicitanteRequired(): boolean {
    return [FormularioSolicitud.GRUPO, FormularioSolicitud.PROYECTO].includes(this.tipoFormularioSolicitud);
  }

  private get isOrigenSolicitudConvocatoriaSGI(): boolean {
    if (this.isInvestigador) {
      return !!this.getFormGroup().controls.convocatoria?.value;
    }
    return this.getFormGroup().controls.origenSolicitud.value === OrigenSolicitud.CONVOCATORIA_SGI;
  }

  private get isOrigenSolicitudSinConvocatoria(): boolean {
    if (this.isInvestigador) {
      return !this.getFormGroup().controls.convocatoria?.value && !this.getFormGroup().controls.codigoExterno?.value;
    }
    return this.getFormGroup().controls.origenSolicitud.value === OrigenSolicitud.SIN_CONVOCATORIA;
  }

  isOrigenSolicitudSinConvocatoria$ = new BehaviorSubject<boolean>(false);
  isOrigenSolicitudConvocatoriaNoSGI$ = new BehaviorSubject<boolean>(false);
  isOrigenSolicitudConvocatoriaSGI$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private service: SolicitudService,
    private convocatoriaService: ConvocatoriaService,
    private empresaService: EmpresaService,
    private personaService: PersonaService,
    private solicitudModalidadService: SolicitudModalidadService,
    private unidadGestionService: UnidadGestionService,
    private solicitudGrupoService: SolicitudGrupoService,
    private authService: SgiAuthService,
    public readonly: boolean,
    public isInvestigador: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.solicitud = { activo: true } as ISolicitud;
    this.hasDocumentosOrHitos = false;
  }

  protected initializer(key: number): Observable<SolicitudDatosGenerales> {

    this.subscriptions.push(
      this.hasDocumentosOrHitos$.subscribe(hasDocumentosOrHitos => {
        this.hasDocumentosOrHitos = hasDocumentosOrHitos;
        this.updateDisabledControlsWhenHasDocumentosOrHitos();
      })
    );

    return this.service.findById(key).pipe(
      map(solicitud => {
        return solicitud as SolicitudDatosGenerales;
      }),
      switchMap((solicitud) => {
        if (!solicitud.solicitante?.id) {
          return of(solicitud);
        }

        return this.personaService.findById(solicitud.solicitante.id).pipe(
          map(solicitente => {
            solicitud.solicitante = solicitente;
            return solicitud;
          }),
          catchError((err) => {
            this.logger.error(err);
            return of(solicitud);
          })
        );
      }),
      switchMap((solicitud) => {
        return this.getUnidadGestion(solicitud.unidadGestion.id).pipe(
          map(unidadGestion => {
            solicitud.unidadGestion = unidadGestion;
            return solicitud;
          })
        );
      }),
      switchMap((solicitud) => {
        if (solicitud.convocatoriaId) {
          const convocatoriaSolicitud$ = this.isInvestigador ?
            this.service.findConvocatoria(solicitud.id) : this.convocatoriaService.findById(solicitud.convocatoriaId);
          return convocatoriaSolicitud$.pipe(
            switchMap(convocatoria => {
              return this.loadEntidadesConvocantesModalidad(solicitud.id, convocatoria.id).pipe(
                map(entidadesConvocantesListado => {
                  solicitud.convocatoria = convocatoria;
                  this.entidadesConvocantesModalidad$.next(entidadesConvocantesListado);
                  return solicitud;
                })
              );
            })
          );
        }
        return of(solicitud);
      }),
      switchMap(solicitud => {
        if (solicitud.tipoSolicitudGrupo === TipoSolicitudGrupo.MODIFICACION) {
          return this.service.findSolicitudGrupo(solicitud.id).pipe(
            tap(solicitudGrupo => this.solicitudGrupo = solicitudGrupo),
            map(() => solicitud)
          );
        } else {
          return of(solicitud);
        }
      }),
      switchMap((solicitud) => {
        return this.service.hasDocumentosOrHitos(solicitud.id).pipe(
          map(hasDocumentosOrHitos => {
            this.hasDocumentosOrHitos = hasDocumentosOrHitos;
            return solicitud;
          })
        );
      }),
      map((solicitud) => {
        this.origenSolicitud$.next(solicitud.origenSolicitud);
        this.showComentariosEstado$.next(solicitud.estado.comentario != null && solicitud.estado.comentario.length > 0);
        return solicitud;
      }),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  protected buildFormGroup(): FormGroup {
    let form: FormGroup;
    this.tipoFormularioSolicitud = FormularioSolicitud.PROYECTO;

    if (this.isInvestigador) {
      form = new FormGroup({
        estado: new FormControl({ value: Estado.BORRADOR, disabled: true }),
        titulo: new FormControl({ value: [], disabled: this.isEdit() }, [I18nValidators.maxLength(250)]),
        convocatoria: new FormControl({ value: '', disabled: true }),
        codigoRegistro: new FormControl({ value: '', disabled: true }),
        codigoExterno: new FormControl('', Validators.maxLength(50)),
        observaciones: new FormControl({ value: [], disabled: this.isEdit() }, I18nValidators.maxLength(2000)),
        comentariosEstado: new FormControl({ value: [], disabled: true }),
        tipoSolicitudGrupo: new FormControl({ value: null, disabled: true }, Validators.required),
        grupo: new FormControl({ value: null, disabled: true }, Validators.required),
        formularioSolicitud: new FormControl({ value: this.tipoFormularioSolicitud, disabled: true }, Validators.required),
        unidadGestion: new FormControl(null, Validators.required),
        modeloEjecucion: new FormControl(null),
        finalidad: new FormControl(null),
      });

      if (!this.readonly) {
        this.initValueChangesSubscriptionsInvestigador(form);
      }

    } else {
      form = new FormGroup({
        estado: new FormControl({ value: Estado.BORRADOR, disabled: true }),
        titulo: new FormControl([], [I18nValidators.maxLength(250)]),
        solicitante: new FormControl('', Validators.required),
        convocatoria: new FormControl({ value: '', disabled: this.isEdit() }),
        comentariosEstado: new FormControl({ value: [], disabled: true }),
        convocatoriaExterna: new FormControl(''),
        formularioSolicitud: new FormControl({ value: this.tipoFormularioSolicitud, disabled: this.isEdit() }, Validators.required),
        tipoSolicitudGrupo: new FormControl({ value: null, disabled: true }, Validators.required),
        grupo: new FormControl({ value: null, disabled: true }, Validators.required),
        unidadGestion: new FormControl(null, Validators.required),
        codigoExterno: new FormControl('', Validators.maxLength(50)),
        codigoRegistro: new FormControl({ value: '', disabled: true }),
        observaciones: new FormControl([], I18nValidators.maxLength(2000)),
        anio: new FormControl(this.isEdit() ? null : this.getCurrentYear(), anioValidator()),
        modeloEjecucion: new FormControl(null),
        finalidad: new FormControl(null),
        origenSolicitud: new FormControl({ value: this.isEdit() ? null : OrigenSolicitud.CONVOCATORIA_SGI, disabled: this.isEdit() }, Validators.required)
      });

      if (!this.readonly) {
        this.initValueChangesSubscriptionsUO(form);
      }
    }

    if (this.readonly) {
      form.disable();
    }
    return form;
  }

  buildPatch(solicitud: SolicitudDatosGenerales): { [key: string]: any } {
    this.solicitud = solicitud;
    this.solicitanteRef = solicitud.solicitante?.id;
    this.tipoFormularioSolicitud = solicitud.formularioSolicitud;

    let formValues: { [key: string]: any } = {
      estado: solicitud?.estado?.estado,
      titulo: solicitud?.titulo ?? [],
      convocatoria: solicitud?.convocatoria,
      codigoRegistro: solicitud.codigoRegistroInterno,
      codigoExterno: solicitud?.codigoExterno,
      observaciones: solicitud?.observaciones ?? [],
      comentariosEstado: solicitud?.estado?.comentario,
      tipoSolicitudGrupo: solicitud.tipoSolicitudGrupo,
      grupo: this.solicitudGrupo?.grupo,
      anio: this.isEdit() ? solicitud?.anio : solicitud?.convocatoria?.anio,
      formularioSolicitud: solicitud.formularioSolicitud,
      unidadGestion: solicitud.unidadGestion,
      modeloEjecucion: solicitud.modeloEjecucion,
      finalidad: solicitud.tipoFinalidad
    };

    if (!this.isInvestigador) {
      formValues = {
        ...formValues,
        origenSolicitud: solicitud.origenSolicitud,
        convocatoriaExterna: solicitud.convocatoriaExterna,
        solicitante: solicitud.solicitante
      };

      if (!this.isSolicitanteRequired) {
        this.getFormGroup().controls.solicitante.disable();
      }
    }

    if (!this.readonly && solicitud?.estado?.estado === Estado.BORRADOR) {
      this.getFormGroup().controls.titulo.enable();
      this.getFormGroup().controls.codigoExterno.enable();
      this.getFormGroup().controls.observaciones.enable();

      if (solicitud.tipoSolicitudGrupo === TipoSolicitudGrupo.MODIFICACION) {
        this.getFormGroup().controls.grupo.enable();
      }
    } else if (!this.readonly && solicitud?.estado?.estado === Estado.RECHAZADA
      || (!this.readonly && solicitud?.estado?.estado === Estado.SUBSANACION
        && solicitud.origenSolicitud === OrigenSolicitud.SIN_CONVOCATORIA
        && solicitud.formularioSolicitud === FormularioSolicitud.PROYECTO)) {
      this.getFormGroup().controls.observaciones.enable();
    }

    return formValues;
  }

  getValue(): ISolicitud {
    const form = this.getFormGroup().controls;
    if (this.isInvestigador) {
      if (!this.solicitud.solicitante) {
        this.solicitud.solicitante = {
          id: this.solicitanteRef ?? this.authService.authStatus$?.getValue()?.userRefId
        } as IPersona;
      }

      this.solicitud.titulo = form.titulo.value;
      this.solicitud.observaciones = form.observaciones.value;
      this.solicitud.codigoExterno = form.codigoExterno.value;
      this.solicitud.tipoSolicitudGrupo = form.tipoSolicitudGrupo.value;
      this.solicitud.anio = this.getCurrentYear();
      if (this.isOrigenSolicitudSinConvocatoria) {
        this.solicitud.formularioSolicitud = FormularioSolicitud.PROYECTO;
        this.solicitud.origenSolicitud = OrigenSolicitud.SIN_CONVOCATORIA;
      } else if (this.isOrigenSolicitudConvocatoriaSGI) {
        this.solicitud.formularioSolicitud = form.formularioSolicitud.value;
        this.solicitud.origenSolicitud = OrigenSolicitud.CONVOCATORIA_SGI;
      }
      this.solicitud.unidadGestion = form.unidadGestion.value;
      this.solicitud.modeloEjecucion = form.modeloEjecucion.value;
      this.solicitud.tipoFinalidad = form.finalidad.value;
    } else {
      this.solicitud.solicitante = form.solicitante.value;
      this.solicitud.titulo = form.titulo.value;
      this.solicitud.convocatoriaId = form.convocatoria.value?.id;
      this.solicitud.convocatoriaExterna = form.convocatoriaExterna.value;
      this.solicitud.formularioSolicitud = form.formularioSolicitud.value;
      this.solicitud.tipoSolicitudGrupo = form.tipoSolicitudGrupo.value;
      this.solicitud.unidadGestion = form.unidadGestion.value;
      this.solicitud.codigoExterno = form.codigoExterno.value;
      this.solicitud.observaciones = form.observaciones.value;
      this.solicitud.anio = form.anio.value;
      this.solicitud.origenSolicitud = form.origenSolicitud.value;
      this.solicitud.modeloEjecucion = form.modeloEjecucion.value;
      this.solicitud.tipoFinalidad = form.finalidad.value;
    }

    return this.solicitud;
  }

  saveOrUpdate(): Observable<number> {
    const datosGenerales = this.getValue();

    return this.saveOrUpdateSolicitud(datosGenerales);

  }

  saveOrUpdateSolicitud(datosGenerales: ISolicitud): Observable<number> {

    const obs = this.isEdit() ? this.service.update(datosGenerales.id, datosGenerales) :
      this.service.create(datosGenerales);
    return obs.pipe(
      tap((value) => {
        this.solicitud = value;
      }),
      switchMap((solicitud) => {
        return merge(
          this.saveOrUpdateSolicitudGrupo(solicitud),
          this.createSolicitudModalidades(solicitud.id),
          this.deleteSolicitudModalidades(),
          this.updateSolicitudModalidades()
        );
      }),
      takeLast(1),
      map(() => {
        return this.solicitud.id;
      })
    );
  }

  saveOrUpdateSolicitudGrupo(solicitud: ISolicitud): Observable<ISolicitud> {
    if (solicitud.tipoSolicitudGrupo === TipoSolicitudGrupo.MODIFICACION) {
      if (!this.solicitudGrupo?.id) {
        this.solicitudGrupo = {
          solicitud
        } as ISolicitudGrupo;
      }

      this.solicitudGrupo.grupo = this.getFormGroup().controls.grupo.value;

      return this.solicitudGrupo.id
        ? this.solicitudGrupoService.update(this.solicitudGrupo.id, this.solicitudGrupo).pipe(map(() => solicitud))
        : this.solicitudGrupoService.create(this.solicitudGrupo).pipe(map(() => solicitud));
    } else {
      return of(solicitud);
    }
  }

  setOrigenSolicitud(origenSolicitud: OrigenSolicitud): void {
    this.origenSolicitud$.next(origenSolicitud);
  }

  setDatosConvocatoria(convocatoria: IConvocatoria) {
    this.subscriptions.push(
      this.loadEntidadesConvocantesModalidad(this.getValue().id, convocatoria.id).subscribe(entidadesConvocantes => {
        this.entidadesConvocantesModalidad$.next(entidadesConvocantes);
      })
    );

    this.solicitud.convocatoriaId = convocatoria.id;
    this.solicitud.origenSolicitud = OrigenSolicitud.CONVOCATORIA_SGI;
    this.tipoFormularioSolicitud = convocatoria.formularioSolicitud;
    this.solicitud.convocatoriaExterna = convocatoria.codigo;
    this.solicitanteRef = this.authService.authStatus$?.getValue()?.userRefId;
    this.solicitud.solicitante = {
      id: this.solicitanteRef
    } as IPersona;

    this.convocatoria$.next(convocatoria);
    this.origenSolicitud$.next(OrigenSolicitud.CONVOCATORIA_SGI);

    this.getFormGroup().controls.convocatoria.setValue(convocatoria);
    this.getFormGroup().controls.unidadGestion.setValue(convocatoria.unidadGestion);
    this.getFormGroup().controls.modeloEjecucion.setValue(convocatoria.modeloEjecucion);
    this.getFormGroup().controls.finalidad.setValue(convocatoria.finalidad);
    this.getFormGroup().controls.formularioSolicitud.setValue(convocatoria.formularioSolicitud);

    if (this.tipoFormularioSolicitud === FormularioSolicitud.GRUPO) {
      this.getFormGroup().controls.tipoSolicitudGrupo.enable();
    }
  }

  /**
   * Añada la solicitudModalidad y la marca como creada
   *
   * @param solicitudModalidad ISolicitudModalidad
   */
  public addSolicitudModalidad(solicitudModalidad: ISolicitudModalidad, entidadConvocanteProgramaId: number): void {
    const current = this.entidadesConvocantesModalidad$.value;
    const index = current.findIndex(value =>
      value.entidadConvocante.entidad.id === solicitudModalidad.entidad.id
      && value.entidadConvocante.programa.id === entidadConvocanteProgramaId
    );
    if (index >= 0) {
      const wrapper = new StatusWrapper(solicitudModalidad);
      current[index].modalidad = wrapper;
      wrapper.setCreated();
      this.setChanges(true);
    }
  }

  /**
   * Actualiza la solicitudModalidad y la marca como editada
   *
   * @param solicitudModalidad ISolicitudModalidad
   */
  public updateSolicitudModalidad(solicitudModalidad: ISolicitudModalidad): void {
    const current = this.entidadesConvocantesModalidad$.value;
    const index = current.findIndex(value => value.modalidad && value.modalidad.value === solicitudModalidad);
    if (index >= 0) {
      current[index].modalidad.value.programa = solicitudModalidad.programa;
      current[index].modalidad.setEdited();
      this.setChanges(true);
    }
  }

  /**
   * Elimina la solicitudModalidad y la marca como eliminada si era una modalidad que ya existia previamente.
   *
   * @param wrapper ISolicitudModalidad
   */
  public deleteSolicitudModalidad(wrapper: StatusWrapper<ISolicitudModalidad>): void {
    const current = this.entidadesConvocantesModalidad$.value;
    const index = current.findIndex(value => value.modalidad && value.modalidad.value === wrapper.value);
    if (index >= 0) {
      if (wrapper.created) {
        current[index].modalidad = undefined;
      } else {
        wrapper.value.programa = undefined;
        wrapper.setDeleted();
        this.setChanges(true);
      }
    }
  }

  private initValueChangesSubscriptionsInvestigador(form: FormGroup): void {
    this.subscriptions.push(
      this.initialized$.pipe(
        filter(initialized => initialized),
        switchMap(() => this.origenSolicitud$)
      ).subscribe(origenSolicitud => {
        this.initOrigenSolicitudSubscription(origenSolicitud);
        this.setConditionalValidatorsInvestigador(form);
      })
    );

    this.subscriptions.push(
      form.controls.convocatoria.valueChanges.subscribe(
        (convocatoria) => {
          this.convocatoria$.next(convocatoria);
        }
      )
    );

    this.initTipoSolicitudGrupoValueChangesSubscription(form);
  }

  private initValueChangesSubscriptionsUO(form: FormGroup): void {
    const origenSolicitud = form.controls.origenSolicitud.value;
    this.initOrigenSolicitudSubscription(origenSolicitud);

    if (!this.isEdit()) {
      this.origenSolicitud$.next(origenSolicitud);
      this.setConditionalValidatorsUO(form);
      this.onOrigenSolicitudChange(origenSolicitud, form);
    }

    this.subscriptions.push(
      form.controls.origenSolicitud.valueChanges.pipe(
        startWith(origenSolicitud),
        pairwise()
      ).subscribe(([prevOrigenSolicitud, newOrigenSolicitud]) => {
        if (prevOrigenSolicitud !== newOrigenSolicitud) {
          this.origenSolicitud$.next(newOrigenSolicitud);
          this.setConditionalValidatorsUO(form);

          if (!!prevOrigenSolicitud) {
            this.onOrigenSolicitudChange(newOrigenSolicitud, form);
          }
        }
      })
    );

    this.subscriptions.push(
      form.controls.convocatoria.valueChanges.subscribe(
        (convocatoria) => {
          this.onConvocatoriaChange(convocatoria);
          this.convocatoria$.next(convocatoria);
        }
      )
    );

    this.subscriptions.push(
      form.controls.formularioSolicitud.valueChanges.subscribe(
        (tipoFormulario) => {
          this.tipoFormularioSolicitud = tipoFormulario;

          if (tipoFormulario === FormularioSolicitud.GRUPO) {
            if (!this.isEdit()) {
              form.controls.tipoSolicitudGrupo.enable();
            }
          } else {
            form.controls.tipoSolicitudGrupo.setValue(null);
            form.controls.tipoSolicitudGrupo.disable();
          }

          if (this.isSolicitanteRequired) {
            Promise.resolve().then(() => this.getFormGroup().controls.solicitante.enable());
          } else {
            Promise.resolve().then(() => this.getFormGroup().controls.solicitante.disable());
          }
        }
      )
    );

    this.subscriptions.push(
      form.controls.solicitante.valueChanges.pipe(
        filter(() => !form.controls.solicitante.disabled)
      ).subscribe(
        (solicitante) => {
          this.solicitanteRef = solicitante?.id;

          if (solicitante && form.controls.tipoSolicitudGrupo.value === TipoSolicitudGrupo.MODIFICACION) {
            if (form.controls.solicitante.value?.id !== solicitante.id) {
              this.getFormGroup().controls.grupo.setValue(null);
            }

            form.controls.grupo.enable();
          } else {
            form.controls.grupo.setValue(null);
            form.controls.grupo.disable();
          }
        }
      )
    );

    this.initTipoSolicitudGrupoValueChangesSubscription(form);
  }

  private initTipoSolicitudGrupoValueChangesSubscription(form: FormGroup): void {
    this.subscriptions.push(
      form.controls.tipoSolicitudGrupo.valueChanges.pipe(
        filter(() => !form.controls.tipoSolicitudGrupo.disabled)
      ).subscribe(
        (tipoSolicitudGrupo) => {
          if (tipoSolicitudGrupo === TipoSolicitudGrupo.MODIFICACION && !!this.solicitanteRef) {
            form.controls.grupo.enable();
          } else {
            form.controls.grupo.setValue(null);
            form.controls.grupo.disable();
          }
        }
      )
    );
  }

  private initOrigenSolicitudSubscription(initialOrigenSolicitud: OrigenSolicitud): void {
    this.subscriptions.push(
      this.origenSolicitud$.pipe(
        startWith(initialOrigenSolicitud)
      ).subscribe(origenSolicitud => {
        this.isOrigenSolicitudSinConvocatoria$.next(origenSolicitud === OrigenSolicitud.SIN_CONVOCATORIA);
        this.isOrigenSolicitudConvocatoriaSGI$.next(origenSolicitud === OrigenSolicitud.CONVOCATORIA_SGI);
        this.isOrigenSolicitudConvocatoriaNoSGI$.next(origenSolicitud === OrigenSolicitud.CONVOCATORIA_NO_SGI);
      })
    );
  }

  /**
   * Crea las modalidades añadidas.
   *
   * @param solicitudId id de la solicitud
   */
  private createSolicitudModalidades(solicitudId: number): Observable<void> {
    const createdSolicitudModalidades = this.entidadesConvocantesModalidad$.value
      .filter((entidadConvocanteModalidad) => !!entidadConvocanteModalidad.modalidad && entidadConvocanteModalidad.modalidad.created)
      .map(entidadConvocanteModalidad => {
        entidadConvocanteModalidad.modalidad.value.solicitudId = solicitudId;
        entidadConvocanteModalidad.modalidad.value.programaConvocatoriaId = entidadConvocanteModalidad.entidadConvocante.programa.id;
        return entidadConvocanteModalidad.modalidad;
      });

    if (createdSolicitudModalidades.length === 0) {
      return of(void 0);
    }

    return from(createdSolicitudModalidades).pipe(
      mergeMap((wrappedSolicitudModalidad) => {
        return this.solicitudModalidadService.create(wrappedSolicitudModalidad.value).pipe(
          map((createdSolicitudModalidad) => {
            const index = this.entidadesConvocantesModalidad$.value
              .findIndex((currentEntidadConvocanteModalidad) =>
                currentEntidadConvocanteModalidad.modalidad === wrappedSolicitudModalidad);

            this.entidadesConvocantesModalidad$.value[index].modalidad =
              new StatusWrapper<ISolicitudModalidad>(createdSolicitudModalidad);
          })
        );
      }));
  }

  /**
   * Elimina las modalidades borradas.
   *
   * @param solicitudId id de la solicitud
   */
  private deleteSolicitudModalidades(): Observable<void> {
    const deletedSolicitudModalidades = this.entidadesConvocantesModalidad$.value
      .filter(entidadConvocanteModalidad => !!entidadConvocanteModalidad.modalidad && entidadConvocanteModalidad.modalidad.deleted)
      .map(entidadConvocanteModalidad => entidadConvocanteModalidad.modalidad);

    if (deletedSolicitudModalidades.length === 0) {
      return of(void 0);
    }

    return from(deletedSolicitudModalidades).pipe(
      mergeMap((wrappedSolicitudModalidad) => {
        return this.solicitudModalidadService.deleteById(wrappedSolicitudModalidad.value.id).pipe(
          map(() => {
            const index = this.entidadesConvocantesModalidad$.value
              .findIndex((currentEntidadConvocanteModalidad) =>
                currentEntidadConvocanteModalidad.modalidad === wrappedSolicitudModalidad);

            this.entidadesConvocantesModalidad$.value[index].modalidad = undefined;
          })
        );
      })
    );
  }

  /**
   * Actualiza las modalidades modificadas.
   *
   * @param solicitudId id de la solicitud
   */
  private updateSolicitudModalidades(): Observable<void> {
    const updatedSolicitudModalidades = this.entidadesConvocantesModalidad$.value
      .filter(entidadConvocanteModalidad => !!entidadConvocanteModalidad.modalidad && entidadConvocanteModalidad.modalidad.edited)
      .map(entidadConvocanteModalidad => entidadConvocanteModalidad.modalidad);

    if (updatedSolicitudModalidades.length === 0) {
      return of(void 0);
    }

    return from(updatedSolicitudModalidades).pipe(
      mergeMap((wrappedSolicitudModalidad) => {
        return this.solicitudModalidadService.update(wrappedSolicitudModalidad.value.id, wrappedSolicitudModalidad.value).pipe(
          map((updatedSolicitudModalidad) => {
            const index = this.entidadesConvocantesModalidad$.value
              .findIndex((currentEntidadConvocanteModalidad) =>
                currentEntidadConvocanteModalidad.modalidad === wrappedSolicitudModalidad);

            this.entidadesConvocantesModalidad$.value[index].modalidad =
              new StatusWrapper<ISolicitudModalidad>(updatedSolicitudModalidad);
          })
        );
      })
    );
  }

  /**
   * Setea la convocatoria seleccionada en el formulario y los campos que dependende de esta (tipo formulario, unidad gestión y modalidades)
   *
   * @param convocatoria una convocatoria
   */
  private onConvocatoriaChange(convocatoria: IConvocatoria): void {
    if (convocatoria) {
      if (!this.isInvestigador) {
        this.getFormGroup().controls.convocatoriaExterna.setValue('', { emitEvent: false });
      }

      if (this.isOrigenSolicitudConvocatoriaSGI) {
        this.getFormGroup().controls.unidadGestion.setValue(convocatoria.unidadGestion);
        this.getFormGroup().controls.modeloEjecucion.setValue(convocatoria.modeloEjecucion);
        this.getFormGroup().controls.finalidad.setValue(convocatoria.finalidad);
        this.getFormGroup().controls.convocatoriaExterna.setValue(convocatoria.codigo);
      }

      this.subscriptions.push(
        this.convocatoriaService.getFormularioSolicitud(convocatoria.id).subscribe(formularioSolicitud => {
          this.getFormGroup().controls.formularioSolicitud.setValue(formularioSolicitud);
        })
      );

      this.subscriptions.push(
        this.loadEntidadesConvocantesModalidad(this.getValue().id, convocatoria.id)
          .subscribe((entidadesConvocantes) =>
            this.entidadesConvocantesModalidad$.next(entidadesConvocantes)
          )
      );

      if (!this.isEdit() && convocatoria.anio) {
        this.getFormGroup().controls.anio.setValue(convocatoria.anio);
      }
    } else if (!this.isEdit()) {
      // Clean dependencies
      this.getFormGroup().controls.unidadGestion.setValue(null);
      this.getFormGroup().controls.formularioSolicitud.setValue(null);
      this.entidadesConvocantesModalidad$.next([]);

      // Enable fields
      this.getFormGroup().controls.convocatoriaExterna.enable();
      this.getFormGroup().controls.unidadGestion.enable();
      this.getFormGroup().controls.formularioSolicitud.enable();
      this.getFormGroup().controls.anio.setValue(this.getCurrentYear());
    }
  }

  /**
   * Carga los datos de la unidad de gestion en la solicitud
   *
   * @param id Identificador de la unidad de gestion
   * @returns observable para recuperar los datos
   */
  private getUnidadGestion(id: number): Observable<IUnidadGestion> {
    return this.unidadGestionService.findById(id).pipe(
      map(result => {
        if (result) {
          return result;
        }
        return { id } as IUnidadGestion;
      })
    );
  }

  /**
   * Carga los datos de la tabla de modalidades de la solicitud y emite el cambio para que
   * se pueda actualizar la tabla
   *
   * @param solicitudId Identificador de la solicitud
   * @param convocatoriaId Identificador de la convocatoria
   * @returns observable para recuperar los datos
   */
  private loadEntidadesConvocantesModalidad(solicitudId: number, convocatoriaId: number):
    Observable<SolicitudModalidadEntidadConvocanteListado[]> {
    const convocatoriaEntidadConvocantes$ = this.isInvestigador && solicitudId
      ? this.service.findAllConvocatoriaEntidadConvocantes(solicitudId)
      : this.convocatoriaService.findAllConvocatoriaEntidadConvocantes(convocatoriaId);
    return convocatoriaEntidadConvocantes$.pipe(
      map(resultEntidadConvocantes => {
        if (resultEntidadConvocantes.total === 0) {
          return [] as SolicitudModalidadEntidadConvocanteListado[];
        }

        return resultEntidadConvocantes.items.map(entidadConvocante => {
          return {
            entidadConvocante,
            plan: this.getPlan(entidadConvocante.programa)
          } as SolicitudModalidadEntidadConvocanteListado;
        });
      }),
      mergeMap(entidadesConvocantesModalidad => {
        if (entidadesConvocantesModalidad.length === 0) {
          return of([]);
        }
        return from(entidadesConvocantesModalidad).pipe(
          mergeMap((element) => {
            return this.empresaService.findById(element.entidadConvocante.entidad.id).pipe(
              map(empresa => {
                element.entidadConvocante.entidad = empresa;
                element.plan = this.getPlan(element.entidadConvocante.programa);
                return element;
              }),
              catchError((err) => {
                this.logger.error(err);
                return of(element)
              })
            );
          }),
          takeLast(1),
          switchMap(() => of(entidadesConvocantesModalidad))
        );
      }),
      switchMap(entidadesConvocantesModalidad => {
        if (!solicitudId) {
          return of(entidadesConvocantesModalidad);
        }

        return this.getSolicitudModalidades(solicitudId).pipe(
          switchMap(solicitudModalidades => {
            entidadesConvocantesModalidad.forEach(element => {
              const solicitudModalidad = solicitudModalidades
                .find(modalidad => modalidad.entidad.id === element.entidadConvocante.entidad.id
                  && modalidad.programaConvocatoriaId === element.entidadConvocante.programa.id);
              if (solicitudModalidad) {
                element.modalidad = new StatusWrapper(solicitudModalidad);
              }
            });

            return of(entidadesConvocantesModalidad);
          })
        );
      })
    );
  }

  /**
   * Recupera las modalidades de la solicitud
   *
   * @param solicitudId Identificador de la solicitud
   * @returns observable para recuperar los datos
   */
  private getSolicitudModalidades(solicitudId: number): Observable<ISolicitudModalidad[]> {
    return this.service.findAllSolicitudModalidades(solicitudId).pipe(
      switchMap(res => {
        return of(res.items);
      })
    );
  }

  /**
   * Recupera el plan de un programa (el programa que no tenga padre)
   *
   * @param programa un IPrograma
   * @returns el plan
   */
  private getPlan(programa: IPrograma): IPrograma {
    let programaRaiz = programa;
    while (programaRaiz?.padre) {
      programaRaiz = programaRaiz.padre;
    }
    return programaRaiz;
  }

  /**
   * Añade/elimina los validadores del formulario que solo son necesarios si se cumplen ciertas condiciones.
   *
   * @param form el formulario
   * @param solicitud datos de la solicitud utilizados para determinar los validadores que hay que añadir.
   *  Si no se indica la evaluacion se hace con los datos rellenos en el formulario.
   */
  private setConditionalValidatorsUO(form: FormGroup): void {
    const convocatoriaControl = form.controls.convocatoria;
    const convocatoriaExternaControl = form.controls.convocatoriaExterna;
    const formularioSolicitudControl = form.controls.formularioSolicitud;
    const unidadGestionControl = form.controls.unidadGestion;
    const modeloEjecucionControl = form.controls.modeloEjecucion;
    const finalidadControl = form.controls.finalidad;
    const origenSolictudControl = form.controls.origenSolicitud;

    switch (origenSolictudControl.value) {
      case OrigenSolicitud.CONVOCATORIA_SGI:
        convocatoriaExternaControl.setValidators(null);
        finalidadControl.setValidators([Validators.required]);
        modeloEjecucionControl.setValidators([Validators.required]);
        unidadGestionControl.setValidators([Validators.required]);

        convocatoriaExternaControl.disable({ emitEvent: false });
        finalidadControl.disable({ emitEvent: false });
        formularioSolicitudControl.disable({ emitEvent: false });
        modeloEjecucionControl.disable({ emitEvent: false });
        unidadGestionControl.disable({ emitEvent: false });
        break;
      case OrigenSolicitud.CONVOCATORIA_NO_SGI:
        convocatoriaControl.setValidators(null);
        convocatoriaExternaControl.setValidators([Validators.required]);
        finalidadControl.setValidators(null);
        modeloEjecucionControl.setValidators(null);
        unidadGestionControl.setValidators([Validators.required]);

        convocatoriaExternaControl.enable({ emitEvent: false });
        finalidadControl.enable({ emitEvent: false });

        if (this.isEdit()) {
          formularioSolicitudControl.disable({ emitEvent: false });

          // Unidad gestion
          const unidadGestionModificableEstado = [Estado.BORRADOR, Estado.SOLICITADA].includes(this.solicitud.estado.estado);
          if (!unidadGestionModificableEstado || this.hasDocumentosOrHitos) {
            this.showInfoUnidadGestionDisabledByEstadoUO = !unidadGestionModificableEstado;
            unidadGestionControl.disable({ emitEvent: false });
          } else {
            unidadGestionControl.enable({ emitEvent: false });
          }

          // Modelo ejecucion
          if (this.hasDocumentosOrHitos) {
            modeloEjecucionControl.disable({ emitEvent: false });
          } else {
            modeloEjecucionControl.enable({ emitEvent: false });
          }
        } else {
          formularioSolicitudControl.enable({ emitEvent: false });
          modeloEjecucionControl.enable({ emitEvent: false });
          unidadGestionControl.enable({ emitEvent: false });
        }

        break;
      case OrigenSolicitud.SIN_CONVOCATORIA:
        convocatoriaControl.setValidators(null);
        convocatoriaExternaControl.setValidators(null);
        finalidadControl.setValidators([Validators.required]);
        modeloEjecucionControl.setValidators([Validators.required]);
        unidadGestionControl.setValidators([Validators.required]);

        finalidadControl.enable({ emitEvent: false });

        if (this.isEdit()) {
          formularioSolicitudControl.disable({ emitEvent: false });

          // Unidad gestion
          const unidadGestionModificableEstado = [Estado.BORRADOR, Estado.SOLICITADA].includes(this.solicitud.estado.estado);
          if (!unidadGestionModificableEstado || this.hasDocumentosOrHitos) {
            this.showInfoUnidadGestionDisabledByEstadoUO = !unidadGestionModificableEstado;
            unidadGestionControl.disable({ emitEvent: false });
          } else {
            unidadGestionControl.enable({ emitEvent: false });
          }

          // Modelo ejecucion
          if (this.hasDocumentosOrHitos) {
            modeloEjecucionControl.disable({ emitEvent: false });
          } else {
            modeloEjecucionControl.enable({ emitEvent: false });
          }
        } else {
          formularioSolicitudControl.enable({ emitEvent: false });
          modeloEjecucionControl.enable({ emitEvent: false });
          unidadGestionControl.enable({ emitEvent: false });
        }

        break;
      default:
        break;
    }

    convocatoriaControl.updateValueAndValidity({ emitEvent: false });
    convocatoriaExternaControl.updateValueAndValidity({ emitEvent: false });
    finalidadControl.updateValueAndValidity({ emitEvent: false });
    modeloEjecucionControl.updateValueAndValidity({ emitEvent: false });
    unidadGestionControl.updateValueAndValidity({ emitEvent: false });
  }

  /**
   * Añade/elimina los validadores del formulario que solo son necesarios si se cumplen ciertas condiciones.
   *
   * @param form el formulario
   */
  private setConditionalValidatorsInvestigador(form: FormGroup): void {
    const convocatoriaControl = form.controls.convocatoria;
    const formularioSolicitudControl = form.controls.formularioSolicitud;
    const unidadGestionControl = form.controls.unidadGestion;
    const modeloEjecucionControl = form.controls.modeloEjecucion;
    const finalidadControl = form.controls.finalidad;

    switch (this.solicitud.origenSolicitud) {
      case OrigenSolicitud.CONVOCATORIA_SGI:
        finalidadControl.setValidators([Validators.required]);
        modeloEjecucionControl.setValidators([Validators.required]);
        unidadGestionControl.setValidators([Validators.required]);

        finalidadControl.disable({ emitEvent: false });
        formularioSolicitudControl.disable({ emitEvent: false });
        modeloEjecucionControl.disable({ emitEvent: false });
        unidadGestionControl.disable({ emitEvent: false });
        break;
      case OrigenSolicitud.CONVOCATORIA_NO_SGI:
        convocatoriaControl.setValidators(null);
        finalidadControl.setValidators(null);
        modeloEjecucionControl.setValidators(null);
        unidadGestionControl.setValidators([Validators.required]);

        finalidadControl.enable({ emitEvent: false });

        if (this.isEdit()) {
          formularioSolicitudControl.disable({ emitEvent: false });

          // Unidad gestion
          if (this.solicitud.estado.estado !== Estado.BORRADOR || this.hasDocumentosOrHitos) {
            unidadGestionControl.disable({ emitEvent: false });
          } else {
            unidadGestionControl.enable({ emitEvent: false });
          }

          // Modelo ejecucion
          if (![Estado.BORRADOR, Estado.RECHAZADA, Estado.SUBSANACION].includes(this.solicitud.estado.estado) || this.hasDocumentosOrHitos) {
            modeloEjecucionControl.disable({ emitEvent: false });
          } else {
            modeloEjecucionControl.enable({ emitEvent: false });
          }

          // Finalidad
          if (![Estado.BORRADOR, Estado.RECHAZADA, Estado.SUBSANACION].includes(this.solicitud.estado.estado)) {
            finalidadControl.disable({ emitEvent: false });
          } else {
            finalidadControl.enable({ emitEvent: false });
          }
        } else {
          formularioSolicitudControl.enable({ emitEvent: false });
          modeloEjecucionControl.enable({ emitEvent: false });
          unidadGestionControl.enable({ emitEvent: false });
        }

        break;
      case OrigenSolicitud.SIN_CONVOCATORIA:
        convocatoriaControl.setValidators(null);
        finalidadControl.setValidators([Validators.required]);
        modeloEjecucionControl.setValidators([Validators.required]);
        unidadGestionControl.setValidators([Validators.required]);

        finalidadControl.enable({ emitEvent: false });

        if (this.isEdit()) {
          formularioSolicitudControl.disable({ emitEvent: false });

          // Unidad gestion
          if (this.solicitud.estado.estado !== Estado.BORRADOR || this.hasDocumentosOrHitos) {
            unidadGestionControl.disable({ emitEvent: false });
          } else {
            unidadGestionControl.enable({ emitEvent: false });
          }

          // Modelo ejecucion
          if (![Estado.BORRADOR, Estado.RECHAZADA, Estado.SUBSANACION].includes(this.solicitud.estado.estado) || this.hasDocumentosOrHitos) {
            modeloEjecucionControl.disable({ emitEvent: false });
          } else {
            modeloEjecucionControl.enable({ emitEvent: false });
          }

          // Finalidad
          if (![Estado.BORRADOR, Estado.RECHAZADA, Estado.SUBSANACION].includes(this.solicitud.estado.estado)) {
            finalidadControl.disable({ emitEvent: false });
          } else {
            finalidadControl.enable({ emitEvent: false });
          }

        } else {
          formularioSolicitudControl.enable({ emitEvent: false });
          modeloEjecucionControl.enable({ emitEvent: false });
          unidadGestionControl.enable({ emitEvent: false });
        }

        break;
      default:
        break;
    }

    convocatoriaControl.updateValueAndValidity({ emitEvent: false });
    finalidadControl.updateValueAndValidity({ emitEvent: false });
    modeloEjecucionControl.updateValueAndValidity({ emitEvent: false });
    unidadGestionControl.updateValueAndValidity({ emitEvent: false });
  }

  private getCurrentYear(): number {
    const today = new Date();
    return today.getFullYear();
  }

  /**
   * Setea la convocatoria seleccionada en el formulario y los campos que dependende de esta (tipo formulario, unidad gestión y modalidades)
   *
   * @param origenSolicitud origen de la solicitud
   * @param form el formulario
   */
  private onOrigenSolicitudChange(origenSolicitud: OrigenSolicitud, form: FormGroup): void {
    switch (origenSolicitud) {
      case OrigenSolicitud.CONVOCATORIA_NO_SGI:
        form.controls.convocatoria.setValue('', { emitEvent: false });
        form.controls.convocatoriaExterna.setValue('', { emitEvent: false });
        this.entidadesConvocantesModalidad$.next([]);
        break;
      case OrigenSolicitud.CONVOCATORIA_SGI:
        form.controls.convocatoriaExterna.setValue('', { emitEvent: false });
        break;
      case OrigenSolicitud.SIN_CONVOCATORIA:
        form.controls.convocatoria.setValue('', { emitEvent: false });
        form.controls.convocatoriaExterna.setValue('', { emitEvent: false });
        this.entidadesConvocantesModalidad$.next([]);
        break;
      default:
        break;
    }

    form.controls.formularioSolicitud.setValue(null);
    form.controls.convocatoria.setValue('', { emitEvent: false });
    form.controls.finalidad.setValue(null);
    form.controls.modeloEjecucion.setValue(null);
    form.controls.unidadGestion.setValue(null);
  }

  private updateDisabledControlsWhenHasDocumentosOrHitos(origenSolicitud?: OrigenSolicitud) {
    const unidadGestionControl = this.getFormGroup().controls.unidadGestion;
    const modeloEjecucionControl = this.getFormGroup().controls.modeloEjecucion;

    if (!origenSolicitud) {
      const origenSolictudControl = this.getFormGroup().controls.origenSolicitud;
      origenSolicitud = origenSolictudControl ? origenSolictudControl.value : this.solicitud.origenSolicitud;
    }

    if ([OrigenSolicitud.CONVOCATORIA_NO_SGI, OrigenSolicitud.SIN_CONVOCATORIA].includes(origenSolicitud)) {
      if (this.isInvestigador) {
        // Unidad gestion
        if (this.solicitud.estado.estado !== Estado.BORRADOR || this.hasDocumentosOrHitos) {
          unidadGestionControl.disable({ emitEvent: false });
        } else {
          unidadGestionControl.enable({ emitEvent: false });
        }

        // Modelo ejecucion
        if (![Estado.BORRADOR, Estado.RECHAZADA, Estado.SUBSANACION].includes(this.solicitud.estado.estado) || this.hasDocumentosOrHitos) {
          modeloEjecucionControl.disable({ emitEvent: false });
        } else {
          modeloEjecucionControl.enable({ emitEvent: false });
        }
      } else {
        // Unidad gestion
        if (![Estado.BORRADOR, Estado.SOLICITADA].includes(this.solicitud.estado.estado) || this.hasDocumentosOrHitos) {
          unidadGestionControl.disable({ emitEvent: false });
        } else {
          unidadGestionControl.enable({ emitEvent: false });
        }

        // Modelo ejecucion
        if (this.hasDocumentosOrHitos) {
          modeloEjecucionControl.disable({ emitEvent: false });
        } else {
          modeloEjecucionControl.enable({ emitEvent: false });
        }
      }
    }
  }
}
