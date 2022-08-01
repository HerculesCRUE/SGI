import { IDictamen } from '@core/models/eti/dictamen';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { Fragment } from '@core/services/action-service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

export interface MemoriaListado {
  /** Id */
  id: number;
  /** número de referencia */
  numReferencia: string;
  /** Version */
  version: number;
  dictamen: IDictamen;
  idEvaluacion: number;
}

export class ActaMemoriasFragment extends Fragment {

  memorias$: BehaviorSubject<MemoriaListado[]> = new BehaviorSubject<MemoriaListado[]>([]);

  private selectedIdConvocatoria: number;

  constructor(key: number, private service: ConvocatoriaReunionService) {
    super(key);
    this.selectedIdConvocatoria = key;
    this.setComplete(true);
  }

  onInitialize(): void {
    if (this.getKey()) {
      this.loadMemorias(this.getKey() as number);
    }
  }

  loadMemorias(idConvocatoria: number): void {
    if (!this.isInitialized() || this.selectedIdConvocatoria !== idConvocatoria) {
      this.selectedIdConvocatoria = idConvocatoria;
      this.service.findEvaluacionesActivas(idConvocatoria).pipe(
        map((response) => {
          if (response.items) {
            const evaluacionesSinDuplicados = response.items.reduce(
              (evaluacionObject, evaluacion: IEvaluacion) => ({ ...evaluacionObject, [evaluacion.id]: evaluacion }), {}
            );
            const memorias: MemoriaListado[] = Object.keys(evaluacionesSinDuplicados).map(
              idEvaluacion => {
                return {
                  id: evaluacionesSinDuplicados[idEvaluacion].memoria?.id,
                  numReferencia: evaluacionesSinDuplicados[idEvaluacion].memoria?.numReferencia,
                  version: evaluacionesSinDuplicados[idEvaluacion].version,
                  dictamen: evaluacionesSinDuplicados[idEvaluacion].dictamen,
                  idEvaluacion: idEvaluacion as unknown as number
                };
              });
            return memorias;
          }
          else {
            return [];
          }
        })
      ).subscribe((memorias) => {
        this.memorias$.next(memorias);
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    return of(void 0);
  }
}
