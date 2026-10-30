# Campus L&F

Aplicação Android para registar e consultar objetos encontrados no campus do ISEP, desenvolvida na unidade curricular de Desenvolvimento de Software e Sistemas Móveis (DSSMV), LETI, ano letivo 2026/2027.

**Equipa:** Afonso Maia (1242115) e Fernanda Vale (1251223) — turma 2DB.

## Checkpoint 2 — funcionalidades implementadas no código

- Lista de objetos encontrados, detalhes e filtros por edifício e categoria.
- Publicação de um objeto com fotografia tirada pela câmara, descrição, data e localização textual.
- Obtenção **opcional** de coordenadas GPS, com pedido de permissão e possibilidade de continuar sem GPS.
- Comunicação com a API restdb.io para edifícios, categorias, objetos e imagens.
- Cache local Room para continuar a apresentar os dados já descarregados sem ligação à rede.
- Validação dos dados e apresentação do estado de carregamento/publicação.

O checkpoint 2 tem a estrutura de código implementada, mas a integração real com a API e o funcionamento em dispositivos devem ser confirmados com a checklist em `Documentation/CHECKPOINT2_VALIDATION.md`. Este checkpoint **não é a entrega final**.

## Arquitetura

```text
Activity (View) -> ViewModel -> CampusRepository -> Room (CampusDao / CampusDatabase)
                                              \-> Retrofit (RestDbService / restdb.io)

Room LiveData -> Repository -> ViewModel -> Activity observers
```

- **Java**; Android SDK 35 (compile), Android 8.0 / API 26 (mínimo), Java 17.
- **MVVM**, Android Jetpack ViewModel + LiveData + SavedStateHandle.
- **Room** para SQLite local, com `CampusDatabase` singleton e `CampusDao`.
- **Retrofit** com `RestDbClient` singleton e conversão Gson.
- **RxJava 3** para trabalho assíncrono e atualização da interface na thread principal.
- **Glide** para pré-visualização e carregamento de fotografias.

O código de acesso à API e à base de dados fica no Repository; as Activities observam os ViewModels e não fazem consultas SQL nem pedidos HTTP diretamente.

## Preparação no IntelliJ IDEA (Windows e macOS)

1. Abrir a pasta **raiz do repositório** (onde está `settings.gradle`), e não apenas `android/`.
2. Configurar JDK 17 e instalar Android SDK Platform 35 no ambiente de desenvolvimento.
3. Criar ou editar `local.properties` na **raiz**. O IDE pode preencher automaticamente `sdk.dir`. Acrescentar:
   ```properties
   restdb.url=https://SUA-BASE.restdb.io/
   restdb.apiKey=SUA_CHAVE_RESTRITA
   ```
4. Na restdb.io, disponibilizar as coleções `buildings`, `categories` e `founditems`, com relações para edifício, categoria e imagens. A aplicação espera os campos `name` nas duas primeiras e, em `founditems`, os campos `title`, `description`, `detailed_location`, `found_date`, `building`, `category`, `photos`, `latitude` e `longitude`.
5. Fazer Gradle Sync, escolher um emulador/dispositivo com API 26 ou superior e iniciar `MainActivity`.

**Segurança:** `local.properties` está no `.gitignore`; nunca publicar a chave. Mesmo assim, os valores de `BuildConfig` ficam acessíveis numa app compilada: utilizar **apenas uma chave limitada**, nunca uma chave administrativa ou com permissões globais. Restringir o acesso à base de dados de demonstração.

Se a API não estiver configurada, a aplicação mostra erro ao tentar sincronizar. Os itens já guardados em Room continuam disponíveis para consulta.

## Compilar

A partir da raiz do repositório:

- **Windows:** `gradlew.bat :app:assembleDebug`
- **macOS/Linux:** `./gradlew :app:assembleDebug`

O wrapper utiliza Gradle 8.13. O módulo Android está em `android/app`, mas é registado como `:app` em `settings.gradle`.

## Testes rápidos do modelo

A partir da raiz do repositório, com um JDK instalado:

- **Windows:** `android\tools\run-model-tests.bat`
- **macOS/Linux:** `sh android/tools/run-model-tests.sh`

Os scripts compilam `FoundItem` e executam nove verificações de validação sem SDK Android. Ver também `Documentation/CHECKPOINT2_VALIDATION.md` para os testes manuais da aplicação e da API.

## Documentação

- `Documentation/DSSMV Relatório.doc`: relatório académico inicial (a atualizar nos próximos checkpoints).
- `Documentation/CHECKPOINT2_VALIDATION.md`: critérios de aceitação e testes manuais do checkpoint 2.

## Utilização de inteligência artificial

O ChatGPT foi utilizado como apoio à organização da documentação, revisão da arquitetura e melhoria de código. A equipa é responsável por rever, compreender, executar e validar os resultados.
