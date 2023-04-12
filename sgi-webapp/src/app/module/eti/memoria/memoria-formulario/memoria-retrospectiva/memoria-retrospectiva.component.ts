import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { IComentario } from '@core/models/eti/comentario';
import { Subscription } from 'rxjs';
import { IBlock } from '../../memoria-formly-form.fragment';
import { MemoriaActionService } from '../../memoria.action.service';
import { MemoriaRetrospectivaFragment } from './memoria-retrospectiva.fragment';

@Component({
  selector: 'sgi-memoria-retrospectiva',
  templateUrl: './memoria-retrospectiva.component.html',
  styleUrls: ['./memoria-retrospectiva.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class MemoriaRetrospectivaComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];

  block: IBlock;

  private memoriaFormularioFragment: MemoriaRetrospectivaFragment;

  get comentariosGenerales(): IComentario[] {
    return this.memoriaFormularioFragment.comentariosGenerales;
  }

  constructor(
    actionService: MemoriaActionService
  ) {
    super(actionService.FRAGMENT.RETROSPECTIVA, actionService);
    this.memoriaFormularioFragment = (this.fragment as MemoriaRetrospectivaFragment);
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
