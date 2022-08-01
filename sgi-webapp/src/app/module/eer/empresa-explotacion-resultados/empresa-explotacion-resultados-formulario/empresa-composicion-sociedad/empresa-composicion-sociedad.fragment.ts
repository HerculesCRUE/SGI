
import { IEmpresaComposicionSociedad } from '@core/models/eer/empresa-composicion-sociedad';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { Fragment } from '@core/services/action-service';
import { EmpresaComposicionSociedadService } from '@core/services/eer/empresa-composicion-sociedad/empresa-composicion-sociedad.service';
import { EmpresaExplotacionResultadosService } from '@core/services/eer/empresa-explotacion-resultados/empresa-explotacion-resultados.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class EmpresaComposicionSociedadFragment extends Fragment {
  composicionesSociedad$ = new BehaviorSubject<StatusWrapper<IEmpresaComposicionSociedad>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly empresaExplotacionResultadosService: EmpresaExplotacionResultadosService,
    private readonly empresaComposicionSociedadService: EmpresaComposicionSociedadService,
    private readonly personaService: PersonaService,
    private readonly empresaService: EmpresaService,
    private readonly: boolean,
  ) {
    super(key);
    this.setComplete(true);
  }

  protected onInitialize(): void {
    if (this.getKey()) {
      const id = this.getKey() as number;
      this.subscriptions.push(
        this.empresaExplotacionResultadosService.findComposicionesSociedad(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                if (element.miembroSociedadPersona?.id) {
                  return this.personaService.findById(element.miembroSociedadPersona?.id).pipe(
                    map(persona => {
                      element.miembroSociedadPersona = persona;
                      return element as IEmpresaComposicionSociedad;
                    })
                  );
                } else {
                  return this.empresaService.findById(element.miembroSociedadEmpresa?.id).pipe(
                    map(empresa => {
                      element.miembroSociedadEmpresa = empresa;
                      return element as IEmpresaComposicionSociedad;
                    })
                  );
                }
              }),
              map(() => result)
            );
          }),
          map(composicionesSociedad => {
            return composicionesSociedad.items.map(composicionSociedad => {
              composicionSociedad.empresa = { id: this.getKey() } as IEmpresaExplotacionResultados;
              return new StatusWrapper<IEmpresaComposicionSociedad>(composicionSociedad as IEmpresaComposicionSociedad);
            });
          })
        ).subscribe(
          result => {
            this.composicionesSociedad$.next(result);
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addEmpresaComposicionSociedad(element: IEmpresaComposicionSociedad) {
    const wrapper = new StatusWrapper<IEmpresaComposicionSociedad>(element);
    wrapper.setCreated();
    const current = this.composicionesSociedad$.value;
    current.push(wrapper);
    this.composicionesSociedad$.next(current);
    this.setChanges(true);
  }

  updateEmpresaComposicionSociedad(wrapper: StatusWrapper<IEmpresaComposicionSociedad>): void {
    const current = this.composicionesSociedad$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.composicionesSociedad$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteEmpresaComposicionSociedad(wrapper: StatusWrapper<IEmpresaComposicionSociedad>) {
    const current = this.composicionesSociedad$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.composicionesSociedad$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.composicionesSociedad$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;

    return this.empresaComposicionSociedadService.updateList(id, values)
      .pipe(
        map(results => {
          return results.map(
            (value: IEmpresaComposicionSociedad) => {
              const empresaComposicionSociedad = values.find(
                composicionSociedad => composicionSociedad.miembroSociedadEmpresa ?
                  (composicionSociedad.miembroSociedadEmpresa?.id === value.miembroSociedadEmpresa?.id) :
                  (composicionSociedad.miembroSociedadPersona?.id === value.miembroSociedadPersona?.id)
              );
              value.miembroSociedadEmpresa = empresaComposicionSociedad.miembroSociedadEmpresa;
              value.miembroSociedadPersona = empresaComposicionSociedad.miembroSociedadPersona;
              return value;
            });
        }),
        map(miembrosEquipo => {
          return miembrosEquipo.map(miembroEquipo => {
            miembroEquipo.empresa = { id: this.getKey() } as IEmpresaExplotacionResultados;
            return new StatusWrapper<IEmpresaComposicionSociedad>(miembroEquipo as IEmpresaComposicionSociedad);
          });
        }),
        takeLast(1),
        map((results) => {
          this.composicionesSociedad$.next(results);
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.composicionesSociedad$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

}
