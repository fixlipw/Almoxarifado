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
            border-bottom: 3px solid #f59e0b;
        }

        .header h1 {
            margin: 0;
            font-size: 24px;
            font-weight: 800;
            line-height: 1.3;
            letter-spacing: 1px;
        }

        .header h1 span {
            color: #f59e0b;
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

        .fallback-link {
            background-color: #f8fafc;
            padding: 20px;
            border-radius: 8px;
            font-size: 13px;
            word-break: break-all;
            color: #475569;
            margin-top: 30px;
            border: 1px solid #e2e8f0;
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
        <h1>Sistema de Almoxarifado<br><span>Engenharia de Computação</span></h1>
    </div>

    <div class="content">
        <h2>Olá, ${nome}!</h2>
        <p>Seu cadastro no Sistema de Almoxarifado foi iniciado com sucesso.</p>
        <p>Para concluir a criação da sua conta e liberar o seu acesso, por favor, clique no botão abaixo:</p>

        <div class="button-container">
            <a href="${linkAtivacao}" class="button">Ativar Minha Conta</a>
        </div>

        <p>Se você não solicitou este cadastro, por favor, ignore este e-mail.</p>

        <div class="fallback-link">
            <strong>O botão não funcionou?</strong> Copie e cole o link abaixo no seu navegador:<br><br>
            <a href="${linkAtivacao}" style="color: #2563eb;">${linkAtivacao}</a>
        </div>
    </div>

    <div class="footer">
        <p>© ${.now?string("yyyy")} Sistema de Almoxarifado Institucional. Todos os direitos reservados.</p>
        <p>Este é um e-mail automático, por favor, não responda.</p>
    </div>
</div>
</body>
</html>