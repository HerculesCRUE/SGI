import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { IDatosContacto } from '@core/models/sgemp/datos-contacto';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FormFragment } from '@core/services/action-service';
import { ProyectoEntidadGestoraService } from '@core/services/csp/proyecto-entidad-gestora.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { DatosContactoService } from '@core/services/sgemp/datos-contacto/datos-contacto.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, filter, map, switchMap, takeLast, tap } from 'rxjs/operators';

export interface ProyectoEntidadGestoraDetail extends IProyectoEntidadGestora {
  direccion: string;
  empresaPadre: IEmpresa;
}

export class ProyectoEntidadGestoraFragment extends FormFragment<ProyectoEntidadGestoraDetail> {

  private proyectoEntidadGestora: ProyectoEntidadGestoraDetail;

  constructor(
    private fb: FormBuilder,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoEntidadGestoraService: ProyectoEntidadGestoraService,
    private empresaService: EmpresaService,
    private datosContactoService: DatosContactoService,
    public readonly: boolean,
    public isVisor: boolean,
  ) {
    super(key, true);
    this.setComplete(true);
    this.proyectoEntidadGestora = {} as ProyectoEntidadGestoraDetail;
  }

  protected initializer(key: number): Observable<ProyectoEntidadGestoraDetail> {
    if (this.getKey()) {
      return this.proyectoService.findEntidadGestora(key).pipe(
        switchMap((entidadesGestoras) => {
          const proyectoEntidadesGestoras = entidadesGestoras.items;
          if (proyectoEntidadesGestoras.length > 0) {
            const entidadGestora = proyectoEntidadesGestoras[0] as ProyectoEntidadGestoraDetail;
            return this.empresaService.findById(entidadGestora.empresa.id).pipe(
              map((empresa) => {
                entidadGestora.empresa = empresa;
                return entidadGestora;
              }),
              switchMap(entidadGestora => {
                if (!entidadGestora.empresa.padreId) {
                  return of(entidadGestora);
                }

                return this.empresaService.findById(entidadGestora.empresa.padreId).pipe(
                  map((empresaPadre) => {
                    entidadGestora.empresaPadre = empresaPadre;
                    return entidadGestora;
                  }),
                  catchError(() => {
                    return of(entidadGestora);
                  })
                );
              }),
              switchMap(entidadGestora => this.datosContactoService.findByEmpresaId(entidadGestora.empresa.id)
                .pipe(
                  map(datosContacto => {
                    entidadGestora.direccion = datosContacto.direccion;
                    return entidadGestora;
                  }),
                  catchError(() => {
                    return of(entidadGestora);
                  })
                )
              )
            );
          }
          return of(this.proyectoEntidadGestora);
        })
      );
    }
  }

  protected buildFormGroup(): FormGroup {
    const form = this.fb.group({
      entidadGestora: new FormControl({
        value: null,
        disabled: this.readonly
      }),
      identificadorFiscal: new FormControl({
        value: '',
        disabled: true
      }),
      numeroIdentificadorFiscal: new FormControl({
        value: '',
        disabled: true
      }),
      nombre: new FormControl({
        value: '',
        disabled: true
      }),
      razonSocial: new FormControl({
        value: '',
        disabled: true
      }),
      entidad: new FormControl({
        value: '',
        disabled: true
      }),
      codigoSubentidad: new FormControl({
        value: '',
        disabled: true
      }),
      direccionPostal: new FormControl({
        value: '',
        disabled: true
      }),
      nombrePrincipal: new FormControl({
        value: '',
        disabled: true
      }),
      razonSocialPrincipal: new FormControl({
        value: '',
        disabled: true
      })
    });
    if (this.isVisor) {
      form.disable();
    }

    if (this.readonly) {
      form.disable();
    }

    return form;
  }

  buildPatch(proyectoEntidadGestora: ProyectoEntidadGestoraDetail): { [key: string]: any } {
    this.proyectoEntidadGestora = proyectoEntidadGestora;

    this.subscriptions.push(
      this.getFormGroup().controls.entidadGestora.valueChanges.pipe(
        filter(entidadGestora => entidadGestora?.id !== this.proyectoEntidadGestora?.empresa?.id && (!!this.proyectoEntidadGestora?.empresa || !!entidadGestora)),
        switchMap((entidadGestora: IEmpresa) =>
          forkJoin({
            entidadGestora: of(entidadGestora),
            empresaPadre: entidadGestora?.padreId ? this.empresaService.findById(entidadGestora.padreId) : of(null),
            datosContacto: entidadGestora?.id ? this.datosContactoService.findByEmpresaId(entidadGestora.id) : of(null)
          }))
      ).subscribe(
        ({ entidadGestora, empresaPadre, datosContacto }) => this.onEntidadGestoraChange(entidadGestora, empresaPadre, datosContacto)
      )
    );

    return {
      entidadGestora: proyectoEntidadGestora?.empresa ?? '',
      codigoSubentidad: proyectoEntidadGestora?.empresa?.padreId ? proyectoEntidadGestora.empresa.numeroIdentificacion : '',
      direccionPostal: proyectoEntidadGestora?.direccion ?? '',
      entidad: !!proyectoEntidadGestora ? (proyectoEntidadGestora.empresa?.padreId ? 2 : 1) : '',
      identificadorFiscal: proyectoEntidadGestora.empresa?.tipoIdentificador?.nombre ?? '',
      nombre: proyectoEntidadGestora.empresa?.nombre ?? '',
      nombrePrincipal: proyectoEntidadGestora?.empresaPadre?.nombre ?? '',
      numeroIdentificadorFiscal: proyectoEntidadGestora?.empresaPadre?.numeroIdentificacion ?? proyectoEntidadGestora?.empresa?.numeroIdentificacion ?? '',
      razonSocial: proyectoEntidadGestora?.empresa?.razonSocial ?? '',
      razonSocialPrincipal: proyectoEntidadGestora?.empresaPadre?.razonSocial ?? '',
    };
  }

  getValue(): ProyectoEntidadGestoraDetail {
    this.proyectoEntidadGestora.empresa = this.getFormGroup().controls.entidadGestora.value;

    return this.proyectoEntidadGestora;
  }

  public isSelectedEntidadGestoraSubentidad(): boolean {
    return !!this.proyectoEntidadGestora?.empresa?.padreId;
  }

  /**
   * Setea la entidad gestora seleccionada en el formulario
   * @param entidadGestora empresa
   */
  private onEntidadGestoraChange(entidadGestora: IEmpresa, empresaPadre: IEmpresa, datosContacto: IDatosContacto): void {
    this.proyectoEntidadGestora.empresa = entidadGestora;
    this.proyectoEntidadGestora.direccion = datosContacto?.direccion ?? '';
    this.proyectoEntidadGestora.empresaPadre = empresaPadre;

    this.getFormGroup().controls.codigoSubentidad.setValue(entidadGestora?.padreId ? entidadGestora.numeroIdentificacion : '');
    this.getFormGroup().controls.direccionPostal.setValue(datosContacto?.direccion ?? '');
    this.getFormGroup().controls.entidad.setValue(!!entidadGestora ? (entidadGestora.padreId ? 2 : 1) : '');
    this.getFormGroup().controls.identificadorFiscal.setValue(entidadGestora?.tipoIdentificador?.nombre ?? '');
    this.getFormGroup().controls.nombre.setValue(entidadGestora?.nombre ?? '');
    this.getFormGroup().controls.nombrePrincipal.setValue(empresaPadre?.nombre ?? '');
    this.getFormGroup().controls.numeroIdentificadorFiscal.setValue(empresaPadre?.numeroIdentificacion ?? entidadGestora?.numeroIdentificacion ?? '');
    this.getFormGroup().controls.razonSocial.setValue(entidadGestora?.razonSocial ?? '');
    this.getFormGroup().controls.razonSocialPrincipal.setValue(empresaPadre?.razonSocial ?? '');
  }

  saveOrUpdate(): Observable<void> {
    let observable$: Observable<any>;
    const proyectoEntidadGestora = this.getValue();
    if (!proyectoEntidadGestora.empresa) {
      observable$ = this.deleteProyectoEntidadGestora(proyectoEntidadGestora);
    } else {
      observable$ = proyectoEntidadGestora.id ?
        this.updateProyectoEntidadGestora(proyectoEntidadGestora) : this.createProyectoEntidadGestora(proyectoEntidadGestora);
    }
    return observable$.pipe(
      takeLast(1)
    );
  }

  private createProyectoEntidadGestora(proyectoEntidadGestora: IProyectoEntidadGestora): Observable<void> {
    proyectoEntidadGestora.proyectoId = this.getKey() as number;
    return this.proyectoEntidadGestoraService.create(proyectoEntidadGestora).pipe(
      map(result => {
        this.proyectoEntidadGestora.id = result.id;
        return void 0;
      })
    );
  }

  private updateProyectoEntidadGestora(proyectoEntidadGestora: IProyectoEntidadGestora): Observable<void> {
    return this.proyectoEntidadGestoraService.update(
      proyectoEntidadGestora.id, proyectoEntidadGestora).pipe(
        map(() => void 0)
      );
  }

  private deleteProyectoEntidadGestora(proyectoEntidadGestora: IProyectoEntidadGestora): Observable<void> {
    return this.proyectoEntidadGestoraService.deleteById(
      proyectoEntidadGestora.id).pipe(
        tap(() => {
          this.proyectoEntidadGestora = {} as ProyectoEntidadGestoraDetail;
          this.proyectoEntidadGestora.empresa = {} as IEmpresa;
        })
      );
  }

}
