/**
 * Transformacion de los ids del objeto clasificacion o listados de clasificaciones de la UM al del SGI
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info("clasificacion-ids-transformer-mediator.mediate() - start");

  var isRoot = (mc.getProperty('isRoot') == true);
  var prefix = mc.getProperty('prefix');
  var payload = mc.getPayloadJSON();
  var response = {};
  if (Array.isArray(payload)) {
    var empresasResponse = [];
    payload.forEach(function (empresa) {
      empresasResponse.push(transformIds(empresa, prefix, isRoot));
    });
    response = empresasResponse
  } else {
    response = transformIds(payload, prefix, isRoot);
  }

  mc.setPayloadJSON(response);

  log.info("clasificacion-ids-transformer-mediator.mediate() - end");
}

/**
 * Añade el prefijo a los ids de la clasificacion
 */
function transformIds(clasificacion, prefix, isRoot) {
  if (prefix) {
    if (clasificacion.id) {
      clasificacion.id = prefix.concat(clasificacion.id);
    }
    if (clasificacion.padreId) {
      clasificacion.padreId = prefix.concat(clasificacion.padreId);
    }
    if (prefix == 'CNAE_' && isRoot && clasificacion.padreId == null) {
      clasificacion.padreId = 'CNAE_ROOT';
    }
  }
  return clasificacion;
}
