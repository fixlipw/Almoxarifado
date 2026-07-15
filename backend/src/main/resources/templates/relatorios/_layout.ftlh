<#ftl encoding="UTF-8">
<#macro reportStyles>
    <style>
        @page {
            size: A4;
            margin: 33mm 14mm 22mm 14mm;

            @top-center {
                content: element(report-header);
            }

            @bottom-left {
                content: element(report-footer-left);
            }

            @bottom-right {
                content: element(report-footer-right);
            }
        }

        body {
            color: #111827;
            font-family: Arial, sans-serif;
            font-size: 11px;
            margin: 0;
        }

        .page-header {
            border-bottom: 1px solid #7c7c7c;
            padding: 0 0 10px;
            position: running(report-header);
        }

        .header-table {
            border-collapse: collapse;
            width: 100%;
        }

        .header-table td {
            vertical-align: middle;
        }

        .brand-cell {
            padding-right: 10px;
            width: 40%;
        }

        .meta-cell {
            width: 60%;
            text-align: right;
        }

        .brand-wrap {
            display: block;
            line-height: 0;
        }

        .brand-image {
            height: 58px;
            width: auto;
            display: inline-block;
            vertical-align: middle;
        }

        .brand-text {
            display: inline-block;
            vertical-align: middle;
            margin-left: 10px;
            color: #1f2937;
            line-height: 1.2;
        }

        .brand-text .main {
            font-family: "Times New Roman", serif;
            font-size: 18px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .brand-text .sub {
            font-family: "Times New Roman", serif;
            font-size: 11px;
            text-transform: uppercase;
        }

        .meta-line {
            font-size: 10px;
            font-weight: 700;
            line-height: 1.3;
        }

        .page-footer-left {
            color: #374151;
            font-size: 9px;
            position: running(report-footer-left);
        }

        .page-footer-right {
            color: #374151;
            font-size: 9px;
            position: running(report-footer-right);
        }

        .page-footer-right .page-number:before {
            content: counter(page);
        }

        .page-footer-right .page-count:before {
            content: counter(pages);
        }

        .report-title {
            font-size: 22px;
            font-weight: 700;
            margin: 18px 0 22px;
            text-align: center;
            text-transform: uppercase;
        }

        .report-meta {
            font-size: 12px;
            margin: 0 0 14px 14px;
        }

        .summary {
            margin: 0 0 14px 4px;
        }

        .summary-label {
            color: #4b5563;
            font-size: 10px;
            font-weight: 700;
            letter-spacing: 0.04em;
            text-transform: uppercase;
        }

        .summary-value {
            font-size: 15px;
            font-weight: 700;
            margin-top: 2px;
        }

        table.items {
            border-collapse: collapse;
            width: 100%;
        }

        table.items th,
        table.items td {
            border: 1px solid #3f3f46;
            padding: 6px 8px;
        }

        table.items th {
            background: #d4d4d8;
            font-size: 11px;
            font-weight: 700;
            text-align: center;
        }

        table.items td {
            font-size: 11px;
        }

        table.items td.center {
            text-align: center;
        }

        table.items tbody tr:nth-child(even) {
            background: #f3f4f6;
        }

        .empty {
            border: 1px dashed #9ca3af;
            color: #4b5563;
            margin-top: 16px;
            padding: 18px;
            text-align: center;
        }
    </style>
</#macro>

<#macro reportHeader brasaoUfc>
    <div class="page-header">
        <table class="header-table">
            <tr>
                <td class="brand-cell">
                    <div class="brand-wrap">
                        <img class="brand-image" src="${brasaoUfc}" alt="Brasao UFC"/>
                        <div class="brand-text">
                            <div class="main">Universidade<br/>Federal do Ceará</div>
                            <div class="sub">Campus Quixadá</div>
                        </div>
                    </div>
                </td>
                <td class="meta-cell">
                    <div class="meta-line">Universidade Federal do Ceará</div>
                    <div class="meta-line">Campus Quixadá - Av. Jose de Freitas Queiroz, 5003, Cedro</div>
                    <div class="meta-line">Quixadá - Ceara, CEP: 63902-580</div>
                    <div class="meta-line">Coordenação do Curso de Engenharia de Computação</div>
                </td>
            </tr>
        </table>
    </div>
</#macro>

<#macro reportFooter>
    <div class="page-footer-left">
        © ${.now?string("yyyy")} Universidade Federal do Ceará - Campus Quixadá
    </div>

    <div class="page-footer-right">
        <span class="page-number"></span>/<span class="page-count"></span>
    </div>
</#macro>