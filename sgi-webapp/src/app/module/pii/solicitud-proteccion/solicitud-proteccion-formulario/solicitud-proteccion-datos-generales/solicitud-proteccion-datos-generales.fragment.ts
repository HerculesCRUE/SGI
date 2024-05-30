import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { IInvencion } from '@core/models/pii/invencion';
import { IPaisValidado } from '@core/models/pii/pais-validado';
import { Estado, ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { ITipoCaducidad } from '@core/models/pii/tipo-caducidad';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { IPais } from '@core/models/sgo/pais';
import { FormFragment } from '@core/services/action-service';
import { PaisValidadoService } from '@core/services/pii/solicitud-proteccion/pais-validado/pais-validado.service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { TipoCaducidadService } from '@core/services/pii/tipo-caducidad/tipo-caducidad.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateValidator } from '@core/validators/date-validator';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, concat, forkJoin, from, Observable, of, Subscription } from 'rxjs';
import { catchError, defaultIfEmpty, last, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class SolicitudProteccionDatosGeneralesFragment extends FormFragment<ISolicitudProteccion> {

  private readonly TITULO_MAX_LENGTH = 250;
  private readonly COMENTARIOS_MAX_LENGTH = 250;
  private readonly NUMERO_REGISTRO_MAX_LENGTH = 24;
  private readonly NUMERO_SOLICITUD_MAX_LENGTH = 24;
  private readonly NUMERO_PUBLICACION_MAX_LENGTH = 24;
  private readonly NUMERO_CONCESION_MAX_LENGTH = 24;

  private isExtensionInternacional$ = new BehaviorSubject<boolean>(false);
  private isViaEuropea = new BehaviorSubject<boolean>(false);

  public paises$ = new BehaviorSubject<IPais[]>([]);
  public tiposCaducidad$ = new BehaviorSubject<ITipoCaducidad[]>([]);

  public solicitudProteccion: ISolicitudProteccion;
  public firstSolicitudProteccionWithPrioridad = new BehaviorSubject<ISolicitudProteccion>(null);

  public showPaisSelector = new BehaviorSubject<boolean>(false);

  public paisesValidados$ = new BehaviorSubject<StatusWrapper<IPaisValidado>[]>([]);
  public paisesValidadosToDelete: StatusWrapper<IPaisValidado>[] = [];

  public get TipoPropiedad(): any {
    return TipoPropiedad;
  }

  public get isExtensionInternacional(): Observable<boolean> {
    return this.isExtensionInternacional$;
  }

  constructor(
    private logger: NGXLogger,
    key: number,
    private invencionId: number,
    public tipoPropiedad: TipoPropiedad,
    private invencionTitulo: string,
    private solicitudProteccionService: SolicitudProteccionService,
    public readonly: boolean,
    private paisService: PaisService,
    private empresaService: EmpresaService,
    private tipoCaducidadService: TipoCaducidadService,
    solicitudesAnteriores: ISolicitudProteccion[],
    private paisValidadoService: PaisValidadoService
  ) {
    super(key, true);
    this.setComplete(true);
    this.solicitudProteccion = {
      invencion:
        { id: invencionId }
    } as ISolicitudProteccion;

    solicitudesAnteriores.sort((a, b) => a.id > b.id ? 1 : -1);
    const firstSolicitudProteccion = solicitudesAnteriores
      .find(solicitud =>
        solicitud.invencion.tipoProteccion.tipoPropiedad === TipoPropiedad.INDUSTRIAL
      );
    this.firstSolicitudProteccionWithPrioridad.next(firstSolicitudProteccion ?? null);
    if (!key) {
      this.loadPaises$().subscribe(paises => {
        this.paises$.next(paises);
      });
    }
  }

  protected buildFormGroup(): FormGroup {

    const form: FormGroup = new FormGroup({
      titulo: new FormControl(
        !this.getKey() ? this.defaultTitle : null, [Validators.maxLength(this.TITULO_MAX_LENGTH), Validators.required]
      ),
      viaProteccion: new FormControl('', Validators.required),
      pais: new FormControl(null, [Validators.required]),
      fechaPrioridad: new FormControl('', [Validators.required]),
      fechaFinPrioridad: new FormControl(null, [Validators.required]),
      numeroSolicitud: new FormControl('', [Validators.required, Validators.maxLength(this.NUMERO_SOLICITUD_MAX_LENGTH)]),
      numeroPublicacion: new FormControl('', [Validators.maxLength(this.NUMERO_PUBLICACION_MAX_LENGTH)]),
      fechaPublicacion: new FormControl(null, []),
      numeroConcesion: new FormControl('', [Validators.maxLength(this.NUMERO_CONCESION_MAX_LENGTH)]),
      fechaConcesion: new FormControl(null, []),
      agentePropiedad: new FormControl(null, []),
      comentarios: new FormControl('', [Validators.maxLength(this.COMENTARIOS_MAX_LENGTH)]),
      numeroRegistro: new FormControl('', [Validators.maxLength(this.NUMERO_REGISTRO_MAX_LENGTH)]),
      fechaCaducidad: new FormControl(null, []),
      tipoCaducidad: new FormControl(null, []),
      estado: new FormControl(null, []),
    }, {
      validators: [
        DateValidator.isAfter('fechaPrioridad', 'fechaFinPrioridad')
      ]
    });

    if (this.readonly) {
      form.disable();
    } else {
      if (this.getKey()) {
        form.controls.estado.setValidators([Validators.required]);
      }
      this.subscriptions.push(
        this.handleViaProteccionOnChangeValueEventSubscription(form)
      );
      this.subscriptions.push(
        this.handleFechaPrioridadOnChangeValueEventSubscription(form)
      );
      this.subscriptions.push(
        this.handleEstadoOnChangeValueEventSubscription(form)
      );
    }
    return form;
  }

  protected buildPatch(solicitudProteccion: ISolicitudProteccion): { [key: string]: any; } {

    this.solicitudProteccion = solicitudProteccion;

    return {
      titulo: this.solicitudProteccion.titulo,
      viaProteccion: solicitudProteccion.viaProteccion,
      pais: solicitudProteccion.paisProteccion,
      agentePropiedad: solicitudProteccion.agentePropiedad.id ? solicitudProteccion.agentePropiedad : null,
      comentarios: solicitudProteccion.comentarios,
      fechaFinPrioridad: solicitudProteccion.fechaFinPriorPresFasNacRec,
      fechaPrioridad: solicitudProteccion.fechaPrioridadSolicitud,
      numeroSolicitud: solicitudProteccion.numeroSolicitud,
      numeroPublicacion: solicitudProteccion.numeroPublicacion,
      fechaPublicacion: solicitudProteccion.fechaPublicacion,
      numeroConcesion: solicitudProteccion.numeroConcesion,
      fechaConcesion: solicitudProteccion.fechaConcesion,
      paisProteccion: solicitudProteccion.paisProteccion,
      numeroRegistro: solicitudProteccion.numeroRegistro,
      fechaCaducidad: solicitudProteccion.fechaCaducidad,
      tipoCaducidad: solicitudProteccion.tipoCaducidad,
      invencion: { id: this.invencionId },
      estado: solicitudProteccion.estado,
    };
  }

  protected initializer(key: number): Observable<ISolicitudProteccion> {

    return forkJoin({
      solicitudProteccion: this.solicitudProteccionService.findById(key),
      paises: this.loadPaises$(),
      tiposCaducidad: this.loadTiposCaducidad$()
    })
      .pipe(
        tap(({ paises }) => {
          this.paises$.next(paises);
        }),
        tap(({ tiposCaducidad }) => {
          this.tiposCaducidad$.next(tiposCaducidad);
        }),
        map(({ solicitudProteccion }) => {
          this.solicitudProteccion = solicitudProteccion;
          return this.solicitudProteccion;
        }),
        switchMap((solicitudProteccion) => {
          if (solicitudProteccion.agentePropiedad?.id) {
            return this.empresaService.findById(solicitudProteccion.agentePropiedad?.id).pipe(
              map(result => {
                solicitudProteccion.agentePropiedad = result;
                return solicitudProteccion;
              },
                catchError(e => {
                  return of(solicitudProteccion);
                })
              ));
          }
          return of(solicitudProteccion);
        }),
        switchMap((solicitudProteccion) => {
          return this.findPaisesValidadosBySolicitudProteccion(solicitudProteccion);
        }),
        catchError((err) => {
          this.logger.error(err);
          return of(void 0);
        })
      );
  }

  private findPaisesValidadosBySolicitudProteccion(solicitudProteccion: ISolicitudProteccion): Observable<ISolicitudProteccion> {
    return this.solicitudProteccionService.findPaisesValidadosBySolicitudProteccionId(solicitudProteccion.id).pipe(
      map(result => {
        this.paisesValidados$.next(result.items.map(elem => {
          elem.pais = this.paises$.value.find(pais => pais.id === elem.pais.id);
          return new StatusWrapper(elem);
        }));
        return solicitudProteccion;
      },
        catchError(e => {
          return of(solicitudProteccion);
        })
      ));
  }

  getValue(): ISolicitudProteccion {

    const formCtrls = this.getFormGroup().controls;

    this.solicitudProteccion.agentePropiedad = formCtrls.agentePropiedad.value;
    this.solicitudProteccion.comentarios = formCtrls.comentarios.value;
    this.solicitudProteccion.fechaFinPriorPresFasNacRec =
      this.shouldShowFechaFinPrioridad(formCtrls.viaProteccion.value) ? formCtrls.fechaFinPrioridad.value : null;
    this.solicitudProteccion.fechaPrioridadSolicitud = formCtrls.fechaPrioridad.value;
    this.solicitudProteccion.numeroSolicitud = formCtrls.numeroSolicitud.value;
    this.solicitudProteccion.numeroPublicacion = formCtrls.numeroPublicacion.value;
    this.solicitudProteccion.fechaPublicacion = formCtrls.fechaPublicacion.value;
    this.solicitudProteccion.numeroConcesion = formCtrls.numeroConcesion.value;
    this.solicitudProteccion.fechaConcesion = formCtrls.fechaConcesion.value;
    this.solicitudProteccion.titulo = formCtrls.titulo.value;
    this.solicitudProteccion.viaProteccion = formCtrls.viaProteccion.value;
    this.solicitudProteccion.paisProteccion = formCtrls.pais.value;
    this.solicitudProteccion.numeroRegistro = formCtrls.numeroRegistro.value;
    this.solicitudProteccion.invencion = { id: this.invencionId } as IInvencion;
    this.solicitudProteccion.fechaCaducidad = formCtrls.fechaCaducidad.value;
    this.solicitudProteccion.tipoCaducidad = formCtrls.tipoCaducidad.value;
    this.solicitudProteccion.estado = formCtrls.estado.value;
    return this.solicitudProteccion;
  }

  saveOrUpdate(): Observable<number | string | void> {

    const solicitudProteccion = this.getValue();
    const createOrDeletePaisesValidados$ = concat(this.createOrUpdatePaisValidado(), this.deletePaisesValidados()).pipe(
      defaultIfEmpty(void 0),
      last());
    return (this.isEdit() ? this.update(solicitudProteccion) : this.create(solicitudProteccion))
      .pipe(
        map((response: ISolicitudProteccion) => {
          this.solicitudProteccion = response;
          return response.id;
        }),
        switchMap(elem => createOrDeletePaisesValidados$),
        tap(() => {
          this.findPaisesValidadosBySolicitudProteccion(this.solicitudProteccion).subscribe();
          this.checkState();
        }),
        map(() => this.solicitudProteccion.id),
      );

  }

  private get defaultTitle(): string {

    return this.solicitudProteccion.titulo || this.invencionTitulo;
  }

  private loadPaises$(): Observable<IPais[]> {
    return this.paisService.findAll().pipe(
      map(response => response.items),
      catchError(error => {
        this.logger.error(error);
        return of([]);
      })
    );
  }

  private loadTiposCaducidad$(): Observable<ITipoCaducidad[]> {
    return this.tipoCaducidadService.findAll().pipe(
      map(response => response.items),
      catchError(error => {
        this.logger.error(error);
        return of([]);
      })
    );
  }

  private enableOrDisablePaisSelector(via: IViaProteccion, paisCtrl: FormControl): void {

    if (via.paisEspecifico) {
      paisCtrl.enable();
      paisCtrl.setValue(null);
      paisCtrl.setValidators([Validators.required]);
      this.showPaisSelector.next(true);
    } else {
      this.solicitudProteccion.paisProteccion = null;
      paisCtrl.setValidators([]);
      paisCtrl.disable();
      this.showPaisSelector.next(false);
    }
    paisCtrl.updateValueAndValidity();
  }

  private create(solicitudProteccion: ISolicitudProteccion): Observable<ISolicitudProteccion> {

    return this.solicitudProteccionService.create(solicitudProteccion);
  }

  private update(solicitudProteccion: ISolicitudProteccion): Observable<ISolicitudProteccion> {

    return this.solicitudProteccionService.update(solicitudProteccion.id, solicitudProteccion);
  }

  private handleViaProteccionOnChangeValueEventSubscription(form: FormGroup): Subscription {

    return form.controls.viaProteccion.valueChanges
      .subscribe((via: IViaProteccion) => {
        this.enableOrDisablePaisSelector(via, form.controls.pais as FormControl);
        this.isExtensionInternacional$.next(via.extensionInternacional);
        this.isViaEuropea.next(via.nombre === 'Europea');
        if (!this.shouldShowFechaFinPrioridad(via)) {
          form.controls.fechaFinPrioridad.setValidators([]);
          form.controls.fechaFinPrioridad.setValue(null);
        } else {
          form.controls.fechaFinPrioridad.setValidators([Validators.required]);
          if (form.controls.viaProteccion.dirty && form.controls.fechaPrioridad.value) {
            this.resolveFechaFinPrioridad(via);
          }
        }
        form.controls.fechaFinPrioridad.updateValueAndValidity();
      });
  }

  private handleEstadoOnChangeValueEventSubscription(form: FormGroup): Subscription {

    return form.controls.estado.valueChanges
      .subscribe((estado: Estado) => {
        if (estado === Estado.CADUCADA) {
          form.controls.fechaCaducidad.setValidators([Validators.required]);
          form.controls.tipoCaducidad.setValidators([Validators.required]);
        } else {
          form.controls.fechaCaducidad.setValidators([]);
          form.controls.tipoCaducidad.setValidators([]);
          form.controls.fechaCaducidad.setValue(null);
        }
        form.controls.fechaCaducidad.updateValueAndValidity();
        form.controls.tipoCaducidad.updateValueAndValidity();
      });
  }

  private handleFechaPrioridadOnChangeValueEventSubscription(form: FormGroup): Subscription {

    return form.controls.fechaPrioridad.valueChanges
      .subscribe((fechaPri: DateTime) => {
        const via: IViaProteccion = form.controls.viaProteccion.value;
        if (!fechaPri || !via) {
          return;
        }
        if (form.controls.fechaPrioridad.dirty) {
          this.resolveFechaFinPrioridad(via);
        }
      });
  }

  public canShowFechaPrioridad(): Observable<boolean> {

    return this.isExtensionInternacional$.pipe(
      map(isExtensionInternacional => {
        return !isExtensionInternacional && TipoPropiedad.INDUSTRIAL === this.tipoPropiedad && !this.solicitudesBefore;
      })
    );
  }

  public canShowFechaFinPrioridad(): Observable<boolean> {

    return this.isExtensionInternacional$.pipe(
      map(isExtensionInternacional => {
        return isExtensionInternacional
          || (TipoPropiedad.INDUSTRIAL === this.tipoPropiedad && !this.solicitudesBefore);
      })
    );
  }

  get solicitudesBefore(): boolean {
    return this.firstSolicitudProteccionWithPrioridad.value?.id !== this.solicitudProteccion?.id;
  }

  /**
   * Determina si es Via Europea la {@link IIViaProteccion} actual
   */
  get isViaEuropea$(): Observable<boolean> {
    return this.isViaEuropea.asObservable();
  }

  public createEmptyPaisValidado(): StatusWrapper<IPaisValidado> {
    const createdElem = new StatusWrapper({} as IPaisValidado);
    createdElem.setCreated();
    return createdElem;
  }

  /**
   *  Crear un {@link IPaisValidado}.
   *
   * @param addedElem Elemento a eliminar
   */
  addPaisValidado(addedElem: StatusWrapper<IPaisValidado>) {
    addedElem.value.solicitudProteccion = { id: this.getKey() } as ISolicitudProteccion;
    const current = this.paisesValidados$.value;
    if (current.indexOf(addedElem) === -1) {
      current.push(addedElem);
      this.paisesValidados$.next(current);
    }
    this.checkState();
  }

  /**
   *  Editar un {@link IPaisValidado}.
   *
   * @param elemToEdit Elemento a eliminar
   */
  editPaisValidado(elemToEdit: StatusWrapper<IPaisValidado>) {
    if (!elemToEdit.created) {
      elemToEdit.setEdited();
    }
    this.checkState();
  }

  /**
   *  Elimina el {@link IPaisValidado}.
   *
   * @param elemToDelete Elemento a eliminar
   */
  deletePaisValidado(elemToDelete: StatusWrapper<IPaisValidado>) {
    const current = this.paisesValidados$.value;
    const index = current.findIndex((value) => value === elemToDelete);
    if (index >= 0) {
      current.splice(index, 1);
      this.paisesValidados$.next(current);
      this.paisesValidadosToDelete.push(elemToDelete);
    }
    this.checkState();
  }

  private checkState(): void {
    if (this.paisesValidados$.value.some(paisWrapper => paisWrapper.touched)) {
      return this.setChanges(true);
    }
    if (this.paisesValidadosToDelete.some(paisWrapper => !paisWrapper.created)) {
      return this.setChanges(true);
    }
  }

  private createOrUpdatePaisValidado(): Observable<ISolicitudProteccion> {
    return from(this.paisesValidados$.value.filter(elem => elem.touched))
      .pipe(
        mergeMap(elem => {
          elem.value.solicitudProteccion = this.solicitudProteccion;
          return (
            elem.created ? this.paisValidadoService.create(elem.value) : this.paisValidadoService.update(elem.value.id, elem.value)).pipe(
              catchError(err => of(void 0))
            );
        }),
      );
  }

  private deletePaisesValidados(): Observable<void> {
    return from(this.paisesValidadosToDelete.filter(elem => !elem.created)).pipe(
      mergeMap(elem => this.paisValidadoService.deleteById(elem.value.id).pipe(
        catchError(e => of(void 0))
      )),
      takeLast(1),
      tap(() => this.paisesValidadosToDelete = [])
    );
  }

  private shouldShowFechaFinPrioridad(viaProteccion: IViaProteccion): boolean {
    return viaProteccion.extensionInternacional
      || (TipoPropiedad.INDUSTRIAL === this.tipoPropiedad && !this.solicitudesBefore);
  }

  private resolveFechaFinPrioridad(via: IViaProteccion): void {
    const form = this.getFormGroup();
    let fechaPri: DateTime = form.controls.fechaPrioridad.value;
    if (this.isExtensionInternacional$.value) {
      fechaPri = this.firstSolicitudProteccionWithPrioridad.value?.fechaPrioridadSolicitud ?? fechaPri;
    }
    if (fechaPri) {
      form.controls.fechaFinPrioridad.setValue(fechaPri.plus({ months: (via ?? this.solicitudProteccion?.viaProteccion).mesesPrioridad }));
    }
  }

}
