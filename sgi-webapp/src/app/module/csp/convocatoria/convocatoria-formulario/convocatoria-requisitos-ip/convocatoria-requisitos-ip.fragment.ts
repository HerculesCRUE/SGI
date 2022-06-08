import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IConvocatoriaRequisitoIP } from '@core/models/csp/convocatoria-requisito-ip';
import { IRequisitoIPCategoriaProfesional } from '@core/models/csp/requisito-ip-categoria-profesional';
import { IRequisitoIPNivelAcademico } from '@core/models/csp/requisito-ip-nivel-academico';
import { IClasificacion } from '@core/models/sgo/clasificacion';
import { ICategoriaProfesional } from '@core/models/sgp/categoria-profesional';
import { INivelAcademico } from '@core/models/sgp/nivel-academico';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaRequisitoIPService } from '@core/services/csp/convocatoria-requisito-ip.service';
import { CategoriaProfesionalService } from '@core/services/sgp/categoria-profesional.service';
import { NivelAcademicosService } from '@core/services/sgp/nivel-academico.service';
import { SexoService } from '@core/services/sgp/sexo.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { DateValidator } from '@core/validators/date-validator';
import { BehaviorSubject, Observable, of, zip } from 'rxjs';
import { map, switchMap, takeLast, tap } from 'rxjs/operators';

export interface SolicitudProyectoClasificacionListado {
  id: number;
  proyectoId: number;
  clasificacion: IClasificacion;
  niveles: IClasificacion[];
  nivelesTexto: string;
  nivelSeleccionado: IClasificacion;
}

export class ConvocatoriaRequisitosIPFragment extends FormFragment<IConvocatoriaRequisitoIP> {
  private requisitoIP: IConvocatoriaRequisitoIP;
  nivelesAcademicos$ = new BehaviorSubject<StatusWrapper<IRequisitoIPNivelAcademico>[]>([]);
  categoriasProfesionales$ = new BehaviorSubject<StatusWrapper<IRequisitoIPCategoriaProfesional>[]>([]);

  constructor(
    private fb: FormBuilder,
    key: number,
    private convocatoriaRequisitoIPService: ConvocatoriaRequisitoIPService,
    private nivelAcademicoService: NivelAcademicosService,
    private categoriaProfesionalService: CategoriaProfesionalService,
    public hasEditPerm: boolean
  ) {
    super(key, true);
    this.setComplete(true);
    this.requisitoIP = {} as IConvocatoriaRequisitoIP;
  }

  protected loadNivelesAcademicos(requisitoIPId: number): void {
    const subscription = this.convocatoriaRequisitoIPService.findNivelesAcademicos(requisitoIPId).pipe(
      switchMap((requisitoIpNivelesAcademicos) => {
        if (requisitoIpNivelesAcademicos.length === 0) {
          return of([]);
        }
        const nivelesAcademicosObservable = requisitoIpNivelesAcademicos.
          map(requisitoIpNivelAcademico => {

            return this.nivelAcademicoService.findById(requisitoIpNivelAcademico.nivelAcademico.id).pipe(
              map(nivelAcademico => {
                const reqNivAcademico = {
                  id: requisitoIpNivelAcademico.id,
                  nivelAcademico,
                  requisitoIP: {
                    id: requisitoIPId
                  } as IConvocatoriaRequisitoIP
                } as IRequisitoIPNivelAcademico;
                return new StatusWrapper<IRequisitoIPNivelAcademico>(reqNivAcademico);
              }),
            );
          });

        return zip(...nivelesAcademicosObservable);
      })
    ).subscribe(
      (nivelesAcademicos) => {
        this.nivelesAcademicos$.next(nivelesAcademicos);
      }
    );
    this.subscriptions.push(subscription);
  }

  protected loadCategoriasProfesionales(requisitoIPId: number): void {
    const subscription = this.convocatoriaRequisitoIPService.findCategoriaProfesional(requisitoIPId).pipe(
      switchMap((requisitoIpCategoriasProfesionales) => {
        if (requisitoIpCategoriasProfesionales.length === 0) {
          return of([]);
        }
        const categoriasProfesionalesObservable = requisitoIpCategoriasProfesionales.
          map(requisitoIpCategoriaProfesional => {

            return this.categoriaProfesionalService.findById(requisitoIpCategoriaProfesional.categoriaProfesional.id).pipe(
              map(categoriaProfesional => {
                const reqCategoriaProfesional = {
                  id: requisitoIpCategoriaProfesional.id,
                  categoriaProfesional,
                  requisitoIP: {
                    id: requisitoIPId
                  } as IConvocatoriaRequisitoIP
                } as IRequisitoIPCategoriaProfesional;
                return new StatusWrapper<IRequisitoIPCategoriaProfesional>(reqCategoriaProfesional);
              }),
            );
          });

        return zip(...categoriasProfesionalesObservable);
      })
    ).subscribe(
      (categoriasProfesionales) => {
        this.categoriasProfesionales$.next(categoriasProfesionales);
      }
    );
    this.subscriptions.push(subscription);
  }

  protected buildFormGroup(): FormGroup {
    const form = this.fb.group({
      numMaximoIP: [null, Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      fechaMaximaNivelAcademico: [null],
      fechaMinimaNivelAcademico: [null],
      edadMaxima: ['', Validators.compose(
        [Validators.min(0), Validators.max(99)])],
      sexo: [null],
      vinculacionUniversidad: [null],
      fechaMaximaCategoriaProfesional: [null],
      fechaMinimaCategoriaProfesional: [null],
      numMinimoCompetitivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      numMinimoNoCompetitivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      numMaximoCompetitivosActivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      numMaximoNoCompetitivosActivos: ['', Validators.compose(
        [Validators.min(0), Validators.max(9999)])],
      otrosRequisitos: ['']
    }, {
      validators: [
        DateValidator.isAfter('fechaMinimaNivelAcademico', 'fechaMaximaNivelAcademico', false),
        DateValidator.isAfter('fechaMinimaCategoriaProfesional', 'fechaMaximaCategoriaProfesional', false)]
    });
    if (!this.hasEditPerm) {
      form.disable();
    }

    return form;
  }

  protected initializer(key: number): Observable<IConvocatoriaRequisitoIP> {
    if (this.getKey()) {
      return this.convocatoriaRequisitoIPService.getRequisitoIPConvocatoria(key).pipe(
        tap(() => this.loadNivelesAcademicos(key)),
        tap(() => this.loadCategoriasProfesionales(key))
      );
    }
  }

  buildPatch(value: IConvocatoriaRequisitoIP): { [key: string]: any } {
    this.requisitoIP = value;
    return {
      numMaximoIP: value ? value.numMaximoIP : null,
      fechaMaximaNivelAcademico: value ? value.fechaMaximaNivelAcademico : null,
      fechaMinimaNivelAcademico: value ? value.fechaMinimaNivelAcademico : null,
      edadMaxima: value ? value.edadMaxima : null,
      sexo: value ? value.sexo : null,
      vinculacionUniversidad: value ? value.vinculacionUniversidad : null,
      fechaMaximaCategoriaProfesional: value ? value.fechaMaximaCategoriaProfesional : null,
      fechaMinimaCategoriaProfesional: value ? value.fechaMinimaCategoriaProfesional : null,
      numMinimoCompetitivos: value ? value.numMinimoCompetitivos : null,
      numMinimoNoCompetitivos: value ? value.numMinimoNoCompetitivos : null,
      numMaximoCompetitivosActivos: value ? value.numMaximoCompetitivosActivos : null,
      numMaximoNoCompetitivosActivos: value ? value.numMaximoNoCompetitivosActivos : null,
      otrosRequisitos: value ? value.otrosRequisitos : null
    };
  }

  getValue(): IConvocatoriaRequisitoIP {
    if (this.requisitoIP === null) {
      this.requisitoIP = {} as IConvocatoriaRequisitoIP;
    }
    const form = this.getFormGroup().getRawValue();
    this.requisitoIP.numMaximoIP = form.numMaximoIP;
    this.requisitoIP.fechaMaximaNivelAcademico = form.fechaMaximaNivelAcademico;
    this.requisitoIP.fechaMinimaNivelAcademico = form.fechaMinimaNivelAcademico;
    this.requisitoIP.edadMaxima = form.edadMaxima;
    this.requisitoIP.sexo = form.sexo;
    this.requisitoIP.vinculacionUniversidad = form.vinculacionUniversidad;
    this.requisitoIP.fechaMaximaCategoriaProfesional = form.fechaMaximaCategoriaProfesional;
    this.requisitoIP.fechaMinimaCategoriaProfesional = form.fechaMinimaCategoriaProfesional;
    this.requisitoIP.numMinimoCompetitivos = form.numMinimoCompetitivos;
    this.requisitoIP.numMinimoNoCompetitivos = form.numMinimoNoCompetitivos;
    this.requisitoIP.numMaximoCompetitivosActivos = form.numMaximoCompetitivosActivos;
    this.requisitoIP.numMaximoNoCompetitivosActivos = form.numMaximoNoCompetitivosActivos;
    this.requisitoIP.otrosRequisitos = form.otrosRequisitos;
    return this.requisitoIP;
  }

  public addNivelAcademico(nivelAcademico: INivelAcademico) {
    if (nivelAcademico) {
      const requisitoIPNivelAcademico = {
        nivelAcademico
      } as IRequisitoIPNivelAcademico;
      const wrapped = new StatusWrapper<IRequisitoIPNivelAcademico>(requisitoIPNivelAcademico);
      wrapped.setCreated();
      const current = this.nivelesAcademicos$.value;
      current.push(wrapped);
      this.nivelesAcademicos$.next(current);

      this.setChanges(true);
    }
  }

  public addCategoriaProfesional(categoriaProfesional: ICategoriaProfesional) {
    if (categoriaProfesional) {
      const requisitoIPCategoriaProfesional = {
        categoriaProfesional
      } as IRequisitoIPCategoriaProfesional;
      const wrapped = new StatusWrapper<IRequisitoIPCategoriaProfesional>(requisitoIPCategoriaProfesional);
      wrapped.setCreated();
      const current = this.categoriasProfesionales$.value;
      current.push(wrapped);
      this.categoriasProfesionales$.next(current);

      this.setChanges(true);
    }
  }

  saveOrUpdate(): Observable<number> {
    const datosRequisitoIP = this.getValue();
    datosRequisitoIP.convocatoriaId = this.getKey() as number;

    const obs = datosRequisitoIP.id ? this.update(datosRequisitoIP) :
      this.create(datosRequisitoIP);
    return obs.pipe(
      map((value) => {
        this.requisitoIP = value;
        return this.requisitoIP.id;
      })
    );
  }

  private create(requisitoIP: IConvocatoriaRequisitoIP): Observable<IConvocatoriaRequisitoIP> {
    if (!requisitoIP.id) {
      requisitoIP.id = this.getKey() as number;
    }
    return this.convocatoriaRequisitoIPService.create(requisitoIP).pipe(
      tap(result => this.requisitoIP = result),
      switchMap((result) => this.saveOrUpdateNivelesAcademicos(result)),
      switchMap((result) => this.saveOrUpdateCategoriasProfesionales(result))
    );
  }

  private update(requisitoIP: IConvocatoriaRequisitoIP): Observable<IConvocatoriaRequisitoIP> {
    return this.convocatoriaRequisitoIPService.update(Number(this.getKey()), requisitoIP).pipe(
      tap(result => this.requisitoIP = result),
      switchMap((result) => this.saveOrUpdateNivelesAcademicos(result)),
      switchMap((result) => this.saveOrUpdateCategoriasProfesionales(result))
    );
  }

  public deleteNivelAcademico(wrapper: StatusWrapper<IRequisitoIPNivelAcademico>): void {
    const current = this.nivelesAcademicos$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      current.splice(index, 1);
      this.nivelesAcademicos$.next(current);
      this.setChanges(true);
    }
  }

  public deleteCategoriaProfesional(wrapper: StatusWrapper<IRequisitoIPCategoriaProfesional>): void {
    const current = this.categoriasProfesionales$.value;
    const index = current.findIndex(
      (value) => value === wrapper
    );
    if (index >= 0) {
      current.splice(index, 1);
      this.categoriasProfesionales$.next(current);
      this.setChanges(true);
    }
  }

  saveOrUpdateNivelesAcademicos(requisitoIp: IConvocatoriaRequisitoIP): Observable<IConvocatoriaRequisitoIP> {
    const values = this.nivelesAcademicos$.value.map(wrapper => {
      wrapper.value.requisitoIP = requisitoIp;
      return wrapper.value;
    }
    );
    const id = this.getKey() as number;
    return this.convocatoriaRequisitoIPService.updateNivelesAcademicos(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.nivelesAcademicos$.next(
            results.map(
              (value) => {
                value.nivelAcademico = values.find(
                  nivelAcademico => nivelAcademico.nivelAcademico.id === value.nivelAcademico.id
                ).nivelAcademico;
                return new StatusWrapper<IRequisitoIPNivelAcademico>(value);
              })
          );
        }),
        switchMap(() => of(requisitoIp))
      );
  }

  saveOrUpdateCategoriasProfesionales(requisitoIp: IConvocatoriaRequisitoIP): Observable<IConvocatoriaRequisitoIP> {
    const values = this.categoriasProfesionales$.value.map(wrapper => {
      wrapper.value.requisitoIP = requisitoIp;
      return wrapper.value;
    }
    );
    const id = this.getKey() as number;
    return this.convocatoriaRequisitoIPService.updateCategoriasProfesionales(id, values)
      .pipe(
        takeLast(1),
        map((results) => {
          this.categoriasProfesionales$.next(
            results.map(
              (value) => {
                value.categoriaProfesional = values.find(
                  categoriaProfesional => categoriaProfesional.categoriaProfesional.id === value.categoriaProfesional.id
                ).categoriaProfesional;
                return new StatusWrapper<IRequisitoIPCategoriaProfesional>(value);
              })
          );
        }),
        switchMap(() => of(requisitoIp))
      );
  }

}
