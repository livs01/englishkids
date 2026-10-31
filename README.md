## Como rodar no Visual Studio Code (sem Docker)

### 1. Pré-requisitos
- **JDK 17 ou superior**.
- **Visual Studio Code** instalado.
- Extensões **Extension Pack for Java** e **Spring Boot Extension Pack** recomendadas.
- **Maven** instalado ou suporte Maven disponível pelas extensões Java.
- **PostgreSQL** instalado e o serviço em execução.
- Internet na primeira execução para baixar as dependências.

### 2. Criar o banco no PostgreSQL
1. Abra o pgAdmin e conecte ao servidor PostgreSQL.
2. Clique com o botão direito em **Databases > Create > Database...**.
3. Informe `englishkids` como nome do banco e clique em **Save**.

Alternativamente, execute o script `database/criar-banco.sql`, se disponível.

As tabelas são criadas pela aplicação ao iniciar.

### 3. Conferir a configuração do banco
Abra `src/main/resources/application.properties` e confira a URL, o usuário e a senha do PostgreSQL.

A configuração padrão utiliza o banco `englishkids` em `localhost:5432`, com o usuário `postgres`. Se sua senha for diferente, ajuste a configuração da propriedade `spring.datasource.password` ou utilize a variável de ambiente `DB_PASSWORD`, conforme configurado no projeto.

### 4. Abrir o projeto no VS Code
1. Abra o Visual Studio Code.
2. Selecione **File > Open Folder**.
3. Escolha a pasta raiz `englishkids`, que contém o arquivo `pom.xml`.
4. Aguarde o VS Code reconhecer o projeto Maven e baixar as dependências.

### 5. Conferir a versão do Java
No terminal do VS Code, execute:

```bash
java -version
```

A versão deve ser 17 ou superior. Se necessário, configure o JDK utilizado pelas extensões Java do VS Code.

### 6. Executar a aplicação
No terminal integrado do VS Code, dentro da pasta que contém o `pom.xml`, execute:

```bash
mvn spring-boot:run
```

Aguarde a mensagem indicando que a aplicação iniciou e acesse:

`http://localhost:8080`

Se o Maven não estiver instalado ou configurado, instale-o ou configure o ambiente Java/Maven do VS Code antes de executar o comando.

| Perfil | Usuário | Senha |
|--------|---------|-------|
| ADMIN | admin | admin123 |
| USER | user | user123 |

Na primeira execução, a aplicação pode criar os usuários e o conteúdo de exemplo, conforme a configuração de inicialização do projeto.

### 7. Rodar os testes
No terminal, execute:

```bash
mvn test
```

Os testes utilizam H2 em arquivo, conforme a configuração do projeto, e não devem depender do PostgreSQL nem alterar os dados do banco principal.

### Problemas comuns

| Mensagem / sintoma | Solução |
|---|---|
| `password authentication failed for user "postgres"` | Confira a senha configurada para o PostgreSQL. |
| `database "englishkids" does not exist` | Crie o banco `englishkids`. |
| `Connection refused` | Confira se o serviço PostgreSQL está em execução e se a porta está correta. |
| `Port 8080 was already in use` | Encerre a aplicação que ocupa a porta ou configure outra porta. |
| Dependências não resolvidas | Verifique a conexão com a internet e atualize o projeto Maven. |
| `release version 17 not supported` | Confira se o Maven está utilizando um JDK 17 ou superior. |

