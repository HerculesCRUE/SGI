/**
 * Conversion del objeto ProduccionCientifica del SGI a la EDMA
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info(
    'produccion-cientifica-response-converter-mediator.mediate() - start'
  );

  var payload = mc.getPayloadJSON();
  var response = {};
  if (Array.isArray(payload)) {
    var produccionesCientificasResponse = [];
    payload.forEach(function (produccionCientifica) {
      produccionesCientificasResponse.push(
        transformIdRef(produccionCientifica)
      );
    });
    response = produccionesCientificasResponse;
  } else {
    response = transformIdRef(payload);
  }

  mc.setPayloadJSON(response);

  log.info('produccion-cientifica-response-converter-mediator.mediate() - end');
}

/**
 * Elimina el prefijo a los idRef de la ProduccionCientifica
 */
function transformIdRef(produccionCientifica) {
  if (!produccionCientifica) {
    return produccionCientifica;
  }
  if (produccionCientifica.idRef) {
    produccionCientifica.idRef = produccionCientifica.idRef.slice(produccionCientifica.idRef.indexOf("_") + 1);
  }
  return produccionCientifica;
}
