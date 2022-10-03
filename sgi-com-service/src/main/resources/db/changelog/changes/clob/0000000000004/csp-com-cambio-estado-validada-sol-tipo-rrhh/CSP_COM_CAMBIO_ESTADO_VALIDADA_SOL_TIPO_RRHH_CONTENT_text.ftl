<#assign data = CSP_COM_CAMBIO_ESTADO_VALIDADA_SOL_TIPO_RRHH_DATA?eval_json />
Estimado/a investigador/a,

Le informamos que con fecha ${data.fechaEstado?datetime.iso?string('dd/MM/yyyy')}, su solicitud ${data.codigoInternoSolicitud} <#if data.tituloConvocatoria?has_content>dentro de la convocatoria ${data.tituloConvocatoria} </#if>ha sido validada por el/la tutor/a.

Reciba un cordial saludo.

Sección de Recursos Humanos de la Investigación y Plan Propio

Área de Investigación