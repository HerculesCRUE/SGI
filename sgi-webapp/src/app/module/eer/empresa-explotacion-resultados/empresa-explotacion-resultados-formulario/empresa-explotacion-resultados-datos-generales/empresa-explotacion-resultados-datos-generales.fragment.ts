import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IEmpresaExplotacionResultados } from '@core/models/eer/empresa-explotacion-resultados';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FormFragment } from '@core/services/action-service';
import { EmpresaExplotacionResultadosService } from '@core/services/eer/empresa-explotacion-resultados/empresa-explotacion-resultados.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable, of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

export class EmpresaExplotacionResultadosDatosGeneralesFragment extends FormFragment<IEmpresaExplotacionResultados> {

  private empresaExplotacionResultados: IEmpresaExplotacionResultados;

  showRazonSocial = true;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private readonly empresaExplotacionResultadosService: EmpresaExplotacionResultadosService,
    private empresaService: EmpresaService,
    private personaService: PersonaService,
    private readonly: boolean
  ) {
    super(key);
    this.empresaExplotacionResultados = !key ? {} as IEmpresaExplotacionResultados : { id: key } as IEmpresaExplotacionResultados;
  }

  protected initializer(key: string | number): Observable<IEmpresaExplotacionResultados> {
    return this.empresaExplotacionResultadosService.findById(key as number).pipe(
      switchMap((empresa) => {
        if (empresa.entidad?.id) {
          return this.empresaService.findById(empresa.entidad?.id).pipe(
            map(entidad => {
              empresa.entidad = entidad;
              return empresa;
            })
          );
        } else {
          return of(empresa);
        }
      }),
      switchMap((empresa) => {
        if (empresa.solicitante?.id) {
          return this.personaService.findById(empresa.solicitante?.id).pipe(
            map(solicitante => {
              empresa.solicitante = solicitante;
              return empresa;
            })
          );
        } else {
          return of(empresa);
        }
      }),
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      fechaSolicitud: new FormControl(null, Validators.required),
      tipoEmpresa: new FormControl(null, Validators.required),
      estado: new FormControl(null, Validators.required),
      solicitante: new FormControl(null),
      nombreRazonSocial: new FormControl(null),
      entidad: new FormControl(null),
      objetoSocial: new FormControl(null, [Validators.required, Validators.maxLength(1000)]),
      conocimientoTecnologia: new FormControl(null, [Validators.required, Validators.maxLength(1000)]),
      numeroProtocolo: new FormControl(null, Validators.maxLength(50)),
      notario: new FormControl(null, Validators.maxLength(250)),
      fechaConstitucion: new FormControl(null),
      fechaAprobacionCG: new FormControl(null),
      fechaIncorporacion: new FormControl(null),
      fechaDesvinculacion: new FormControl(null),
      fechaCese: new FormControl(null),
      observaciones: new FormControl(null, Validators.maxLength(2000))
    });

    this.subscriptions.push(
      formGroup.controls.entidad.valueChanges.subscribe(
        (value) => {
          this.addValidations(value);
        }
      )
    );

    return formGroup;
  }

  buildPatch(empresaExplotacionResultados: IEmpresaExplotacionResultados): { [key: string]: any } {
    this.empresaExplotacionResultados = empresaExplotacionResultados;
    let formValues: { [key: string]: any } = {
      fechaSolicitud: empresaExplotacionResultados.fechaSolicitud,
      tipoEmpresa: empresaExplotacionResultados.tipoEmpresa,
      estado: empresaExplotacionResultados.estado,
      solicitante: empresaExplotacionResultados.solicitante ?? null,
      nombreRazonSocial: empresaExplotacionResultados.nombreRazonSocial,
      objetoSocial: empresaExplotacionResultados.objetoSocial,
      entidad: empresaExplotacionResultados.entidad ?? null,
      conocimientoTecnologia: empresaExplotacionResultados.conocimientoTecnologia,
      numeroProtocolo: empresaExplotacionResultados.numeroProtocolo,
      notario: empresaExplotacionResultados.notario,
      fechaConstitucion: empresaExplotacionResultados.fechaConstitucion,
      fechaAprobacionCG: empresaExplotacionResultados.fechaAprobacionCG,
      fechaIncorporacion: empresaExplotacionResultados.fechaIncorporacion,
      fechaDesvinculacion: empresaExplotacionResultados.fechaDesvinculacion,
      fechaCese: empresaExplotacionResultados.fechaCese,
      observaciones: empresaExplotacionResultados.observaciones
    };

    this.addValidations(empresaExplotacionResultados.entidad);

    return formValues;
  }

  private addValidations(entidad: IEmpresa) {
    const form = this.getFormGroup().controls;

    if (form.entidad.value || entidad) {
      form.nombreRazonSocial.setValidators(Validators.maxLength(250));
      this.showRazonSocial = false;
    } else {
      form.nombreRazonSocial.setValidators([Validators.required, Validators.maxLength(250)]);
      this.showRazonSocial = true;
    }
    form.nombreRazonSocial.updateValueAndValidity();
  }

  getValue(): IEmpresaExplotacionResultados {
    const form = this.getFormGroup().controls;
    this.empresaExplotacionResultados.fechaSolicitud = form.fechaSolicitud.value;
    this.empresaExplotacionResultados.tipoEmpresa = form.tipoEmpresa.value;
    this.empresaExplotacionResultados.estado = form.estado.value;
    this.empresaExplotacionResultados.solicitante = form.solicitante.value;
    if (form.entidad.value) {
      this.empresaExplotacionResultados.nombreRazonSocial = null;
    } else {
      this.empresaExplotacionResultados.nombreRazonSocial = form.nombreRazonSocial.value;
    }
    this.empresaExplotacionResultados.objetoSocial = form.objetoSocial.value;
    this.empresaExplotacionResultados.entidad = form.entidad.value;
    this.empresaExplotacionResultados.conocimientoTecnologia = form.conocimientoTecnologia.value;
    this.empresaExplotacionResultados.numeroProtocolo = form.numeroProtocolo.value;
    this.empresaExplotacionResultados.notario = form.notario.value;
    this.empresaExplotacionResultados.fechaConstitucion = form.fechaConstitucion.value;
    this.empresaExplotacionResultados.fechaAprobacionCG = form.fechaAprobacionCG.value;
    this.empresaExplotacionResultados.fechaIncorporacion = form.fechaIncorporacion.value;
    this.empresaExplotacionResultados.fechaDesvinculacion = form.fechaDesvinculacion.value;
    this.empresaExplotacionResultados.fechaCese = form.fechaCese.value;
    this.empresaExplotacionResultados.observaciones = form.observaciones.value;

    return this.empresaExplotacionResultados;
  }

  saveOrUpdate(): Observable<number> {
    const empresaExplotacionResultados = this.getValue();
    const observable$ = this.isEdit() ? this.update(empresaExplotacionResultados) :
      this.create(empresaExplotacionResultados);
    return observable$.pipe(
      map(value => {
        this.empresaExplotacionResultados = value;
        return this.empresaExplotacionResultados.id;
      })
    );
  }

  private create(empresaExplotacionResultados: IEmpresaExplotacionResultados): Observable<IEmpresaExplotacionResultados> {
    return this.empresaExplotacionResultadosService.create(empresaExplotacionResultados).pipe(
      tap(result => this.empresaExplotacionResultados.id = result.id),
    );
  }

  private update(empresaExplotacionResultados: IEmpresaExplotacionResultados): Observable<IEmpresaExplotacionResultados> {
    return this.empresaExplotacionResultadosService.update(empresaExplotacionResultados.id, empresaExplotacionResultados).pipe(
      tap(result => this.empresaExplotacionResultados = result)
    );
  }

}
