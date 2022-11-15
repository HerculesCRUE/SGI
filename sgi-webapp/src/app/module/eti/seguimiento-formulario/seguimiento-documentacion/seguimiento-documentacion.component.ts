import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { DocumentacionMemoriaListadoMemoriaComponent } from '../../documentacion-memoria/documentacion-memoria-listado-memoria/documentacion-memoria-listado-memoria.component';
import { Rol, SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';
import { SeguimientoDocumentacionFragment } from './seguimiento-documentacion.fragment';


@Component({
  selector: 'sgi-seguimiento-documentacion',
  templateUrl: './seguimiento-documentacion.component.html',
  styleUrls: ['./seguimiento-documentacion.component.scss']
})
export class SeguimientoDocumentacionComponent extends FragmentComponent implements AfterViewInit {
  @ViewChild('documentacionMemoriaListado') documentacion: DocumentacionMemoriaListadoMemoriaComponent;

  formPart: SeguimientoDocumentacionFragment;

  constructor(
    private actionService: SeguimientoFormularioActionService
  ) {
    super(actionService.FRAGMENT.DOCUMENTACION, actionService);

    this.formPart = this.fragment as SeguimientoDocumentacionFragment;
  }

  ngAfterViewInit() {
    this.documentacion.memoriaId = this.actionService.getEvaluacion()?.memoria?.id;
    this.documentacion.tipoEvaluacion = this.actionService.getEvaluacion()?.tipoEvaluacion?.id;
    this.documentacion.evaluacionId = this.actionService.getEvaluacion()?.id;
    this.actionService.getRol() === Rol.EVALUADOR ? this.documentacion.fichaEvaluador = false : this.documentacion.fichaEvaluador = true;
    this.documentacion?.ngAfterViewInit();
  }
}
