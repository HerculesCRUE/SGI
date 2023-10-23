import { IComite } from '@core/models/eti/comite';
import { ESTADO_MEMORIA } from '@core/models/eti/tipo-estado-memoria';
import { TIPO_EVALUACION } from '@core/models/eti/tipo-evaluacion';
import { ApartadoService } from '@core/services/eti/apartado.service';
import { BloqueService } from '@core/services/eti/bloque.service';
import { EvaluacionService } from '@core/services/eti/evaluacion.service';
import { FormularioService } from '@core/services/eti/formulario.service';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { RespuestaService } from '@core/services/eti/respuesta.service';
import { DatosAcademicosService } from '@core/services/sgp/datos-academicos.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { VinculacionService } from '@core/services/sgp/vinculacion/vinculacion.service';
import { NGXLogger } from 'ngx-logger';
import { MemoriaFormlyFormFragment } from '../../memoria-formly-form.fragment';

export class MemoriaSeguimientoFinalFragment extends MemoriaFormlyFormFragment {

  constructor(
    logger: NGXLogger,
    readonly: boolean,
    key: number,
    comite: IComite,
    moduloInv: boolean,
    formularioService: FormularioService,
    bloqueService: BloqueService,
    apartadoService: ApartadoService,
    respuestaService: RespuestaService,
    peticionEvaluacionService: PeticionEvaluacionService,
    vinculacionService: VinculacionService,
    datosAcademicosService: DatosAcademicosService,
    personaService: PersonaService,
    memoriaService: MemoriaService,
    evaluacionService: EvaluacionService
  ) {
    super(
      logger,
      key,
      comite,
      readonly,
      TIPO_EVALUACION.SEGUIMIENTO_FINAL,
      moduloInv,
      formularioService,
      memoriaService,
      evaluacionService,
      bloqueService,
      respuestaService,
      peticionEvaluacionService,
      vinculacionService,
      datosAcademicosService,
      personaService,
      apartadoService
    );
  }

  protected isEditable(): boolean {
    if (!this.memoria?.estadoActual?.id) {
      return false;
    }
    const estado = this.memoria.estadoActual.id as ESTADO_MEMORIA;
    switch (+estado) {
      case ESTADO_MEMORIA.COMPLETADA_SEGUIMIENTO_FINAL:
      case ESTADO_MEMORIA.FIN_EVALUACION_SEGUIMIENTO_ANUAL:
      case ESTADO_MEMORIA.EN_ACLARACION_SEGUIMIENTO_FINAL:
        // Un año o más de diferencia entre la fecha actual y la fecha de fin.
        return this.memoria.peticionEvaluacion.fechaFin.diffNow('years').years <= -1;
      default:
        return false;
    }
  }

}
