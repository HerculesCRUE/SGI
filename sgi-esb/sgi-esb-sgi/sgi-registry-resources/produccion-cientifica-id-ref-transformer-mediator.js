/**
 * Transformacion del id del objeto ProduccionCientifica de la EDMA al SGI
 */
function mediate(mc) {
  var log = mc.getServiceLog();
  log.info('produccion-cientifica-id-ref-transformer-mediator.mediate() - start');

  var payload = mc.getPayloadJSON();
  var idRefPrefix = 'CVN_';
  mc.setProperty('idRefTransformed', payload.idRef = idRefPrefix.concat(payload.idRef));
  mc.setPayloadJSON(payload);

  log.info('produccion-cientifica-id-ref-transformer-mediator.mediate() - end');
}
