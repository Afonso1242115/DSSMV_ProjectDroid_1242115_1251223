# Campus L&F

Aplicação Android desenvolvida em Java para registar e consultar objetos encontrados no campus do ISEP.

**Equipa:** Afonso Maia (1242115) e Fernanda Vale (1251223) - DSSMV, turma 2DB.

## Funcionalidades

- Consultar objetos encontrados e filtrar por edifício ou categoria.
- Ver detalhes e fotografias dos objetos.
- Publicar um objeto com descrição, fotografia, data e local.
- Adicionar coordenadas GPS opcionalmente.

## Tecnologias

Java, Android, MVVM, Room, LiveData, Retrofit, RxJava e restdb.io.

## Executar

1. Abrir a raiz do projeto no IntelliJ IDEA, com **JDK 17** e **Android SDK 35**.
2. Configurar a ligação à restdb.io no ficheiro `local.properties` (na raiz do projeto):

   ```properties
   restdb.url=https://SUA-BASE.restdb.io/
   restdb.apiKey=SUA_CHAVE_RESTRITA
   ```

3. Sincronizar o Gradle e executar a aplicação num emulador ou dispositivo Android (API 26 ou superior).

Para compilar a partir da raiz:

- **Windows:** `gradlew.bat :app:assembleDebug`
- **macOS/Linux:** `./gradlew :app:assembleDebug`

> Não incluir chaves da API nos commits. Utilizar apenas uma chave com permissões limitadas.

## Documentação

O relatório académico encontra-se na pasta `Documentation/`.
