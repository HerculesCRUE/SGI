import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SgiAuthService } from '@sgi/framework/auth';
import { SelectorModuloComponent } from '../selector-modulo/selector-modulo.component';

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'sgi-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  constructor(
    public matDialog: MatDialog,
    public authService: SgiAuthService
  ) {
  }

  /**
   * Abre ventana modal para cambiar de módulo de la aplicación
   */
  selectorModulo() {
    const config = {
      maxWidth: '500px',
      maxHeight: '500px',
    };
    this.matDialog.open(SelectorModuloComponent, config);
  }
}
