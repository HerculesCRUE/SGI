import { FormControl, FormGroup } from '@angular/forms';
import { INotificacionCVNEntidadFinanciadora } from '@core/models/csp/notificacion-cvn-entidad-financiadora';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { IDocumento } from '@core/models/sgdoc/documento';
import { FormFragment } from '@core/services/action-service';
import { NotificacionProyectoExternoCvnService } from '@core/services/csp/notificacion-proyecto-externo-cvn/notificacion-proyecto-externo-cvn.service';
import { DocumentoService, triggerDownloadToUser } from '@core/services/sgdoc/documento.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, toArray } from 'rxjs/operators';

export class NotificacionCvnDatosGeneralesFragment extends FormFragment<INotificacionProyectoExternoCVN> {

  public notificacion: INotificacionProyectoExternoCVN;

  notificacionCVNEntidadFinanciadoras$ = new BehaviorSubject<INotificacionCVNEntidadFinanciadora[]>([]);

  constructor(
    key: number,
    private readonly logger: NGXLogger,
    private service: NotificacionProyectoExternoCvnService,
    private personaService: PersonaService,
    private documentoService: DocumentoService,
    private empresaService: EmpresaService,
    public authService: SgiAuthService
  ) {
    super(key, true);
    this.setComplete(true);
    this.notificacion = {} as INotificacionProyectoExternoCVN;
  }

  protected buildFormGroup(): FormGroup {
    return new FormGroup({
      investigador: new FormControl({ value: null, disabled: true }),
      tituloProyecto: new FormControl({ value: null, disabled: true }),
      acreditacion: new FormControl({ value: null, disabled: true }),
      codigoExterno: new FormControl({ value: null, disabled: true }),
      fechaInicio: new FormControl({ value: null, disabled: true }),
      fechaFin: new FormControl({ value: null, disabled: true }),
      ambitoGeografico: new FormControl({ value: null, disabled: true }),
      gradoContribucion: new FormControl({ value: null, disabled: true }),
      responsable: new FormControl({ value: null, disabled: true }),
      entidadParticipacion: new FormControl({ value: null, disabled: true }),
      nombrePrograma: new FormControl({ value: null, disabled: true }),
      importeTotal: new FormControl({ value: null, disabled: true }),
      porcentaje: new FormControl({ value: null, disabled: true }),

    });
  }

  buildPatch(notificacion: INotificacionProyectoExternoCVN): { [key: string]: any } {
    this.notificacion = notificacion;
    return {
      investigador: this.notificacion?.solicitante,
      tituloProyecto: this.notificacion?.titulo,
      acreditacion: this.notificacion?.documento.documentoRef
        ? this.notificacion?.documento?.nombre : this.notificacion.urlDocumentoAcreditacion,
      codigoExterno: this.notificacion?.codExterno,
      fechaInicio: this.notificacion?.fechaInicio,
      fechaFin: this.notificacion?.fechaFin,
      ambitoGeografico: this.notificacion?.ambitoGeografico,
      gradoContribucion: this.notificacion?.gradoContribucion,
      responsable: this.notificacion?.responsable.id ? this.notificacion?.responsable : this.notificacion?.datosResponsable,
      entidadParticipacion: this.notificacion?.entidadParticipacion.id ?
        this.notificacion?.entidadParticipacion : this.notificacion?.datosEntidadParticipacion,
      nombrePrograma: this.notificacion?.nombrePrograma,
      importeTotal: this.notificacion?.importeTotal,
      porcentaje: this.notificacion?.porcentajeSubvencion,
    };
  }

  protected initializer(key: string | number): Observable<INotificacionProyectoExternoCVN> {

    return this.service.findById(key as number).pipe(
      switchMap(notificacion => {
        this.notificacion = notificacion;
        if (notificacion.responsable?.id) {
          return this.personaService.findById(notificacion.responsable?.id).pipe(
            map(responsable => {
              notificacion.responsable = responsable;
              return notificacion;
            })
          );
        } else {
          return of(notificacion);
        }
      }),
      switchMap(notificacion => {
        if (notificacion.solicitante?.id) {
          return this.personaService.findById(notificacion.solicitante?.id).pipe(
            map(solicitante => {
              notificacion.solicitante = solicitante;
              return notificacion;
            })
          );
        } else {
          return of(notificacion);
        }
      }),
      switchMap(notificacion => {
        if (notificacion.documento?.documentoRef) {
          return this.documentoService.getInfoFichero(notificacion.documento?.documentoRef).pipe(
            map(documento => {
              notificacion.documento = documento;
              return notificacion;
            }),
            catchError((error) => {
              this.logger.error(error);
              return EMPTY;
            }));
        } else {
          return of(notificacion);
        }
      }),
      switchMap(notificacion => {
        if (notificacion.id) {
          return this.service.findAllNotificacionesCvnEntidadFinanciadoraByNotificacionId(notificacion.id).pipe(
            switchMap(entidadesFinanciadoras => {
              return from(entidadesFinanciadoras.items).pipe(
                mergeMap(entidad => {
                  if (entidad.entidadFinanciadora.id) {
                    return this.empresaService.findById(entidad.entidadFinanciadora.id).pipe(
                      map(entidadFinanciadora => {
                        entidad.entidadFinanciadora = entidadFinanciadora;
                        return entidadFinanciadora;
                      })
                    );
                  } else {
                    return of(entidad.entidadFinanciadora);
                  }
                }),
                toArray(),
                map(() => {
                  this.notificacionCVNEntidadFinanciadoras$.next(entidadesFinanciadoras.items);
                  return entidadesFinanciadoras;
                })

              );
            }),
            map(() => notificacion)
          );
        } else {
          return of(notificacion);
        }
      }),
      switchMap(notificacion => {
        if (notificacion.entidadParticipacion?.id) {
          return this.empresaService.findById(notificacion.entidadParticipacion?.id).pipe(
            map(entidadParticipacion => {
              notificacion.entidadParticipacion = entidadParticipacion;
              return notificacion;
            })
          );
        } else {
          return of(notificacion);
        }
      }));
  }

  getValue(): INotificacionProyectoExternoCVN {
    return this.notificacion;
  }

  saveOrUpdate(): Observable<number> {
    return null;
  }

  /**
   * Descarga el documento de la Notificacion
   * @param documentoRef referencia del documento
   */
  descargarDocumento(): void {
    if (this.notificacion.documento?.documentoRef) {
      this.documentoService.downloadFichero(this.notificacion.documento.documentoRef).subscribe(response => {
        triggerDownloadToUser(response, this.notificacion.documento.nombre);
      });
    }
  }

}
