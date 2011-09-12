package it.drwolf.force.timer;

import it.drwolf.force.entity.CategoriaMerceologica;
import it.drwolf.force.entity.Fonte;
import it.drwolf.force.entity.Gara;
import it.drwolf.force.entity.SOA;
import it.drwolf.force.enums.TipoGara;
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
				.createQuery("from Fonte").getResultList();
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

						List<String> categorie = startFeed.getCategorie();
						if (categorie.size() > 0) {
							System.out.println("trovate le categorie : "
									+ categorie);
							// ci sono della categorie
							HashSet<CategoriaMerceologica> listaCategorie = new HashSet<CategoriaMerceologica>();
							HashSet<SOA> listaSOA = new HashSet<SOA>();
							for (String categoria : categorie) {
								if (categoria.startsWith("OS")
										|| categoria.startsWith("OG")) {
									try {
										SOA soa = (SOA) entityManager
												.createQuery(
														"from SOA where codice = :codice")
												.setParameter("codice",
														categoria)
												.getSingleResult();
										listaSOA.add(soa);
									} catch (NoResultException e) {
										e.printStackTrace();
									} catch (NonUniqueResultException e) {
										e.printStackTrace();
									}
								} else {
									CategoriaMerceologica cm;
									try {
										cm = (CategoriaMerceologica) entityManager
												.createQuery(
														"from CategoriaMerceologica where categoria = lower(:categoria)")
												.setParameter("categoria",
														categoria.toLowerCase())
												.getSingleResult();
										listaCategorie.add(cm);
									} catch (NoResultException e) {
										System.out
												.println("Categoria non trovata "
														+ categoria);
										e.printStackTrace();
									} catch (NonUniqueResultException e) {
										e.printStackTrace();
									}
								}
							}
							if (!listaCategorie.isEmpty()) {
								gara.setCategorieMerceologiche(listaCategorie);
							}
							if (!listaSOA.isEmpty()) {
								gara.setSOA(listaSOA);
							}
						} else {
							System.out
									.println("Non sono riuscito ad individuare le categorie della gara");
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
