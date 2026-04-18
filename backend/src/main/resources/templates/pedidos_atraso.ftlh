<#ftl encoding="UTF-8">
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
            margin: 0;
            padding: 40px 0;
            color: #0f172a;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background-color: #ffffff;
            border: 1px solid rgba(255, 255, 255, 0.52);
            border-radius: 16px;
            box-shadow: 0 20px 60px rgba(15, 23, 42, 0.12);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #0f172a, #1e293b);
            padding: 40px 20px;
            text-align: center;
            color: #ffffff;
            border-bottom: 3px solid #ef4444;
        }
        .header h1 {
            margin: 0;
            font-size: 24px;
            font-weight: 800;
            line-height: 1.3;
            letter-spacing: 1px;
        }
        .header h1 span {
            color: #ef4444;
        }
        .content {
            padding: 40px 30px;
            line-height: 1.6;
        }
        .content h2 {
            color: #0f172a;
            font-size: 1.25rem;
            font-weight: 800;
            margin-top: 0;
            margin-bottom: 20px;
        }
        .table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 25px;
            font-size: 14px;
        }
        .table th, .table td {
            border: 1px solid #e5e7eb;
            padding: 10px;
            text-align: left;
        }
        .table th {
            background-color: #f9fafb;
            font-weight: 600;
        }
        .button-container {
            text-align: center;
            margin: 35px 0;
        }
        .button {
            background-color: #2563eb;
            color: #ffffff !important;
            padding: 14px 32px;
            text-decoration: none;
            border-radius: 8px;
            font-weight: bold;
            display: inline-block;
            transition: background-color 0.3s;
            box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
        }
        .button:hover {
            background-color: #1d4ed8;
        }
        .footer {
            background-color: #f8fafc;
            padding: 20px;
            text-align: center;
            font-size: 12px;
            color: #64748b;
            border-top: 1px solid #e2e8f0;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Aviso de <span>Pedidos em Atraso</span></h1>
        </div>

        <div class="content">
            <h2>Olá, ${nome}!</h2>
            <p>Os seguintes pedidos encontram-se em atraso para devolução:</p>

            <table class="table">
                <thead>
                    <tr>
                        <th>ID Pedido</th>
                        <th>Data de Devolução Prevista</th>
                        <th>Dias em Atraso</th>
                    </tr>
                </thead>
                <tbody>
                    <#list pedidos as pedido>
                        <tr>
                            <td>#${pedido.id}</td>
                            <td>${pedido.dataDevolucaoPrevista}</td>
                            <td>${pedido.diasAtraso}</td>
                        </tr>
                    </#list>
                </tbody>
            </table>

            <p>Por favor, acesse o sistema para mais detalhes e realize a devolução o quanto antes para evitar bloqueios.</p>

            <div class="button-container">
                <a href="${linkSistema}" class="button">Acessar Sistema</a>
            </div>
        </div>

        <div class="footer">
            <p>&copy; ${.now?string("yyyy")} Sistema de Almoxarifado Institucional. Todos os direitos reservados.</p>
            <p>Este é um e-mail automático, por favor, não responda.</p>
        </div>
    </div>
</body>
</html>
