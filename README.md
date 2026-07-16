# 📦 Sistema de Almoxarifado

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.4-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-3.5-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-5.9-3178C6?style=flat-square&logo=typescript&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=flat-square&logo=postgresql&logoColor=white)

Sistema moderno e completo para gestão de almoxarifado, desenvolvido como um monorepo contendo uma API REST robusta (
Spring Boot) e uma interface de usuário dinâmica (Vue.js).

## 🖥️ Interface do Sistema

### Visão geral

![Painel com a visão geral do sistema de almoxarifado](docs/imagens/Imagem%201%20-%20Vis%C3%A3o%20Geral.png)

### Estoque

![Tela de gerenciamento do estoque](docs/imagens/Imagem%202%20-%20Estoque.png)

### Pedidos

![Tela de gerenciamento de pedidos](docs/imagens/Imagem%203%20-%20Pedidos.png)

### Relatórios

![Tela de relatórios](docs/imagens/Imagem%204%20-%20Relat%C3%B3rios.png)

### Perfil

![Tela de perfil do usuário](docs/imagens/Imagem%205%20-%20Perfil.png)

### Configurações

![Tela de configurações do sistema](docs/imagens/Imagem%206%20-%20Configura%C3%A7%C3%B5es.png)

## 🚀 Tecnologias

### ⚙️ Backend

- **Java 25**
- **Spring Boot 4.0.4**
- **Arquitetura em camadas** (`controller`, `service`, `repository`, `model`, `dto` e `mapper`)
- **Spring Security & OAuth2 (Keycloak)** para autenticação e autorização
- **Spring Data JPA** com banco de dados **PostgreSQL**
- Versionamento do banco via changelogs em `db/changelog`
- **SpringDoc OpenAPI (Swagger)** para documentação interativa da API
- **Lombok** para redução de boilerplate
- **Quartz** para tarefas agendadas (schedulers)
- **OpenHTMLtoPDF & FreeMarker** para geração de relatórios em PDF
- **Spring Boot Mail** para envio de e-mails transacionais

### 💻 Frontend

- **Vue 3** (Composition API + `<script setup>`)
- **Vite 8** para build ultra-rápido
- **TypeScript** para tipagem estática
- **Tailwind CSS v4** para estilização utilitária
- **Pinia** para gestão de estado centralizada
- **Keycloak JS** para integração de segurança e login
- **Vue Router 5** para navegação SPA
- **Reka UI & Lucide Vue** para componentes acessíveis e ícones
- **Vue PDF Viewer** para visualização de relatórios em PDF diretamente no navegador
- **Supabase JS** (integração complementar)

---

## 📂 Estrutura do Projeto

O projeto adota uma abordagem *monorepo*, separando claramente o cliente e o servidor:

- `/backend/`: Contém toda a lógica de negócio, API REST, geração de relatórios e configurações do Spring Boot.
- `/frontend/`: Aplicação web construída em Vue.js para interação do usuário.
- `docker-compose.yaml`: Facilita a subida de dependências locais (como PostgreSQL, Keycloak, etc).

O backend utiliza uma estrutura convencional em camadas sob `com.ufc.almoxarifado`:

- `controller`: endpoints REST e tratamento das requisições HTTP.
- `service`: regras de negócio e integrações da aplicação.
- `repository`: acesso aos dados com Spring Data JPA.
- `model`: entidades e enumerações de domínio.
- `dto`: contratos de entrada e saída da API.
- `mapper`: conversão entre entidades e DTOs.
- `config` e `exception`: configurações transversais e tratamento de erros.

---

## ⚙️ Configuração e Execução

### 📋 Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas na sua máquina:

- **JDK 25**
- **Node.js** (v18 ou superior)
- **Docker** e **Docker Compose**
- **Maven** (Opcional, o projeto inclui o wrapper `mvnw`)

### 🔐 Variáveis de ambiente

Crie os arquivos de configuração a partir dos exemplos versionados:

```bash
cp .env.example .env
cp frontend/.env.example frontend/.env
```

O `.env` da raiz configura o Docker Compose e o backend. O arquivo `frontend/.env` contém as variáveis expostas ao
Vite. Revise os valores dos dois arquivos, principalmente as credenciais e os identificadores do Keycloak, antes de
iniciar as aplicações. Os arquivos `.env` reais não devem ser versionados.

### 1️⃣ Inicializando a Infraestrutura (Banco de Dados)

O projeto depende do PostgreSQL. Para iniciar o banco de dados via Docker:

```bash
docker-compose up -d
```

### 2️⃣ Executando o Backend

Para rodar a aplicação:

```bash
cd backend
./mvnw spring-boot:run
```

- **API REST**: `http://localhost:8090/api`
- **Documentação Swagger**: `http://localhost:8090/api/swagger-ui.html`

### 3️⃣ Executando o Frontend

Em um novo terminal, instale as dependências e inicie o servidor de desenvolvimento do Vite:

```bash
cd frontend
npm install
npm run dev
```

- **Acesso à Interface**: O frontend estará disponível em `http://localhost:5173` (ou na porta informada pelo Vite).

---

## 📄 Licença

Este projeto é de uso interno/acadêmico (Desenvolvido para a **UFC**).
