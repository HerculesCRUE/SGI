import { FormControl, FormGroup } from '@angular/forms';
import { IEntidad } from '@core/models/csp/entidad';
import { IEntidadFinanciadora } from '@core/models/csp/entidad-financiadora';
import { FormFragment } from '@core/services/action-service';
import { Observable, of } from 'rxjs';

export class SolicitudProyectoPresupuestoDatosGeneralesFragment extends FormFragment<IEntidad> {

  constructor(
    solicitudId: number,
    private entidad: IEntidad,
    public financiadora: boolean,
  ) {
    super(solicitudId);
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup(
      {
        nombre: new FormControl({ value: '', disabled: true }),
        cif: new FormControl({ value: '', disabled: true })
      }
    );

    if (this.financiadora) {
      form.addControl('fuenteFinanciacion', new FormControl({ value: undefined, disabled: true }));
      form.addControl('ambito', new FormControl({ value: undefined, disabled: true }));
      form.addControl('tipoFinanciacion', new FormControl({ value: undefined, disabled: true }));
      form.addControl('porcentajeFinanciacion', new FormControl({ value: undefined, disabled: true }));
      form.addControl('importeFinanciacion', new FormControl({ value: undefined, disabled: true }));
    }

    return form;
  }

  protected buildPatch(entidadFinanciadora: IEntidadFinanciadora): { [key: string]: any; } {
    this.entidad = entidadFinanciadora;
    const result = {
      nombre: entidadFinanciadora?.empresa?.nombre,
      cif: entidadFinanciadora?.empresa?.numeroIdentificacion,
      fuenteFinanciacion: entidadFinanciadora?.fuenteFinanciacion?.nombre,
      ambito: entidadFinanciadora?.fuenteFinanciacion?.tipoAmbitoGeografico?.nombre,
      tipoFinanciacion: entidadFinanciadora?.tipoFinanciacion?.nombre,
      porcentajeFinanciacion: entidadFinanciadora?.porcentajeFinanciacion,
      importeFinanciacion: entidadFinanciadora?.importeFinanciacion
    };

    return result;
  }

  protected initializer(key: number): Observable<IEntidad> {
    return of(this.entidad);
  }

  getValue(): IEntidad {
    return this.entidad;
  }

  saveOrUpdate(): Observable<number> {
    return of(this.getKey() as number);
  }

}
