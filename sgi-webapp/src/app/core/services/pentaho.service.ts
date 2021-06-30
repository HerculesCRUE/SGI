
function open(uri: string) {
  const downloadLink = document.createElement('a');
  downloadLink.href = uri;
  downloadLink.target = '_blank';
  document.body.appendChild(downloadLink);
  downloadLink.click();
  document.body.removeChild(downloadLink);
}

export function openInformeFavorableMemoria(idEvaluacion: number) {
  const uri = `/pentaho/api/repos/%3Apublic%3AÉtica%3A0050.prpt/viewer?userid=user-002&password=user-002&output-type=pdf&showParameters=false&ID_EVALUACION=${idEvaluacion}`;
  open(uri);
}

export function openInformeFavorableTipoRatificacion(idEvaluacion: number) {
  const uri = `/pentaho/api/repos/%3Apublic%3AÉtica%3A0120.prpt/viewer?userid=user-002&password=user-002&output-type=pdf&showParameters=false&ID_EVALUACION=${idEvaluacion}`;
  open(uri);
}
