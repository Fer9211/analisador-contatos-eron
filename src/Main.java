import java.io.File;

public class Main {
  public static void main(String[] args) {
    Analisador analisador = new Analisador();
    File pastaAmostra = new File("data/Amostra Enron");

    System.out.println("Lendo base de dados...");

    if (pastaAmostra.exists()) {
      analisador.lerArquivosDaPasta(pastaAmostra);

      System.out.println("\n=== RESULTADOS GERAIS ===");
      System.out.println("Vértices: " + analisador.calcularTotalDeVertices());
      System.out.println("Arestas: " + analisador.calcularTotalDeArestas());

      // Requisitos do Integrante A (1.0 ponto)[cite: 1]
      analisador.mostrarTop20Saida();
      analisador.mostrarTop20Entrada();

      
    } else {
      System.out.println("Erro: Pasta não encontrada em " + pastaAmostra.getAbsolutePath());
    }
  }
}