import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { DocumentacionMemoriaListadoMemoriaComponent } from '../../documentacion-memoria/documentacion-memoria-listado-memoria/documentacion-memoria-listado-memoria.component';
import { Rol, SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';


@Component({
  selector: 'sgi-seguimiento-documentacion',
  templateUrl: './seguimiento-documentacion.component.html',
  styleUrls: ['./seguimiento-documentacion.component.scss']
})
export class SeguimientoDocumentacionComponent extends FormFragmentComponent<IDocumentacionMemoria> implements AfterViewInit {
  @ViewChild('documentacionMemoriaListado') documentacion: DocumentacionMemoriaListadoMemoriaComponent;

  constructor(
    private actionService: SeguimientoFormularioActionService
  ) {
    super(actionService.FRAGMENT.DOCUMENTACION, actionService);
  }

  ngAfterViewInit() {
    this.documentacion.memoriaId = this.actionService.getEvaluacion()?.memoria?.id;
    this.documentacion.tipoEvaluacion = this.actionService.getEvaluacion()?.tipoEvaluacion?.id;
    this.actionService.getRol() === Rol.EVALUADOR ? this.documentacion.fichaEvaluador = false : this.documentacion.fichaEvaluador = true;
    this.documentacion?.ngAfterViewInit();
  }
}
