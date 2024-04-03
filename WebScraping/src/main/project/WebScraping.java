package project;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import model.Filmes;

public class WebScraping {

	public static void main(String[] args) {
		rasparDados();
	}

	private static void rasparDados() {
		System.out.println("Inicio");
		// definir o caminho do driver
		System.setProperty("webdriver.edge.driver", "resources/msedgedriver.exe");

		EdgeOptions options = new EdgeOptions();

		// nao exibe o navegador
		options.addArguments("--headless=new");

		// para corrigir possiveis erros na execu��o
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");

		// evitar deteccao de sites

		options.addArguments("--disable-blink-features=AutomationControlled");
		options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
		options.setExperimentalOption("useAutomationExtension", null);

		// tamanho da janela
		options.addArguments("window-size=800,600");

		// para ajudar a nao ser identificado como bot
		options.addArguments("user-agent" + "=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
				+ "(KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");

		// abrindo o site
		WebDriver driver = new EdgeDriver(options = options);

		driver.get("https://nahoradoocio.lowlevel.com.br/2020/12/22/15-filmes-para-assistir-no-natal/");

		waitForIt(5000);

		// Conexao com banco de dados
		 EntityManagerFactory emf = Persistence.createEntityManagerFactory("Web-Scraping");
		 EntityManager em = emf.createEntityManager();
		 
		// Buscando os Titulos
		List<WebElement> tituloDosFilmes = driver.findElements(By.tagName("strong"));

		// Buscando as Descricoes
		List<WebElement> descricoesFilmes = driver.findElements(By.tagName("p"));
		
		//ArrayList<Filmes> filmes = new ArrayList<Filmes>();
		System.out.println("Buscando informacoes");
		for (int i = 0; i < 15; i++) {
			if(i != 15) {
				em.getTransaction().begin(); //Iniciar transacao com banco
			}
			String nom = tituloDosFilmes.get(i).getText();
			String desc = descricoesFilmes.get(i + 4).getText();
			Filmes film = new Filmes (null, nom, desc); 
			
			if (i != 15) {
				em.persist(film);
				em.getTransaction().commit();
			}
		}
		System.out.println("Commit");
		
		 //Confirmar as alteracoes do banco
		em.close();
		emf.close();
		System.out.println("Fim");
		
		waitForIt(5000);
		driver.quit();
	}

	// metodo para parar a execucao
	private static void waitForIt(long tempo) {

		try {
			new Thread().sleep(tempo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
