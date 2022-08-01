
import { IEmpresaAdministracionSociedad } from '@core/models/eer/empresa-administracion-sociedad';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { IVinculacion } from '@core/models/sgp/vinculacion';
import { IVinculacionCategoriaProfesional } from '@core/models/sgp/vinculacion-categoria-profesional';
import { Fragment } from '@core/services/action-service';
import { EmpresaAdministracionSociedadService } from '@core/services/eer/empresa-administracion-sociedad/empresa-administracion-sociedad.service';
import { EmpresaExplotacionResultadosService } from '@core/services/eer/empresa-explotacion-resultados/empresa-explotacion-resultados.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { map, mergeMap, switchMap, takeLast, tap } from 'rxjs/operators';

export class EmpresaAdministracionSociedadFragment extends Fragment {
  administracionesSociedad$ = new BehaviorSubject<StatusWrapper<IEmpresaAdministracionSociedad>[]>([]);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly empresaExplotacionResultadosService: EmpresaExplotacionResultadosService,
    private readonly empresaAdministracionSociedadService: EmpresaAdministracionSociedadService,
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
        this.empresaExplotacionResultadosService.findAdministracionesSociedad(id).pipe(
          switchMap(result => {
            return from(result.items).pipe(
              mergeMap(element => {
                if (element.miembroEquipoAdministracion?.id) {
                  return this.personaService.findById(element.miembroEquipoAdministracion?.id).pipe(
                    map(persona => {
                      element.miembroEquipoAdministracion = persona;
                      return element as IEmpresaAdministracionSociedad;
                    }),
                    switchMap(empresaAdministracionSociedad => this.getEntidadPersona(empresaAdministracionSociedad))
                  );
                } else {
                  return of(element);
                }
              }),
              map(() => result)
            );
          }),
          map(administracionesSociedad => {
            return administracionesSociedad.items.map(administracionSociedad => {
              administracionSociedad.empresa = { id: this.getKey() } as IEmpresaExplotacionResultados;
              return new StatusWrapper<IEmpresaAdministracionSociedad>(administracionSociedad as IEmpresaAdministracionSociedad);
            });
          })
        ).subscribe(
          result => {
            this.administracionesSociedad$.next(result);
          },
          error => {
            this.logger.error(error);
          }
        )
      );
    }
  }

  addEmpresaAdministracionSociedad(element: IEmpresaAdministracionSociedad) {
    this.getEntidadPersona(element).pipe(
      map(element => {
        const wrapper = new StatusWrapper<IEmpresaAdministracionSociedad>(element);
        wrapper.setCreated();
        return wrapper;
      })
    ).subscribe(wrapper => {
      const current = this.administracionesSociedad$.value;
      current.push(wrapper);
      this.administracionesSociedad$.next(current);
      this.setChanges(true);
    });
  }

  updateEmpresaAdministracionSociedad(wrapper: StatusWrapper<IEmpresaAdministracionSociedad>): void {
    const current = this.administracionesSociedad$.value;
    const index = current.findIndex(value => value.value.id === wrapper.value.id);
    if (index >= 0) {
      wrapper.setEdited();
      this.administracionesSociedad$.value[index] = wrapper;
      this.setChanges(true);
    }
  }

  deleteEmpresaAdministracionSociedad(wrapper: StatusWrapper<IEmpresaAdministracionSociedad>) {
    const current = this.administracionesSociedad$.value;
    const index = current.findIndex((value) => value === wrapper);
    if (index >= 0) {
      current.splice(index, 1);
      this.administracionesSociedad$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<void> {
    const values = this.administracionesSociedad$.value.map(wrapper => wrapper.value);
    const id = this.getKey() as number;

    return this.empresaAdministracionSociedadService.updateList(id, values)
      .pipe(
        map(results => {
          return results.map(
            (value: IEmpresaAdministracionSociedad) => {
              const empresaAdministracionSociedad = values.find(
                administracionSociedad =>
                  (administracionSociedad.miembroEquipoAdministracion?.id === value.miembroEquipoAdministracion?.id)
              );
              value.miembroEquipoAdministracion = empresaAdministracionSociedad.miembroEquipoAdministracion;
              return value;
            });
        }),
        map(miembrosEquipo => {
          return miembrosEquipo.map(miembroEquipo => {
            miembroEquipo.empresa = { id: this.getKey() } as IEmpresaExplotacionResultados;
            return new StatusWrapper<IEmpresaAdministracionSociedad>(miembroEquipo as IEmpresaAdministracionSociedad);
          });
        }),
        takeLast(1),
        map((results) => {
          this.administracionesSociedad$.next(results);
        }),
        tap(() => {
          if (this.isSaveOrUpdateComplete()) {
            this.setChanges(false);
          }
        })
      );
  }

  private isSaveOrUpdateComplete(): boolean {
    const hasTouched = this.administracionesSociedad$.value.some((wrapper) => wrapper.touched);
    return !hasTouched;
  }

  private getEntidadPersona(element: IEmpresaAdministracionSociedad): Observable<IEmpresaAdministracionSociedad> {
    const entidadId = element.miembroEquipoAdministracion.entidad?.id ?? element.miembroEquipoAdministracion.entidadPropia?.id;
    if (entidadId) {
      return this.empresaService.findById(entidadId).pipe(
        map(entidad => {
          element.miembroEquipoAdministracion.entidad = entidad;
          return element as IEmpresaAdministracionSociedad;
        })
      );
    } else {
      return of(element);
    }
  }

}
