import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { IComentario } from '@core/models/eti/comentario';
import { Subscription } from 'rxjs';
import { IBlock } from '../../memoria-formly-form.fragment';
import { MemoriaActionService } from '../../memoria.action.service';
import { MemoriaSeguimientoFinalFragment } from './memoria-seguimiento-final.fragment';

@Component({
  selector: 'sgi-memoria-seguimiento-final',
  templateUrl: './memoria-seguimiento-final.component.html',
  styleUrls: ['./memoria-seguimiento-final.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class MemoriaSeguimientoFinalComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];

  block: IBlock;

  private memoriaFormularioFragment: MemoriaSeguimientoFinalFragment;

  get comentariosGenerales(): IComentario[] {
    return this.memoriaFormularioFragment.comentariosGenerales;
  }

  constructor(
    actionService: MemoriaActionService
  ) {
    super(actionService.FRAGMENT.SEGUIMIENTO_FINAL, actionService);
    this.memoriaFormularioFragment = (this.fragment as MemoriaSeguimientoFinalFragment);
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
