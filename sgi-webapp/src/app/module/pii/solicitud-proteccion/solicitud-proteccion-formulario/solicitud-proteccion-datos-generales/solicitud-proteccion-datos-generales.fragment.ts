import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { IInvencion } from '@core/models/pii/invencion';
import { Estado, ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { ITipoCaducidad } from '@core/models/pii/tipo-caducidad';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { IPais } from '@core/models/sgo/pais';
import { FormFragment } from '@core/services/action-service';
import { SolicitudProteccionService } from '@core/services/pii/solicitud-proteccion/solicitud-proteccion.service';
import { TipoCaducidadService } from '@core/services/pii/tipo-caducidad/tipo-caducidad.service';
import { ViaProteccionService } from '@core/services/pii/via-proteccion/via-proteccion.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { DateValidator } from '@core/validators/date-validator';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, forkJoin, Observable, of, Subscription } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

export class SolicitudProteccionDatosGeneralesFragment extends FormFragment<ISolicitudProteccion> {

  private readonly TITULO_MAX_LENGTH = 250;
  private readonly COMENTARIOS_MAX_LENGTH = 250;
  private readonly NUMERO_REGISTRO_MAX_LENGTH = 24;
  private readonly NUMERO_SOLICITUD_MAX_LENGTH = 24;
  private readonly NUMERO_PUBLICACION_MAX_LENGTH = 24;
  private readonly NUMERO_CONCESION_MAX_LENGTH = 24;

  public viasProteccion$ = new BehaviorSubject<IViaProteccion[]>([]);
  public paises$ = new BehaviorSubject<IPais[]>([]);
  public tiposCaducidad$ = new BehaviorSubject<ITipoCaducidad[]>([]);

  public solicitudProteccion: ISolicitudProteccion;

  public showPaisSelector = new BehaviorSubject<boolean>(false);

  public get TipoPropiedad(): any {
    return TipoPropiedad;
  }

  public get isExtensionInternacional(): Observable<boolean> {
    return this.isExtensionInternacional$;
  }

  private isExtensionInternacional$ = new BehaviorSubject<boolean>(false);

  constructor(
    private logger: NGXLogger,
    key: number,
    private invencionId: number,
    public tipoPropiedad: TipoPropiedad,
    private invencionTitulo: string,
    public solicitudesBefore: boolean,
    private solicitudProteccionService: SolicitudProteccionService,
    public readonly: boolean,
    private viaProteccionService: ViaProteccionService,
    private paisService: PaisService,
    private empresaService: EmpresaService,
    private tipoCaducidadService: TipoCaducidadService
  ) {
    super(key);
    this.solicitudProteccion = {
      invencion:
        { id: invencionId }
    } as ISolicitudProteccion;
    if (!key) {
      this.loadViasProteccion$().subscribe(viasProteccion => {
        this.viasProteccion$.next(viasProteccion);
      });
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
      fechaFinPriorPresFasNacRec: solicitudProteccion.fechaFinPriorPresFasNacRec,
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
      viasProteccion: this.loadViasProteccion$(),
      paises: this.loadPaises$(),
      tiposCaducidad: this.loadTiposCaducidad$()
    })
      .pipe(
        tap(({ viasProteccion }) => {
          this.viasProteccion$.next(viasProteccion);
        }),
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
        catchError((err) => {
          this.logger.error(err);
          return of(void 0);
        })
      );
  }

  getValue(): ISolicitudProteccion {

    const formCtrls = this.getFormGroup().controls;

    this.solicitudProteccion.agentePropiedad = formCtrls.agentePropiedad.value;
    this.solicitudProteccion.comentarios = formCtrls.comentarios.value;
    this.solicitudProteccion.fechaFinPriorPresFasNacRec = formCtrls.fechaFinPrioridad.value;
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

  saveOrUpdate(): Observable<number> {

    const solicitudProteccion = this.getValue();
    const saveActionObservable$ = this.isEdit() ? this.update(solicitudProteccion) : this.create(solicitudProteccion);
    return saveActionObservable$.pipe(
      map((response: ISolicitudProteccion) => {
        return response.id;
      })
    );
  }

  private get defaultTitle(): string {

    return this.solicitudProteccion.titulo || this.invencionTitulo;
  }

  private loadViasProteccion$(): Observable<IViaProteccion[]> {

    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('tipoPropiedad', SgiRestFilterOperator.EQUALS, this.tipoPropiedad)
        .and('activo', SgiRestFilterOperator.EQUALS, 'true')
    };

    return this.viaProteccionService.findTodos(options).pipe(
      map(response => response.items),
      catchError(error => {
        this.logger.error(error);
        return of([]);
      }),
    );
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
        if (!this.shouldShowFechaFinPrioridad(via)) {
          form.controls.fechaFinPrioridad.setValidators([]);
          form.controls.fechaFinPrioridad.setValue(null);
        } else {
          form.controls.fechaFinPrioridad.setValidators([Validators.required]);
          const fechaPri: DateTime = form.controls.fechaPrioridad.value;

          if (fechaPri) {
            this.resolveFechaFinPrioridad(via, form.controls.fechaFinPrioridad as FormControl, fechaPri);
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
        this.resolveFechaFinPrioridad(via, form.controls.fechaFinPrioridad as FormControl, fechaPri);
      });
  }

  public canShowFechaPrioridad(): Observable<boolean> {

    return this.isExtensionInternacional$.pipe(
      map(isPCT => {
        return !isPCT && TipoPropiedad.INDUSTRIAL === this.tipoPropiedad && !this.solicitudesBefore;
      })
    );
  }

  public canShowFechaFinPrioridad(): Observable<boolean> {

    return this.isExtensionInternacional$.pipe(
      map(isPCT => {
        return isPCT
          || (TipoPropiedad.INDUSTRIAL === this.tipoPropiedad && !this.solicitudesBefore);
      })
    );
  }

  private shouldShowFechaFinPrioridad(viaProteccion: IViaProteccion): boolean {

    return viaProteccion.extensionInternacional
      || (TipoPropiedad.INDUSTRIAL === this.tipoPropiedad && !this.solicitudesBefore);
  }

  private resolveFechaFinPrioridad(via: IViaProteccion, fechaFinPriCtrl: FormControl, fechaPri: DateTime): void {

    if ((this.tipoPropiedad === TipoPropiedad.INDUSTRIAL && !this.solicitudesBefore)
      || via.extensionInternacional) {

      fechaFinPriCtrl.setValue(fechaPri.plus({ months: via.mesesPrioridad }));
    }
  }
}
