import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { DocumentacionMemoriaListadoMemoriaComponent } from '../../documentacion-memoria/documentacion-memoria-listado-memoria/documentacion-memoria-listado-memoria.component';
import { EvaluacionFormularioActionService, Rol } from '../evaluacion-formulario.action.service';
import { EvaluacionDocumentacionFragment } from './evaluacion-documentacion.fragment';



@Component({
  selector: 'sgi-evaluacion-documentacion',
  templateUrl: './evaluacion-documentacion.component.html',
  styleUrls: ['./evaluacion-documentacion.component.scss']
})
export class EvaluacionDocumentacionComponent extends FragmentComponent implements AfterViewInit {

  @ViewChild('documentacionMemoriaListado') documentacion: DocumentacionMemoriaListadoMemoriaComponent;

  formPart: EvaluacionDocumentacionFragment;

  constructor(
    private actionService: EvaluacionFormularioActionService
  ) {
    super(actionService.FRAGMENT.DOCUMENTACION, actionService);

    this.formPart = this.fragment as EvaluacionDocumentacionFragment;
  }

  ngAfterViewInit() {
    this.documentacion.memoriaId = this.actionService.getEvaluacion()?.memoria?.id;
    this.documentacion.tipoEvaluacion = this.actionService.getEvaluacion()?.tipoEvaluacion?.id;
    this.documentacion.evaluacionId = this.actionService.getEvaluacion()?.id;
    this.actionService.getRol() === Rol.EVALUADOR ? this.documentacion.fichaEvaluador = false : this.documentacion.fichaEvaluador = true;
    this.documentacion.ngAfterViewInit();
  }
}
