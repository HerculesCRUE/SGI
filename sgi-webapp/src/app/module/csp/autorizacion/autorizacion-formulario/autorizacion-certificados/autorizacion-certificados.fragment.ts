import { IAutorizacion } from '@core/models/csp/autorizacion';
import { ICertificadoAutorizacion } from '@core/models/csp/certificado-autorizacion';
import { Fragment } from '@core/services/action-service';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { CertificadoAutorizacionService } from '@core/services/csp/certificado-autorizacion/certificado-autorizacion.service';
import { EstadoAutorizacionService } from '@core/services/csp/estado-autorizacion/estado-autorizacion.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, concat, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export interface CertificadoAutorizacionListado {
  certificado: ICertificadoAutorizacion;
  generadoAutomatico: boolean;
}

export class AutorizacionCertificadosFragment extends Fragment {
  certificadosAutorizacion$ = new BehaviorSubject<StatusWrapper<CertificadoAutorizacionListado>[]>([]);
  private certificadosAutorizacionEliminados: StatusWrapper<CertificadoAutorizacionListado>[] = [];

  constructor(
    key: number,
    private service: CertificadoAutorizacionService,
    private autorizacionService: AutorizacionService,
    private estadoAutorizacionService: EstadoAutorizacionService,
    private documentoService: DocumentoService
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const subscription = this.autorizacionService.findCertificadosAutorizacion(this.getKey() as number).pipe(
        map(response => response.items.map(certificadoAutorizacion => {
          const certificadoAutorizacionListado: CertificadoAutorizacionListado = {
            certificado: certificadoAutorizacion,
            generadoAutomatico: null,
          };
          return certificadoAutorizacionListado;
        })),
        switchMap((certificadoAutorizacionListado) => {
          return from(certificadoAutorizacionListado);
        }),
        mergeMap(certificadoAutorizacionListado => {
          if (certificadoAutorizacionListado.certificado.autorizacion.id) {
            return this.autorizacionService.findById(certificadoAutorizacionListado.certificado.autorizacion.id).pipe(
              map(autorizacion => {
                certificadoAutorizacionListado.certificado.autorizacion = autorizacion;
                return certificadoAutorizacionListado;
              })
            );
          }
          return of(certificadoAutorizacionListado);
        }),
        mergeMap(certificadoAutorizacionListado => {
          if (certificadoAutorizacionListado.certificado.autorizacion.estado.id) {
            return this.estadoAutorizacionService.findById(certificadoAutorizacionListado.certificado.autorizacion.estado.id).pipe(
              map(estado => {
                certificadoAutorizacionListado.certificado.autorizacion.estado = estado;
                return certificadoAutorizacionListado;
              })
            );
          }
          return of(certificadoAutorizacionListado);
        }),
        mergeMap(certificadoAutorizacionListado => {
          if (certificadoAutorizacionListado.certificado.documento.documentoRef) {
            return this.documentoService.getInfoFichero(certificadoAutorizacionListado.certificado.documento.documentoRef).pipe(
              map(documento => {
                certificadoAutorizacionListado.certificado.documento = documento;
                return certificadoAutorizacionListado;
              })
            );
          }
          return of(certificadoAutorizacionListado);
        }),
      ).subscribe((certificadoAutorizacionListado) => {
        this.certificadosAutorizacion$.value.push(new StatusWrapper<CertificadoAutorizacionListado>(certificadoAutorizacionListado));
        this.certificadosAutorizacion$.next(this.certificadosAutorizacion$.value);
      });

      this.subscriptions.push(subscription);
    }
  }

  public addCertificado(certificadoAutorizacionListado: CertificadoAutorizacionListado) {
    if (certificadoAutorizacionListado) {
      certificadoAutorizacionListado.certificado.autorizacion = { id: this.getKey() as number } as IAutorizacion;
      const wrapped = new StatusWrapper<CertificadoAutorizacionListado>(certificadoAutorizacionListado);
      wrapped.setCreated();
      const current = this.certificadosAutorizacion$.value;
      current.push(wrapped);
      this.certificadosAutorizacion$.next(current);

      this.setChanges(true);
    }
  }

  private createCertificados(): Observable<void> {
    const createdCertificadosAutorizacion = this.certificadosAutorizacion$.value.filter((certificado) => certificado.created);
    if (createdCertificadosAutorizacion.length === 0) {
      return of(void 0);
    }

    return from(createdCertificadosAutorizacion).pipe(
      mergeMap((wrappedCertificadoAutorizacion) => {
        return this.service.create(wrappedCertificadoAutorizacion.value.certificado).pipe(
          map((createdCertificado) => {
            const index = this.certificadosAutorizacion$.value.findIndex((currentCertificado) =>
              currentCertificado === wrappedCertificadoAutorizacion);
            wrappedCertificadoAutorizacion.value.certificado.id = createdCertificado.id;
            this.certificadosAutorizacion$.value[index] = new StatusWrapper<CertificadoAutorizacionListado>(wrappedCertificadoAutorizacion.value);
            this.certificadosAutorizacion$.next(this.certificadosAutorizacion$.value);
          })
        );
      })
    );
  }

  public deleteCertificado(wrapper: StatusWrapper<CertificadoAutorizacionListado>) {
    const current = this.certificadosAutorizacion$.value;
    const index = current.findIndex(
      (value) => value.value === wrapper.value
    );
    if (index >= 0) {
      if (!wrapper.created) {
        this.certificadosAutorizacionEliminados.push(current[index]);
      }
      current.splice(index, 1);
      this.certificadosAutorizacion$.next(current);
      this.setChanges(true);
    }
  }

  public updateCertificado(wrapper: StatusWrapper<CertificadoAutorizacionListado>): void {
    const current = this.certificadosAutorizacion$.value;
    const index = current.findIndex(value => value.value.certificado.id === wrapper.value.certificado.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.certificadosAutorizacion$.value[index] = wrapper;
      this.certificadosAutorizacion$.next(this.certificadosAutorizacion$.value);
      this.setChanges(true);

    }
  }

  public createCertificado(wrapper: StatusWrapper<CertificadoAutorizacionListado>): void {
    const current = this.certificadosAutorizacion$.value;
    const index = current.findIndex(value => value.value.certificado.id === wrapper.value.certificado.id);
    if (index >= 0) {
      wrapper.setCreated();
      this.certificadosAutorizacion$.value[index] = wrapper;
      this.certificadosAutorizacion$.next(this.certificadosAutorizacion$.value);
      this.setChanges(true);

    }
  }

  private deletecertificados(): Observable<void> {
    if (this.certificadosAutorizacionEliminados.length === 0) {
      return of(void 0);
    }
    return from(this.certificadosAutorizacionEliminados).pipe(
      mergeMap((wrapped) => {
        return this.service.deleteById(wrapped.value.certificado.id)
          .pipe(
            tap(() => {
              this.certificadosAutorizacionEliminados = this.certificadosAutorizacionEliminados.filter(deletedCertificado =>
                deletedCertificado.value.certificado.id !== wrapped.value.certificado.id);
            })
          );
      })
    );
  }

  private updateCertificadosAutorizacion(): Observable<void> {
    const updateCertificados = this.certificadosAutorizacion$.value.filter((certificadoAutorizacion) => certificadoAutorizacion.edited);
    if (updateCertificados.length === 0) {
      return of(void 0);
    }
    return from(updateCertificados).pipe(
      mergeMap((wrappedCertificado) => {
        return this.service.update(wrappedCertificado.value.certificado.id, wrappedCertificado.value.certificado).pipe(
          map((updateCertificado) => {
            const index = this.certificadosAutorizacion$.value.findIndex((currentCertificado) => currentCertificado === wrappedCertificado);
            this.certificadosAutorizacion$.value[index] = new StatusWrapper<CertificadoAutorizacionListado>(
              { certificado: updateCertificado } as CertificadoAutorizacionListado);
          })
        );
      })
    );
  }

  saveOrUpdate(): Observable<void> {
    return concat(
      this.deletecertificados(),
      this.updateCertificadosAutorizacion(),
      this.createCertificados()
    ).pipe(
      takeLast(1),
      tap(() => {
        if (this.isSaveOrUpdateComplete()) {
          this.setChanges(false);
        }
      })
    );
  }

  private isSaveOrUpdateComplete(): boolean {
    const touched: boolean = this.certificadosAutorizacion$.value.some((wrapper) => wrapper.touched);
    return !(this.certificadosAutorizacionEliminados.length > 0 || touched);
  }
}
