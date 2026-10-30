# Campus L&F — Checklist de validação do checkpoint 2

**Âmbito:** revisão final do checkpoint 2. A aplicação não deve ser considerada testada integralmente até executar os casos seguintes em dispositivos/emuladores com a API configurada.

## Revisão estática da arquitetura

- [x] Activities enviam ações aos ViewModels; consultas HTTP/SQL são feitas pelo Repository.
- [x] ViewModels expõem LiveData para a interface; o cache observado é Room.
- [x] `CampusDatabase` e `RestDbClient` utilizam instâncias singleton.
- [x] `CampusRepository` utiliza Retrofit + RxJava fora da thread de UI.
- [x] O código SQLiteOpenHelper antigo foi removido do fluxo da aplicação.
- [x] O formulário inclui câmara, edifício, categoria, data e GPS opcional.
- [x] `local.properties` está ignorado pelo Git; não contém credenciais no repositório.

## Testes automáticos sem Android

A partir da raiz:

```text
Windows:      android\tools\run-model-tests.bat
macOS/Linux:  sh android/tools/run-model-tests.sh
```

Estes testes cobrem nove situações: item válido, normalização de espaços, título demasiado curto, título excessivamente longo, descrição vazia, data nula, data futura, ID negativo e localização demasiado curta.

## Testes manuais obrigatórios (pendentes de execução)

- [ ] Configurar `restdb.url` e `restdb.apiKey` em `local.properties`, com permissões limitadas.
- [ ] Executar Gradle Sync e `:app:assembleDebug` sem erros no IntelliJ IDEA.
- [ ] Abrir a aplicação no Android Emulator e, se possível, num dispositivo real.
- [ ] Consultar os edifícios e categorias recebidos da restdb.io.
- [ ] Verificar a lista de objetos e aplicar os filtros por edifício/categoria (incluindo filtro combinado).
- [ ] Abrir detalhes de um objeto, com fotografia, data, descrição e localização.
- [ ] Publicar um objeto com campos válidos, fotografia e data; confirmar na restdb.io e na lista.
- [ ] Confirmar que campos vazios ou inválidos impedem a publicação e apresentam erro compreensível.
- [ ] Pedir GPS, conceder permissão e confirmar coordenadas plausíveis no formulário.
- [ ] Negar permissão de localização; continuar a publicar com localização textual.
- [ ] Limpar coordenadas GPS e publicar sem latitude/longitude.
- [ ] Recusar/desligar acesso à rede e confirmar que os registos previamente guardados em Room continuam visíveis.
- [ ] Reabrir a aplicação após rotação e verificar que os dados do formulário/filtros são conservados.
- [ ] Testar numa máquina macOS com JDK 17, Android SDK 35 e `./gradlew :app:assembleDebug`.
- [ ] Verificar se não foram enviados para o repositório `local.properties`, chaves da API nem outros segredos.

## Riscos e limites conhecidos

- A chave restdb.io configurada em `BuildConfig` é extraível de uma APK; utilizar chave com acesso mínimo e apenas dados de demonstração.
- Não existe uma GitHub Action de compilação configurada no repositório; cada colaborador deve executar o build localmente.
- A publicação com upload, a compatibilidade do modelo de dados na restdb.io e as permissões GPS só podem ser confirmadas com teste de integração real.
- A base de dados Room é uma cache: sem ligação à rede, a aplicação exibe os registos previamente descarregados.
- Checkpoints seguintes: testes adicionais, melhoria de UX e documentação/diagramas finais.
