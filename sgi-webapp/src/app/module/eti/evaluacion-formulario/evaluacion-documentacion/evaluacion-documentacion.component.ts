import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IDocumentacionMemoria } from '@core/models/eti/documentacion-memoria';
import { DocumentacionMemoriaListadoMemoriaComponent } from '../../documentacion-memoria/documentacion-memoria-listado-memoria/documentacion-memoria-listado-memoria.component';
import { EvaluacionFormularioActionService, Rol } from '../evaluacion-formulario.action.service';



@Component({
  selector: 'sgi-evaluacion-documentacion',
  templateUrl: './evaluacion-documentacion.component.html',
  styleUrls: ['./evaluacion-documentacion.component.scss']
})
export class EvaluacionDocumentacionComponent extends FormFragmentComponent<IDocumentacionMemoria> implements AfterViewInit {

  @ViewChild('documentacionMemoriaListado') documentacion: DocumentacionMemoriaListadoMemoriaComponent;

  constructor(
    private actionService: EvaluacionFormularioActionService
  ) {
    super(actionService.FRAGMENT.DOCUMENTACION, actionService);
  }

  ngAfterViewInit() {
    this.documentacion.memoriaId = this.actionService.getEvaluacion()?.memoria?.id;
    this.documentacion.tipoEvaluacion = this.actionService.getEvaluacion()?.tipoEvaluacion?.id;
    this.actionService.getRol() === Rol.EVALUADOR ? this.documentacion.fichaEvaluador = false : this.documentacion.fichaEvaluador = true;
    this.documentacion.ngAfterViewInit();
  }
}
