# streaming-api

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

### 1. Inicializar o Back-end (Quarkus)

1. Certifique-se de ter o **Java JDK 17+** instalado.
2. Navegue até o diretório raiz do projeto:
   ```bash
   cd "c:\Users\user\Downloads\Streaming App"
   ```
3. Instale o Maven (caso não tenha) e baixe as dependências rodando a API em modo de desenvolvimento com live-reload:
   ```bash
   mvn quarkus:dev
   ```
4. O servidor estará rodando nativamente na porta **8080** interceptando todas as rotas em `/api`.

### 2. Inicializar o Front-end

1. Certifique-se de ter o **Node.js** instalado.
2. Navegue até o diretório do front-end:
   ```bash
   cd "c:\Users\user\Downloads\Streaming App\frontend"
   ```
3. Instale as dependências:
   ```bash
   npm install
   ```
4. Inicie o servidor de desenvolvimento:
   ```bash
   npm run dev
   ```
5. O front-end estará disponível em **http://localhost:5173**.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/streaming-api-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
