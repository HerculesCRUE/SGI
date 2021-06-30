import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IProyectoSocio } from '@core/models/csp/proyecto-socio';
import { FormFragment } from '@core/services/action-service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { DateValidator } from '@core/validators/date-validator';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { Observable } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

export class ProyectoSocioDatosGeneralesFragment extends FormFragment<IProyectoSocio> {
  proyectoSocio: IProyectoSocio;

  constructor(
    key: number,
    proyectoId: number,
    private service: ProyectoSocioService,
    private empresaService: EmpresaService
  ) {
    super(key);
    this.proyectoSocio = { proyectoId } as IProyectoSocio;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup(
      {
        empresa: new FormControl({
          value: '',
          disabled: this.isEdit()
        }, [
          Validators.required
        ]),
        rolSocio: new FormControl('', [
          Validators.required,
          IsEntityValidator.isValid()
        ]),
        numInvestigadores: new FormControl(undefined, [
          Validators.min(1),
          Validators.max(9999)
        ]),
        importePresupuesto: new FormControl(undefined, [
          Validators.min(1),
          Validators.max(2_147_483_647)
        ]),
        importeConcedido: new FormControl(undefined, [
          Validators.min(1),
          Validators.max(2_147_483_647)
        ]),
        fechaInicio: new FormControl(null),
        fechaFin: new FormControl(null),
      },
      {
        validators: [
          DateValidator.isAfter('fechaInicio', 'fechaFin', false)
        ]
      }
    );
    return form;
  }

  protected buildPatch(proyectoSocio: IProyectoSocio): { [key: string]: any; } {
    const result = {
      empresa: proyectoSocio.empresa,
      rolSocio: proyectoSocio.rolSocio,
      numInvestigadores: proyectoSocio.numInvestigadores,
      importeConcedido: proyectoSocio.importeConcedido,
      importePresupuesto: proyectoSocio.importePresupuesto,
      fechaInicio: proyectoSocio.fechaInicio,
      fechaFin: proyectoSocio.fechaFin,
    };
    return result;
  }

  protected initializer(key: number): Observable<IProyectoSocio> {
    return this.service.findById(key)
      .pipe(
        switchMap(proyectoSocio => {
          return this.empresaService.findById(proyectoSocio.empresa.id)
            .pipe(
              map(empresa => {
                proyectoSocio.empresa = empresa;
                return proyectoSocio;
              })
            );
        }),
        tap((proyectoSocio) => this.proyectoSocio = proyectoSocio)
      );
  }

  getValue(): IProyectoSocio {
    const form = this.getFormGroup().controls;
    this.proyectoSocio.empresa = form.empresa.value;
    this.proyectoSocio.rolSocio = form.rolSocio.value;
    this.proyectoSocio.numInvestigadores = form.numInvestigadores.value;
    this.proyectoSocio.importeConcedido = form.importeConcedido.value;
    this.proyectoSocio.importePresupuesto = form.importePresupuesto.value;
    this.proyectoSocio.fechaInicio = form.fechaInicio.value;
    this.proyectoSocio.fechaFin = form.fechaFin.value;
    return this.proyectoSocio;
  }

  saveOrUpdate(): Observable<number> {
    const proyectoSocio = this.getValue();

    const observable$ = this.isEdit() ? this.update(proyectoSocio) : this.create(proyectoSocio);
    return observable$.pipe(
      map(result => {
        this.proyectoSocio = result;
        return this.proyectoSocio.id;
      })
    );
  }

  private create(proyectoSocio: IProyectoSocio): Observable<IProyectoSocio> {
    return this.service.create(proyectoSocio);
  }

  private update(proyectoSocio: IProyectoSocio): Observable<IProyectoSocio> {
    return this.service.update(proyectoSocio.id, proyectoSocio);
  }
}
