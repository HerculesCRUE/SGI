import { IAcreditacion } from '@core/models/prc/acreditacion';
import { IAutorWithGrupos } from '@core/models/prc/autor';
import { ICampoProduccionCientifica, ICampoProduccionCientificaWithConfiguracion } from '@core/models/prc/campo-produccion-cientifica';
import { IEstadoProduccionCientifica, TipoEstadoProduccion } from '@core/models/prc/estado-produccion-cientifica';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { IProyectoPrc } from '@core/models/prc/proyecto-prc';
import { IValorCampo } from '@core/models/prc/valor-campo';
import { Fragment } from '@core/services/action-service';
import { BehaviorSubject, forkJoin, Observable, of } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { CvnValorCampoService } from '../../../shared/cvn/services/cvn-valor-campo.service';
import { ProduccionCientificaInitializerService } from '../../../shared/produccion-cientifica-initializer.service';

export class ObraArtisticaDatosGeneralesFragment extends Fragment {
  private camposProduccionCientificaWithConfiguracionMap$ =
    new BehaviorSubject<Map<string, ICampoProduccionCientificaWithConfiguracion>>(new Map());
  private autores$ = new BehaviorSubject<IAutorWithGrupos[]>([]);
  private proyectos$ = new BehaviorSubject<IProyectoPrc[]>([]);
  private acreditaciones$ = new BehaviorSubject<IAcreditacion[]>([]);
  private valoresCampoMap = new Map<string, IValorCampo[]>();
  private produccionCientifica$: BehaviorSubject<IProduccionCientifica>;

  get produccionCientifica(): IProduccionCientifica {
    return this.produccionCientifica$.value;
  }

  get estadoProduccionCientifica(): IEstadoProduccionCientifica {
    return this.produccionCientifica?.estado;
  }

  constructor(
    produccionCientifica: IProduccionCientifica,
    private initializerService: ProduccionCientificaInitializerService,
    private cvnValorCampoService: CvnValorCampoService,
  ) {
    super(produccionCientifica?.id);
    this.produccionCientifica$ = new BehaviorSubject(produccionCientifica);
    this.setComplete(true);
  }

  protected onInitialize(): void | Observable<any> {
    return forkJoin({
      camposProduccionCientificaMap: this.initializerService.initializeCamposProduccionCientifica$(this.produccionCientifica),
      autores: this.initializerService.initializeAutores$(this.produccionCientifica),
      proyectos: this.initializerService.initializeProyectos$(this.produccionCientifica),
      acreditaciones: this.initializerService.initializeAcreditaciones$(this.produccionCientifica)
    }).pipe(
      tap(({ camposProduccionCientificaMap }) => this.camposProduccionCientificaWithConfiguracionMap$.next(camposProduccionCientificaMap)),
      tap(({ autores }) => this.autores$.next(autores)),
      tap(({ proyectos }) => this.proyectos$.next(proyectos)),
      tap(({ acreditaciones }) => this.acreditaciones$.next(acreditaciones)),
    );
  }

  saveOrUpdate(action?: any): Observable<string | number | void> {
    return of(void 0);
  }

  getCamposProduccionCientificaWithConfiguracionMap$(): Observable<Map<string, ICampoProduccionCientificaWithConfiguracion>> {
    return this.camposProduccionCientificaWithConfiguracionMap$.asObservable();
  }

  getAutores$(): Observable<IAutorWithGrupos[]> {
    return this.autores$.asObservable();
  }

  getProyectos$(): Observable<IProyectoPrc[]> {
    return this.proyectos$.asObservable();
  }

  getAcreditaciones$(): Observable<IAcreditacion[]> {
    return this.acreditaciones$.asObservable();
  }

  getValoresCampo$ = (campoProduccionCientifica: ICampoProduccionCientifica) => {
    const valoresCampo = this.valoresCampoMap.get(campoProduccionCientifica.codigo);
    if (!valoresCampo) {
      return this.cvnValorCampoService.findCvnValorCampo(campoProduccionCientifica)
        .pipe(
          tap(valoresCampoFetched => this.valoresCampoMap.set(campoProduccionCientifica.codigo, valoresCampoFetched))
        );
    } else {
      return of(valoresCampo);
    }
  }

  getProduccionCientifica$(): Observable<IProduccionCientifica> {
    return this.produccionCientifica$.asObservable();
  }

  isProduccionCientificaDisabled$(): Observable<boolean> {
    return this.getProduccionCientifica$().pipe(
      map(({ estado }) => estado?.estado === TipoEstadoProduccion.VALIDADO || estado?.estado === TipoEstadoProduccion.RECHAZADO)
    );
  }

  refreshDatosGenerales(produccionCientifica: IProduccionCientifica): void {
    this.produccionCientifica$.next(produccionCientifica);
    this.refreshAutores(produccionCientifica);
  }

  refreshAutores(produccionCientifica: IProduccionCientifica): void {
    this.subscriptions.push(
      this.initializerService.initializeAutores$(produccionCientifica)
        .subscribe((autores) => this.autores$.next(autores))
    );
  }
}
