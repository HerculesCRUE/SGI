<#assign data = ETI_COM_REVISION_ACTA_DATA?eval_json />
Revisión de acta de reunión de evaluación ${data.fechaEvaluacion?datetime.iso?string("dd/MM/yyyy")}