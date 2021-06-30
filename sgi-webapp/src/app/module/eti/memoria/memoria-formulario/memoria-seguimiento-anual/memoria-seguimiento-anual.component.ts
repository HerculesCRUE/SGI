import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { Subscription } from 'rxjs';
import { IBlock } from '../../memoria-formly-form.fragment';
import { MemoriaActionService } from '../../memoria.action.service';
import { MemoriaSeguimientoAnualFragment } from './memoria-seguimiento-anual.fragment';

@Component({
  selector: 'sgi-memoria-seguimiento-anual',
  templateUrl: './memoria-seguimiento-anual.component.html',
  styleUrls: ['./memoria-seguimiento-anual.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class MemoriaSeguimientoAnualComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];

  block: IBlock;

  private memoriaFormularioFragment: MemoriaSeguimientoAnualFragment;

  constructor(
    actionService: MemoriaActionService
  ) {
    super(actionService.FRAGMENT.SEGUIMIENTO_ANUAL, actionService);
    this.memoriaFormularioFragment = (this.fragment as MemoriaSeguimientoAnualFragment);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.subscriptions.push(
      this.memoriaFormularioFragment.blocks$.subscribe((values) => this.block = values[0])
    );
    // Mark first block as selected
    this.memoriaFormularioFragment.selectedIndex$.next(0);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
