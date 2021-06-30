import { IAsistente } from '@core/models/eti/asistente';
import { Fragment } from '@core/services/action-service';
import { AsistenteService } from '@core/services/eti/asistente.service';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { endWith, map, mergeMap } from 'rxjs/operators';

export class ActaAsistentesFragment extends Fragment {

  asistentes$: BehaviorSubject<StatusWrapper<IAsistente>[]> = new BehaviorSubject<StatusWrapper<IAsistente>[]>([]);

  private selectedIdConvocatoria: number;

  constructor(
    key: number,
    private service: ConvocatoriaReunionService,
    private personaService: PersonaService,
    private asistenteService: AsistenteService
  ) {
    super(key);
    this.selectedIdConvocatoria = key;
  }

  onInitialize(): void {
    if (this.getKey()) {
      this.loadAsistentes(this.getKey() as number);
    }
  }

  loadAsistentes(idConvocatoria: number): void {
    if (!this.isInitialized() || this.selectedIdConvocatoria !== idConvocatoria) {
      this.selectedIdConvocatoria = idConvocatoria;
      this.service.findAsistentes(idConvocatoria).pipe(
        map((response) => {
          if (response.items) {
            response.items.forEach((asistente) => {
              this.personaService.findById(asistente.evaluador.persona.id).pipe(
                map((usuarioInfo) => {
                  asistente.evaluador.persona = usuarioInfo;
                })
              ).subscribe();
            });
            return response.items.map((asistente) => new StatusWrapper<IAsistente>(asistente));
          }
          else {
            return [];
          }
        })
      ).subscribe((asistentes) => {
        if (this.isEdit() && this.selectedIdConvocatoria !== this.getKey()) {
          this.setChanges(true);
          this.setComplete(this.selectedIdConvocatoria ? true : false);
        }
        else if (this.isEdit() && this.selectedIdConvocatoria === this.getKey()) {
          this.setChanges(false);
          this.setComplete(false);
        }
        else {
          this.setChanges(this.selectedIdConvocatoria ? true : false);
          this.setComplete(this.selectedIdConvocatoria ? true : false);
        }
        this.asistentes$.next(asistentes);
      });
    }
  }

  saveOrUpdate(): Observable<void> {
    const editedAsistentes = this.asistentes$.value.filter((asistente) => asistente.edited);
    if (editedAsistentes.length === 0) {
      return of(void 0);
    }
    return from(editedAsistentes).pipe(
      mergeMap((wrappedAsistente) => {
        return this.asistenteService.update(wrappedAsistente.value.id, wrappedAsistente.value).pipe(
          map((updatedAsistente) => {
            const index = this.asistentes$.value.findIndex((currentAsistente) => currentAsistente === wrappedAsistente);
            this.asistentes$[index] = new StatusWrapper<IAsistente>(updatedAsistente);
          })
        );
      }),
      endWith()
    );
  }
}
