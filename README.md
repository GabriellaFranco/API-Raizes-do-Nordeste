API desenvolvida como Projeto Final do curso de ADS na UNINTER (2026), trilha Back-end. O sistema gerencia uma rede de lanchonetes com foco em 
controle de pedidos, estoque, cardápio por unidade e programa de fidelidade com conformidade à LGPD.

## Arquitetura

O projeto utiliza arquitetura em camadas, onde a **Repository Layer** é responsável pelas operações de banco de dados, a **Service Layer** 
concentra a lógica de negócio e a **Controller Layer** atua como interface REST. 

## Funcionalidades

### Autenticação com token JWT e controle de acesso por perfil

A autenticação é baseada em JWT com Spring Security. O token é gerado no momento do login e deve ser enviado no header em todas as 
requisições protegidas. O sistema possui 4 perfis iniciais de acesso com permissões distintas:

**CLIENTE**: Realizar pedidos, acompanhar status, gerenciar consentimento LGPD;

**ATENDENTE**: Registrar pedidos no balcão, atualizar status;

**GERENTE**: Cardápio, estoque, funcionários, relatórios da unidade;

**MATRIZ**: Dados consolidados, auditoria, programa de fidelidade;       

<img width="1588" height="274" alt="image" src="https://github.com/user-attachments/assets/26172299-eb50-44aa-91ed-c8a6940406c9" />

### Gestão de Unidades

Cada restaurante da rede é cadastrado como uma **Unidade**, com informações de CNPJ, endereço, contato, região e estado. O status da unidade 
pode ser ativado ou desativado, e os funcionários são vinculados diretamente a ela. Unidades são a base para cardápios, estoque e pedidos.

### Gerenciamento de Funcionários

Gerentes podem cadastrar, atualizar e ativar/desativar funcionários da sua unidade. Cada usuário pode ter um ou mais perfis de autoridade 
vinculados, permitindo acesso granular às funcionalidades do sistema. O vínculo de perfis é gerenciado de forma independente do cadastro 
do usuário.

### Cardapio

Cada unidade da rede possui seu próprio cardápio, com itens que podem ser ativados ou desativados individualmente. Um mesmo item pode estar 
em múltiplos cardápios com disponibilidade independente.

### Pedidos e Pagamento

O fluxo de um pedido passa pelas seguintes etapas:

CONFIRMADO → EM_PREPARO → PRONTO → ENTREGUE → FINALIZADO
| CANCELADO

Pedidos podem ser criados atrvés de 4 canais: `APP`, `TOTEM`, `BALCAO` e `PICKUP`. O pagamento é processado antes da confirmação (Se o 
pagamento não é aprovado, o pedido não é criado) e pedidos com valor acima de R$ 10.000 são automaticamente recusados pelo serviço de 
pagamento mockado. 

<img width="558" height="441" alt="image" src="https://github.com/user-attachments/assets/8f68efff-7de9-45a0-88b2-61d36a9bb4c6" />

### Gestão de Estoque

O estoque é **decrementado automaticamente** ao confirmar o pedido e **estornado** em caso de cancelamento. O gerente pode registrar 
movimentações de entrada no estoque da unidade pela qual é responsável.

### Programa de Fidelidade

A cada pedido com status FINALIZADO, o cliente cadastrado acumula automaticamente 10 pontos no programa de fidelidade. Os pontos podem ser resgatados 
como desconto na hora de criar um novo pedido, seguindo as regras:

- Mínimo de 100 pontos para resgate;
- **1 ponto = R$ 1,00** de desconto;
- Resgate ativado via campo usarPontos: true no request do pedido;
- Em caso de cancelamento, os pontos utilizados são estornado;

O programa é gerenciado 100% de forma automática.A Matriz pode consultar o ranking de clientes por pontos acumulados via `GET /fidelidade/ranking`.

<img width="357" height="152" alt="image" src="https://github.com/user-attachments/assets/5616ea8d-8ba9-42bd-a098-d810de38c8ba" />

### Auditoria automática em todas as entidades

Foi criada uma classe abstrata **AuditableEntity** que todas as entidades do sistema estendem (exceto AuditoriaLogin). Isso garante 
que todos os registros possuam `createdAt`, `updatedAt`, `createdBy` e `updatedBy` preenchidos automaticamente pelo Spring Data JPA, 
sem necessidade de código manual em cada service.

<img width="741" height="630" alt="image" src="https://github.com/user-attachments/assets/5394b99c-1801-4565-813f-de2df0285f13" />

### Rastreabilidade de alterações com Hibernate Envers

Além da auditoria automática de criação e atualização, o sistema mantém um histórico completo de alterações de todas as entidades 
via Hibernate Envers. Cada alteração gera um registro nas tabelas _aud correspondentes, com o tipo de operação (`ADD`, `MOD`, `DEL`) 
e a revisão na tabela `revinfo`.Isso permite rastrear qualquer mudança realizada em uma entidade para fins de auditoria de segurança.

<img width="1320" height="282" alt="image" src="https://github.com/user-attachments/assets/3bde3ccf-bc93-4c83-969e-679c53eab89f" />

<br>

revtype = 0 -> Criação do cliente

revtype = 1 -> Atualização de seu telefone

### Documentação com Swagger/OpenAPI

Todos os endpoints estão documentados e disponíveis para teste via **Swagger**, acessível em `/swagger-ui/index.html`. A documentação 
inclui schemas com exemplos de request/response, códigos de retorno e autenticação via Bearer token JWT, o que permite testar qualquer 
endpoint diretamente pelo browser sem ferramentas externas.

<img width="1660" height="584" alt="image" src="https://github.com/user-attachments/assets/e7180c09-c4be-4ebe-9ed1-046a15750fda" />

### Exportação de Relatórios em PDF

Gerentes e usuários da Matriz podem exportar relatórios variados em PDF gerados com a biblioteca **iText7**, contendo dados de 
vendas por período e movimentação por unidade por exemplo.  

<img width="1059" height="390" alt="image" src="https://github.com/user-attachments/assets/094a9c0d-0c51-49aa-9d54-21c3d74db869" />

## Testes

O projeto possui 40 testes unitários cobrindo as principais regras de negócio da Service Layer, utilizando JUnit 5, Mockito e AssertJ. 

| Classe de Teste           | Cenários cobertos                                        |
|---------------------------|----------------------------------------------------------|
| `AuthServiceTest`         | Registro, login, alteração de senha                      |
| `UsuarioServiceTest`      | CRUD de usuários, vínculos de perfil, status             |
| `ClienteServiceTest`      | Busca, atualização, revogação de consentimento LGPD      |
| `PedidoServiceTest`       | Criação, atualização de status, cancelamento             |
| `EstoqueServiceTest`      | Entrada, decremento, estorno, estoque crítico            |
| `FidelidadeServiceTest`   | Pontos por cliente, listagem, ranking                    |
| `CpfValidatorTest`        | Validação de dígitos verificadores                       |
| `PagamentoMockServiceTest`| Aprovação e recusa de pagamento                          |


## Tecnologias Utilizadas
- Java 21
- Spring Boot 3.5.14
- Spring Security 6 + JWT (jjwt 0.12.6)
- Spring Data JPA + Hibernate 6
- Hibernate Envers
- PostgreSQL 16
- Flyway 11.7.2
- Lombok
- SpringDoc OpenAPI / Swagger UI 2.8.8
- iText7 7.2.5
- JUnit 5.12.2
- Mockito 5.17.0
- AssertJ 3.27.7
- Docker

## Como Executar

### Pré-requisitos

- Docker Desktop instalado e em execução
- Java 21
- Maven 3.9+

#### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/raizesnordeste.git
```

#### 2. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
DB_NAME=raizesnordeste
DB_USER=postgres
DB_PASSWORD=postgres
DB_PORT=5432
DB_HOST=postgres
SERVER_PORT=8080
JWT_SECRET=sua-chave-secreta-muito-longa-aqui
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

#### 3. Suba os containers

```bash
docker-compose up -d --build
```

#### 4. Acesse

| Serviço    | URL                                                      |
|------------|----------------------------------------------------------|
| Swagger UI | http://localhost:8080/api/v1/swagger-ui/index.html       |
| API Base   | http://localhost:8080/api/v1                             |
| PostgreSQL | localhost:5432                                           |

<img width="1919" height="987" alt="image" src="https://github.com/user-attachments/assets/7fe5d7c9-66a2-4615-8683-5f0b7ace82ca" />

## Executando os Testes via Postman

### Pré-requisitos
- [Postman](https://www.postman.com/downloads/) instalado
- Docker Desktop em execução

### Passo a Passo

**1. Subir a aplicação**
```bash
docker compose up -d
```

**2. Importar a coleção no Postman**
- Abra o Postman
- Clique em **Import**
- Selecione o arquivo `Raízes do Nordeste - API REST.postman_collection.json` (localizado neste repositório)
- A coleção aparecerá na barra lateral esquerda

**3. Configurar o ambiente**
- No canto superior direito, clique em **New Environment**
- Adicione a variável:
  - `baseUrl` → `http://localhost:8080/api/v1`
- Selecione o ambiente criado no seletor do canto superior direito

**4. Executar os testes**
- Clique nos **três pontinhos** ao lado da coleção
- Selecione **Run collection**
- Clique em **Run Raízes do Nordeste - API REST**

**Ordem de execução obrigatória:**
> Os testes devem ser executados na ordem das pastas (T01 → T20),
> pois os tokens e IDs são salvos automaticamente entre os testes.
> O T01 (login) deve sempre ser o primeiro.

### Credenciais de Teste
| Usuário | E-mail | Senha | Perfil |
|---|---|---|---|
| Carlos Eduardo Silva | carlos.silva@raizesnordeste.com | Senha@123 | GERENTE |
| Maria Teste Postman | maria.postman@email.com | Senha@123 | CLIENTE (criada pelo T04) |

<img width="1918" height="1015" alt="image" src="https://github.com/user-attachments/assets/d4088814-af7f-45f5-8d05-65dca6eb4134" />

