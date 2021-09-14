import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { FormFragment } from '@core/services/action-service';
import { ProyectoAgrupacionGastoService } from '@core/services/csp/proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.service';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export class ProyectoAgrupacionGastoDatosGeneralesFragment extends FormFragment<IProyectoAgrupacionGasto> {
  proyectoAgrupacionGasto: IProyectoAgrupacionGasto;
  nombre: string;

  constructor(
    key: number,
    proyectoId: number,
    private service: ProyectoAgrupacionGastoService,
  ) {
    super(key);
    this.proyectoAgrupacionGasto = { proyectoId } as IProyectoAgrupacionGasto;
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup(
      {
        nombre: new FormControl()
      }, [
      Validators.required
    ]);
    return form;
  }
  protected buildPatch(value: IProyectoAgrupacionGasto): { [key: string]: any; } {
    const result = {
      nombre: value.nombre,
    };
    this.nombre = value.nombre;
    return result;
  }
  protected initializer(key: number): Observable<IProyectoAgrupacionGasto> {
    return this.service.findById(key).pipe(tap((proyectoAgrupacionGasto) => this.proyectoAgrupacionGasto = proyectoAgrupacionGasto));
  }
  getValue(): IProyectoAgrupacionGasto {
    const form = this.getFormGroup().controls;
    this.proyectoAgrupacionGasto.nombre = form.nombre.value;
    return this.proyectoAgrupacionGasto;
  }
  saveOrUpdate(): Observable<string | number | void> {
    const proyectoAgrupacionGasto = this.getValue();
    const observable$ = this.isEdit() ? this.update(proyectoAgrupacionGasto) : this.create(proyectoAgrupacionGasto);
    return observable$.pipe(
      map(result => {
        this.proyectoAgrupacionGasto = result;
        return this.proyectoAgrupacionGasto.id;
      })
    );
  }

  private create(proyectoAgrupacionGasto: IProyectoAgrupacionGasto): Observable<IProyectoAgrupacionGasto> {
    return this.service.create(proyectoAgrupacionGasto);
  }

  private update(proyectoAgrupacionGasto: IProyectoAgrupacionGasto): Observable<IProyectoAgrupacionGasto> {
    return this.service.update(proyectoAgrupacionGasto.id, proyectoAgrupacionGasto);
  }


}
