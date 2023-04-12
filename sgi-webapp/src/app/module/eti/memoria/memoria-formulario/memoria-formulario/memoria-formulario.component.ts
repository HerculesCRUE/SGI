import { StepperSelectionEvent, STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { FragmentComponent } from '@core/component/fragment.component';
import { IComentario } from '@core/models/eti/comentario';
import { Subscription } from 'rxjs';
import { IBlock } from '../../memoria-formly-form.fragment';
import { MemoriaActionService } from '../../memoria.action.service';
import { MemoriaFormularioFragment } from './memoria-formulario.fragment';

@Component({
  selector: 'sgi-memoria-formulario',
  templateUrl: './memoria-formulario.component.html',
  styleUrls: ['./memoria-formulario.component.scss'],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false }
    }
  ],
  encapsulation: ViewEncapsulation.None
})
export class MemoriaFormularioComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];

  blocks: IBlock[] = [];

  @ViewChild(MatStepper, { static: true }) private stepper: MatStepper;

  private memoriaFormularioFragment: MemoriaFormularioFragment;

  get comentariosGenerales(): IComentario[] {
    return this.memoriaFormularioFragment.comentariosGenerales;
  }

  constructor(
    actionService: MemoriaActionService
  ) {
    super(actionService.FRAGMENT.FORMULARIO, actionService);
    this.memoriaFormularioFragment = (this.fragment as MemoriaFormularioFragment);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.subscriptions.push(
      this.memoriaFormularioFragment.blocks$.subscribe((values) => this.blocks = values)
    );
    // Mark first block as selected
    this.memoriaFormularioFragment.selectedIndex$.next(this.stepper.selectedIndex);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  onStepChange(stepperEvent: StepperSelectionEvent): void {
    this.memoriaFormularioFragment.selectedIndex$.next(stepperEvent.selectedIndex);
  }

  nextStep(): void {
    this.memoriaFormularioFragment.performChecks(true);
    this.stepper.next();
  }

  previousStep(): void {
    this.stepper.previous();
  }
}
