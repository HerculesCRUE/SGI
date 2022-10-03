import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { Estado, IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaAreaTematica } from '@core/models/csp/convocatoria-area-tematica';
import { IConvocatoriaEntidadGestora } from '@core/models/csp/convocatoria-entidad-gestora';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { UnidadGestionPublicService } from '@core/services/csp/unidad-gestion-public.service';
import { EmpresaPublicService } from '@core/services/sgemp/empresa-public.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

export interface AreaTematicaData {
  padre: IAreaTematica;
  convocatoriaAreaTematica: StatusWrapper<IConvocatoriaAreaTematica>;
}

export class ConvocatoriaDatosGeneralesPublicFragment extends FormFragment<IConvocatoria> {
  readonly areasTematicas$ = new BehaviorSubject<AreaTematicaData[]>([]);

  private convocatoria: IConvocatoria;
  private convocatoriaEntidadGestora: IConvocatoriaEntidadGestora;

  readonly modeloEjecucion$: Subject<IModeloEjecucion> = new BehaviorSubject<IModeloEjecucion>(null);

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private convocatoriaService: ConvocatoriaPublicService,
    private empresaService: EmpresaPublicService,
    private unidadGestionService: UnidadGestionPublicService
  ) {
    super(key, true);
    this.setComplete(true);
    this.convocatoria = {
      activo: true,
      estado: Estado.BORRADOR
    } as IConvocatoria;
    this.convocatoriaEntidadGestora = {} as IConvocatoriaEntidadGestora;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      estado: new FormControl({ value: null, disabled: true }),
      codigo: new FormControl(null, Validators.maxLength(50)),
      unidadGestion: new FormControl(null, Validators.required),
      fechaPublicacion: new FormControl(null),
      fechaProvisional: new FormControl(null),
      fechaConcesion: new FormControl(null),
      titulo: new FormControl('', [Validators.required, Validators.maxLength(1000)]),
      modeloEjecucion: new FormControl(null),
      finalidad: new FormControl(null),
      duracion: new FormControl('', [Validators.min(1), Validators.max(9999)]),
      excelencia: new FormControl(null),
      ambitoGeografico: new FormControl(null),
      formularioSolicitud: new FormControl(FormularioSolicitud.PROYECTO),
      clasificacionCVN: new FormControl(null),
      regimenConcurrencia: new FormControl(null),
      entidadGestora: new FormControl(null),
      objeto: new FormControl('', Validators.maxLength(2000)),
      observaciones: new FormControl('', Validators.maxLength(2000)),
      palabrasClave: new FormControl(null)
    });

    form.disable();

    return form;
  }

  buildPatch(convocatoria: IConvocatoria): { [key: string]: any } {
    this.convocatoria = convocatoria;
    return {
      modeloEjecucion: convocatoria.modeloEjecucion?.nombre ?? '',
      unidadGestion: convocatoria.unidadGestion?.acronimo ?? '',
      codigo: convocatoria.codigo,
      fechaPublicacion: convocatoria.fechaPublicacion,
      fechaProvisional: convocatoria.fechaProvisional,
      fechaConcesion: convocatoria.fechaConcesion,
      titulo: convocatoria.titulo,
      objeto: convocatoria.objeto,
      observaciones: convocatoria.observaciones,
      finalidad: convocatoria.finalidad?.nombre ?? '',
      regimenConcurrencia: convocatoria.regimenConcurrencia?.nombre ?? '',
      formularioSolicitud: convocatoria.formularioSolicitud,
      duracion: convocatoria.duracion,
      ambitoGeografico: convocatoria.ambitoGeografico?.nombre ?? '',
      clasificacionCVN: convocatoria.clasificacionCVN,
      estado: convocatoria.estado,
      excelencia: convocatoria.excelencia
    };
  }

  isEstadoRegistrada() {
    return this.convocatoria.estado === Estado.REGISTRADA;
  }

  protected initializer(key: number): Observable<IConvocatoria> {
    return this.convocatoriaService.findById(key).pipe(
      switchMap((convocatoria) => {
        return this.unidadGestionService.findById(convocatoria.unidadGestion.id).pipe(
          map(unidadGestion => {
            convocatoria.unidadGestion = unidadGestion;
            return convocatoria;
          })
        );
      }),
      switchMap((convocatoria) => {
        return this.convocatoriaService.findAllConvocatoriaEntidadGestora(key).pipe(
          switchMap((listResult) => {
            const convocatoriasEntidadGestoras = listResult.items;
            if (convocatoriasEntidadGestoras.length > 0) {
              this.convocatoriaEntidadGestora = convocatoriasEntidadGestoras[0];
              return this.empresaService.findById(this.convocatoriaEntidadGestora.empresa.id).pipe(
                map((empresa) => {
                  this.getFormGroup().get('entidadGestora').setValue(empresa);
                  return convocatoria;
                })
              );
            }
            else {
              return of(convocatoria);
            }
          })
        );
      }),
      switchMap(convocatoria =>
        this.convocatoriaService.findPalabrasClave(key).pipe(
          map(({ items }) => items.map(convocatoriaPalabraClave => convocatoriaPalabraClave.palabraClave)),
          tap(palabrasClave => this.getFormGroup().controls.palabrasClave.setValue(palabrasClave)),
          map(() => convocatoria)
        )
      ),
      tap(() => this.loadAreasTematicas(key)),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  private loadAreasTematicas(id: number): void {
    const subscription = this.convocatoriaService.findAreaTematicas(id).pipe(
      map(response => response.items),
      map(convocatoriasAreaTematicas => {
        const list: AreaTematicaData[] = [];
        convocatoriasAreaTematicas.forEach(
          convocatoriaAreaTematica => {
            const element: AreaTematicaData = {
              padre: undefined,
              convocatoriaAreaTematica: new StatusWrapper<IConvocatoriaAreaTematica>(convocatoriaAreaTematica),
            };
            list.push(this.loadAreaData(element));
          }
        );
        return list;
      })
    ).subscribe(areas => {
      this.areasTematicas$.next(areas);
    });
    this.subscriptions.push(subscription);
  }

  private getSecondLevelAreaTematica(areaTematica: IAreaTematica): IAreaTematica {
    if (areaTematica?.padre?.padre) {
      return this.getSecondLevelAreaTematica(areaTematica.padre);
    }
    return areaTematica;
  }

  private loadAreaData(data: AreaTematicaData): AreaTematicaData {
    const areaTematica = data.convocatoriaAreaTematica.value.areaTematica;
    if (areaTematica) {
      const result = this.getSecondLevelAreaTematica(areaTematica);
      const padre = result.padre ? result.padre : areaTematica;
      const element: AreaTematicaData = {
        padre,
        convocatoriaAreaTematica: data.convocatoriaAreaTematica,
      };
      return element;
    }
    return data;
  }

  getValue(): IConvocatoria {
    const form = this.getFormGroup().controls;
    this.convocatoria.unidadGestion = form.unidadGestion.value;
    this.convocatoria.modeloEjecucion = form.modeloEjecucion.value;
    this.convocatoria.codigo = form.codigo.value;
    this.convocatoria.fechaPublicacion = form.fechaPublicacion.value;
    this.convocatoria.fechaProvisional = form.fechaProvisional.value;
    this.convocatoria.fechaConcesion = form.fechaConcesion.value;
    this.convocatoria.titulo = form.titulo.value;
    this.convocatoria.objeto = form.objeto.value;
    this.convocatoria.observaciones = form.observaciones.value;
    this.convocatoria.finalidad = form.finalidad.value;
    this.convocatoria.regimenConcurrencia = form.regimenConcurrencia.value;
    this.convocatoria.duracion = form.duracion.value;
    this.convocatoria.ambitoGeografico = form.ambitoGeografico.value;
    this.convocatoria.formularioSolicitud = form.formularioSolicitud.value;
    this.convocatoria.clasificacionCVN = form.clasificacionCVN.value;
    this.convocatoria.excelencia = form.excelencia.value;

    return this.convocatoria;
  }

  saveOrUpdate(): Observable<number> {
    throw new Error('Method not implemented.');
  }

}
