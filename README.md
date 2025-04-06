# Escolhas Técnicas

Para este projeto, optei por utilizar Java com o framework Spring, uma vez que tenho mais familiaridade com ele, o que facilita o desenvolvimento e a manutenção. Adotei o modelo em camadas para garantir uma separação clara das responsabilidades, proporcionando uma estrutura mais organizada e fácil de entender. Além disso, implementei alguns princípios do SOLID, em especial a Inversão de Dependência, com o objetivo de manter as classes desacopladas e independentes. Essa abordagem contribui para a flexibilidade e escalabilidade do sistema, facilitando futuras modificações e a reutilização de componentes.

# Como executar o projeto

1. Acesse o site do **Ngrok** e crie uma conta gratuita. Em seguida, gere um domínio estático (Static Domain) e defina seu valor na variável de ambiente `NGROK_DOMAIN` no arquivo **.env**.

2. Instale o Docker e o Docker Compose, caso ainda não estejam disponíveis na sua máquina.

3. Copie o arquivo `.env.example` para `.env` e preencha as variáveis de ambiente necessárias.  
As informações para configurar as credenciais podem ser obtidas diretamente no site do **HubSpot**.

4. Execute o comando abaixo na raiz do projeto para iniciar os containers:

```bash
  docker compose up -d
```

Pronto! Agora a aplicação estará em execução.  
Para acessá-la, utilize a URL estática fornecida pelo **Ngrok** e acesse os endpoints listados abaixo.

**Observação:** O **Ngrok** atua como um proxy reverso, expondo sua aplicação local de forma segura via **HTTPS**. Isso é necessário para que o **Webhook do HubSpot** consiga enviar notificações sobre os eventos registrados na plataforma.


# 📫 Endpoints da Aplicação

### 🟢 GET `/api/v1/auth/hubspot/authorize-url`


Endpoint responsável por gerar e retornar a URL de autorização para iniciar o fluxo OAuth com o HubSpot.

#### 🔁 Saída

![alt text](/docs/image.png)

A URL gerada por este endpoint irá redirecioná-lo para o servidor de autorização do **HubSpot**, onde o fluxo OAuth será iniciado.

![alt text](/docs/hubspot-authorization-server.png)

Após escolher a conta, o servidor de autenticação exibirá uma solicitação de permissão, perguntando se você autoriza o acesso do aplicativo às informações definidas no escopo.

![alt text](/docs/hubspot-authorization-server-2.png)


### 🟢  GET `/api/v1/auth/hubspot/oauth-callback`

Este endpoint recebe o código de autorização fornecido pelo HubSpot e realiza a troca por um token de acesso válido.

Esse token permitirá que a aplicação realize chamadas autenticadas à API do HubSpot em nome do usuário autorizado.

#### 🔁 Saída

Neste momento, o código de autorização será capturado pela aplicação, que iniciará automaticamente a troca pelo token de acesso.

![alt text](/docs/image-1.png)

#### 🔍 Regras de Validação

Caso o valor do code recebido no endpoint oauth-callback já tenha sido utilizado ou esteja incorreto, uma mensagem de erro será exibida informando que a autenticação falhou.

![alt text](/docs/image-2.png)

### 🟡 POST  `/api/v1/contact/hubspot/create-contact`

Endpoint responsável por criar um novo contato no CRM do HubSpot utilizando a API. A requisição deve respeitar as políticas de rate limit definidas pela plataforma do HubSpot.

Para acessar este endpoint, é necessário enviar um token de acesso válido. Existe um filtro de autenticação que valida a veracidade do token por meio de uma requisição externa à API do HubSpot.

#### 🔑 Entrada

```json
{
  "email": "johndoe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1 (555) 123-4567",
  "company": "Example Inc.",
  "website": "https://www.example.com"
}
```

O campo **email** é o único obrigatório. Os demais campos são opcionais, porém, caso preenchidos, estarão sujeitos a validações apropriadas.

![alt text](/docs/email-required.png)

![alt text](/docs/image -7.png)


#### 🔁 Saída

O cadastro bem-sucedido de um contato resultará no registro do mesmo na API do HubSpot.

![alt text](/docs/contact-created.png)

#### 🔍 Regras de Validação

Caso o token de acesso esteja expirado ou seja inválido, mensagens apropriadas serão retornadas, indicando o motivo da falha na autenticação

![alt text](/docs/access-token-invalid.png)

# API Webhooks

### 🟡 POST  `/api/v1/webhook/hubspot/contacts/created`

Endpoint responsável por escutar e processar eventos do tipo "contact.creation" enviados pelo webhook do HubSpot.

#### 🔑 Entrada

A entrada só será permitida se a requisição estiver corretamente assinada com o cabeçalho `X-Hubspot-Signature`, gerado pelo **HubSpot**, e a assinatura for validada com o hash SHA-256 da concatenação de clientSecret + requestBody. Caso contrário, o envio dos eventos será rejeitado.iseconds` é uma abordagem eficiente. Isso permitirá que você faça requisições para a API do HubSpot sem ultrapassar os limites de taxa impostos. A classe que você mencionou pode ser usada para armazenar temporariamente essas informações e ajudar a controlar o ritmo das requisições.

O cabeçalho `X-Hubspot-Signature` será exibido contendo a assinatura gerada pelo HubSpot para validar a autenticidade da requisição.

![alt text](/docs/image-4.png)

O payload do evento enviado pelo HubSpot conterá os dados relacionados ao evento `contact.creation`, incluindo informações do contato e outros detalhes relevantes para processamento.

![alt text](/docs/image-3.png)

#### 🔁 Saída

O endpoint não retorna uma resposta, mas salva o evento de criação de contato em uma entidade no banco em memória H2, chamada `contact_webhook_event`.

### 🟢 GET `/api/v1/contact/hubspot/events/contact-created`

Este endpoint permite recuperar todos os eventos de criação de contatos registrados. Para acessar este endpoint, é necessário fornecer o token de acesso no cabeçalho `Authorization` da requisição, utilizando o prefixo `Bearer`. Caso contrário, a requisição será rejeitada.

#### 🔑 Entrada

![alt text](/docs/image-5.png)

#### 🔁 Saída

![alt text](/docs/image-6.png)

# Tratamento de Rate Limit

Para garantir que as requisições à API do HubSpot não excedam os limites de taxa definidos pela plataforma, implementamos um sistema de cache baseado nos cabeçalhos de rate limit enviados pelo HubSpot. Esses cabeçalhos são:

* `X-HubSpot-RateLimit-Max:` Número máximo de requisições permitidas.

* `X-HubSpot-RateLimit-Remaining:` Número de requisições restantes no intervalo atual.

* `X-HubSpot-RateLimit-Interval-Milliseconds:` O intervalo, em milissegundos, entre as requisições permitidas.


![alt text](/docs/image-8.png)

A função em questão na classe `HubSpotHttpClient` implementa a lógica necessária para garantir que as requisições à API do HubSpot sejam feitas dentro dos limites de rate limit definidos pela plataforma. Ela utiliza um agendamento assíncrono e a criação de virtual threads para otimizar o uso de recursos e garantir que as requisições que não podem ser feitas dentro da janela atual de tempo sejam executadas assim que possível.

![alt text](/docs/image-9.png)


# Ideias de Melhoria

Implementar um circuit breaker para lidar com falhas em requisições à API externa de maneira mais ágil e eficiente. Além disso, adotar uma abordagem baseada em jobs assíncronos com fila para evitar os problemas que surgem atualmente com o agendamento das tarefas. A abordagem atual sofre com o risco de exceções de timeout, caso uma tarefa leve mais de 10 segundos para ser executada.

# 🚀 Tecnologias Utilizadas

* Java
* Spring Boot
* Swagger
* Docker
* H2
* Lombok
* HubSpot API