import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { FormFragment } from '@core/services/action-service';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
import { DocumentoRequeridoSolicitudService } from '@core/services/csp/documento-requerido-solicitud.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class ConvocatoriaConfiguracionSolicitudesFragment extends FormFragment<IConfiguracionSolicitud> {
  private configuracionSolicitud: IConfiguracionSolicitud;
  documentosRequeridos$ = new BehaviorSubject<StatusWrapper<IDocumentoRequeridoSolicitud>[]>([]);
  private documentosRequeridosEliminados: StatusWrapper<IDocumentoRequeridoSolicitud>[] = [];
  public convocatoriaFases$: BehaviorSubject<IConvocatoriaFase[]> = new BehaviorSubject<IConvocatoriaFase[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private configuracionSolicitudService: ConfiguracionSolicitudService,
    private documentoRequeridoSolicitudService: DocumentoRequeridoSolicitudService,
    public isConvocatoriaVinculada: boolean,
    public hasEditPerm: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.configuracionSolicitud = {} as IConfiguracionSolicitud;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      tramitacionSGI: new FormControl(false, Validators.required),
      fasePresentacionSolicitudes: new FormControl(null),
      formularioSolicitud: new FormControl(null),
      fechaInicioFase: new FormControl({ value: null, disabled: true }),
      fechaFinFase: new FormControl({ value: null, disabled: true }),
      importeMaximoSolicitud: new FormControl(null, Validators.maxLength(50)),
    });
    if (!this.hasEditPerm) {
      form.disable();
    } else if (this.isConvocatoriaVinculada) {
      form.controls.formularioSolicitud.disable();
    }

    this.subscriptions.push(form.controls.tramitacionSGI.valueChanges.subscribe(
      (value) => {
        if (value) {
          form.controls.fasePresentacionSolicitudes.setValidators(Validators.required);
        }
        else {
          form.controls.fasePresentacionSolicitudes.setValidators([]);
        }
        form.controls.fasePresentacionSolicitudes.updateValueAndValidity();
        form.controls.fasePresentacionSolicitudes.markAsTouched();
      }
    ));

    this.subscriptions.push(form.controls.fasePresentacionSolicitudes.valueChanges.subscribe(
      (value) => {
        if (value) {
          form.controls.fechaInicioFase.setValue(value?.fechaInicio ?? null);
          form.controls.fechaFinFase.setValue(value?.fechaFin ?? null);
        } else {
          form.controls.fechaInicioFase.setValue(null);
          form.controls.fechaFinFase.setValue(null);
        }
      }
    ));

    return form;
  }

  protected buildPatch(configuracionSolicitud: IConfiguracionSolicitud): { [key: string]: any; } {
    return {
      tramitacionSGI: configuracionSolicitud?.tramitacionSGI ?? false,
      fasePresentacionSolicitudes: configuracionSolicitud?.fasePresentacionSolicitudes ?? null,
      fechaInicioFase: configuracionSolicitud?.fasePresentacionSolicitudes?.fechaInicio ?? null,
      fechaFinFase: configuracionSolicitud?.fasePresentacionSolicitudes?.fechaFin ?? null,
      importeMaximoSolicitud: configuracionSolicitud?.importeMaximoSolicitud ?? null,
      formularioSolicitud: configuracionSolicitud?.formularioSolicitud ?? null
    };
  }

  protected initializer(key: number): Observable<IConfiguracionSolicitud> {
    return this.configuracionSolicitudService.findById(key).pipe(
      switchMap((configuracionSolicitud) => {
        this.configuracionSolicitud = configuracionSolicitud;
        return this.configuracionSolicitudService.findAllConvocatoriaDocumentoRequeridoSolicitud(key).pipe(
          switchMap((documentosRequeridos) => {
            const documentos = documentosRequeridos.items;
            if (documentos.length > 0) {
              this.documentosRequeridos$.next(documentosRequeridos.items.map(
                doc => new StatusWrapper<IDocumentoRequeridoSolicitud>(doc))
              );
            }
            return of(this.configuracionSolicitud);
          })
        );
      }),
      catchError((err) => {
        this.logger.error(err);
        return of({} as IConfiguracionSolicitud);
      })
    );
  }

  public setFases(convocatoriaFases: IConvocatoriaFase[]) {
    this.convocatoriaFases$.next(convocatoriaFases);
  }

  getValue(): IConfiguracionSolicitud {
    const controls = this.getFormGroup().controls;
    if (this.configuracionSolicitud === null) {
      this.configuracionSolicitud = {} as IConfiguracionSolicitud;
    }
    this.configuracionSolicitud.tramitacionSGI = controls.tramitacionSGI.value ? true : false;
    this.configuracionSolicitud.fasePresentacionSolicitudes = controls.fasePresentacionSolicitudes.value;

    this.configuracionSolicitud.formularioSolicitud = controls.formularioSolicitud.value;
    this.configuracionSolicitud.importeMaximoSolicitud = controls.importeMaximoSolicitud.value;

    return this.configuracionSolicitud;
  }

  public addDocumentoRequerido(docRequerido: IDocumentoRequeridoSolicitud): void {
    const wrapped = new StatusWrapper<IDocumentoRequeridoSolicitud>(docRequerido);
    wrapped.setCreated();
    const current = this.documentosRequeridos$.value;
    current.push(wrapped);
    this.documentosRequeridos$.next(current);
    this.setChanges(true);
  }

  public deleteDocumentoRequerido(wrapper: StatusWrapper<IDocumentoRequeridoSolicitud>): void {
    const current = this.documentosRequeridos$.value;
    const index = current.findIndex(
      (value: StatusWrapper<IDocumentoRequeridoSolicitud>) => value === wrapper
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.documentosRequeridosEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.documentosRequeridos$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<number> {
    const configuracion = this.getValue();
    const observable$ = configuracion.id ? this.update(configuracion) : this.create(configuracion);
    return observable$.pipe(
      map(value => {
        this.configuracionSolicitud = value;
        return this.configuracionSolicitud.id;
      })
    );
  }

  private create(configuracion: IConfiguracionSolicitud): Observable<IConfiguracionSolicitud> {
    configuracion.convocatoriaId = this.getKey() as number;

    if (configuracion.fasePresentacionSolicitudes != null && !configuracion.fasePresentacionSolicitudes.id) {
      const fasePresentacionSolicitudes = this.convocatoriaFases$.value.find(plazoFase =>
        plazoFase.tipoFase.id === configuracion.fasePresentacionSolicitudes.tipoFase.id);
      configuracion.fasePresentacionSolicitudes = { id: fasePresentacionSolicitudes.id } as IConvocatoriaFase;
      this.getFormGroup().controls.fasePresentacionSolicitudes.setValue(fasePresentacionSolicitudes, { onlySelf: true, emitEvent: false });
    }

    return this.configuracionSolicitudService.create(configuracion).pipe(
      tap(result => this.configuracionSolicitud = result),
      switchMap(result => this.saveOrUpdateDocumentoRequeridos(result))
    );
  }

  private update(configuracion: IConfiguracionSolicitud): Observable<IConfiguracionSolicitud> {
    if (typeof configuracion.fasePresentacionSolicitudes === 'string') {
      configuracion.fasePresentacionSolicitudes = null;
    }

    if (configuracion.fasePresentacionSolicitudes != null && !configuracion.fasePresentacionSolicitudes.id) {
      const fasePresentacionSolicitudes = this.convocatoriaFases$.value.find(plazoFase =>
        plazoFase.tipoFase.id === configuracion.fasePresentacionSolicitudes.tipoFase.id);
      configuracion.fasePresentacionSolicitudes = { id: fasePresentacionSolicitudes.id } as IConvocatoriaFase;
      this.getFormGroup().controls.fasePresentacionSolicitudes.setValue(fasePresentacionSolicitudes, { onlySelf: true, emitEvent: false });
    }

    return this.configuracionSolicitudService.update(Number(this.getKey()), configuracion).pipe(
      tap(result => this.configuracionSolicitud = result),
      switchMap(result => this.saveOrUpdateDocumentoRequeridos(result))
    );
  }

  private saveOrUpdateDocumentoRequeridos(result: IConfiguracionSolicitud) {
    return merge(
      this.deleteDocumentoRequeridos(),
      this.updateDocumentoRequeridos(result),
      this.createDocumentoRequeridos(result)
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      }),
      map(() => result)
    );
  }

  private deleteDocumentoRequeridos(): Observable<void> {
    if (this.documentosRequeridosEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.documentosRequeridosEliminados).pipe(
      mergeMap((wrapped) => {
        return this.documentoRequeridoSolicitudService.deleteById(wrapped.value.id)
          .pipe(
            tap(() => {
              this.documentosRequeridosEliminados = this.documentosRequeridosEliminados.filter(
                deletedModelo => deletedModelo.value.id !== wrapped.value.id);
            })
          );
      })
    );
  }

  private updateDocumentoRequeridos(result: IConfiguracionSolicitud): Observable<void> {
    const editedDocumentos = this.documentosRequeridos$.value.filter((value) => value.value.id && value.edited);
    if (editedDocumentos.length === 0) {
      return of(void 0);
    }
    editedDocumentos.forEach(documento =>
      documento.value.configuracionSolicitudId = result.id);
    return from(editedDocumentos).pipe(
      mergeMap((wrapped) => {
        return this.documentoRequeridoSolicitudService.update(wrapped.value.id, wrapped.value).pipe(
          map((updatedEntidad) => {
            const index = this.documentosRequeridos$.value.findIndex((currentEntidad) => currentEntidad === wrapped);
            this.documentosRequeridos$.value[index] = new StatusWrapper<IDocumentoRequeridoSolicitud>(updatedEntidad);
          })
        );
      })
    );
  }

  private createDocumentoRequeridos(configuracion: IConfiguracionSolicitud): Observable<void> {
    const createdDocumentos = this.documentosRequeridos$.value.filter((value) => !value.value.id && value.created);
    if (createdDocumentos.length === 0) {
      return of(void 0);
    }
    createdDocumentos.forEach(documento => {
      documento.value.configuracionSolicitudId = configuracion.id;
    });
    return from(createdDocumentos).pipe(
      mergeMap((wrapped) => {
        return this.documentoRequeridoSolicitudService.create(wrapped.value).pipe(
          map((createdEntidad) => {
            const index = this.documentosRequeridos$.value.findIndex((currentEntidad) => currentEntidad === wrapped);
            this.documentosRequeridos$[index] = new StatusWrapper<IDocumentoRequeridoSolicitud>(createdEntidad);
          })
        );
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.documentosRequeridos$.value.some((wrapper) => wrapper.touched);
    return (this.documentosRequeridosEliminados.length > 0 || touched);
  }
}
