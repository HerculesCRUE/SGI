import { STEPPER_GLOBAL_OPTIONS, StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { FragmentComponent } from '@core/component/fragment.component';
import { IBloque } from '@core/models/eti/bloque';
import { IComentario } from '@core/models/eti/comentario';
import { Group } from '@core/services/action-service';
import { LanguageService } from '@core/services/language.service';
import { TranslateService } from '@ngx-translate/core';
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
    private readonly actionService: MemoriaActionService,
    private readonly languageService: LanguageService,
    private readonly translateService: TranslateService
  ) {
    super(actionService.FRAGMENT.FORMULARIO, actionService, translateService);
    this.memoriaFormularioFragment = (this.fragment as MemoriaFormularioFragment);

    this.subscriptions.push(this.translateService.onLangChange.subscribe((value) => {
      this.blocks.forEach((block, index) => {
        const lastBlock = this.memoriaFormularioFragment.getLastFilledBlockIndex();
        if (index <= lastBlock) {
          if (block.loaded$.value) {

            const mapGroup = new Map<string, Group>();
            block.formlyData.fields.forEach(f => {
              mapGroup.set(f.key.toString(), f.group);
            });

            block.formlyData.fields = [];
            this.memoriaFormularioFragment.fillFormlyData(true, block.formlyData.model, block.formlyData.options.formState, block.formlyData.fields, block.questions, mapGroup);
          }
        }
      });

    }));
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

  getNombreBloque(bloque: IBloque): string {
    return this.languageService.getFieldValue(bloque.nombre);
  }

  getOrdenBloque(key: string): number {
    const keyString = key.replace(/ap/g, "");
    const keyArray = keyString.split("_");
    return Number(keyArray[0]);
  }

  protected setupI18N(): void { }
}
