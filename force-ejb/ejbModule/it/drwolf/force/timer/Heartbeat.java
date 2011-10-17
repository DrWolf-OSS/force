package it.drwolf.force.timer;

import it.drwolf.force.entity.CategoriaMerceologica;
import it.drwolf.force.entity.Fonte;
import it.drwolf.force.entity.Gara;
import it.drwolf.force.entity.SOA;
import it.drwolf.force.enums.TipoGara;
import it.drwolf.force.enums.TipoProceduraGara;
import it.drwolf.force.utils.StartFeedParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.FinalExpiration;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.async.QuartzTriggerHandle;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@AutoCreate
@Name("heartbeat")
public class Heartbeat {

	@Asynchronous
	@Transactional
	public QuartzTriggerHandle startFetcher(@Expiration Date date,
			@IntervalDuration Long intervall, @FinalExpiration Date end) {

		System.out
				.println("################parte il fetcher feed start###############");
		QuartzTriggerHandle handle = new QuartzTriggerHandle(
				"Fetcher dei fedd del sistema START");

		EntityManager entityManager = null;

		while (entityManager == null) {
			try {
				entityManager = (EntityManager) Component
						.getInstance("entityManager");
				System.out.println("---> Sleep!");
				Thread.sleep(2000);
			} catch (Exception e) {

			}
		}

		ArrayList<Fonte> listaFonti = (ArrayList<Fonte>) entityManager
				.createQuery("from Fonte where attiva = true").getResultList();
		for (Fonte fonte : listaFonti) {
			URL url = null;
			try {
				url = new URL(fonte.getUrl());
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Nuova fonte : " + fonte.getNome());
			ClassLoader c1 = Heartbeat.class.getClassLoader();
			Thread.currentThread().setContextClassLoader(c1);
			SyndFeed feed = null;
			try {
				feed = new SyndFeedInput().build(new XmlReader(url));
				Iterator i = feed.getEntries().iterator();
				while (i.hasNext()) {
					SyndEntry post = (SyndEntry) i.next();
					List results = entityManager
							.createQuery("from Gara where oggetto = :oggetto")
							.setParameter("oggetto", post.getTitle())
							.getResultList();
					if (results.size() == 0) {
						System.out.println("Nuova Gara : " + post.getTitle());
						// nuova Gara. La inserisco
						System.out.println(post.getLink());
						Gara gara = new Gara(post.getTitle(), post.getLink(),
								TipoGara.NUOVA.getNome(), fonte);
						// devo prenderemi dal link i valori della gara che sono
						// nella pagina del dettaglio
						StartFeedParser startFeed = new StartFeedParser();
						startFeed.parse(post.getLink());
						Date dataInizio = startFeed.getDataInizio();
						if (dataInizio != null) {
							gara.setDataPubblicazione(dataInizio);
						}
						Date dataFine = startFeed.getDataFine();
						if (dataInizio != null) {
							gara.setDataScadenza(dataFine);
						}
						if (startFeed.isAperta()) {
							gara.setTipoProcedura(TipoProceduraGara.APERTA.getNome());
						}else if (startFeed.isNegoziata()) {
							gara.setTipoProcedura(TipoProceduraGara.NEGOZIATA.getNome());
						}

						List<String> categorie = startFeed.getCategorie();
						if (startFeed.haveCm()) {
							System.out.println("trovate le categorie : "
									+ startFeed.getCM());
							HashSet<CategoriaMerceologica> listaCategorie = new HashSet<CategoriaMerceologica>();
							for (String element : startFeed.getCM()) {
								try {
									String cat = element.toLowerCase()
											.replaceAll("\\W", "_");
									// .replaceAll("\u2012", "-")
									// .replaceAll("\u2013", "-")
									// .replaceAll("\u2014", "-")
									// .replaceAll("\u2015", "-")
									// .replaceAll("\u2053", "-");
									CategoriaMerceologica cm = (CategoriaMerceologica) entityManager
											.createQuery(
													"from CategoriaMerceologica where lower(categoria) like (:categoria)")
											.setParameter("categoria",
													cat.trim())
											.getSingleResult();
									listaCategorie.add(cm);
								} catch (NoResultException e) {
									System.out.println("Categoria non trovata"
											+ element);
									e.printStackTrace();
								} catch (NonUniqueResultException e) {
									e.printStackTrace();
								}
							}
							gara.setCategorieMerceologiche(listaCategorie);
							gara.setType(TipoGara.GESTITA.getNome());
						}
						if (startFeed.haveSoa()) {
							System.out.println("trovate le soa : "
									+ startFeed.getSOA());
							HashSet<SOA> listaSOA = new HashSet<SOA>();
							for (String element : startFeed.getSOA()) {
								try {
									SOA soa = (SOA) entityManager
											.createQuery(
													"from SOA where codice = :codice")
											.setParameter("codice", element)
											.getSingleResult();
									listaSOA.add(soa);
								} catch (NoResultException e) {
									e.printStackTrace();
								} catch (NonUniqueResultException e) {
									e.printStackTrace();
								}
							}
							gara.setSOA(listaSOA);
							gara.setType(TipoGara.GESTITA.getNome());
						}

						entityManager.persist(gara);
					}
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FeedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return handle;
	}
}
