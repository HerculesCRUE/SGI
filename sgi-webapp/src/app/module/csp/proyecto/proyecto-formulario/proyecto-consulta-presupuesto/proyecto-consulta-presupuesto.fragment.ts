import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IAnualidadGasto } from '@core/models/csp/anualidad-gasto';
import { IConceptoGasto } from '@core/models/csp/concepto-gasto';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoPartida } from '@core/models/csp/proyecto-partida';
import { Fragment } from '@core/services/action-service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { LanguageService } from '@core/services/language.service';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { concatAll, concatMap, map } from 'rxjs/operators';

const CONCEPTO_GASTO_SIN_CLASIFICAR = marker('csp.proyecto-consulta-presupuesto.concepto-gasto.sin-clasificar');

export class ProyectoConsultaPresupuestoFragment extends Fragment {

  public anualidades$: BehaviorSubject<IProyectoAnualidad[]> = new BehaviorSubject<IProyectoAnualidad[]>([]);
  public aplicaciones$: BehaviorSubject<IProyectoPartida[]> = new BehaviorSubject<IProyectoPartida[]>([]);
  public conceptos$: BehaviorSubject<IAnualidadGasto[]> =
    new BehaviorSubject<IAnualidadGasto[]>([]);
  public anualidadesGastos$: BehaviorSubject<IAnualidadGasto[]> =
    new BehaviorSubject<IAnualidadGasto[]>([]);
  public pureAnualidadesGastos: IAnualidadGasto[];

  constructor(
    key: number,
    private proyectoService: ProyectoService,
    private readonly translate: TranslateService,
    private readonly languageService: LanguageService
  ) {
    super(key);
    this.setComplete(true);
  }

  saveOrUpdate(): Observable<string | number | void> {
    throw new Error('Method not implemented.');
  }

  protected onInitialize(): Observable<any> {
    if (!this.getKey()) {
      return of(void (0));
    }

    return this.proyectoService.findAllProyectoAnualidadesByProyectoId(this.getKey() as number).pipe(
      map(response => {
        this.anualidades$.next(response.items);
        this.conformAnualidadesGastoWithProyectoAgrupacionGasto(response.items);
        return of(void (0));
      })
    );
  }

  private conformAnualidadesGastoWithProyectoAgrupacionGasto(anualidades: IProyectoAnualidad[]): void {
    const anualidadesGasto: IAnualidadGasto[] = [];
    let totalAnualidades = 0;
    this.subscriptions.push(
      this.proyectoService.findAnualidadesGastosByProyectoId(this.getKey() as number).pipe(
        map(response => {
          totalAnualidades = response.items.length;
          return from(response.items).pipe(
            concatMap((anualidadGasto: IAnualidadGasto) => {
              if (!anualidadGasto.conceptoGasto) {
                anualidadGasto.conceptoGasto = this.getConceptoGastoSinClasificar();
              }

              const anualidad = {
                ...anualidadGasto,
                proyectoAnualidad: anualidades.find(pAnualidad => pAnualidad.id === anualidadGasto.proyectoAnualidad.id),
                proyectoAgrupacionGasto: {}
              } as IAnualidadGasto;
              return of(anualidad);
            })
          );

        }), concatAll()
      ).subscribe((anualidad: IAnualidadGasto) => {
        anualidadesGasto.push(anualidad);
        if (totalAnualidades === anualidadesGasto.length) {
          this.pureAnualidadesGastos = anualidadesGasto;
          this.anualidadesGastos$.next(anualidadesGasto);
          this.aplicaciones$.next(this.excludeRepeatedProyectoPartida(
            [...anualidadesGasto].map(currentAnualidad => currentAnualidad.proyectoPartida)));
          this.conceptos$.next(this.excludeRepeatedConceptosGastos(anualidadesGasto));
        }
      })
    );
  }

  private excludeRepeatedConceptosGastos(anualidadesGastos: IAnualidadGasto[]):
    IAnualidadGasto[] {

    const founded: string[] = [];

    return anualidadesGastos.filter((anualidad: IAnualidadGasto) => {
      if (founded.includes(this.languageService.getFieldValue(anualidad.conceptoGasto.nombre))) {
        return false;
      }
      founded.push(this.languageService.getFieldValue(anualidad.conceptoGasto.nombre));
      return true;
    });
  }

  private excludeRepeatedProyectoPartida(partidas: IProyectoPartida[]): IProyectoPartida[] {

    const founded: string[] = [];

    return partidas.filter((partida: IProyectoPartida) => {
      if (founded.includes(partida.codigo)) {
        return false;
      }
      founded.push(partida.codigo);
      return true;
    });
  }

  private getConceptoGastoSinClasificar(): IConceptoGasto {
    return {
      id: 0,
      nombre: [
        {
          lang: this.languageService.getLanguage(),
          value: this.translate.instant(CONCEPTO_GASTO_SIN_CLASIFICAR)
        }
      ]
    } as IConceptoGasto;
  }
}
