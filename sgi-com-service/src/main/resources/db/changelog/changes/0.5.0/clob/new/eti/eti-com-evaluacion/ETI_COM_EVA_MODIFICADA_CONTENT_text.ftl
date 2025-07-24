<#assign data = ETI_COM_EVA_MODIFICADA_DATA?eval_json />
<#macro renderEs>
<#setting locale="es">
Estimado/a miembro del Comité:
    
Le informamos de que la persona responsable de la memoria ha realizado las modificaciones solicitadas. Puede proceder a su revisión:
    
- Título de la memoria: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Referencia: ${data.referenciaMemoria}

Reciba un cordial saludo,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEn>
<#setting locale="en">
Dear Committee Member:
    
Please be advised that the person responsible for the report has implemented the requested modifications. You may proceed with your review:
    
- Report title: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Reference: ${data.referenciaMemoria}

Yours sincerely,
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#macro renderEu>
<#setting locale="eu">
Batzordekide agurgarria:
    
Memoriaren arduradunak eskatutako aldaketak egin dituela jakinarazten dizugu. Berrikusteko prest dituzu:
    
- Memoriaren izenburua: ${sgi.getFieldValue(data.tituloSolicitudEvaluacion)}
- Erreferentzia: ${data.referenciaMemoria}

Jaso agur bero bat.
Nombre del servicio, cargo o persona responsable de la Universidad que firma el comunicado
Email unidad responsable
</#macro>
<#list languagePriorities as renderLang>
<@.vars["render${renderLang?capitalize}"] />
<#if renderLang?has_next>

-------------------------------------------------------------------------------

</#if>
</#list>