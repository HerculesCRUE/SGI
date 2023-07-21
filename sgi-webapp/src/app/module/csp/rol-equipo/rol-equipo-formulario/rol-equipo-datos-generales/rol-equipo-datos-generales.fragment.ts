import { FormControl, FormGroup, Validators } from '@angular/forms';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { FormFragment } from '@core/services/action-service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

export class RolEquipoDatosGeneralesFragment extends FormFragment<IRolProyecto> {

  private rolEquipo: IRolProyecto;

  constructor(
    private readonly logger: NGXLogger,
    key: number,
    private rolProyectoService: RolProyectoService,
  ) {
    super(key);
    this.rolEquipo = {} as IRolProyecto;
    this.rolEquipo.activo = true;
  }


  protected buildFormGroup(): FormGroup {
    const fb = new FormGroup({
      nombre: new FormControl(null, [Validators.maxLength(50), Validators.required]),
      abreviatura: new FormControl(null, [Validators.maxLength(5), Validators.required]),
      equipo: new FormControl(null, [Validators.required]),
      rolPrincipal: new FormControl(null, [Validators.required]),
      orden: new FormControl(null),
      baremablePRC: new FormControl(null, [Validators.required]),
      descripcion: new FormControl('', [Validators.maxLength(250)]),
    });
    return fb;
  }

  protected buildPatch(rolEquipo: IRolProyecto): { [key: string]: any; } {
    const result = {
      id: rolEquipo.id,
      nombre: rolEquipo.nombre,
      abreviatura: rolEquipo.abreviatura,
      equipo: rolEquipo.equipo,
      rolPrincipal: rolEquipo.rolPrincipal,
      orden: rolEquipo.orden,
      baremablePRC: rolEquipo.baremablePRC,
      descripcion: rolEquipo.descripcion,
      activo: rolEquipo.activo,
    } as IRolProyecto;
    this.rolEquipo = rolEquipo;
    return result;
  }

  protected initializer(key: number): Observable<IRolProyecto> {
    return this.rolProyectoService.findById(key).pipe(
      catchError((err) => {
        this.logger.error(err);
        return EMPTY;
      })
    );
  }

  getValue(): IRolProyecto {
    const form = this.getFormGroup().value;
    const rolEquipo = this.rolEquipo;
    rolEquipo.nombre = form.nombre;
    rolEquipo.abreviatura = form.abreviatura;
    rolEquipo.equipo = form.equipo;
    rolEquipo.rolPrincipal = form.rolPrincipal;
    rolEquipo.orden = form.orden;
    rolEquipo.baremablePRC = form.baremablePRC;
    rolEquipo.descripcion = form.descripcion;
    return rolEquipo;
  }

  saveOrUpdate(): Observable<number> {
    const rolEquipos = this.getValue();
    const observable$ = this.isEdit() ? this.update(rolEquipos) : this.create(rolEquipos);
    return observable$.pipe(
      map((result: IRolProyecto) => {
        return result.id;
      })
    );
  }

  private create(rolEquipo: IRolProyecto): Observable<IRolProyecto> {
    return this.rolProyectoService.create(rolEquipo).pipe(
      tap((result: IRolProyecto) => {
        this.rolEquipo = result;
      })
    );
  }

  private update(rolEquipo: IRolProyecto): Observable<IRolProyecto> {
    return this.rolProyectoService.update(rolEquipo.id, rolEquipo).pipe(
      tap((result: IRolProyecto) => {
        this.rolEquipo = result;
      })
    );
  }
}
