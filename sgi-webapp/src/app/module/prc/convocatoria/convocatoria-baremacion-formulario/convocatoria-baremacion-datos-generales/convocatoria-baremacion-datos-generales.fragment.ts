import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { FormFragment } from '@core/services/action-service';
import { ConvocatoriaBaremacionService } from '@core/services/prc/convocatoria-baremacion/convocatoria-baremacion.service';
import { Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

export class ConvocatoriaBaremacionDatosGeneralesFragment extends FormFragment<IConvocatoriaBaremacion> {

  constructor(
    key: number,
    private convocatoriaBaremacion: IConvocatoriaBaremacion,
    private readonly isEditPerm: boolean,
    private readonly convocatoriaBaremacionService: ConvocatoriaBaremacionService
  ) {
    super(key);
  }

  protected buildFormGroup(): FormGroup {
    const form = new FormGroup({
      id: new FormControl({ value: '', disabled: true }),
      nombre: new FormControl('', [Validators.required, Validators.maxLength(100)]),
      anio: new FormControl('', Validators.required),
      aniosBaremables: new FormControl('', Validators.required),
      ultimoAnio: new FormControl('', Validators.required),
      importeTotal: new FormControl('', Validators.required),
      partidaPresupuestaria: new FormControl('', Validators.maxLength(100))
    });

    if (this.isEdit() && !this.isEditPerm) {
      form.disable();
    }

    return form;
  }

  protected buildPatch(convocatoriaBaremacion: IConvocatoriaBaremacion): { [key: string]: any; } {
    return {
      id: convocatoriaBaremacion.id,
      nombre: convocatoriaBaremacion.nombre,
      anio: convocatoriaBaremacion.anio,
      aniosBaremables: convocatoriaBaremacion.aniosBaremables,
      ultimoAnio: convocatoriaBaremacion.ultimoAnio,
      importeTotal: convocatoriaBaremacion.importeTotal,
      partidaPresupuestaria: convocatoriaBaremacion.partidaPresupuestaria,
    };
  }

  protected initializer(key: number): Observable<IConvocatoriaBaremacion> {
    return of(this.convocatoriaBaremacion);
  }

  getValue(): IConvocatoriaBaremacion {
    const form = this.getFormGroup().value;
    const convocatoriaBaremacion = this.convocatoriaBaremacion ?? {} as IConvocatoriaBaremacion;
    convocatoriaBaremacion.nombre = form.nombre;
    convocatoriaBaremacion.anio = form.anio;
    convocatoriaBaremacion.aniosBaremables = form.aniosBaremables;
    convocatoriaBaremacion.ultimoAnio = form.ultimoAnio;
    convocatoriaBaremacion.importeTotal = form.importeTotal;
    convocatoriaBaremacion.partidaPresupuestaria = form.partidaPresupuestaria;

    return convocatoriaBaremacion;
  }

  saveOrUpdate(): Observable<string | number | void> {
    const convocatoriaBaremacion = this.getValue();
    const observable$ = this.isEdit() ? this.update(convocatoriaBaremacion) : this.create(convocatoriaBaremacion);

    return observable$.pipe(
      map((result: IConvocatoriaBaremacion) => {
        this.convocatoriaBaremacion = result;
        return result?.id;
      })
    );
  }

  private create(convocatoriaBaremacion: IConvocatoriaBaremacion): Observable<IConvocatoriaBaremacion> {
    let cascade = of(void 0);
    if (this.formGroupStatus$.value.changes) {
      cascade = cascade.pipe(
        switchMap(() => this.createConvocatoriaBaremacion(convocatoriaBaremacion))
      );
    }

    return cascade;
  }

  private createConvocatoriaBaremacion(convocatoriaBaremacion: IConvocatoriaBaremacion): Observable<IConvocatoriaBaremacion> {
    return this.convocatoriaBaremacionService.create(convocatoriaBaremacion).pipe(
      tap((result: IConvocatoriaBaremacion) => {
        this.convocatoriaBaremacion = result;
        this.refreshInitialState(true);
      })
    );
  }

  private update(convocatoriaBaremacion: IConvocatoriaBaremacion): Observable<IConvocatoriaBaremacion> {
    let cascade = of(void 0);
    if (this.formGroupStatus$.value.changes) {
      cascade = cascade.pipe(
        switchMap(() => this.updateConvocatoriaBaremacion(convocatoriaBaremacion))
      );
    } else {
      cascade = cascade.pipe(
        switchMap(() => of(convocatoriaBaremacion))
      );
    }

    return cascade;
  }

  private updateConvocatoriaBaremacion(convocatoriaBaremacion: IConvocatoriaBaremacion): Observable<IConvocatoriaBaremacion> {
    return this.convocatoriaBaremacionService.update(Number(this.getKey()), convocatoriaBaremacion).pipe(
      tap((result: IConvocatoriaBaremacion) => {
        this.convocatoriaBaremacion = result;
      })
    );
  }
}
