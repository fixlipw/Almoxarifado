<#ftl encoding="UTF-8">
<#import "_layout.ftlh" as layout>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8"/>
    <title>${titulo}</title>
    <@layout.reportStyles />
    <style>
        .page-break {
            page-break-after: always;
        }

        tr {
            page-break-inside: avoid;
        }
    </style>
</head>
<body>
<@layout.reportHeader brasaoUfc=brasaoUfc />
<@layout.reportFooter />

<div class="report-title">${titulo}</div>

<div class="report-meta">
    Relatorio gerado em: ${geradoEm}
</div>

<#if itens?has_content>
    <#list itens?chunk(30) as grupoItens>
        <table class="items">
            <thead>
            <tr>
                <th>Item</th>
                <th>Quantidade Total</th>
                <th>Quantidade Disponível</th>
                <th>Tipo</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody>
            <#list grupoItens as item>
                <tr>
                    <td>${item.nome}</td>
                    <td class="center">${item.quantidade}</td>
                    <td class="center">${item.quantidadeDisponivel}</td>
                    <td class="center">${item.tipo}</td>
                    <td class="center">${item.status}</td>
                </tr>
            </#list>
            </tbody>
        </table>

        <#if grupoItens?has_next>
            <div class="page-break"></div>
        </#if>
    </#list>
<#else>
    <div class="empty">
        Nenhum item encontrado no estoque.
    </div>
</#if>
</body>
</html>