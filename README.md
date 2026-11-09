# kruskal-ecopontos

Árvore geradora mínima com o algoritmo de Kruskal aplicada aos ecopontos da Comurg, em Goiânia.

Projeto do Seminário de Grafos da disciplina **Estrutura de Dados II**, do curso de Engenharia de Software da **Faculdade SENAI FATESG**, sob orientação do Prof. Luciano Lima. O trabalho também é a atividade extensionista da disciplina.

**Grupo 1 · Tema 4: Árvore geradora mínima**

## Integrantes

- Gilvan Pedro de Castro Melo Campos
- Giovanna Alice Ferreira da Costa
- Guilherme Scarcela Bueno
- João Paulo Marques Ferreira
- João Pedro Martini Denani
- Pedro da Mata Santos
- Sidney Emanuel Barbosa de Oliveira

## O problema

Goiânia tem cinco ecopontos permanentes, operados pela Companhia de Urbanização de Goiânia (Comurg). O projeto responde a uma pergunta:

> Quais ligações entre os cinco ecopontos formam uma rede conectada com a menor soma de distâncias?

Uma árvore geradora mínima liga todos os pontos de um grafo usando o menor custo total possível, sem formar ciclos. Com `n` pontos, ela sempre tem `n - 1` ligações.

O resultado indica a rede de ligações mais econômica entre os ecopontos. Ele **não** representa a distância exata que um caminhão percorreria, porque a árvore não define uma ordem de visitas.

## Como o Kruskal funciona

1. Ordena todas as ligações da menor para a maior.
2. Testa cada ligação, na ordem:
    - se as duas pontas estão em **grupos diferentes**, aceita a ligação e junta os grupos;
    - se estão no **mesmo grupo**, já existe caminho entre elas, então descarta, porque formaria ciclo.
3. Para ao aceitar `n - 1` ligações.

O controle dos grupos é feito pela estrutura de união-busca (`UniaoBusca`). Cada ponto tem um índice, que é a sua posição no vetor de grupos.

**Complexidade:** O(m log m), em que `m` é o número de ligações. O custo é dominado pela ordenação.

## Os dois grafos

O programa executa o mesmo método `Kruskal.executar()` em dois grafos diferentes, mudando só os dados de entrada.

### Grafo A–F (exemplo do professor)

Seis centros de distribuição fictícios. O peso é o tempo de transporte em **horas**. As rotas são tratadas como não dirigidas, valendo nos dois sentidos com o mesmo peso.

| Rota | Tempo (h) | Rota | Tempo (h) |
|---|---|---|---|
| A – B | 4 | B – D | 10 |
| A – C | 2 | C – E | 3 |
| B – C | 5 | E – D | 4 |
| D – F | 11 | E – F | 8 |

Quando duas ligações têm o mesmo peso, o desempate é pela ordem alfabética do primeiro ponto.

### Ecopontos Comurg (case real)

| Ecoponto | Endereço |
|---|---|
| Guanabara | Rua GB-5 com Rua GB-6, Jardim Guanabara II |
| Faiçalville | Av. Nadra Bufaiçal com Av. Madri, Setor Faiçalville |
| São José | Rua Frei Nazareno Confaloni com Rua Irmã Maria Bernarda, Jardim São José |
| Campos Dourados | Rua São João Del Rei, APM 07, Residencial Campos Dourados |
| Parque Eldorado Oeste | Rua Elo 43, Parque Eldorado Oeste |

**Critério do peso:** média, em km, das distâncias de ida e de volta medidas no Google Maps (modo carro, rota sugerida) em 07/11/2026. A média é usada porque a ida e a volta podem ter distâncias diferentes por causa de vias de mão única, e o Kruskal precisa de um único peso por ligação.

| Origem | Destino | Ida (km) | Volta (km) | Média (km) |
|---|---|---|---|---|
| Guanabara | Faiçalville | 25,5 | 26,2 | 25,85 |
| Guanabara | São José | 26,0 | 26,6 | 26,3 |
| Guanabara | Campos Dourados | 38,8 | 40,6 | 39,7 |
| Guanabara | Parque Eldorado Oeste | 27,4 | 28,1 | 27,75 |
| Faiçalville | São José | 14,0 | 13,9 | 13,95 |
| Faiçalville | Campos Dourados | 11,0 | 11,8 | 11,4 |
| Faiçalville | Parque Eldorado Oeste | 16,1 | 16,1 | 16,1 |
| São José | Campos Dourados | 18,1 | 19,7 | 18,9 |
| São José | Parque Eldorado Oeste | 2,7 | 2,7 | 2,7 |
| Campos Dourados | Parque Eldorado Oeste | 21,8 | 20,3 | 21,05 |

## Resultados

### Grafo A–F

```
Aceita:     A - C (2 h)
Aceita:     C - E (3 h)
Aceita:     A - B (4 h)
Aceita:     E - D (4 h)
Descartada: B - C (5 h) -> formaria ciclo
Aceita:     E - F (8 h)
Custo total: 21 h
```

### Ecopontos Comurg

```
Aceita:     São José - Parque Eldorado Oeste (2,7 km)
Aceita:     Faiçalville - Campos Dourados (11,4 km)
Aceita:     Faiçalville - São José (13,95 km)
Descartada: Faiçalville - Parque Eldorado Oeste (16,1 km) -> formaria ciclo
Descartada: São José - Campos Dourados (18,9 km) -> formaria ciclo
Descartada: Campos Dourados - Parque Eldorado Oeste (21,05 km) -> formaria ciclo
Aceita:     Guanabara - Faiçalville (25,85 km)
Custo total: 53,9 km
```

Os quatro ecopontos da região sudoeste ficam próximos entre si. O Guanabara, isolado no nordeste da cidade, entra na rede por uma única ligação, a mais curta disponível para ele.

## Estrutura do projeto

```
src/
├── domain/
│   ├── Ponto.java          nome, endereço e índice de cada local
│   ├── Aresta.java         ligação entre dois pontos com um peso
│   ├── Grafo.java          cadastro e validação de pontos e ligações
│   ├── UniaoBusca.java     grupos usados para detectar ciclos
│   └── Kruskal.java        o algoritmo
├── dados/
│   └── GrafosExemplo.java  grafo A–F e grafo dos ecopontos
└── console/
    └── Main.java           menu do programa
```

## Como executar

Requer Java 17 ou superior.

Pela linha de comando, a partir da pasta do projeto:

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out console.Main
```

Também é possível abrir o projeto numa IDE e executar a classe `console.Main`.

## Menu

```
1 - Executar Kruskal no grafo A–F (exemplo do professor)
2 - Executar Kruskal nos ecopontos
3 - Listar ecopontos e ligações
4 - Adicionar novo ecoponto
5 - Adicionar ligação entre ecopontos existentes
6 - Restaurar os 5 ecopontos originais
0 - Sair
```

Os ecopontos adicionados pelo usuário entram no mesmo grafo dos originais, e as opções 2 e 3 passam a considerá-los. A opção 6 volta ao case original.

### Validações

- Todo ponto novo precisa ser ligado a pelo menos um ponto existente.
- Distâncias precisam ser maiores que zero e aceitam vírgula ou ponto (ex.: `11,4`).
- Não é permitido cadastrar um ponto com nome repetido, ligar um ponto a ele mesmo ou repetir uma ligação já existente.
- Nomes são comparados sem diferenciar maiúsculas e minúsculas nem espaços extras.
- Se a rede ficar desconectada, o programa avisa que não existe árvore geradora e lista os pontos sem nenhuma ligação.

## Limitações

- As distâncias são as rotas sugeridas pelo Google Maps na data da medição e podem mudar com obras ou alterações de trânsito.
- A média entre ida e volta é uma simplificação para tratar a rede como não dirigida.
- O modelo considera apenas os cinco ecopontos permanentes, sem os pontos provisórios de descarte.
- O custo total é a soma das ligações escolhidas, não a distância de um percurso real.

## Fontes

- Comurg. Ecopontos de Goiânia são opções gratuitas para descarte correto de resíduos. 11 abr. 2026.
- Jornal Opção e Tribuna do Planalto: endereços dos ecopontos.
- Google Maps: distâncias por via rodoviária, consultadas em 07/11/2026.
- CORMEN, Thomas H. et al. Algoritmos: teoria e prática. 3. ed. Rio de Janeiro: Campus, 2012. Cap. 23.