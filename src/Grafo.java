import java.util.*;

public class Grafo {
    private Aresta[] listaDeAdjacencias;
    private String[] rotulosVertices;
    public int totalVertices;

    public Grafo(int totalVerticies) {
        this.totalVertices = totalVerticies;
        this.listaDeAdjacencias = new Aresta[totalVerticies];
        this.rotulosVertices = new String[totalVerticies];
    }

    public void cria_ou_atualiza_adjacencia(int verticeOrigem, int verticeDestino) {
        if (verticeOrigem < 0 || verticeOrigem >= totalVertices || verticeDestino < 0 || verticeDestino >= totalVertices)
            return;

        Aresta atual = listaDeAdjacencias[verticeOrigem];
        while (atual != null) {
            if (atual.verticeDestino == verticeDestino) {
                atual.peso += 1.0;
                return;
            }
            atual = atual.proximaAresta;
        }
        Aresta novaAresta = new Aresta(verticeDestino, 1.0);
        novaAresta.proximaAresta = listaDeAdjacencias[verticeOrigem];
        listaDeAdjacencias[verticeOrigem] = novaAresta;
    }

    public void imprime_adjacencias() {
        for (int vertice = 0; vertice < totalVertices; vertice++) {
            if (rotulosVertices[vertice] == null) continue;

            System.out.print(rotulosVertices[vertice] + " -> ");
            Aresta arestaAtual = listaDeAdjacencias[vertice];
            while (arestaAtual != null) {
                String nomeDestino = rotulosVertices[arestaAtual.verticeDestino];
                System.out.print("[" + nomeDestino + ", peso: " + arestaAtual.peso + "] ");
                arestaAtual = arestaAtual.proximaAresta;
            }
            System.out.println();
        }
    }

    public void setRotulos(String[] nomesColetados) {
        this.rotulosVertices = nomesColetados;
    }

//No 3 ele basicamente quer ir de x pra y e que mostre o caminho
    public List<String> buscaProfundidade(String emailOrigem, String emailDestino, GerenciadorIndices idx) {
        //esse é pra passar do texto email pra numero id
        int idOrigem = idx.getId(emailOrigem);
        int idDestino = idx.getId(emailDestino);

        Set<Integer> visitados = new HashSet<>();//marcar quem ja visitou
        //ai a lista pra guardar a sequencia
        List<Integer> caminhoIds = new ArrayList<>();

        if (dfsRecursivo(idOrigem, idDestino, visitados, caminhoIds)) {
            List<String> caminhoEmails = new ArrayList<>();//se der certo cria uma lista de strings, no caso os emails
            for (int id : caminhoIds) {
                caminhoEmails.add(rotulosVertices[id]);//traduzir os ids pra mostrar
            }
            return caminhoEmails;
        }
        return null;
    }

    private boolean dfsRecursivo(int atual, int destino, Set<Integer> visitados, List<Integer> caminho) {
        visitados.add(atual);
        caminho.add(atual);

        if (atual == destino) return true;

        Aresta aresta = listaDeAdjacencias[atual];
        while (aresta != null) {
            if (!visitados.contains(aresta.verticeDestino)) {//se ainda n foi visitado
                if (dfsRecursivo(aresta.verticeDestino, destino, visitados, caminho)) {//tenta achar
                    return true;
                }
            }
            aresta = aresta.proximaAresta;//se não der certo por aquele caminho, só passa pra proxima aresta da lista
        }

        caminho.remove(caminho.size() - 1);// aqui se ele explorou td e n achou o destino,da pra tirar o nó do caminho
        return false;
    }

    public List<String> buscaLargura(String emailOrigem, String emailDestino, GerenciadorIndices idx) {
        int idOrigem = idx.getId(emailOrigem);
        int idDestino = idx.getId(emailDestino);

        if (idOrigem == idDestino) return Arrays.asList(rotulosVertices[idOrigem]);

        Queue<Integer> fila = new LinkedList<>();
        Map<Integer, Integer> antecessor = new HashMap<>();
        Set<Integer> visitados = new HashSet<>();

        fila.add(idOrigem);
        visitados.add(idOrigem);

        boolean encontrou = false;
        while (!fila.isEmpty()) {
            int atual = fila.poll();
            if (atual == idDestino) {
                encontrou = true;
                break;
            }

            Aresta aresta = listaDeAdjacencias[atual];
            while (aresta != null) {
                if (!visitados.contains(aresta.verticeDestino)) {
                    visitados.add(aresta.verticeDestino);
                    antecessor.put(aresta.verticeDestino, atual);
                    fila.add(aresta.verticeDestino);
                }
                aresta = aresta.proximaAresta;
            }
        }

        if (encontrou) {
            List<String> caminhoEmails = new LinkedList<>();
            Integer passo = idDestino;
            while (passo != null) {
                caminhoEmails.add(0, rotulosVertices[passo]);
                passo = antecessor.get(passo);
            }
            return caminhoEmails;
        }
        return null;
    }

    public List<String> getNosDistanciaD(String emailRaiz, int distanciaAlvo, GerenciadorIndices idx) {
        int idRaiz = idx.getId(emailRaiz);
        if (idRaiz < 0 || idRaiz >= totalVertices || rotulosVertices[idRaiz] == null) return new ArrayList<>();

        List<String> resultado = new ArrayList<>();
        if (distanciaAlvo == 0) {
            resultado.add(rotulosVertices[idRaiz]);
            return resultado;
        }

        Queue<Integer> fila = new LinkedList<>();
        int[] distancia = new int[totalVertices];
        Arrays.fill(distancia, -1);

        fila.add(idRaiz);
        distancia[idRaiz] = 0;

        while (!fila.isEmpty()) {
            int atual = fila.poll();

            if (distancia[atual] == distanciaAlvo) {
                resultado.add(rotulosVertices[atual]);
                continue; // Não precisa explorar vizinhos se já atingiu a distância D
            }

            if (distancia[atual] < distanciaAlvo) {
                Aresta aresta = listaDeAdjacencias[atual];
                while (aresta != null) {
                    if (distancia[aresta.verticeDestino] == -1) {
                        distancia[aresta.verticeDestino] = distancia[atual] + 1;
                        fila.add(aresta.verticeDestino);
                    }
                    aresta = aresta.proximaAresta;
                }
            }
        }

        return resultado;
    }


    public int getNumVertices() {
        int contador = 0;
        for (int i = 0; i < totalVertices; i++) {
            if (rotulosVertices[i] != null) contador++;
        }
        return contador;
    }

    public int getNumArestas() {
        int total = 0;
        for (int i = 0; i < totalVertices; i++) {
            Aresta atual = listaDeAdjacencias[i];
            while (atual != null) {
                total++;
                atual = atual.proximaAresta;
            }
        }
        return total;
    }

    public void imprimirTop20Saida() {
        List<Map.Entry<String, Integer>> listaRanking = new ArrayList<>();
        for (int i = 0; i < totalVertices; i++) {
            if (rotulosVertices[i] == null) continue;
            int grau = 0;
            Aresta atual = listaDeAdjacencias[i];
            while (atual != null) {
                grau++;
                atual = atual.proximaAresta;
            }
            listaRanking.add(new AbstractMap.SimpleEntry<>(rotulosVertices[i], grau));
        }
        listaRanking.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        System.out.println("\n--- TOP 20 GRAU DE SAÍDA ---");
        for (int i = 0; i < Math.min(20, listaRanking.size()); i++) {
            System.out.println((i + 1) + ". " + listaRanking.get(i).getKey() + " | Valor: " + listaRanking.get(i).getValue());
        }
    }

    public void imprimirTop20Entrada() {
        int[] contagemEntrada = new int[totalVertices];
        for (int i = 0; i < totalVertices; i++) {
            Aresta atual = listaDeAdjacencias[i];
            while (atual != null) {
                contagemEntrada[atual.verticeDestino]++;
                atual = atual.proximaAresta;
            }
        }
        List<Map.Entry<String, Integer>> listaRanking = new ArrayList<>();
        for (int i = 0; i < totalVertices; i++) {
            if (rotulosVertices[i] == null) continue;
            listaRanking.add(new AbstractMap.SimpleEntry<>(rotulosVertices[i], contagemEntrada[i]));
        }
        listaRanking.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        System.out.println("\n--- TOP 20 GRAU DE ENTRADA ---");
        for (int i = 0; i < Math.min(20, listaRanking.size()); i++) {
            System.out.println((i + 1) + ". " + listaRanking.get(i).getKey() + " | Valor: " + listaRanking.get(i).getValue());
        }
    }
}