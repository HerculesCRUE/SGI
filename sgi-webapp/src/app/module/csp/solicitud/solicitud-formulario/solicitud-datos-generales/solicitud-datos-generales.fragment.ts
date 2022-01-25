import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { Estado } from '@core/models/csp/estado-solicitud';
import { IPrograma } from '@core/models/csp/programa';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { IPersona } from '@core/models/sgp/persona';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SolicitudModalidadService } from '@core/services/csp/solicitud-modalidad.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, from, merge, Observable, of, Subject } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface SolicitudModalidadEntidadConvocanteListado {
  entidadConvocante: IConvocatoriaEntidadConvocante;
  plan: IPrograma;
  modalidad: StatusWrapper<ISolicitudModalidad>;
}

export interface SolicitudDatosGenerales extends ISolicitud {
  convocatoria: IConvocatoria;
}

export class SolicitudDatosGeneralesFragment extends FormFragment<ISolicitud> {

  private solicitud: ISolicitud;

  entidadesConvocantes = [] as IConvocatoriaEntidadConvocante[];

  entidadesConvocantesModalidad$ = new BehaviorSubject<SolicitudModalidadEntidadConvocanteListado[]>([]);

  public showComentariosEstado$ = new BehaviorSubject<boolean>(false);
  public convocatoria$: Subject<IConvocatoria> = new BehaviorSubject(null);

  convocatoriaRequired = false;
  convocatoriaExternaRequired = false;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private service: SolicitudService,
    private convocatoriaService: ConvocatoriaService,
    private empresaService: EmpresaService,
    private personaService: PersonaService,
    private solicitudModalidadService: SolicitudModalidadService,
    private unidadGestionService: UnidadGestionService,
    private authService: SgiAuthService,
    public readonly: boolean,
    public isInvestigador: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.solicitud = { activo: true } as ISolicitud;
  }

  protected initializer(key: number): Observable<SolicitudDatosGenerales> {
    return this.service.findById(key).pipe(
      map(solicitud => {
        return solicitud as SolicitudDatosGenerales;
      }),
      switchMap((solicitud) => {
        return this.personaService.findById(solicitud.solicitante.id).pipe(
          map(solicitente => {
            solicitud.solicitante = solicitente;
            return solicitud;
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
          const convocatoriaSolicitud$ = this.isInvestigador ? this.service.findConvocatoria(solicitud.id) : this.convocatoriaService.findById(solicitud.convocatoriaId);
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
      map((solicitud) => {
        const estadosComentarioNoVisble = [
          Estado.BORRADOR
        ];

        this.showComentariosEstado$.next(!estadosComentarioNoVisble.includes(solicitud.estado.estado));

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
    if (this.isInvestigador) {
      form = new FormGroup({
        estado: new FormControl({ value: Estado.BORRADOR, disabled: true }),
        titulo: new FormControl({ value: '', disabled: this.isEdit() }, [Validators.maxLength(250)]),
        convocatoria: new FormControl({ value: '', disabled: true }),
        codigoExterno: new FormControl({ value: '', disabled: true }, Validators.maxLength(50)),
        observaciones: new FormControl({ value: '', disabled: this.isEdit() }, Validators.maxLength(2000)),
        comentariosEstado: new FormControl({ value: '', disabled: true })
      });

      this.subscriptions.push(
        form.controls.convocatoria.valueChanges.subscribe(
          (convocatoria) => {
            this.convocatoria$.next(convocatoria);
          }
        )
      );
    } else {
      form = new FormGroup({
        estado: new FormControl({ value: Estado.BORRADOR, disabled: true }),
        titulo: new FormControl(undefined, [Validators.maxLength(250)]),
        solicitante: new FormControl('', Validators.required),
        convocatoria: new FormControl({ value: '', disabled: this.isEdit() }),
        comentariosEstado: new FormControl({ value: '', disabled: true }),
        convocatoriaExterna: new FormControl(''),
        formularioSolicitud: new FormControl({ value: FormularioSolicitud.PROYECTO, disabled: this.isEdit() }),
        unidadGestion: new FormControl(null, Validators.required),
        codigoExterno: new FormControl('', Validators.maxLength(50)),
        codigoRegistro: new FormControl({ value: '', disabled: true }),
        observaciones: new FormControl('', Validators.maxLength(2000))
      });
      // Se setean los validaores condicionales y se hace una subscripcion a los campos que provocan
      // cambios en los validadores del formulario
      this.setConditionalValidators(form);

      this.subscriptions.push(
        merge(
          form.controls.convocatoria.valueChanges,
          form.controls.convocatoriaExterna.valueChanges
        ).subscribe(_ => {
          this.setConditionalValidators(form);
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
    }

    if (this.readonly) {
      form.disable();
    }
    return form;

  }
  buildPatch(solicitud: SolicitudDatosGenerales): { [key: string]: any } {
    this.solicitud = solicitud;
    if (solicitud?.estado?.estado === Estado.BORRADOR) {
      this.getFormGroup().controls.titulo.enable();
      this.getFormGroup().controls.convocatoria.enable();
      this.getFormGroup().controls.codigoExterno.enable();
      this.getFormGroup().controls.observaciones.enable();
      this.getFormGroup().controls.comentariosEstado.enable();
    }
    if (this.isInvestigador) {
      return {
        estado: solicitud?.estado?.estado,
        titulo: solicitud?.titulo ?? '',
        convocatoria: solicitud?.convocatoria,
        codigoExterno: solicitud?.codigoExterno,
        observaciones: solicitud?.observaciones ?? '',
        comentariosEstado: solicitud?.estado?.comentario,
      };
    } else {
      return {
        estado: solicitud.estado?.estado,
        titulo: solicitud.titulo,
        comentariosEstado: solicitud.estado?.comentario,
        solicitante: solicitud.solicitante,
        convocatoria: solicitud.convocatoria,
        convocatoriaExterna: solicitud.convocatoriaExterna,
        formularioSolicitud: solicitud.formularioSolicitud,
        unidadGestion: solicitud.unidadGestion,
        codigoRegistro: solicitud.codigoRegistroInterno,
        codigoExterno: solicitud.codigoExterno,
        observaciones: solicitud.observaciones
      };
    }

  }

  getValue(): ISolicitud {
    const form = this.getFormGroup().controls;
    if (this.isInvestigador) {
      if (!this.solicitud.solicitante) {
        this.solicitud.solicitante = {
          id: this.authService.authStatus$?.getValue()?.userRefId
        } as IPersona;
      }

      this.solicitud.titulo = form.titulo.value;
      this.solicitud.observaciones = form.observaciones.value;
      this.solicitud.codigoExterno = form.codigoExterno.value;
    } else {
      this.solicitud.solicitante = form.solicitante.value;
      this.solicitud.titulo = form.titulo.value;
      this.solicitud.convocatoriaId = form.convocatoria.value?.id;
      this.solicitud.convocatoriaExterna = form.convocatoriaExterna.value;
      this.solicitud.formularioSolicitud = form.formularioSolicitud.value;
      this.solicitud.unidadGestion = form.unidadGestion.value;
      this.solicitud.codigoExterno = form.codigoExterno.value;
      this.solicitud.observaciones = form.observaciones.value;
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

  setDatosConvocatoria(convocatoria: IConvocatoria) {
    this.subscriptions.push(
      this.unidadGestionService.findById(convocatoria.unidadGestion.id).subscribe(unidadGestion => {
        this.solicitud.unidadGestion = unidadGestion;
      })
    );

    this.subscriptions.push(
      this.convocatoriaService.getFormularioSolicitud(convocatoria.id).subscribe(formularioSolicitud => {
        this.solicitud.formularioSolicitud = formularioSolicitud;
      })
    );

    this.subscriptions.push(
      this.loadEntidadesConvocantesModalidad(this.getValue().id, convocatoria.id).subscribe(entidadesConvocantes => {
        this.entidadesConvocantesModalidad$.next(entidadesConvocantes);
      })
    );

    this.solicitud.convocatoriaId = convocatoria.id;

    this.convocatoria$.next(convocatoria);
    this.getFormGroup().controls.convocatoria.setValue(convocatoria);
  }

  /**
   * Añada la solicitudModalidad y la marca como creada
   *
   * @param solicitudModalidad ISolicitudModalidad
   */
  public addSolicitudModalidad(solicitudModalidad: ISolicitudModalidad): void {
    const current = this.entidadesConvocantesModalidad$.value;
    const index = current.findIndex(value => value.entidadConvocante.entidad.id === solicitudModalidad.entidad.id);
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
      this.subscriptions.push(
        this.unidadGestionService.findById(convocatoria.unidadGestion.id).subscribe(unidadGestion => {
          this.getFormGroup().controls.unidadGestion.setValue(unidadGestion);
        })
      );

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
    } else if (!this.isEdit()) {
      // Clean dependencies
      this.getFormGroup().controls.unidadGestion.setValue(null);
      this.getFormGroup().controls.formularioSolicitud.setValue(null);
      this.entidadesConvocantesModalidad$.next([]);

      // Enable fields
      this.getFormGroup().controls.convocatoriaExterna.enable();
      this.getFormGroup().controls.unidadGestion.enable();
      this.getFormGroup().controls.formularioSolicitud.enable();
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
    const convocatoriaEntidadConvocantes$ = this.isInvestigador && solicitudId ? this.service.findAllConvocatoriaEntidadConvocantes(solicitudId)
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
              catchError(() => of(element))
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
                .find(modalidad => modalidad.entidad.id === element.entidadConvocante.entidad.id);
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
  private setConditionalValidators(form: FormGroup): void {
    const convocatoriaControl = form.controls.convocatoria;
    const convocatoriaExternaControl = form.controls.convocatoriaExterna;
    const formularioSolicitudControl = form.controls.formularioSolicitud;
    const unidadGestionControl = form.controls.unidadGestion;

    const convocatoriaSolicitud = convocatoriaControl.value;
    const convocatoriaExternaSolicitud = convocatoriaExternaControl.value;

    if (!this.isEdit()) {
      if (!convocatoriaSolicitud && !convocatoriaExternaSolicitud) {
        convocatoriaControl.setValidators([Validators.required]);
        convocatoriaExternaControl.setValidators([Validators.required, Validators.maxLength(50)]);
      }
      else if (!convocatoriaSolicitud) {
        convocatoriaControl.setValidators(null);
        convocatoriaExternaControl.setValidators([Validators.required, Validators.maxLength(50)]);
        formularioSolicitudControl.setValidators([Validators.required]);
        unidadGestionControl.setValidators([Validators.required, IsEntityValidator.isValid()]);
      } else {
        convocatoriaControl.setValidators([Validators.required]);
        convocatoriaExternaControl.setValidators(null);
        formularioSolicitudControl.setValidators(null);
        unidadGestionControl.setValidators(null);

        convocatoriaExternaControl.disable({ emitEvent: false });
        formularioSolicitudControl.disable({ emitEvent: false });
        unidadGestionControl.disable({ emitEvent: false });
      }

      convocatoriaControl.updateValueAndValidity({ emitEvent: false });
      convocatoriaExternaControl.updateValueAndValidity({ emitEvent: false });
      formularioSolicitudControl.updateValueAndValidity({ emitEvent: false });
      unidadGestionControl.updateValueAndValidity({ emitEvent: false });
    } else if (!this.readonly && convocatoriaSolicitud) {
      unidadGestionControl.disable({ emitEvent: false });
      unidadGestionControl.updateValueAndValidity({ emitEvent: false });
      convocatoriaControl.disable({ emitEvent: false });
      convocatoriaControl.updateValueAndValidity({ emitEvent: false });
      convocatoriaExternaControl.disable({ emitEvent: false });
      convocatoriaExternaControl.updateValueAndValidity({ emitEvent: false });
    }

    this.convocatoriaRequired = !convocatoriaExternaSolicitud;
    this.convocatoriaExternaRequired = !convocatoriaSolicitud;
  }
}


