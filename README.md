# Como incializar o projeto em localhost:
- Execute `docker compose up --build` na raiz do repositório (ou `docker-compose up --build`, se estiver usando a versão antiga do Docker Compose).
- Isso fará com que dois contêineres docker sejam inicializados: `marketplace` e `receiver`.

# Uma breve descrição do que foi desenvolvido
- Este projeto contém dois módulos Java SpringBoot correspondentes às duas APIs requisitadas pelo documento que descreve o desafio técnico: `marketplace` e `receiver`.

### Marketplace
- `marketplace` simula um marketplace e dois de seus domínios: `Store` e `Order`.
- Cada loja `Store` está vinculada a uma URL de webhook de eventos para qual serão enviadas atualizações sobre seus pedidos.
- `Order` representa um pedido e está ligado a exatamente uma `Store`, descrevendo um relacionamento `Order` 1:N `Store` (OneToMany).
- `marketplace` contém uma estrutura de integração com `receiver` para o envio dos eventos.

### Receiver
- `receiver` simula uma API de recebimento e registro de eventos. Ela contém somente o domínio `Event`.
- `Event` é um evento webhook que descreve uma atualização sobre uma entidade de um domínio do `marketplace`. Para fins de simplicidade desta implementação, o único domínio suportado é `Order`.
- `receiver` contém uma estrutura de integração com `marketplace` para o enriquecimento dos eventos via obtenção do snapshot da entidade a ser registrada.

# Validando o fluxo geral
 
### Observações

1) Para as etapas a seguir, é recomendado o uso da coleção Postman "Test" presente na raiz deste repositório. Ela contém as chamadas para todos os endpoints do projeto e pode facilitar a validação do funcionamento do fluxo. Caso não seja possível importar essa coleção, exemplos das requisições nela presentes estão disponíveis no final deste arquivo.
2) Além disso, já com ambos os contêineres sendo executados na sua máquina, abra no navegador ambas as URLs para acessar o gerenciador de banco de dados do H2: `http://localhost:8080/h2-console` e `http://localhost:8081/h2-console`. Em ambas, defina o campo `JDBC URL` como `jdbc:h2:mem:testdb` e clique em `Connect` para acessar o console do H2 do `marketplace` e do `receiver`, respectivamente. Para consultar o conteúdo de uma tabela, basta clicar no nome dela na aba da esquerda e, em seguida, clicar em `Run` já com a query de `SELECT * FROM ...` já exibida na tela.

### Fluxo

O fluxo geral que mostra o funcionamento das duas APIs como descrito no documento do desafio (dadas as especificidades de implementação descritas no tópico anterior) é o seguinte:
- No `marketplace`:
  - Crie uma loja ao consumir `POST /stores`. Lembre-se de informar "http://receiver:8081/events" como o callbackUrl. Isso vai definir que as atualizações sobre as entidades relacionadas a essa loja deverão ser notificadas como eventos para o nosso `receiver`.
  - Confirme que a loja foi criada ao buscá-la com `GET /stores/{id}`. Você também pode consultar a tabela `STORES` pelo console do H2 do `marketplace` no navegador.
  - Em seguida, crie um pedido nessa loja ao consumir `POST /orders`. O id da loja em que o pedido está sendo feito precisa ser informado no body da requisição.
  - Confirme a criação do pedido no `marketplace` ao consumir `GET /orders/{id}`. Você também pode consultar a tabela `ORDERS` pelo console do H2 do `marketplace` no navegador.
  - Com o pedido criado, é esperado que um evento `order.created` tenha sido emitido do `marketplace` para o `receiver`. Confirme a recepção do evento ao consultar a tabela `EVENTS` pelo console do H2 do `receiver` no navegador. Confirme também que o snapshot do pedido foi corretamente salvo no evento ao executar a etapa de enriquecimento durante a criação e persistência do evento no `receiver`.
  - Em seguida, consuma `POST /orders/{id}/status` para atualizar o status do pedido alvo para `PAID`, por exemplo. Essa atualização também será registrada como um evento no `receiver` e sua existência pode ser confirmada ao consultar o console do H2 do `receiver` como na etapa anterior.
  - Isso conclui o fluxo geral :)

# Exemplos de Requisição

### Marketplace: Store

#### Create

`curl --location 'localhost:8080/stores' \
--header 'Content-Type: application/json' \
--data '{
    "store": {
        "name": "Loja Teste",
        "callbackUrl": "http://receiver:8081/events"
    }
}'`

#### Get

`curl --location --request GET 'localhost:8080/stores/ca7a1d53-305f-405b-aa1d-03bb8fd6dc4a' \
--header 'Content-Type: application/json' \
--data '{
"mock": "Teste"
}'`

#### Update

`curl --location --request PATCH 'localhost:8080/stores/ca7a1d53-305f-405b-aa1d-03bb8fd6dc4a' \
--header 'Content-Type: application/json' \
--data '{
"store": {
"name": "Loja Teste 2",
"callbackUrl": "http://receiver:8081/events2"
}
}'`

### Marketplace: Order

#### Create

`curl --location 'localhost:8080/orders' \
--header 'Content-Type: application/json' \
--data '{
    "storeId": "e3e4c05d-dbeb-4e5d-8d9a-62ae6f2d1f5c",
    "order": {
        "mock": "campo exemplo teste"
    }
}'`

#### Get

`curl --location --request GET 'localhost:8080/orders/615c65f5-c445-4560-99eb-d119384d910d' \
--header 'Content-Type: application/json' \
--data '{
    "mock": "Teste"
}'`

#### Update Status

`curl --location --request PATCH 'localhost:8080/orders/5422835e-4b7c-4787-b4b4-908ecd8f9a5e/status' \
--header 'Content-Type: application/json' \
--data '{
    "status": "PAID"
}'`


