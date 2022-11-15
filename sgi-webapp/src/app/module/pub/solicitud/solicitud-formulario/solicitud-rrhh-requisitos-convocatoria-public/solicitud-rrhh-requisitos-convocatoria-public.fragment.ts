import { ValidacionRequisitosEquipoIp } from '@core/enums/validaciones-requisitos-equipo-ip';
import { IConvocatoriaRequisitoEquipo } from '@core/models/csp/convocatoria-requisito-equipo';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { Estado, IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { IRequisitoEquipoCategoriaProfesional } from '@core/models/csp/requisito-equipo-categoria-profesional';
import { IRequisitoEquipoNivelAcademico } from '@core/models/csp/requisito-equipo-nivel-academico';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { ISolicitudRrhhRequisitoCategoria } from '@core/models/csp/solicitud-rrhh-requisito-categoria';
import { ISolicitudRrhhRequisitoNivelAcademico } from '@core/models/csp/solicitud-rrhh-requisito-nivel-academico';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { IPersona } from '@core/models/sgp/persona';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { ConvocatoriaRequisitoEquipoPublicService } from '@core/services/csp/convocatoria-requisito-equipo-public.service';
import { ConvocatoriaRequisitoIPPublicService } from '@core/services/csp/convocatoria-requisito-ip-public.service';
import { SolicitudRrhhRequisitoCategoriaPublicService } from '@core/services/csp/solicitud-rrhh-requisito-categoria/solicitud-rrhh-requisito-categoria-public.service';
import { SolicitudRrhhRequisitoNivelAcademicoPublicService } from '@core/services/csp/solicitud-rrhh-requisito-nivel-academico/solicitud-rrhh-requisito-nivel-academico-public.service';
import { SolicitudRrhhPublicService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh-public.service';
import { DocumentoPublicService } from '@core/services/sgdoc/documento-public.service';
import { CategoriaProfesionalPublicService } from '@core/services/sgp/categoria-profesional-public.service';
import { DatosAcademicosPublicService } from '@core/services/sgp/datos-academicos-public.service';
import { NivelAcademicoPublicService } from '@core/services/sgp/nivel-academico-public.service';
import { VinculacionPublicService } from '@core/services/sgp/vinculacion/vinculacion-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateTime } from 'luxon';
import { BehaviorSubject, forkJoin, from, merge, Observable, of, zip } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export enum HelpIconClass {
  DANGER = 'danger',
}

interface HelpIcon {
  class: HelpIconClass;
  tooltip: ValidacionRequisitosEquipoIp;
}

export interface RequisitoCategoriaProfesionalExigido {
  nivelExigido: ICategoriaProfesional;
  fechaMaximaObtencion: DateTime;
  fechaMinimaObtencion: DateTime;
  help: HelpIcon;
}

export interface RequisitoCategoriaProfesionalExigidoAndAcreditado extends RequisitoCategoriaProfesionalExigido {
  nivelAcreditado: ISolicitudRrhhRequisitoCategoria;
}

export interface RequisitoNivelAcademicoExigido {
  nivelExigido: INivelAcademico;
  fechaMaximaObtencion: DateTime;
  fechaMinimaObtencion: DateTime;
  help: HelpIcon;
}

export interface RequisitoNivelAcademicoExigidoAndAcreditado extends RequisitoNivelAcademicoExigido {
  nivelAcreditado: ISolicitudRrhhRequisitoNivelAcademico;
}

export class SolicitudRrhhRequisitosConvocatoriaPublicFragment extends Fragment {
  acreditacionesCategoriaProfesionalEliminadas: ISolicitudRrhhRequisitoCategoria[] = [];
  acreditacionesNivelAcademicoEliminadas: ISolicitudRrhhRequisitoNivelAcademico[] = [];
  private documentosRefUnrelated: string[] = [];

  requisitosCategoriasExigidasSolicitante$ = new BehaviorSubject<StatusWrapper<RequisitoCategoriaProfesionalExigidoAndAcreditado>[]>([]);
  requisitosNivelesAcademicosExigidosSolicitante$ = new BehaviorSubject<StatusWrapper<RequisitoNivelAcademicoExigidoAndAcreditado>[]>([]);

  requisitosCategoriasExigidasTutor$ = new BehaviorSubject<StatusWrapper<RequisitoCategoriaProfesionalExigido>[]>([]);
  requisitosNivelesAcademicosExigidosTutor$ = new BehaviorSubject<StatusWrapper<RequisitoNivelAcademicoExigido>[]>([]);

  solicitante$ = new BehaviorSubject<IPersona>(null);
  tutor$ = new BehaviorSubject<IPersona>(null);

  needDocumentoAcreditativo = false;

  readonly solicitudId: number;

  constructor(
    key: number,
    private readonly solicitudPublicKey,
    readonly convocatoriaId: number,
    readonly estado: IEstadoSolicitud,
    readonly isInvestigador,
    private convocatoriaService: ConvocatoriaPublicService,
    private solicitudRrhhService: SolicitudRrhhPublicService,
    private solicitudRrhhRequisitoCategoriaService: SolicitudRrhhRequisitoCategoriaPublicService,
    private solicitudRrhhRequisitoNivelAcademicoService: SolicitudRrhhRequisitoNivelAcademicoPublicService,
    private convocatoriaRequisitoEquipoService: ConvocatoriaRequisitoEquipoPublicService,
    private convocatoriaRequisitoIpService: ConvocatoriaRequisitoIPPublicService,
    private nivelAcademicoService: NivelAcademicoPublicService,
    private categoriasProfesionalesService: CategoriaProfesionalPublicService,
    private datosAcademicosService: DatosAcademicosPublicService,
    private vinculacionService: VinculacionPublicService,
    private documentoService: DocumentoPublicService,
    public readonly: boolean
  ) {
    super(key);
    this.setComplete(true);
    this.solicitudId = key;
  }

  protected onInitialize(): void {
    const id = this.getKey() as number;
    if (!!id && !!this.convocatoriaId) {
      this.subscriptions.push(
        forkJoin({
          requisitosEquipo: this.convocatoriaRequisitoEquipoService.findByConvocatoriaId(this.convocatoriaId),
          requisitosEquipoCategoria: this.getRequisitosEquipoCategoriasProfesionales(this.convocatoriaId),
          requisitosEquipoNivelAcademico: this.getRequisitosEquipoNivelesAcademicos(this.convocatoriaId),
          requisitosIp: this.convocatoriaRequisitoIpService.getRequisitoIPConvocatoria(this.convocatoriaId),
          requisitosIpCategoria: this.getRequisitosIpCategoriasProfesionales(this.convocatoriaId),
          requisitosIpNivelAcademico: this.getRequisitosIpNivelesAcademicos(this.convocatoriaId),
          nivelesCategoriaAcreditados: this.solicitudRrhhService.findAllRequisitosCategoriaAcreditados(this.solicitudPublicKey),
          nivelesAcademicosAcreditados: this.solicitudRrhhService.findAllRequisitosNivelAcademicoAcreditados(this.solicitudPublicKey)
        }).subscribe(response => {

          this.initializeRequisitosCategoriasExigidasSolicitante$(
            response.requisitosIpCategoria,
            response.requisitosIp,
            response.nivelesCategoriaAcreditados.items
          );

          this.initializeRequisitosCategoriasExigidasTutor$(
            response.requisitosEquipoCategoria,
            response.requisitosEquipo,
          );

          this.initializeRequisitosNivelesAcademicosExigidosSolicitante$(
            response.requisitosIpNivelAcademico,
            response.requisitosIp,
            response.nivelesAcademicosAcreditados.items
          );

          this.initializeRequisitosNivelesAcademicosExigidosTutor$(
            response.requisitosEquipoNivelAcademico,
            response.requisitosEquipo
          );

          this.validateRequisitosCategoriasExigidasSolicitante(this.solicitante$.value);
          this.validateRequisitosCategoriasExigidasTutor(this.tutor$.value);
          this.validateRequisitosNivelesAcademicosExigidosSolicitante(this.solicitante$.value);
          this.validateRequisitosNivelesAcademicosExigidosTutor(this.tutor$.value);
          this.checkRequisitosAcreditatos();

        })
      );

      this.subscriptions.push(
        this.solicitante$.pipe(
          switchMap(solicitante => this.fillVinculaciones(solicitante)),
          switchMap(solicitante => this.fillDatosAcademicos(solicitante))
        ).subscribe(solicitante => {
          this.validateRequisitosCategoriasExigidasSolicitante(solicitante);
          this.validateRequisitosNivelesAcademicosExigidosSolicitante(solicitante);
        })
      );

      this.subscriptions.push(
        this.tutor$.pipe(
          switchMap(tutor => this.fillVinculaciones(tutor)),
          switchMap(tutor => this.fillDatosAcademicos(tutor))
        ).subscribe(tutor => {
          this.validateRequisitosCategoriasExigidasTutor(tutor);
          this.validateRequisitosNivelesAcademicosExigidosTutor(tutor);
        })
      );

    }
  }

  private createAcreditacionesCategoriasProfesionales(solicitudRrhhId: number): Observable<void> {
    const createdAcreditaciones = this.requisitosCategoriasExigidasSolicitante$.value
      .filter(requisitoAcreditacion => !!requisitoAcreditacion.value.nivelAcreditado && requisitoAcreditacion.created)
      .map(requisitoAcreditacion => {
        requisitoAcreditacion.value.nivelAcreditado.solicitudRrhhId = solicitudRrhhId;
        return requisitoAcreditacion;
      });

    if (createdAcreditaciones.length === 0) {
      return of(void 0);
    }

    return from(createdAcreditaciones).pipe(
      mergeMap((wrappedRequisito) => {
        return this.solicitudRrhhRequisitoCategoriaService.create(this.solicitudPublicKey, wrappedRequisito.value.nivelAcreditado).pipe(
          map((createdRequisito) => {
            const index = this.requisitosCategoriasExigidasSolicitante$.value
              .findIndex(currentRequisito => currentRequisito.value.nivelExigido.id === wrappedRequisito.value.nivelExigido.id);

            wrappedRequisito.value.nivelAcreditado = createdRequisito;

            this.requisitosCategoriasExigidasSolicitante$.value[index] =
              new StatusWrapper<RequisitoCategoriaProfesionalExigidoAndAcreditado>(wrappedRequisito.value);
            this.requisitosCategoriasExigidasSolicitante$.next(this.requisitosCategoriasExigidasSolicitante$.value);
          })
        );
      }));
  }

  private createAcreditacionesNivelesAcademicos(solicitudRrhhId: number): Observable<void> {
    const createdAcreditaciones = this.requisitosNivelesAcademicosExigidosSolicitante$.value
      .filter(requisitoAcreditacion => !!requisitoAcreditacion.value.nivelAcreditado?.documento && requisitoAcreditacion.created)
      .map(requisitoAcreditacion => {
        requisitoAcreditacion.value.nivelAcreditado.solicitudRrhhId = solicitudRrhhId;
        return requisitoAcreditacion;
      });

    if (createdAcreditaciones.length === 0) {
      return of(void 0);
    }

    return from(createdAcreditaciones).pipe(
      mergeMap((wrappedRequisito) => {
        return this.solicitudRrhhRequisitoNivelAcademicoService.create(
          this.solicitudPublicKey,
          wrappedRequisito.value.nivelAcreditado
        ).pipe(
          map((createdRequisito) => {
            const index = this.requisitosNivelesAcademicosExigidosSolicitante$.value
              .findIndex(currentRequisito => currentRequisito.value.nivelExigido.id === wrappedRequisito.value.nivelExigido.id);

            wrappedRequisito.value.nivelAcreditado = createdRequisito;

            this.requisitosNivelesAcademicosExigidosSolicitante$.value[index] =
              new StatusWrapper<RequisitoNivelAcademicoExigidoAndAcreditado>(wrappedRequisito.value);
            this.requisitosNivelesAcademicosExigidosSolicitante$.next(this.requisitosNivelesAcademicosExigidosSolicitante$.value);
          })
        );
      }));
  }

  private deleteAcreditacionesCategoriasProfesionales(): Observable<void> {
    if (this.acreditacionesCategoriaProfesionalEliminadas.length === 0) {
      return of(void 0);
    }

    return from(this.acreditacionesCategoriaProfesionalEliminadas).pipe(
      mergeMap(acreditacion =>
        this.solicitudRrhhRequisitoCategoriaService.delete(this.solicitudPublicKey, acreditacion.id)
          .pipe(
            tap(() => {
              this.acreditacionesCategoriaProfesionalEliminadas =
                this.acreditacionesCategoriaProfesionalEliminadas.filter(deleted => deleted.id !== acreditacion.id);
            })
          )
      )
    );
  }

  private deleteAcreditacionesNivelesAcademicos(): Observable<void> {
    if (this.acreditacionesNivelAcademicoEliminadas.length === 0) {
      return of(void 0);
    }

    return from(this.acreditacionesNivelAcademicoEliminadas).pipe(
      mergeMap(acreditacion =>
        this.solicitudRrhhRequisitoNivelAcademicoService.delete(this.solicitudPublicKey, acreditacion.id)
          .pipe(
            tap(() => {
              this.acreditacionesNivelAcademicoEliminadas =
                this.acreditacionesNivelAcademicoEliminadas.filter(deleted => deleted.id !== acreditacion.id);
            })
          )
      )
    );
  }

  private deleteDocumentosUnrelated(): Observable<void> {
    if (this.documentosRefUnrelated.length === 0) {
      return of(void 0);
    }

    return from(this.documentosRefUnrelated).pipe(
      mergeMap(documentoRef =>
        this.documentoService.eliminarFichero(documentoRef)
          .pipe(
            tap(() =>
              this.documentosRefUnrelated = this.documentosRefUnrelated
                .filter(documentoRefEliminado => documentoRefEliminado !== documentoRef)
            )
          )
      ),
      takeLast(1)
    );
  }

  saveOrUpdate(): Observable<void> {
    return merge(
      this.deleteAcreditacionesCategoriasProfesionales(),
      this.deleteAcreditacionesNivelesAcademicos(),
      this.createAcreditacionesCategoriasProfesionales(this.solicitudId),
      this.createAcreditacionesNivelesAcademicos(this.solicitudId),
      this.deleteDocumentosUnrelated()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  /**
   * Si la acreditacion ya esta creado lo anade a la lista de elementos a eliminar
   * y si no esta persistido aun se elimina directamente el documento asociado.
   */
  deleteAcreditacionCategoriaProfesional(wrapper: StatusWrapper<RequisitoCategoriaProfesionalExigidoAndAcreditado>): void {
    if (wrapper.created) {
      this.documentoService.eliminarFichero(wrapper.value.nivelAcreditado.documento.documentoRef).subscribe();
    } else {
      this.acreditacionesCategoriaProfesionalEliminadas.push(wrapper.value.nivelAcreditado);
      this.documentosRefUnrelated.push(wrapper.value.nivelAcreditado.documento.documentoRef);
      this.setChanges(true);
    }

    wrapper.value.nivelAcreditado.documento = null;
    wrapper.setDeleted();
    this.checkRequisitosAcreditatos();
  }

  /**
   * Si la acreditacion ya esta creado lo anade a la lista de elementos a eliminar
   * y si no esta persistido aun se elimina directamente el documento asociado.
   */
  deleteAcreditacionNivelAcademico(wrapper: StatusWrapper<RequisitoNivelAcademicoExigidoAndAcreditado>): void {
    if (wrapper.created) {
      this.documentoService.eliminarFichero(wrapper.value.nivelAcreditado.documento.documentoRef).subscribe();
    } else {
      this.acreditacionesNivelAcademicoEliminadas.push(wrapper.value.nivelAcreditado);
      this.documentosRefUnrelated.push(wrapper.value.nivelAcreditado.documento.documentoRef);
      this.setChanges(true);
    }

    wrapper.value.nivelAcreditado.documento = null;
    wrapper.setDeleted();
    this.checkRequisitosAcreditatos();
  }

  acreditarRequisito(
    wrapper: StatusWrapper<RequisitoCategoriaProfesionalExigidoAndAcreditado | RequisitoNivelAcademicoExigidoAndAcreditado>
  ): void {
    wrapper.setCreated();
    this.setChanges(true);
    this.checkRequisitosAcreditatos();
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.requisitosNivelesAcademicosExigidosSolicitante$.value.some((wrapper) => wrapper.touched);
    return !touched;
  }

  private checkRequisitosAcreditatos(): void {
    if (![Estado.BORRADOR, Estado.RECHAZADA].includes(this.estado.estado)) {
      return;
    }

    const hasNivelAcreditado = this.requisitosCategoriasExigidasSolicitante$.value.length === 0
      || this.requisitosCategoriasExigidasSolicitante$.value.some(requisito => !!requisito.value.nivelAcreditado.documento);
    const hasCategoriaAcreditada = this.requisitosNivelesAcademicosExigidosSolicitante$.value.length === 0
      || this.requisitosNivelesAcademicosExigidosSolicitante$.value.some(requisito => !!requisito.value.nivelAcreditado.documento);

    this.needDocumentoAcreditativo = !(hasNivelAcreditado && hasCategoriaAcreditada);
  }

  private getRequisitosEquipoCategoriasProfesionales(convocatoriaId: number): Observable<IRequisitoEquipoCategoriaProfesional[]> {
    if (!!!convocatoriaId) {
      return of([]);
    }

    return this.convocatoriaService.findRequisitosEquipoCategoriasProfesionales(convocatoriaId)
      .pipe(
        switchMap((requisitoEquipoCategoria) => {
          if (requisitoEquipoCategoria.length === 0) {
            return of([]);
          }
          const categoriasProfesionalesObservable = requisitoEquipoCategoria.
            map(requisitoEquipoCategoriaProfesional => {

              return this.categoriasProfesionalesService.findById(requisitoEquipoCategoriaProfesional.categoriaProfesional.id).pipe(
                map(categoriaProfesional => {
                  requisitoEquipoCategoriaProfesional.categoriaProfesional = categoriaProfesional;
                  return requisitoEquipoCategoriaProfesional;
                }),
              );
            });
          return zip(...categoriasProfesionalesObservable);
        })
      );
  }

  private getRequisitosIpCategoriasProfesionales(convocatoriaId: number): Observable<IRequisitoIPCategoriaProfesional[]> {
    if (!!!convocatoriaId) {
      return of([]);
    }

    return this.convocatoriaService.findRequisitosIpCategoriasProfesionales(convocatoriaId)
      .pipe(
        switchMap((requisitoIpCategoria) => {
          if (requisitoIpCategoria.length === 0) {
            return of([]);
          }
          const categoriasProfesionalesObservable = requisitoIpCategoria.
            map(requisitoIpCategoriaProfesional => {

              return this.categoriasProfesionalesService.findById(requisitoIpCategoriaProfesional.categoriaProfesional.id).pipe(
                map(categoriaProfesional => {
                  requisitoIpCategoriaProfesional.categoriaProfesional = categoriaProfesional;
                  return requisitoIpCategoriaProfesional;
                }),
              );
            });
          return zip(...categoriasProfesionalesObservable);
        })
      );
  }

  private getRequisitosEquipoNivelesAcademicos(convocatoriaId: number): Observable<IRequisitoEquipoNivelAcademico[]> {
    return this.convocatoriaService.findRequisitosEquipoNivelesAcademicos(convocatoriaId)
      .pipe(
        switchMap((requisitoEquipoNivelesAcademicos) => {
          if (requisitoEquipoNivelesAcademicos.length === 0) {
            return of([]);
          }
          const nivelesAcademicosObservable = requisitoEquipoNivelesAcademicos.
            map(requisitoIpNivelAcademico => {

              return this.nivelAcademicoService.findById(requisitoIpNivelAcademico.nivelAcademico.id).pipe(
                map(nivelAcademico => {
                  requisitoIpNivelAcademico.nivelAcademico = nivelAcademico;
                  return requisitoIpNivelAcademico;
                }),
              );
            });
          return zip(...nivelesAcademicosObservable);
        })
      );
  }

  private getRequisitosIpNivelesAcademicos(convocatoriaId: number): Observable<IRequisitoIPNivelAcademico[]> {
    return this.convocatoriaService.findRequisitosIpNivelesAcademicos(convocatoriaId)
      .pipe(
        switchMap((requisitoIpNivelesAcademicos) => {
          if (requisitoIpNivelesAcademicos.length === 0) {
            return of([]);
          }
          const nivelesAcademicosObservable = requisitoIpNivelesAcademicos.
            map(requisitoIpNivelAcademico => {

              return this.nivelAcademicoService.findById(requisitoIpNivelAcademico.nivelAcademico.id).pipe(
                map(nivelAcademico => {
                  requisitoIpNivelAcademico.nivelAcademico = nivelAcademico;
                  return requisitoIpNivelAcademico;
                }),
              );
            });
          return zip(...nivelesAcademicosObservable);
        })
      );
  }

  private initializeRequisitosCategoriasExigidasSolicitante$(
    requisitosCategoria: IRequisitoIPCategoriaProfesional[],
    requisitosIp: IConvocatoriaRequisitoIP,
    nivelesCategoriaAcreditados: ISolicitudRrhhRequisitoCategoria[]
  ): void {
    const categoriasProfesionalesExigidas = requisitosCategoria.map(requisitoCategoria => {
      const requisitoCategoriaExigida: RequisitoCategoriaProfesionalExigidoAndAcreditado = {
        nivelExigido: requisitoCategoria.categoriaProfesional,
        fechaMaximaObtencion: requisitosIp.fechaMaximaCategoriaProfesional,
        fechaMinimaObtencion: requisitosIp.fechaMinimaCategoriaProfesional,
        nivelAcreditado: nivelesCategoriaAcreditados
          .find(requisitoCategoriaAcreditado => requisitoCategoriaAcreditado.requisitoIpCategoria.id === requisitoCategoria.id),
        help: null
      };

      if (!!!requisitoCategoriaExigida.nivelAcreditado) {
        requisitoCategoriaExigida.nivelAcreditado = {
          requisitoIpCategoria: requisitoCategoria
        } as ISolicitudRrhhRequisitoCategoria;
      }

      return new StatusWrapper<RequisitoCategoriaProfesionalExigidoAndAcreditado>(requisitoCategoriaExigida);
    });

    this.requisitosCategoriasExigidasSolicitante$.next(categoriasProfesionalesExigidas);
  }

  private initializeRequisitosCategoriasExigidasTutor$(
    requisitosCategoria: IRequisitoEquipoCategoriaProfesional[],
    requisitosEquipo: IConvocatoriaRequisitoEquipo
  ): void {
    const categoriasProfesionalesExigidas = requisitosCategoria.map(requisitoCategoria => {
      const requisitoCategoriaExigida: RequisitoCategoriaProfesionalExigido = {
        nivelExigido: requisitoCategoria.categoriaProfesional,
        fechaMaximaObtencion: requisitosEquipo.fechaMaximaCategoriaProfesional,
        fechaMinimaObtencion: requisitosEquipo.fechaMinimaCategoriaProfesional,
        help: null
      };
      return new StatusWrapper<RequisitoCategoriaProfesionalExigido>(requisitoCategoriaExigida);
    });

    this.requisitosCategoriasExigidasTutor$.next(categoriasProfesionalesExigidas);
  }

  private initializeRequisitosNivelesAcademicosExigidosSolicitante$(
    requistosNivelAcademico: IRequisitoIPNivelAcademico[],
    requisitosIp: IConvocatoriaRequisitoIP,
    nivelesAcademicosAcreditados: ISolicitudRrhhRequisitoNivelAcademico[]
  ): void {
    const nivelesAcademicosExigidos = requistosNivelAcademico.map(requistoNivelAcademico => {
      const requisitoNivelAcademicoExigido: RequisitoNivelAcademicoExigidoAndAcreditado = {
        nivelExigido: requistoNivelAcademico.nivelAcademico,
        fechaMaximaObtencion: requisitosIp.fechaMaximaNivelAcademico,
        fechaMinimaObtencion: requisitosIp.fechaMinimaNivelAcademico,
        nivelAcreditado: nivelesAcademicosAcreditados
          .find(requisitoNivelAcreditado => requisitoNivelAcreditado.requisitoIpNivelAcademico.id === requistoNivelAcademico.id),
        help: null
      };

      if (!!!requisitoNivelAcademicoExigido.nivelAcreditado) {
        requisitoNivelAcademicoExigido.nivelAcreditado = {
          requisitoIpNivelAcademico: requistoNivelAcademico
        } as ISolicitudRrhhRequisitoNivelAcademico;
      }

      return new StatusWrapper<RequisitoNivelAcademicoExigidoAndAcreditado>(requisitoNivelAcademicoExigido);
    });

    this.requisitosNivelesAcademicosExigidosSolicitante$.next(nivelesAcademicosExigidos);
  }

  private initializeRequisitosNivelesAcademicosExigidosTutor$(
    requistosNivelAcademico: IRequisitoEquipoNivelAcademico[],
    requisitosEquipo: IConvocatoriaRequisitoEquipo
  ): void {
    const nivelesAcademicosExigidos = requistosNivelAcademico.map(requistoNivelAcademico => {
      const requisitoNivelAcademicoExigido: RequisitoNivelAcademicoExigido = {
        nivelExigido: requistoNivelAcademico.nivelAcademico,
        fechaMaximaObtencion: requisitosEquipo.fechaMaximaNivelAcademico,
        fechaMinimaObtencion: requisitosEquipo.fechaMinimaNivelAcademico,
        help: null
      };
      return new StatusWrapper<RequisitoNivelAcademicoExigido>(requisitoNivelAcademicoExigido);
    });

    this.requisitosNivelesAcademicosExigidosTutor$.next(nivelesAcademicosExigidos);
  }

  private fillDatosAcademicos(persona: IPersona): Observable<IPersona> {
    if (!!!persona || !!persona.datosAcademicos?.id || this.requisitosNivelesAcademicosExigidosSolicitante$.value.length === 0) {
      return of(persona);
    }

    return this.datosAcademicosService.findByPersonaId(persona.id).pipe(
      map(datosAcademicos => {
        persona.datosAcademicos = datosAcademicos;
        return persona;
      })
    );
  }

  private fillVinculaciones(persona: IPersona): Observable<IPersona> {
    if (!!!persona || !!persona.vinculacion?.id || this.requisitosCategoriasExigidasSolicitante$.value.length === 0) {
      return of(persona);
    }

    return this.vinculacionService.findByPersonaId(persona.id).pipe(
      map(vinculacion => {
        persona.vinculacion = vinculacion;
        return persona;
      })
    );
  }

  private getHelpIcon(tooltip: ValidacionRequisitosEquipoIp): HelpIcon {
    return {
      class: HelpIconClass.DANGER,
      tooltip
    };
  }


  private validateRequisitoCategoriaProfesional(
    requisito: RequisitoCategoriaProfesionalExigido,
    persona: IPersona
  ): ValidacionRequisitosEquipoIp {
    if (requisito.nivelExigido.id !== persona.vinculacion?.categoriaProfesional?.id) {
      return ValidacionRequisitosEquipoIp.NO_CATEGORIA_PROFESIONAL;
    }

    if (!!!persona.vinculacion?.fechaObtencionCategoria
      && (!!requisito.fechaMinimaObtencion || !!requisito.fechaMaximaObtencion)) {
      return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_DESCONOCIDA;
    }

    if (!!requisito.fechaMinimaObtencion && persona.vinculacion?.fechaObtencionCategoria < requisito.fechaMinimaObtencion) {
      return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MIN;
    }

    if (!!requisito.fechaMaximaObtencion && persona.vinculacion?.fechaObtencionCategoria > requisito.fechaMaximaObtencion) {
      return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_CATEGORIA_PROFESIONAL_MAX;
    }

    return null;
  }

  private validateRequisitosCategoriasExigidasSolicitante(solicitante: IPersona): void {
    if (!!!solicitante) {
      return;
    }

    this.requisitosCategoriasExigidasSolicitante$.next(
      this.requisitosCategoriasExigidasSolicitante$.value.map(requisito => {
        const validationError = this.validateRequisitoCategoriaProfesional(requisito.value, solicitante);
        if (!!validationError) {
          requisito.value.help = this.getHelpIcon(ValidacionRequisitosEquipoIp.SOLICITANTE_NO_CUMPLE_REQUISITO);
        } else {
          requisito.value.help = null;
        }

        return requisito;
      })
    );
  }

  private validateRequisitosCategoriasExigidasTutor(tutor: IPersona): void {
    if (!!!tutor) {
      return;
    }

    this.requisitosCategoriasExigidasTutor$.next(
      this.requisitosCategoriasExigidasTutor$.value.map(requisito => {
        const validationError = this.validateRequisitoCategoriaProfesional(requisito.value, tutor);
        if (!!validationError) {
          requisito.value.help = this.getHelpIcon(ValidacionRequisitosEquipoIp.TUTOR_NO_CUMPLE_REQUISITO);
        } else {
          requisito.value.help = null;
        }

        return requisito;
      })
    );
  }

  private validateRequisitoNivelAcademico(
    requisito: RequisitoNivelAcademicoExigido,
    persona: IPersona
  ): ValidacionRequisitosEquipoIp {
    if (requisito.nivelExigido.id !== persona.datosAcademicos?.nivelAcademico?.id) {
      return ValidacionRequisitosEquipoIp.NO_NIVEL_ACADEMICO;
    }

    if (!!!persona.datosAcademicos?.fechaObtencion
      && (!!requisito.fechaMinimaObtencion || !!requisito.fechaMaximaObtencion)) {
      return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_DESCONOCIDA;
    }

    if (!!requisito.fechaMinimaObtencion && persona.datosAcademicos?.fechaObtencion < requisito.fechaMinimaObtencion) {
      return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_MIN;
    }

    if (!!requisito.fechaMaximaObtencion && persona.datosAcademicos?.fechaObtencion > requisito.fechaMaximaObtencion) {
      return ValidacionRequisitosEquipoIp.FECHA_OBTENCION_NIVEL_ACADEMICO_MAX;
    }

    return null;
  }

  private validateRequisitosNivelesAcademicosExigidosSolicitante(solicitante: IPersona): void {
    if (!!!solicitante) {
      return;
    }

    this.requisitosNivelesAcademicosExigidosSolicitante$.next(
      this.requisitosNivelesAcademicosExigidosSolicitante$.value.map(requisito => {
        const validationError = this.validateRequisitoNivelAcademico(requisito.value, solicitante);

        if (!!validationError) {
          requisito.value.help = this.getHelpIcon(ValidacionRequisitosEquipoIp.SOLICITANTE_NO_CUMPLE_REQUISITO);
        } else {
          requisito.value.help = null;
        }

        return requisito;
      })
    );
  }

  private validateRequisitosNivelesAcademicosExigidosTutor(tutor: IPersona): void {
    if (!!!tutor) {
      return;
    }

    this.requisitosNivelesAcademicosExigidosTutor$.next(
      this.requisitosNivelesAcademicosExigidosTutor$.value.map(requisito => {
        const validationError = this.validateRequisitoNivelAcademico(requisito.value, tutor);

        if (!!validationError) {
          requisito.value.help = this.getHelpIcon(ValidacionRequisitosEquipoIp.TUTOR_NO_CUMPLE_REQUISITO);
        } else {
          requisito.value.help = null;
        }

        return requisito;
      })
    );
  }

}
