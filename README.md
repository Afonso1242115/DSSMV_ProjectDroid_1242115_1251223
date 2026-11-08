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

3. Na coleção `founditems` do restdb, criar o campo **`photo_base64`**, de tipo
   **Text / Multi-line**, sem `Required`, `Unique`, máscara ou expressão de cálculo.
   Se o campo antigo `photos` estiver marcado como obrigatório, desmarcar `Required`.
4. Sincronizar o Gradle e executar a aplicação num emulador ou dispositivo Android (API 26 ou superior).

A fotografia é reduzida e comprimida para um JPEG de até 60 kB. A app envia-a em
Base64 no campo `photo_base64`, juntamente com os dados do objeto e o GPS opcional,
num único `POST /rest/founditems`. O JSON enviado é limitado a 90 kB, deixando
margem face ao limite de 100 kB por registo indicado pelo restdb. A resolução e a
qualidade podem ser reduzidas para respeitar esse orçamento.

Novas publicações usam apenas a API das coleções e a chave restrita com `GET` e
`POST`; não fazem upload para `/media`. A fotografia também fica na cache Room,
para consulta após sincronizar. A atualização migra a base local sem a apagar.

Objetos antigos com IDs no campo `photos` continuam a usar o Media Archive.
A propriedade opcional `restdb.mediaUrl` define o endereço HTTPS dessas imagens
antigas; sem ela, usa-se `restdb.url`.

Para compilar a partir da raiz:

- **Windows:** `gradlew.bat :app:assembleDebug`
- **macOS/Linux:** `./gradlew :app:assembleDebug`

## Documentação

O relatório académico encontra-se na pasta `Documentation/`.
