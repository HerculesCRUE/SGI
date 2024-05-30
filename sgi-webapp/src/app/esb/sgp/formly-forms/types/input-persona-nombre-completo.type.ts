import { Component, OnDestroy, OnInit } from '@angular/core';
import { PersonaService } from '@core/services/sgp/persona.service';
import { FieldType } from '@ngx-formly/material/form-field';
import { Subscription } from 'rxjs';

@Component({
  template: `
      <input
        matInput 
        type="text"
        [id]="id"
        [formControl]="formControl"
        [formlyAttributes]="field"
        [placeholder]="to.placeholder"
        [tabIndex]="to.tabindex"
        [required]="to.required"
        [errorStateMatcher]="errorStateMatcher"
      >
 `
})
export class InputPersonaNombreCompletoTypeComponent extends FieldType implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];

  constructor(
    private readonly personaService: PersonaService
  ) {
    super();
  }

  ngOnInit(): void {
    if (!this.value) {
      return;
    }

    this.subscriptions.push(
      this.personaService.findById(this.value).subscribe(persona => {
        this.value = `${persona.nombre} ${persona.apellidos}`;
      })
    );

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe());
  }

}
