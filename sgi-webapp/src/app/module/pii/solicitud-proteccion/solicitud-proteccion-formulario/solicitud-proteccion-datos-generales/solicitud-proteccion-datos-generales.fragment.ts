import { FormControl, FormGroup, Validators } from '@angular/forms';
import { TipoPropiedad } from '@core/enums/tipo-propiedad';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { IViaProteccion } from '@core/models/pii/via-proteccion';
import { IPais } from '@core/models/sgo/pais';
import { FormFragment } from '@core/services/action-service';
import { SolicitudProteccionService } from '@core/services/pii/invencion/solicitud-proteccion/solicitud-proteccion.service';
import { ViaProteccionService } from '@core/services/pii/via-proteccion/via-proteccion.service';
import { PaisService } from '@core/services/sgo/pais/pais.service';
import { DateValidator } from '@core/validators/date-validator';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, of, Subscription } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export class SolicitudProteccionDatosGeneralesFragment extends FormFragment<ISolicitudProteccion> {

  private readonly TITULO_MAX_LENGTH = 50;
  private readonly COMENTARIOS_MAX_LENGTH = 250;
  private readonly NUMERO_REGISTRO_MAX_LENGTH = 24;
  private readonly NUMERO_SOLICITUD_MAX_LENGTH = 24;

  public viasProteccion$ = new BehaviorSubject<IViaProteccion[]>([]);
  public paises$ = new BehaviorSubject<IPais[]>([]);

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
    private paisService: PaisService
  ) {
    super(key);
    this.solicitudProteccion = {
      invencion:
        { id: invencionId }
    } as ISolicitudProteccion;

    this.loadViasProteccion();
    this.loadPaises();
  }

  protected buildFormGroup(): FormGroup {

    const form: FormGroup = new FormGroup({
      titulo: new FormControl(this.defaultTitle, [Validators.maxLength(this.TITULO_MAX_LENGTH), Validators.required]),
      viaProteccion: new FormControl('', Validators.required),
      pais: new FormControl(null, [Validators.required]),
      fechaPrioridad: new FormControl('', [Validators.required]),
      fechaFinPrioridad: new FormControl(null, [Validators.required]),
      numeroSolicitud: new FormControl('', [Validators.required, Validators.maxLength(this.NUMERO_SOLICITUD_MAX_LENGTH)]),
      agentePropiedad: new FormControl('', []),
      comentarios: new FormControl('', [Validators.maxLength(this.COMENTARIOS_MAX_LENGTH)]),
      numeroRegistro: new FormControl('', [Validators.maxLength(this.NUMERO_REGISTRO_MAX_LENGTH)])
    }, {
      validators: [
        DateValidator.isAfter('fechaPrioridad', 'fechaFinPrioridad')
      ]
    });

    if (this.readonly) {
      form.disable();
    } else {

      this.subscriptions.push(
        this.handleViaProteccionOnChangeValueEventSubscription(form)
      );

      this.subscriptions.push(
        this.handleFechaPrioridadOnChangeValueEventSubscription(form)
      );
    }
    return form;
  }

  protected buildPatch(solicitudProteccion: ISolicitudProteccion): { [key: string]: any; } {

    this.solicitudProteccion = solicitudProteccion;

    return {
      titulo: this.defaultTitle,
      viaProteccion: solicitudProteccion.viaProteccion,
      pais: solicitudProteccion.paisProteccion
    };
  }

  protected initializer(key: number): Observable<ISolicitudProteccion> {

    return this.solicitudProteccionService.findById(key).pipe(
      map(response => {
        this.solicitudProteccion = response;
        return response;
      }),
      catchError((err) => {
        this.logger.error(err);
        return of(void 0);
      })
    );
  }

  getValue(): ISolicitudProteccion {

    const formCtrls = this.getFormGroup().controls;

    return {
      agentePropiedad: formCtrls.agentePropiedad.value,
      comentarios: formCtrls.comentarios.value,
      fechaFinPriorPresFasNacRec: formCtrls.fechaFinPrioridad.value,
      fechaPrioridadSolicitud: formCtrls.fechaPrioridad.value,
      numeroSolicitud: formCtrls.numeroSolicitud.value,
      titulo: formCtrls.titulo.value,
      viaProteccion: formCtrls.viaProteccion.value,
      paisProteccion: formCtrls.pais.value,
      numeroRegistro: formCtrls.numeroRegistro.value,
      invencion: { id: this.invencionId },
    } as ISolicitudProteccion;
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

  private loadViasProteccion(): void {

    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('tipoPropiedad', SgiRestFilterOperator.EQUALS, this.tipoPropiedad)
        .and('activo', SgiRestFilterOperator.EQUALS, 'true')
    };

    this.viaProteccionService.findTodos(options).pipe(
      map(response => response.items),
      catchError(error => {
        this.logger.error(error);
        return of(void 0);
      })
    ).subscribe((vias: IViaProteccion[]) => this.viasProteccion$.next(vias));
  }

  private loadPaises(): void {
    this.paisService.findAll().pipe(
      map(response => response.items),
      catchError(error => {
        this.logger.error(error);
        return of(void 0);
      })
    ).subscribe(paises => this.paises$.next(paises));
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
