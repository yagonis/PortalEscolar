# Portal Escolar — Documentação da API

## Informações Gerais

- **Base URL:** `http://localhost:8080`
- **Formato:** Todas as requisições e respostas usam `Content-Type: application/json`
- **Autenticação:** JWT via header `Authorization: Bearer {token}`

---

## Como funciona a autenticação

O sistema usa JWT (JSON Web Token). O fluxo é simples:

1. O usuário faz login em `POST /api/auth/login`
2. A API retorna um token
3. Esse token deve ser enviado no header de **todas as rotas protegidas**
4. O token expira em **8 horas**

**Como enviar o token:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## Níveis de acesso

| Role | Descrição |
|---|---|
| `ADMIN` | Acesso total — cria, edita, exclui e gerencia tudo |
| `USER` | Acesso de leitura e pode votar em enquetes |
| Público | Sem token — acessa apenas rotas marcadas como públicas |

---

## Formato de erros

Todos os erros seguem o mesmo formato:

```json
{
    "status": 404,
    "error": "Not Found",
    "message": "Usuário não encontrado.",
    "timestamp": "2026-06-17T10:00:00"
}
```

| Campo | Descrição |
|---|---|
| `status` | Código HTTP |
| `error` | Tipo do erro |
| `message` | Mensagem descritiva |
| `timestamp` | Momento do erro |

**Códigos de erro comuns:**

| Código | Significado |
|---|---|
| 400 | Dados inválidos no body |
| 401 | Token ausente ou inválido |
| 403 | Sem permissão para esta ação |
| 404 | Recurso não encontrado |
| 422 | Regra de negócio violada |
| 500 | Erro interno do servidor |

---

## Paginação

Todos os endpoints de listagem suportam paginação via query params:

| Parâmetro | Tipo | Padrão | Descrição |
|---|---|---|---|
| `page` | Integer | 0 | Número da página (começa em 0) |
| `size` | Integer | 20 | Quantidade de itens por página |
| `sort` | String | — | Campo e direção (ex: `createdAt,desc`) |

**Exemplo:**
```
GET /api/news?page=0&size=10&sort=createdAt,desc
```

**Formato da resposta paginada:**
```json
{
    "content": [...],
    "totalElements": 50,
    "totalPages": 5,
    "number": 0,
    "size": 10,
    "first": true,
    "last": false
}
```

---

---

# AUTH — Autenticação

## POST /api/auth/login
**Acesso:** Público

Autentica o usuário e retorna o token JWT.

**Request body:**
```json
{
    "email": "admin@escola.com",
    "password": "senha123"
}
```

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `email` | String | Sim | E-mail cadastrado |
| `password` | String | Sim | Senha do usuário |

**Response 200:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "expiresAt": "2026-06-17T22:00:00",
    "user": {
        "id": "9ec0a613-97f1-45a9-943e-8870a5335b3d",
        "name": "Maria Admin",
        "email": "admin@escola.com",
        "role": "ADMIN",
        "active": true,
        "createdAt": "2026-06-17T10:00:00"
    }
}
```

**Erros possíveis:**
- `422` — Email ou senha inválidos
- `422` — Usuário desativado

---

## POST /api/auth/logout
**Acesso:** Autenticado (qualquer role)

Invalida o token atual. Após isso o token não poderá mais ser usado.

**Headers obrigatórios:**
```
Authorization: Bearer {token}
```

**Sem body.**

**Response 204:** Sem conteúdo

---

---

# USERS — Usuários

> Todos os endpoints de usuário exigem role **ADMIN**, exceto onde indicado.

## GET /api/users
**Acesso:** ADMIN

Lista todos os usuários com paginação.

**Query params opcionais:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `role` | String | Filtra por role: `ADMIN` ou `USER` |
| `page` | Integer | Página (padrão: 0) |
| `size` | Integer | Itens por página (padrão: 20) |

**Exemplo:**
```
GET /api/users?role=USER&page=0&size=10
```

**Response 200:**
```json
{
    "content": [
        {
            "id": "9ec0a613-97f1-45a9-943e-8870a5335b3d",
            "name": "João Silva",
            "email": "joao@escola.com",
            "role": "USER",
            "active": true,
            "createdAt": "2026-06-17T10:00:00"
        }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "number": 0,
    "size": 20
}
```

---

## GET /api/users/{id}
**Acesso:** ADMIN

Retorna um usuário pelo ID.

**Path param:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `id` | UUID | ID do usuário |

**Response 200:**
```json
{
    "id": "9ec0a613-97f1-45a9-943e-8870a5335b3d",
    "name": "João Silva",
    "email": "joao@escola.com",
    "role": "USER",
    "active": true,
    "createdAt": "2026-06-17T10:00:00"
}
```

**Erros possíveis:**
- `404` — Usuário não encontrado

---

## POST /api/users
**Acesso:** Público (mas só ADMIN pode criar outro ADMIN)

Cria um novo usuário.

**Request body:**
```json
{
    "name": "João Silva",
    "email": "joao@escola.com",
    "password": "senha123",
    "role": "USER"
}
```

| Campo | Tipo | Obrigatório | Regras |
|---|---|---|---|
| `name` | String | Sim | Máximo 100 caracteres |
| `email` | String | Sim | Formato de e-mail válido, único |
| `password` | String | Sim | Mínimo 8 caracteres |
| `role` | String | Sim | `ADMIN` ou `USER` |

**Response 201:**
```json
{
    "id": "9ec0a613-97f1-45a9-943e-8870a5335b3d",
    "name": "João Silva",
    "email": "joao@escola.com",
    "role": "USER",
    "active": true,
    "createdAt": "2026-06-17T10:00:00"
}
```

**Erros possíveis:**
- `400` — Campos inválidos
- `422` — E-mail já cadastrado
- `422` — Apenas administradores podem criar outros administradores

---

## PUT /api/users/{id}
**Acesso:** ADMIN

Atualiza nome e e-mail de um usuário.

**Request body:**
```json
{
    "name": "João Silva Santos",
    "email": "joao.novo@escola.com"
}
```

| Campo | Tipo | Obrigatório | Regras |
|---|---|---|---|
| `name` | String | Sim | Máximo 100 caracteres |
| `email` | String | Sim | Formato de e-mail válido |

**Response 200:** Retorna o usuário atualizado.

**Erros possíveis:**
- `404` — Usuário não encontrado
- `422` — E-mail já em uso por outro usuário

---

## PATCH /api/users/{id}/senha
**Acesso:** ADMIN ou o próprio usuário

Altera a senha do usuário.

**Request body:**
```json
{
    "currentPassword": "senha123",
    "newPassword": "novaSenha456",
    "passwordConfirmation": "novaSenha456"
}
```

| Campo | Tipo | Obrigatório | Regras |
|---|---|---|---|
| `currentPassword` | String | Sim | Senha atual |
| `newPassword` | String | Sim | Mínimo 8 caracteres |
| `passwordConfirmation` | String | Sim | Deve ser igual a `newPassword` |

**Response 204:** Sem conteúdo

**Erros possíveis:**
- `422` — Senha atual incorreta
- `422` — Nova senha e confirmação não conferem

---

## PATCH /api/users/{id}/status
**Acesso:** ADMIN

Ativa ou desativa um usuário (toggle). Usuário desativado não consegue fazer login.

Cada chamada **inverte** o estado atual — se estava ativo, desativa; se estava inativo, ativa.

**Sem body.**

**Response 200:** Retorna o usuário com o novo status.

**Erros possíveis:**
- `404` — Usuário não encontrado

---

---

# WARNINGS — Avisos

## GET /api/warnings
**Acesso:** Público

Lista todos os avisos ativos. Avisos arquivados não aparecem nesta listagem.

**Query params opcionais:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `priority` | String | Filtra por prioridade: `LOW`, `MEDIUM` ou `HIGH` |
| `includeInactive` | Boolean | `true` para incluir arquivados (apenas ADMIN) |
| `page` | Integer | Página |
| `size` | Integer | Itens por página |

**Response 200:**
```json
{
    "content": [
        {
            "id": "abc123",
            "title": "Reunião de pais",
            "content": "Reunião marcada para sexta-feira às 19h.",
            "priority": "HIGH",
            "active": true,
            "pinned": true,
            "createdAt": "2026-06-17T10:00:00"
        }
    ],
    "totalElements": 5,
    "totalPages": 1,
    "number": 0,
    "size": 20
}
```

---

## GET /api/warnings/{id}
**Acesso:** Público

Retorna um aviso pelo ID.

**Response 200:**
```json
{
    "id": "abc123",
    "title": "Reunião de pais",
    "content": "Reunião marcada para sexta-feira às 19h.",
    "priority": "HIGH",
    "active": true,
    "pinned": true,
    "createdAt": "2026-06-17T10:00:00"
}
```

**Erros possíveis:**
- `404` — Aviso não encontrado

---

## POST /api/warnings
**Acesso:** ADMIN

Cria um novo aviso.

**Request body:**
```json
{
    "title": "Reunião de pais",
    "content": "Reunião marcada para sexta-feira às 19h no auditório.",
    "priority": "HIGH",
    "pinned": false
}
```

| Campo | Tipo | Obrigatório | Regras |
|---|---|---|---|
| `title` | String | Sim | Máximo 150 caracteres |
| `content` | String | Sim | Sem limite |
| `priority` | String | Sim | `LOW`, `MEDIUM` ou `HIGH` |
| `pinned` | Boolean | Não | Padrão `false` |

**Response 201:** Retorna o aviso criado.

**Erros possíveis:**
- `400` — Campos inválidos
- `422` — Prioridade inválida

---

## PUT /api/warnings/{id}
**Acesso:** ADMIN

Atualiza um aviso existente. Não é possível editar avisos arquivados.

**Request body:** Mesmo formato do POST.

**Response 200:** Retorna o aviso atualizado.

**Erros possíveis:**
- `404` — Aviso não encontrado
- `422` — Não é possível editar um aviso arquivado

---

## PATCH /api/warnings/{id}/pin
**Acesso:** ADMIN

Alterna o estado de fixado do aviso (toggle). Aviso fixado aparece destacado no topo.

**Sem body.**

**Response 200:** Retorna o aviso com o novo estado de `pinned`.

**Erros possíveis:**
- `404` — Aviso não encontrado
- `422` — Não é possível fixar um aviso arquivado

---

## PATCH /api/warnings/{id}/archive
**Acesso:** ADMIN

Arquiva o aviso. Avisos arquivados somem da listagem pública mas ficam no histórico.

**Sem body.**

**Response 200:** Retorna o aviso arquivado.

**Erros possíveis:**
- `404` — Aviso não encontrado
- `422` — Aviso já está arquivado

---

## DELETE /api/warnings/{id}
**Acesso:** ADMIN

Exclui permanentemente um aviso. **Só funciona se o aviso já estiver arquivado.**

**Sem body.**

**Response 204:** Sem conteúdo.

**Erros possíveis:**
- `404` — Aviso não encontrado
- `422` — Arquive o aviso antes de excluí-lo

---

---

# NEWS — Notícias

## GET /api/news
**Acesso:** Público (retorna apenas `PUBLISHED`). ADMIN pode ver todos os status.

Lista notícias com paginação. Resultados ordenados por data de publicação decrescente.

**Query params opcionais:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `status` | String | `DRAFT`, `PUBLISHED` ou `ARCHIVED` (ADMIN) |
| `isAdmin` | Boolean | `true` para ver todos os status |
| `page` | Integer | Página |
| `size` | Integer | Itens por página |

**Response 200:**
```json
{
    "content": [
        {
            "id": "def456",
            "title": "Festa Junina 2026",
            "subtitle": "Evento especial da escola",
            "body": "A festa junina será realizada no pátio principal...",
            "imageUrl": "https://exemplo.com/imagem.jpg",
            "status": "PUBLISHED",
            "publishedAt": "2026-06-17T10:00:00",
            "createdAt": "2026-06-15T08:00:00",
            "updatedAt": "2026-06-17T09:00:00"
        }
    ],
    "totalElements": 10,
    "totalPages": 1,
    "number": 0,
    "size": 20
}
```

---

## GET /api/news/{id}
**Acesso:** Público (apenas `PUBLISHED`). ADMIN pode ver qualquer status.

**Query params opcionais:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `isAdmin` | Boolean | `true` para ver qualquer status |

**Response 200:** Retorna a notícia completa.

**Erros possíveis:**
- `404` — Notícia não encontrada

---

## POST /api/news
**Acesso:** ADMIN

Cria uma nova notícia. Criada sempre como `DRAFT`.

**Request body:**
```json
{
    "title": "Festa Junina 2026",
    "subtitle": "Evento especial da escola",
    "body": "A festa junina será realizada no pátio principal no dia 20 de junho.",
    "imageUrl": "https://exemplo.com/imagem.jpg"
}
```

| Campo | Tipo | Obrigatório | Regras |
|---|---|---|---|
| `title` | String | Sim | Máximo 200 caracteres |
| `subtitle` | String | Não | Máximo 300 caracteres |
| `body` | String | Sim | Sem limite |
| `imageUrl` | String | Não | URL válida |

**Response 201:** Retorna a notícia criada com `status: "DRAFT"`.

---

## PUT /api/news/{id}
**Acesso:** ADMIN

Atualiza uma notícia. Não é possível editar notícias arquivadas.

**Request body:** Mesmo formato do POST.

**Response 200:** Retorna a notícia atualizada.

**Erros possíveis:**
- `404` — Notícia não encontrada
- `422` — Não é possível editar uma notícia arquivada

---

## PATCH /api/news/{id}/publish
**Acesso:** ADMIN

Publica uma notícia. Só funciona se o status atual for `DRAFT`. Define `publishedAt` automaticamente.

**Sem body.**

**Response 200:** Retorna a notícia com `status: "PUBLISHED"`.

**Erros possíveis:**
- `422` — Apenas rascunhos podem ser publicados

---

## PATCH /api/news/{id}/archive
**Acesso:** ADMIN

Arquiva uma notícia. Notícia arquivada some da listagem pública.

**Sem body.**

**Response 200:** Retorna a notícia com `status: "ARCHIVED"`.

**Erros possíveis:**
- `422` — Notícia já está arquivada

---

## PATCH /api/news/{id}/draft
**Acesso:** ADMIN

Volta uma notícia arquivada para rascunho. Útil para reeditar e republicar.

**Sem body.**

**Response 200:** Retorna a notícia com `status: "DRAFT"`.

**Erros possíveis:**
- `422` — Apenas notícias arquivadas podem voltar para rascunho

---

## DELETE /api/news/{id}
**Acesso:** ADMIN

Exclui uma notícia. **Só funciona se o status for `DRAFT`.**

**Response 204:** Sem conteúdo.

**Erros possíveis:**
- `422` — Apenas rascunhos podem ser excluídos

---

---

# POLLS — Enquetes

## GET /api/polls
**Acesso:** Público

Lista todas as enquetes com paginação.

**Query params opcionais:**

| Parâmetro | Tipo | Descrição |
|---|---|---|
| `status` | String | `OPEN`, `CLOSED` ou `CANCELLED` |
| `page` | Integer | Página |
| `size` | Integer | Itens por página |

**Response 200:**
```json
{
    "content": [
        {
            "id": "ghi789",
            "question": "Qual o melhor horário para reunião?",
            "description": "Escolha o horário de sua preferência.",
            "status": "OPEN",
            "opensAt": "2026-06-17T08:00:00",
            "closesAt": "2026-06-20T23:59:00",
            "allowMultipleVotes": false,
            "createdAt": "2026-06-17T07:00:00",
            "options": [
                { "id": "opt1", "text": "Manhã (9h)", "displayOrder": 1 },
                { "id": "opt2", "text": "Tarde (14h)", "displayOrder": 2 },
                { "id": "opt3", "text": "Noite (19h)", "displayOrder": 3 }
            ]
        }
    ],
    "totalElements": 3,
    "totalPages": 1,
    "number": 0,
    "size": 20
}
```

---

## GET /api/polls/{id}
**Acesso:** Público

Retorna uma enquete pelo ID com suas opções.

**Response 200:** Mesmo formato do item acima.

**Erros possíveis:**
- `404` — Enquete não encontrada

---

## GET /api/polls/{id}/result
**Acesso:** Público

Retorna o resultado da enquete com total de votos e percentual por opção.

**Response 200:**
```json
{
    "pollId": "ghi789",
    "question": "Qual o melhor horário para reunião?",
    "status": "CLOSED",
    "totalVotes": 42,
    "closed": true,
    "options": [
        {
            "id": "opt1",
            "text": "Manhã (9h)",
            "displayOrder": 1,
            "totalVotes": 10,
            "percentage": 23.81
        },
        {
            "id": "opt2",
            "text": "Tarde (14h)",
            "displayOrder": 2,
            "totalVotes": 20,
            "percentage": 47.62
        },
        {
            "id": "opt3",
            "text": "Noite (19h)",
            "displayOrder": 3,
            "totalVotes": 12,
            "percentage": 28.57
        }
    ]
}
```

---

## POST /api/polls
**Acesso:** ADMIN

Cria uma nova enquete. Criada sempre com status `OPEN`.

**Request body:**
```json
{
    "question": "Qual o melhor horário para reunião?",
    "description": "Escolha o horário de sua preferência.",
    "opensAt": "2026-06-17T08:00:00",
    "closesAt": "2026-06-20T23:59:00",
    "allowMultipleVotes": false,
    "options": [
        { "text": "Manhã (9h)", "displayOrder": 1 },
        { "text": "Tarde (14h)", "displayOrder": 2 },
        { "text": "Noite (19h)", "displayOrder": 3 }
    ]
}
```

| Campo | Tipo | Obrigatório | Regras |
|---|---|---|---|
| `question` | String | Sim | Máximo 500 caracteres |
| `description` | String | Não | Texto livre |
| `opensAt` | LocalDateTime | Sim | Formato: `YYYY-MM-DDTHH:mm:ss` |
| `closesAt` | LocalDateTime | Sim | Deve ser após `opensAt` |
| `allowMultipleVotes` | Boolean | Não | Padrão `false` |
| `options` | Array | Sim | Mínimo 2 opções |
| `options[].text` | String | Sim | Máximo 300 caracteres |
| `options[].displayOrder` | Integer | Sim | Ordem de exibição, mínimo 1 |

**Response 201:** Retorna a enquete criada.

**Erros possíveis:**
- `400` — Campos inválidos ou menos de 2 opções
- `422` — Data de encerramento deve ser após a data de abertura

---

## PUT /api/polls/{id}
**Acesso:** ADMIN

Atualiza uma enquete. **Não é permitido editar enquetes que já possuem votos.**

**Request body:** Mesmo formato do POST.

**Response 200:** Retorna a enquete atualizada.

**Erros possíveis:**
- `404` — Enquete não encontrada
- `422` — Não é possível editar uma enquete que já possui votos

---

## PATCH /api/polls/{id}/close
**Acesso:** ADMIN

Encerra a enquete manualmente antes da data prevista.

**Sem body.**

**Response 200:** Retorna a enquete com `status: "CLOSED"`.

**Erros possíveis:**
- `422` — Enquete já está encerrada

---

## PATCH /api/polls/{id}/cancel
**Acesso:** ADMIN

Cancela a enquete. Diferente de encerrar, indica que a enquete foi cancelada por algum motivo.

**Sem body.**

**Response 200:** Retorna a enquete com `status: "CANCELLED"`.

**Erros possíveis:**
- `422` — Enquete já está cancelada

---

## POST /api/polls/{id}/vote
**Acesso:** Autenticado (qualquer role)

Registra o voto do usuário autenticado em uma opção da enquete.

**Headers obrigatórios:**
```
Authorization: Bearer {token}
```

**Request body:**
```json
{
    "optionId": "opt2-uuid-aqui"
}
```

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `optionId` | UUID | Sim | ID da opção escolhida |

**Response 204:** Sem conteúdo.

**Erros possíveis:**
- `401` — Token ausente ou inválido
- `404` — Enquete não encontrada
- `422` — Enquete não está aberta para votação
- `422` — Você já votou nesta enquete
- `422` — Opção não pertence a esta enquete

---

## DELETE /api/polls/{id}
**Acesso:** ADMIN

Exclui uma enquete. **Só funciona se não houver votos registrados.** Para enquetes com votos, use cancel.

**Response 204:** Sem conteúdo.

**Erros possíveis:**
- `422` — Não é possível excluir uma enquete que já possui votos. Cancele-a.

---

---

# Fluxos importantes

## Fluxo de login e uso do token

```
1. POST /api/auth/login → recebe token
2. Salva o token no frontend (localStorage, cookie, etc)
3. Em toda requisição protegida adiciona o header:
   Authorization: Bearer {token}
4. Se receber 401, o token expirou → redireciona para login
5. POST /api/auth/logout → invalida o token no servidor
```

## Fluxo de publicação de notícia

```
1. POST /api/news → cria como DRAFT
2. Edita se necessário com PUT /api/news/{id}
3. PATCH /api/news/{id}/publish → publica
4. Para despublicar: PATCH /api/news/{id}/archive
5. Para reeditar: PATCH /api/news/{id}/draft → volta para DRAFT
```

## Fluxo de votação em enquete

```
1. GET /api/polls → lista enquetes abertas
2. GET /api/polls/{id} → vê as opções disponíveis
3. Usuário precisa estar logado
4. POST /api/polls/{id}/vote com { "optionId": "uuid" }
5. GET /api/polls/{id}/result → vê o resultado
```

## Status das entidades

**Notícias:**
```
DRAFT → (publicar) → PUBLISHED → (arquivar) → ARCHIVED → (rascunho) → DRAFT
```

**Enquetes:**
```
OPEN → (encerrar/data vencer) → CLOSED
OPEN → (cancelar) → CANCELLED
```

**Avisos:**
```
ativo = true → (arquivar) → ativo = false
```

---

# Exemplos de requisições completas

## Login
```http
POST /api/auth/login
Content-Type: application/json

{
    "email": "admin@escola.com",
    "password": "senha123"
}
```

## Criar aviso urgente e fixado
```http
POST /api/warnings
Content-Type: application/json
Authorization: Bearer {token}

{
    "title": "ATENÇÃO: Aula cancelada amanhã",
    "content": "Devido à manutenção elétrica, as aulas do dia 18/06 estão canceladas.",
    "priority": "HIGH",
    "pinned": true
}
```

## Criar e publicar notícia
```http
POST /api/news
Content-Type: application/json
Authorization: Bearer {token}

{
    "title": "Resultados do ENEM 2025",
    "subtitle": "Confira o desempenho dos alunos",
    "body": "Os resultados foram divulgados nesta semana...",
    "imageUrl": null
}

---

PATCH /api/news/{id}/publish
Authorization: Bearer {token}
```

## Criar enquete e votar
```http
POST /api/polls
Content-Type: application/json
Authorization: Bearer {token}

{
    "question": "Qual data preferem para a reunião de pais?",
    "opensAt": "2026-06-17T08:00:00",
    "closesAt": "2026-06-19T23:59:00",
    "allowMultipleVotes": false,
    "options": [
        { "text": "Quinta 19/06 às 18h", "displayOrder": 1 },
        { "text": "Sexta 20/06 às 18h", "displayOrder": 2 },
        { "text": "Sábado 21/06 às 9h", "displayOrder": 3 }
    ]
}

---

POST /api/polls/{id}/vote
Content-Type: application/json
Authorization: Bearer {token}

{
    "optionId": "uuid-da-opcao-escolhida"
}
```