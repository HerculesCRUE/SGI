<#assign data = CSP_COM_SOL_CAMB_EST_CONC_DATA?eval_json />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  </head>
  <body>
    <p>Estimado/a investigador/a,</p>
    <p>le informamos que <#if data.fechaConcesionConvocatoria??>con fecha ${data.fechaConcesionConvocatoria?datetime.iso?string("dd/MM/yyyy")} </#if>se ha publicado la “Resolución de Concesión” de la convocatoria de ${data.tituloConvocatoria} en la que su solicitud aparece concedida.</p>
    <p>Más información disponible en los siguientes enlaces:</p>
    <ul>
      <#list data.enlaces as enlace>
          <ul>
            <#assign url>${enlace.url}</#assign>
            <#if enlace.tipoEnlace?? && enlace.descripcion?? && enlace.url?? >
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: <#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.descripcion?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.descripcion?esc}</#outputformat> (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.tipoEnlace?? && enlace.url?? >          
            <li><#outputformat "HTML">${enlace.tipoEnlace?esc}</#outputformat>: (link a <a href="${url}">${url}</a>)</li>
            <#elseif enlace.url??>
            <li>Link a <a href="${url}">${url}</a></li>
            </#if>          
          </ul>
        </li>
      </#list>
    </ul>
    <p>En breve nos pondremos en contacto con usted para tramitar su incorporación a la Universidad de Murcia.</p>
    <p>Reciba un cordial saludo.</p>
    <p>Sección de Recursos Humanos de la Investigación y Plan Propio</p>
    <p>Área de Investigación</p>
  </body>
</html>