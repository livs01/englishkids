# English Kids — Spring Boot + Thymeleaf

Trabalho Prático de Programação WEB. Plataforma de vocabulário de **inglês para crianças**: temas (Animals, Colors, Fruits...),
palavras com tradução, pronúncia aproximada, frase de exemplo e nível, e uma área de estudo com cartões e áudio.

## Stack
Java 17+ · Spring Boot 3.3 · Spring MVC + Thymeleaf · Spring Data JPA · Spring Security · **PostgreSQL** · JUnit 5 + Mockito

---

## Como rodar no Eclipse (sem Docker)

### 1. Pré-requisitos
- **JDK 17 ou superior** (17, 21... qualquer um serve)
- **Eclipse IDE for Enterprise Java and Web Developers** (já inclui o Maven/m2e). Use uma versão recente (2023-09 ou mais nova).
- **PostgreSQL** e **pgAdmin** instalados e o serviço do PostgreSQL em execução
- Internet na primeira execução (o Maven baixa as dependências)

### 2. Criar o banco no pgAdmin
1. Abra o pgAdmin e conecte no seu servidor PostgreSQL.
2. Clique com o botão direito em **Databases > Create > Database...**
3. Em *Database*, digite **englishkids** e clique em **Save**.

(Alternativa: abra a *Query Tool* e execute o script `database/criar-banco.sql`.)

As tabelas **não** precisam ser criadas: a aplicação cria tudo sozinha ao iniciar.

### 3. Conferir usuário e senha do PostgreSQL
Abra `src/main/resources/application.properties`. Por padrão a aplicação usa:

| Configuração | Padrão                                         |
|--------------|------------------------------------------------|
| URL          | `jdbc:postgresql://localhost:5432/englishkids` |
| Usuário      | `postgres`                                     |
| Senha        | `postgres`                                     |

Se a **sua senha do PostgreSQL for outra** (a que você definiu na instalação), troque o valor depois dos dois pontos:
```properties
spring.datasource.password=${DB_PASSWORD:SUA_SENHA_AQUI}
```
(Se usar outra porta ou usuário, ajuste `DB_URL` e `DB_USER` da mesma forma.)

### 4. Importar o projeto
1. Descompacte o `.zip`.
2. No Eclipse: **File > Import... > Maven > Existing Maven Projects > Next**.
3. Em *Root Directory*, selecione a pasta `englishkids` (a que contém o `pom.xml`) e clique em **Finish**.
4. Aguarde o Eclipse baixar as dependências (barra de progresso no canto inferior direito).

### 5. Conferir o Java
- **Window > Preferences > Java > Installed JREs**: deve haver um JDK 17+ marcado.
- Se aparecer erro de versão do Java: botão direito no projeto > **Properties > Java Compiler** e escolha o nível 17 (ou superior).

### 6. Executar
1. Abra `src/main/java/br/ufms/englishkids/EnglishKidsApplication.java`.
2. Botão direito no arquivo > **Run As > Java Application**.
   (Se você tiver o Spring Tools instalado, também pode usar **Run As > Spring Boot App**.)
3. No Console deve aparecer algo como `Started EnglishKidsApplication`.
4. Acesse **http://localhost:8080** no navegador.

| Perfil | Usuário | Senha    |
|--------|---------|----------|
| ADMIN  | admin   | admin123 |
| USER   | user    | user123  |

Na primeira execução são criados os usuários acima e conteúdo de exemplo: 5 temas e 18 palavras.
Depois, você pode ver as tabelas no pgAdmin em *englishkids > Schemas > public > Tables* (clique com o botão direito > *View/Edit Data*).

### 7. Rodar os testes
Botão direito no projeto (ou na pasta `src/test/java`) > **Run As > JUnit Test**.
Os testes usam **H2 em arquivo** (pasta `target/`), então **não dependem** do PostgreSQL nem alteram o seu banco.

### Problemas comuns
| Mensagem / sintoma | Solução |
|---|---|
| `password authentication failed for user "postgres"` | A senha no `application.properties` não confere com a do seu PostgreSQL (passo 3). |
| `database "englishkids" does not exist` | Crie o banco no pgAdmin (passo 2). |
| `Connection refused` / `connect timed out` | O serviço do PostgreSQL não está rodando, ou está em outra porta. |
| `Port 8080 was already in use` | Feche a outra aplicação ou adicione `server.port=8081` ao `application.properties`. |
| Erros vermelhos no projeto logo após importar | Aguarde o download terminar; depois botão direito no projeto > **Maven > Update Project...** (ou `Alt+F5`) > OK. |
| `release version 17 not supported` | O Eclipse está usando um JRE antigo: ajuste o passo 5. |

---

## Perfis de acesso
| Recurso                                       | USER | ADMIN |
|-----------------------------------------------|:----:|:-----:|
| Praticar (cartões), listar temas e palavras   |  ✔   |  ✔   |
| Cadastrar, editar e excluir temas e palavras  |  ✘   |  ✔   |
| Painel `/admin` e gestão de usuários          |  ✘   |  ✔   |

A restrição existe em duas camadas: na interface (botões ocultos) e no back-end (`SecurityConfig` + `@PreAuthorize`).

## Arquitetura em camadas
```
br.ufms.englishkids
├── config/       SecurityConfig, Permissoes, DataInitializer (usuários), ConteudoSeeder (temas e palavras de exemplo)
├── controller/   Tema, Palavra, Praticar, Admin, UsuarioAdmin + GlobalExceptionHandler
├── dto/          *Form (entrada + Bean Validation) e *Resumo (saída, records imutáveis)
├── exception/    RecursoNaoEncontradoException, RegraNegocioException
├── model/        Entidades JPA: Usuario, Perfil, Tema, Palavra, Nivel
├── repository/   Spring Data JPA
└── service/      Regras de negócio e transações
```
Regras de negócio: nome de tema único · palavra única dentro do tema · tema com palavras não pode ser excluído ·
administrador não pode desativar nem rebaixar a própria conta.

## Acessibilidade (WCAG 2.1 AA)
- Contraste ≥ 4.5:1 (paleta documentada em `static/css/style.css`)
- Tags semânticas: `header`, `nav`, `main`, `footer`, `section`, `article`, `caption`, `details/summary`
- `<label for>` em todos os campos, `aria-required`, `aria-invalid`, `aria-describedby`, `role="alert"/"status"`
- Trechos em inglês marcados com `lang="en"` (leitores de tela pronunciam corretamente)
- Cartões de estudo com `<details>` (teclado e leitor de tela, sem JavaScript); áudio é aprimoramento opcional
- Link "Pular para o conteúdo", foco visível, `aria-current` no menu, `lang="pt-BR"`
- Confirmação de exclusão renderizada no servidor (funciona sem JavaScript)

## Testes
- Unitários (Mockito): `TemaServiceTest`, `PalavraServiceTest`, `UsuarioServiceTest`
- Repositório (`@DataJpaTest`): `PalavraRepositoryTest`
- Integração web/segurança (`MockMvc`): `SecurityIntegrationTest`, `WebCrudIntegrationTest`

