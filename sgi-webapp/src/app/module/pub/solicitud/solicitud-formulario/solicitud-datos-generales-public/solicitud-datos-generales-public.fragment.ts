import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { Estado } from '@core/models/csp/estado-solicitud';
import { IPrograma } from '@core/models/csp/programa';
import { ISolicitanteExterno } from '@core/models/csp/solicitante-externo';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudGrupo } from '@core/models/csp/solicitud-grupo';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { ISolicitudRrhh } from '@core/models/csp/solicitud-rrhh';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { IPersona } from '@core/models/sgp/persona';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { SolicitanteExternoPublicService } from '@core/services/csp/solicitante-externo/solicitante-externo-public.service';
import { SolicitudModalidadPublicService } from '@core/services/csp/solicitud-modalidad-public.service';
import { SolicitudPublicService } from '@core/services/csp/solicitud-public.service';
import { SolicitudRrhhPublicService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh-public.service';
import { UnidadGestionPublicService } from '@core/services/csp/unidad-gestion-public.service';
import { EmpresaPublicService } from '@core/services/sgemp/empresa-public.service';
import { ClasificacionPublicService } from '@core/services/sgo/clasificacion-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, forkJoin, from, Observable, of, Subject } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface SolicitudModalidadEntidadConvocantePublicListado {
  entidadConvocante: IConvocatoriaEntidadConvocante;
  plan: IPrograma;
  modalidad: StatusWrapper<ISolicitudModalidad>;
}

export interface SolicitudDatosGeneralesPublic extends ISolicitud {
  convocatoria: IConvocatoria;
  solicitudRrhh: ISolicitudRrhh;
  solicitanteExterno: ISolicitanteExterno;
  areaAnepListado: SolicitudRrhhAreaAnepPublicListado;
  publicKey: string;
}

export interface SolicitudRrhhAreaAnepPublicListado {
  niveles: IClasificacion[];
  nivelesTexto: string;
  nivelSeleccionado: IClasificacion;
}

export class SolicitudDatosGeneralesPublicFragment extends FormFragment<SolicitudDatosGeneralesPublic> {
  public solicitud: SolicitudDatosGeneralesPublic;
  private solicitudGrupo: ISolicitudGrupo;
  public solicitanteRef: string;

  entidadesConvocantes = [] as IConvocatoriaEntidadConvocante[];

  entidadesConvocantesModalidad$ = new BehaviorSubject<SolicitudModalidadEntidadConvocantePublicListado[]>([]);
  readonly areasAnep$ = new BehaviorSubject<SolicitudRrhhAreaAnepPublicListado[]>([]);

  public showComentariosEstado$ = new BehaviorSubject<boolean>(false);
  public convocatoria$: Subject<IConvocatoria> = new BehaviorSubject(null);

  convocatoriaRequired = false;
  convocatoriaExternaRequired = false;
  tipoFormularioSolicitud: FormularioSolicitud;

  get isSolicitanteRequired(): boolean {
    return [FormularioSolicitud.GRUPO, FormularioSolicitud.PROYECTO].includes(this.tipoFormularioSolicitud);
  }

  constructor(
    private readonly logger: NGXLogger,
    key: string,
    private service: SolicitudPublicService,
    private convocatoriaService: ConvocatoriaPublicService,
    private empresaService: EmpresaPublicService,
    private solicitudModalidadService: SolicitudModalidadPublicService,
    private unidadGestionService: UnidadGestionPublicService,
    private clasificacionService: ClasificacionPublicService,
    private readonly solicitudRrhhService: SolicitudRrhhPublicService,
    private readonly solicitanteExternoService: SolicitanteExternoPublicService,
    public readonly: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.solicitud = { activo: true } as SolicitudDatosGeneralesPublic;
  }

  protected initializer(key: string): Observable<SolicitudDatosGeneralesPublic> {
    return this.service.findById(key).pipe(
      map(solicitud => {
        const solicitudDatosGenerales = solicitud as SolicitudDatosGeneralesPublic;
        solicitudDatosGenerales.publicKey = key;
        return solicitudDatosGenerales;
      }),
      switchMap((solicitud) => {
        return this.service.findSolicitudRrhh(solicitud.publicKey).pipe(
          map(solicitudRrhh => {
            solicitud.solicitudRrhh = solicitudRrhh;
            return solicitud;
          }),
          switchMap(result => {
            return forkJoin({
              solicitante: this.loadSolicitanteExterno(result.publicKey),
              universidad: this.loadUniversidad(result.solicitudRrhh.universidad?.id),
              areaAnepListado: this.loadAreaAnep(result.solicitudRrhh.areaAnep?.id)
            }).pipe(
              map(({ solicitante, universidad, areaAnepListado }) => {
                result.solicitanteExterno = solicitante;
                result.solicitudRrhh.universidad = universidad;
                result.areaAnepListado = areaAnepListado;
                return result;
              })
            );
          }),
        );
      }),
      switchMap((solicitud) => {
        if (solicitud.convocatoriaId) {
          return this.convocatoriaService.findById(solicitud.convocatoriaId).pipe(
            switchMap(convocatoria => {
              return this.loadEntidadesConvocantesModalidad(solicitud.publicKey, convocatoria.id).pipe(
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
    const form = new FormGroup({
      estado: new FormControl({ value: Estado.BORRADOR, disabled: true }),
      tituloConvocatoria: new FormControl({ value: '', disabled: true }),
      codigoRegistro: new FormControl({ value: '', disabled: true }),
      codigoExterno: new FormControl('', Validators.maxLength(50)),
      observaciones: new FormControl({ value: '', disabled: this.isEdit() }, Validators.maxLength(2000)),
      comentariosEstado: new FormControl({ value: '', disabled: true }),
      solicitanteExterno: new FormGroup({
        nombre: new FormControl(null, Validators.required),
        apellidos: new FormControl(null, Validators.required),
        tipoDocumento: new FormControl(null, Validators.required),
        numeroDocumento: new FormControl(null, Validators.required),
        sexo: new FormControl(null),
        fechaNacimiento: new FormControl(null),
        paisNacimiento: new FormControl(null),
        telefono: new FormControl(null, Validators.required),
        email: new FormControl(null, Validators.required),
        direccionContacto: new FormControl(null),
        paisContacto: new FormControl(null),
        comunidadAutonomaContacto: new FormControl(null),
        provinciaContacto: new FormControl(null),
        localidadContacto: new FormControl(null),
        codigoPostalContacto: new FormControl(null)
      }),
      universidadText: new FormControl(null),
      areaAnep: new FormControl(null)
    });

    if (this.readonly) {
      form.disable();
    }
    return form;
  }

  buildPatch(solicitud: SolicitudDatosGeneralesPublic): { [key: string]: any } {
    this.solicitud = solicitud;
    this.solicitanteRef = solicitud.solicitante?.id;
    this.tipoFormularioSolicitud = solicitud.formularioSolicitud;

    const formValues: { [key: string]: any } = {
      estado: solicitud?.estado?.estado,
      tituloConvocatoria: solicitud?.convocatoria?.titulo ?? '',
      codigoRegistro: solicitud?.codigoRegistroInterno,
      codigoExterno: solicitud?.codigoExterno,
      observaciones: solicitud?.observaciones ?? '',
      comentariosEstado: solicitud?.estado?.comentario,
      universidadText: solicitud.solicitudRrhh.universidad?.nombre ?? solicitud.solicitudRrhh.universidadDatos,
      areaAnep: solicitud.solicitudRrhh.areaAnep,
      solicitanteExterno: {
        nombre: solicitud.solicitanteExterno.nombre ?? null,
        apellidos: solicitud.solicitanteExterno.apellidos ?? null,
        tipoDocumento: solicitud.solicitanteExterno.tipoDocumento ?? null,
        numeroDocumento: solicitud.solicitanteExterno.numeroDocumento ?? null,
        sexo: solicitud.solicitanteExterno.sexo ?? null,
        fechaNacimiento: solicitud.solicitanteExterno.fechaNacimiento ?? null,
        paisNacimiento: solicitud.solicitanteExterno.paisNacimiento ?? null,
        direccionContacto: solicitud.solicitanteExterno.direccion ?? null,
        paisContacto: solicitud.solicitanteExterno.paisContacto ?? null,
        comunidadAutonomaContacto: solicitud.solicitanteExterno.comunidad ?? null,
        provinciaContacto: solicitud.solicitanteExterno.provincia ?? null,
        localidadContacto: solicitud.solicitanteExterno.ciudad ?? null,
        codigoPostalContacto: solicitud.solicitanteExterno.codigoPostal ?? null,
        telefono: solicitud.solicitanteExterno.telefono ?? null,
        email: solicitud.solicitanteExterno.email ?? null
      }
    };

    this.convocatoria$.next(solicitud?.convocatoria);

    if (!this.readonly && [Estado.BORRADOR, Estado.RECHAZADA].includes(solicitud?.estado?.estado)) {
      this.getFormGroup().controls.codigoExterno.enable();
      this.getFormGroup().controls.observaciones.enable();
    }

    return formValues;
  }

  getValue(): SolicitudDatosGeneralesPublic {
    const form = this.getFormGroup().controls;

    this.solicitud.observaciones = form.observaciones.value;
    this.solicitud.codigoExterno = form.codigoExterno.value;
    this.solicitud.solicitanteExterno = this.getDatosFormSolicitanteExterno();
    this.solicitud.solicitudRrhh = this.getDatosFormSolicitudRrhh();

    return this.solicitud;
  }

  saveOrUpdate(): Observable<string> {
    return this.saveOrUpdateSolicitud(this.getValue()).pipe(
      switchMap(() => this.saveOrUpdateSolicitanteExterno(this.getDatosFormSolicitanteExterno())),
      switchMap(() => this.saveOrUpdateSolicitudModalidades(this.getValue())),
      switchMap(() => this.saveOrUpdateSolicitudRrhh(this.getValue().solicitudRrhh)),
      map(() => {
        return this.solicitud.publicKey;
      })
    );
  }

  private saveOrUpdateSolicitudRrhh(solicitudRrhh: ISolicitudRrhh): Observable<ISolicitudRrhh> {
    return this.isEdit() ? this.updateSolicitudRrhh(solicitudRrhh) : this.createSolicitudRrhh(solicitudRrhh);
  }

  private createSolicitudRrhh(solicitudRrhh: ISolicitudRrhh): Observable<ISolicitudRrhh> {
    return this.solicitudRrhhService.create(this.solicitud.publicKey, solicitudRrhh);
  }

  private updateSolicitudRrhh(solicitud: ISolicitudRrhh): Observable<ISolicitudRrhh> {
    return this.solicitudRrhhService.update(this.solicitud.publicKey, solicitud.id, solicitud);
  }

  private saveOrUpdateSolicitud(datosGenerales: SolicitudDatosGeneralesPublic): Observable<number> {

    const obs = this.isEdit() ? this.service.update(datosGenerales.publicKey, datosGenerales as ISolicitud) :
      this.service.create(datosGenerales);
    return obs.pipe(
      tap((value) => {
        this.solicitud.id = value.id;
      }),
      map(() => {
        return this.solicitud.id;
      })
    );
  }

  private saveOrUpdateSolicitudModalidades(solicitud: ISolicitud): Observable<number> {
    return forkJoin([
      this.createSolicitudModalidades(solicitud.id),
      this.deleteSolicitudModalidades(),
      this.updateSolicitudModalidades()
    ]).pipe(
      map(() => {
        return solicitud.id;
      })
    );
  }

  private saveOrUpdateSolicitanteExterno(solicitante: ISolicitanteExterno): Observable<void> {

    return solicitante?.id ? this.updateSolicitanteExterno(solicitante)
      : this.createSolicitanteExterno(this.getDatosFormSolicitanteExterno());
  }

  private createSolicitanteExterno(solicitante: ISolicitanteExterno): Observable<void> {
    return this.solicitanteExternoService.create(solicitante).pipe(
      map(solicitanteExterno => {
        this.solicitud.publicKey = solicitanteExterno.solicitudUUID;
        this.solicitud.solicitanteExterno = solicitante;
        this.solicitud.solicitanteExterno.id = solicitanteExterno.id;
      })
    );
  }

  private updateSolicitanteExterno(solicitante: ISolicitanteExterno): Observable<void> {
    return this.solicitanteExternoService.update(this.solicitud.publicKey, solicitante.id, solicitante).pipe(
      map(solicitanteExterno => {
        this.solicitud.solicitanteExterno.id = solicitanteExterno.id;
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
      this.loadEntidadesConvocantesModalidad(this.getValue().publicKey, convocatoria.id).subscribe(entidadesConvocantes => {
        this.entidadesConvocantesModalidad$.next(entidadesConvocantes);
      })
    );

    this.solicitud.convocatoriaId = convocatoria.id;
    this.tipoFormularioSolicitud = convocatoria.formularioSolicitud;
    this.solicitud.solicitante = {
      id: this.solicitanteRef
    } as IPersona;

    this.convocatoria$.next(convocatoria);
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
        return this.solicitudModalidadService.create(this.solicitud.publicKey, wrappedSolicitudModalidad.value).pipe(
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
        return this.solicitudModalidadService.delete(this.solicitud.publicKey, wrappedSolicitudModalidad.value.id).pipe(
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
        return this.solicitudModalidadService.update(
          this.solicitud.publicKey, wrappedSolicitudModalidad.value.id, wrappedSolicitudModalidad.value
        ).pipe(
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
   * Carga los datos de la tabla de modalidades de la solicitud y emite el cambio para que
   * se pueda actualizar la tabla
   *
   * @param solicitudId Identificador de la solicitud
   * @param convocatoriaId Identificador de la convocatoria
   * @returns observable para recuperar los datos
   */
  private loadEntidadesConvocantesModalidad(
    solicitudPublicId: string,
    convocatoriaId: number
  ): Observable<SolicitudModalidadEntidadConvocantePublicListado[]> {
    return this.convocatoriaService.findAllConvocatoriaEntidadConvocantes(convocatoriaId).pipe(
      map(resultEntidadConvocantes => {
        if (resultEntidadConvocantes.total === 0) {
          return [] as SolicitudModalidadEntidadConvocantePublicListado[];
        }

        return resultEntidadConvocantes.items.map(entidadConvocante => {
          return {
            entidadConvocante,
            plan: this.getPlan(entidadConvocante.programa)
          } as SolicitudModalidadEntidadConvocantePublicListado;
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
        if (!solicitudPublicId) {
          return of(entidadesConvocantesModalidad);
        }

        return this.getSolicitudModalidades(solicitudPublicId).pipe(
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
   * @param solicitudPublicId Identificador de la solicitud
   * @returns observable para recuperar los datos
   */
  private getSolicitudModalidades(solicitudPublicId: string): Observable<ISolicitudModalidad[]> {
    return this.service.findAllSolicitudModalidades(solicitudPublicId).pipe(
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


  updateAreaAnep(areaAnep: SolicitudRrhhAreaAnepPublicListado): void {
    this.solicitud.solicitudRrhh.areaAnep = areaAnep.nivelSeleccionado;

    if (this.areasAnep$.value?.length > 0) {
      this.areasAnep$.value[0] = areaAnep;
      this.areasAnep$.next(this.areasAnep$.value);
    } else {
      this.areasAnep$.next([areaAnep]);
    }

    this.setChanges(true);
  }

  deleteAreaAnep(): void {
    this.solicitud.solicitudRrhh.areaAnep = null;
    this.areasAnep$.next([]);
    this.setChanges(true);
  }

  private loadSolicitanteExterno(solicitudId: string): Observable<ISolicitanteExterno> {
    return solicitudId ? this.service.findSolicitanteExterno(solicitudId) : of(null);
  }

  private loadUniversidad(id: string): Observable<IEmpresa> {
    return id ? this.empresaService.findById(id) : of(null);
  }

  private loadAreaAnep(id: string): Observable<SolicitudRrhhAreaAnepPublicListado> {
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

  private getNiveles(solicitudRrhhAreaAnep: SolicitudRrhhAreaAnepPublicListado): Observable<SolicitudRrhhAreaAnepPublicListado> {
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

  private getDatosFormSolicitanteExterno(): ISolicitanteExterno {
    const solicitanteExternoFormGroup = this.getFormGroup().controls.solicitanteExterno as FormGroup;

    return {
      id: this.solicitud.solicitanteExterno?.id,
      solicitudId: this.solicitud?.id,
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

  private getDatosFormSolicitudRrhh(): ISolicitudRrhh {
    const form = this.getFormGroup().controls;

    return {
      id: this.solicitud.id,
      universidadDatos: form.universidadText.value,
      areaAnep: this.areasAnep$.value?.length > 0 ? this.areasAnep$.value[0].nivelSeleccionado : null,
      universidad: null
    };
  }

}
