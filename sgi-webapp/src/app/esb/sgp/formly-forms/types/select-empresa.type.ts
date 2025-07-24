import { Component, OnDestroy, OnInit } from '@angular/core';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { FieldType } from '@ngx-formly/material/form-field';
import { NGXLogger } from 'ngx-logger';
import { of, Subscription } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  template: `
      <sgi-select-empresa
        [formControl]="formControl"
        [formlyAttributes]="field"
        [required]="to.required"
        [onlyEmpresasPrincipales]="to.onlyEmpresasPrincipales"
      >
      </sgi-select-empresa>
 `
})
export class SelectEmpresaTypeComponent extends FieldType implements OnInit, OnDestroy {

  private subscription: Subscription;

  constructor(
    private readonly logger: NGXLogger,
    private readonly empresaService: EmpresaService
  ) {
    super();
  }

  ngOnInit(): void {
    // Si el campo tiene el atributo returnFullObject a true, se enviara el objeto emnpresa completo en el formly
    if (!this.field.templateOptions?.returnFullObject) {
      // El comportamiento por defecto es que el select de empresa devuelva solo el id de la empresa seleccionada.
      this.setIdParser();
    }

    this.initValueResolver();
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  private setIdParser(): void {
    this.field.parsers = [(value) => value?.id];
  }

  private initValueResolver(): void {
    const id = this.value?.id ?? this.value;
    if (!id) {
      return;
    }

    this.subscription = this.empresaService.findById(id).pipe(
      catchError((error) => {
        this.logger.error(error);
        return of({ id } as IEmpresa);
      })
    ).subscribe((response) => {
      this.value = response;
      this.formControl.setValue(this.value);
    });
  }

}
