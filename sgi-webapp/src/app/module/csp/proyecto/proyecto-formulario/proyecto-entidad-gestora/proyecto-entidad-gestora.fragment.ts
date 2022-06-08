import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { IProyectoEntidadGestora } from '@core/models/csp/proyecto-entidad-gestora';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FormFragment } from '@core/services/action-service';
import { ProyectoEntidadGestoraService } from '@core/services/csp/proyecto-entidad-gestora.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { Observable, of } from 'rxjs';
import { map, switchMap, takeLast, tap } from 'rxjs/operators';

export class ProyectoEntidadGestoraFragment extends FormFragment<IProyectoEntidadGestora> {

  private proyectoEntidadGestora: IProyectoEntidadGestora;

  ocultarSubEntidad: boolean;

  constructor(
    private fb: FormBuilder,
    key: number,
    private proyectoService: ProyectoService,
    private proyectoEntidadGestoraService: ProyectoEntidadGestoraService,
    private empresaService: EmpresaService,
    public readonly: boolean,
    public isVisor: boolean,
  ) {
    super(key, true);
    this.setComplete(true);
    this.proyectoEntidadGestora = {} as IProyectoEntidadGestora;
  }

  protected initializer(key: number): Observable<IProyectoEntidadGestora> {
    if (this.getKey()) {
      return this.proyectoService.findEntidadGestora(key).pipe(
        switchMap((entidadesGestoras) => {
          const proyectoEntidadesGestoras = entidadesGestoras.items;
          if (proyectoEntidadesGestoras.length > 0) {
            const entidadGestora = proyectoEntidadesGestoras[0];
            return this.empresaService.findById(entidadGestora.empresa.id).pipe(
              map((empresa) => {
                entidadGestora.empresa = empresa;
                return entidadGestora;
              })
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
      tipoEmpresa: new FormControl({
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

    this.subscriptions.push(
      form.controls.entidadGestora.valueChanges.subscribe(
        (entidadGestora) => this.onEntidadGestoraChange(entidadGestora)
      )
    );

    if (this.readonly) {
      form.disable();
    }

    return form;
  }

  buildPatch(entidadGestora: IProyectoEntidadGestora): { [key: string]: any } {
    this.proyectoEntidadGestora = entidadGestora;
    return {
      entidadGestora: entidadGestora.empresa
    };
  }

  getValue(): IProyectoEntidadGestora {
    this.proyectoEntidadGestora.empresa = this.getFormGroup().controls.entidadGestora.value;

    return this.proyectoEntidadGestora;
  }

  /**
   * Setea la entidad gestora seleccionada en el formulario
   * @param entidadGestora empresa
   */
  private onEntidadGestoraChange(entidadGestora: IEmpresa): void {
    if (entidadGestora) {

      this.getFormGroup().controls.identificadorFiscal.setValue(entidadGestora.tipoIdentificador?.nombre ?? '');
      this.getFormGroup().controls.nombre.setValue(entidadGestora.nombre);
      this.getFormGroup().controls.razonSocial.setValue(entidadGestora.razonSocial);
      this.getFormGroup().controls.direccionPostal.setValue(''); // TODO: añadir cuando se implemente /datos-contacto/empresa/{id}
      this.getFormGroup().controls.tipoEmpresa.setValue(''); // TODO: añadir cuando se implemente /datos-tipo-empresa/empresa/{id}

      if (entidadGestora.padreId) {
        this.getFormGroup().controls.entidad.setValue(2);
        this.getFormGroup().controls.codigoSubentidad.setValue(entidadGestora.numeroIdentificacion);

        this.subscriptions.push(
          this.empresaService.findById(entidadGestora.padreId)
            .subscribe(empresa => {
              if (empresa) {
                this.getFormGroup().controls.numeroIdentificadorFiscal.setValue(empresa.numeroIdentificacion);
                this.getFormGroup().controls.razonSocialPrincipal.setValue(empresa.razonSocial);
                this.getFormGroup().controls.nombrePrincipal.setValue(empresa.nombre);
              } else {
                this.getFormGroup().controls.numeroIdentificadorFiscal.setValue(entidadGestora.numeroIdentificacion);
                this.getFormGroup().controls.razonSocialPrincipal.setValue('');
                this.getFormGroup().controls.nombrePrincipal.setValue('');
              }
            }
            )
        );

        this.ocultarSubEntidad = true;
      } else {
        this.getFormGroup().controls.entidad.setValue(1);
        this.getFormGroup().controls.numeroIdentificadorFiscal.setValue(entidadGestora.numeroIdentificacion);
        this.getFormGroup().controls.nombrePrincipal.setValue('');
        this.getFormGroup().controls.razonSocialPrincipal.setValue('');
        this.getFormGroup().controls.codigoSubentidad.setValue('');
        this.ocultarSubEntidad = false;
      }
    } else {
      this.getFormGroup().controls.identificadorFiscal.setValue('');
      this.getFormGroup().controls.numeroIdentificadorFiscal.setValue('');
      this.getFormGroup().controls.nombre.setValue('');
      this.getFormGroup().controls.razonSocial.setValue('');
      this.getFormGroup().controls.entidad.setValue('');
      this.getFormGroup().controls.direccionPostal.setValue('');
      this.getFormGroup().controls.tipoEmpresa.setValue('');
      this.getFormGroup().controls.nombrePrincipal.setValue('');
      this.getFormGroup().controls.razonSocialPrincipal.setValue('');
    }
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
        this.proyectoEntidadGestora = Object.assign(this.proyectoEntidadGestora, result);
      })
    );
  }

  private updateProyectoEntidadGestora(proyectoEntidadGestora: IProyectoEntidadGestora): Observable<void> {
    return this.proyectoEntidadGestoraService.update(
      proyectoEntidadGestora.id, proyectoEntidadGestora).pipe(
        map(result => {
          this.proyectoEntidadGestora = Object.assign(this.proyectoEntidadGestora, result);
        })
      );
  }

  private deleteProyectoEntidadGestora(proyectoEntidadGestora: IProyectoEntidadGestora): Observable<void> {
    return this.proyectoEntidadGestoraService.deleteById(
      proyectoEntidadGestora.id).pipe(
        tap(() => {
          this.proyectoEntidadGestora = {} as IProyectoEntidadGestora;
          this.proyectoEntidadGestora.empresa = {} as IEmpresa;
        })
      );
  }

}
