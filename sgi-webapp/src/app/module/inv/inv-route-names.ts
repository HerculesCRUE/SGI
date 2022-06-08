import { ACTAS_ROUTE } from '../eti/acta/acta-route-names';
import { CHECKLIST_ROUTE } from '../eti/checklist/checklist-route-names';
import { MEMORIAS_ROUTE } from '../eti/memoria/memoria-route-names';
import { PETICION_EVALUACION_ROUTE } from '../eti/peticion-evaluacion/peticion-evaluacion-route-names';

export const INV_ROUTE_NAMES = {
  EVALUACIONES: 'evaluaciones',
  SEGUIMIENTOS: 'seguimientos',
  PETICIONES_EVALUACION: PETICION_EVALUACION_ROUTE,
  MEMORIAS: MEMORIAS_ROUTE,
  CHECKLIST: CHECKLIST_ROUTE,
  ACTAS: ACTAS_ROUTE,
  CONVOCATORIAS: 'convocatorias',
  SOLICITUDES: 'solicitudes',
  PROYECTOS: 'proyectos',
  AUTORIZACIONES: 'autorizaciones',
  GRUPOS: 'grupos',
  VALIDACION_PUBLICACIONES: 'validacion-publicaciones',
  VALIDACION_CONGRESOS: 'validacion-congresos',
  VALIDACION_OBRAS_ARTISTICAS: 'validacion-obras-artisticas',
  VALIDACION_COMITES_EDITORIALES: 'validacion-comites-editoriales',
  VALIDACION_TESIS_TFM_TFG: 'validacion-tesis-tfm-tfg',
  VALIDACION_ACTIVIDADES_IDI: 'validacion-actividades-idi',
  INFORMES: 'informes',
};
