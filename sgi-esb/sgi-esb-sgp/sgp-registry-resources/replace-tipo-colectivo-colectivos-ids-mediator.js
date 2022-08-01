/**
 * Reemplaza el parametro de busqueda tipoColectivo por los colectivoId correspondientes
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("replace-tipo-colectivo-colectivos-ids-mediator.mediate() - start");

  var path = decodeURI(mc.getProperty('path'));
  var hasRsqlParams = mc.getProperty('hasRsqlParams') == String(true);
  log.info("replace-tipo-colectivo-colectivos-ids-mediator.mediate() - initial query: " + path + ", hasRsqlParams: " + hasRsqlParams);
  var regex = hasRsqlParams ? /tipoColectivo==([^;,&)])*[;,]?/g : /tipoColectivo=([^;,&)])*[;,]?/g;
  var tipoColectivoQueries = path.match(regex);
  if (tipoColectivoQueries) {
    var tipoColectivoQuery = tipoColectivoQueries[0];

    var regexSeparador = hasRsqlParams ? /[;,]/g : /[&]/g;
    if (regexSeparador.test(tipoColectivoQuery)) {
      tipoColectivoQuery = tipoColectivoQuery.substring(0, tipoColectivoQuery.length - 1);
    }

    // Remplaza el tipoColectivo por los colectivos correspondientes
    var tipoColectivoId = hasRsqlParams ? tipoColectivoQuery.split("==")[1].replace(/"/g, '')
      : tipoColectivoQuery.split("=")[1].replace(/"/g, '');
    var filterColectivos = hasRsqlParams ? 'colectivoId=in=(#ids#)' : 'colectivoId=(#ids#)';
    var colectivosId = '';
    switch (tipoColectivoId) {
      case 'SOLICITANTE_ETICA':
        colectivosId = '1,3,4';
        break;
      case 'EVALUADOR_ETICA':
        colectivosId = '2,3,4';
        break;
      case 'EQUIPO_TRABAJO_ETICA':
        colectivosId = '1,2,3,4';
        break;
      case 'SOLICITANTE_CSP':
        colectivosId = '1,2,3,4';
        break;
      case 'RESPONSABLE_ECONOMICO_CSP':
        colectivosId = '2,3';
        break;
      case 'AUTOR_INVENCION':
        colectivosId = '1,2,3,4';
        break;
      case 'RESPONSABLE_PROYECTO_EXTERNO':
        colectivosId = '4';
        break;
      case 'AUTOR_PRC':
        colectivosId = '1,2,3,4';
        break;
      case 'DESTINATARIO_COMUNICADO':
        colectivosId = '1,2,3,4';
        break;
      case 'PERSONA_AUTORIZADA_GRUPO':
        colectivosId = '1,2,3,4';
        break;
      case 'TUTOR_CSP':
        colectivosId = '2,3';
        break;
      case 'MIEMBRO_EQUIPO_EMPRESA_EXPLOTACION_RESULTADOS':
        colectivosId = '1,2,3,4';
        break;
      default:
        colectivosId = '0';
        break;
    }
    filterColectivos = filterColectivos.replace('#ids#', colectivosId);

    path = path.replace(tipoColectivoQuery, filterColectivos);
  }

  mc.setProperty('pathWithColectivosIds', encodeURI(path));

  log.info("replace-tipo-colectivo-colectivos-ids-mediator.mediate() - final query = " + path);

  log.info("replace-tipo-colectivo-colectivos-ids-mediator.mediate() - end");
}
