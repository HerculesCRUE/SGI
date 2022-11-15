import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IPrograma } from '@core/models/csp/programa';
import { FormFragment } from '@core/services/action-service';
import { ProgramaService } from '@core/services/csp/programa.service';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

export class PlanInvestigacionDatosGeneralesFragment extends FormFragment<IPrograma> {

  programas: IPrograma;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private planService: ProgramaService
  ) {
    super(key);
    this.programas = {} as IPrograma;
    this.programas.activo = true;
  }

  protected buildFormGroup(): FormGroup {
    const fb = new FormGroup({
      nombre: new FormControl('', [Validators.maxLength(200)]),
      descripcion: new FormControl('', [Validators.maxLength(4000)]),
    });
    return fb;
  }

  protected buildPatch(programa: IPrograma): { [key: string]: any; } {
    const result = {
      id: programa.id,
      activo: programa.activo,
      descripcion: programa.descripcion,
      nombre: programa.nombre
    } as IPrograma;
    this.programas = programa;
    return result;
  }

  protected initializer(key: number): Observable<IPrograma> {
    return this.planService.findById(key).pipe(
      catchError((error) => {
        this.logger.error(error);
        return EMPTY;
      })
    );
  }

  getValue(): IPrograma {
    const form = this.getFormGroup().value;
    const programa = this.programas;
    programa.nombre = form.nombre;
    programa.descripcion = form.descripcion;
    return programa;
  }

  saveOrUpdate(): Observable<number> {
    const programas = this.getValue();
    const observable$ = this.isEdit() ? this.update(programas) : this.create(programas);
    return observable$.pipe(
      map((result: IPrograma) => {
        return result.id;
      })
    );
  }

  private create(programa: IPrograma): Observable<IPrograma> {
    return this.planService.create(programa).pipe(
      tap((result: IPrograma) => {
        this.programas = result;
      })
    );
  }

  private update(programa: IPrograma): Observable<IPrograma> {
    return this.planService.update(programa.id, programa).pipe(
      tap((result: IPrograma) => {
        this.programas = result;
      })
    );
  }
}
