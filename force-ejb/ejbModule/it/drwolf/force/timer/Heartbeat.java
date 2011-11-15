package it.drwolf.force.timer;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.entity.CategoriaMerceologica;
import it.drwolf.force.entity.ComunicazioneAziendaGara;
import it.drwolf.force.entity.ComunicazioneAziendaGaraId;
import it.drwolf.force.entity.Fonte;
import it.drwolf.force.entity.Gara;
import it.drwolf.force.entity.SOA;
import it.drwolf.force.enums.StatoGara;
import it.drwolf.force.enums.TipoGara;
import it.drwolf.force.enums.TipoProceduraGara;
import it.drwolf.force.enums.TipoSvolgimento;
import it.drwolf.force.utils.StartFeedParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.FinalExpiration;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.transaction.UserTransaction;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@AutoCreate
@Name("heartbeat")
public class Heartbeat {

	private static String[] CPVcodes = { "90900000-6" };

	@Asynchronous
	@Transactional
	public QuartzTriggerHandle avcpFetcher(@Expiration Date date,
			@IntervalDuration Long intervall, @FinalExpiration Date end) {
		System.out
				.println("################parte il fetcher feed avcp###############");
		QuartzTriggerHandle handle = new QuartzTriggerHandle(
				"Fetcher dei fedd del sistema AVCP");

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
		String url = "http://bandigara.avcp.it/AVCP-ConsultazioneBandiGara/GoToAdvancedSearch.action";

		String urlPost = "http://bandigara.avcp.it/AVCP-ConsultazioneBandiGara/AdvancedSearch.action";

		HttpClient client = new HttpClient();
		client.getParams()
				.setParameter(
						"http.useragent",
						"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:7.0.1) Gecko/20100101 Firefox/7.0.1");
		client.getState().setCredentials(AuthScope.ANY, new Credentials() {
		});
		BufferedReader br = null;

		PostMethod method = new PostMethod(urlPost);
		method.addParameter("CPV", Heartbeat.CPVcodes[0]);
		try {
			int returnCode = client.executeMethod(method);
			if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
				System.err
						.println("The Post method is not implemented by this URI");
				// still consume the response body
				method.getResponseBodyAsString();
			} else {
				br = new BufferedReader(new InputStreamReader(
						method.getResponseBodyAsStream()));
				String readLine;
				while (((readLine = br.readLine()) != null)) {
					// System.err.println(readLine);
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			method.releaseConnection();
			if (br != null) {
				try {
					br.close();
				} catch (Exception fe) {
				}
			}
		}

		for (String code : Heartbeat.CPVcodes) {
			System.out.println(code);
		}
		return handle;
	}

	@Asynchronous
	@Transactional
	public QuartzTriggerHandle comunicaGare(@Expiration Date date,
			@IntervalDuration Long intervall, @FinalExpiration Date end) {
		System.out
				.println("################parte il servizio di comunicaiozne delle Gare###############");
		QuartzTriggerHandle handle = new QuartzTriggerHandle(
				"Comunicatore gare");

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
		// per prima cosa devo prelevare l'elenco delle gare inserite e non
		// comunicate
		ArrayList<Gara> gare = (ArrayList<Gara>) entityManager.createQuery(
				"from Gara where type = 'GESTITA' and stato = 'INSERITA'")
				.getResultList();
		for (Gara gara : gare) {
			// pre ogni gara devo prendere l'elenco delle aziende a cui devo
			// comunicarla
			List<ComunicazioneAziendaGara> resultList = entityManager
					.createQuery(
							" from ComunicazioneAziendaGara  where garaId = :gid and email = false")
					.setParameter("gid", gara.getId()).getResultList();
			// a questo punto devo predenre le singole aziende e mandargli una
			// mail
			for (ComunicazioneAziendaGara comunicazioneAziendaGara : resultList) {
			}
		}
		return handle;
	}

	private void setComunicaizioneAziende(EntityManager entityManager, Gara gara) {
		if (gara.getCategorieMerceologicheAsList().size() > 0) {
			List<Azienda> resultList = entityManager
					.createQuery(
							" select distinct  a from Gara g join g.categorieMerceologiche cm join cm.aziende a where g.id = :g")
					.setParameter("g", gara.getId()).getResultList();
			for (Azienda azienda : resultList) {
				// per ogni azienda devo andare a inserire un riga nelle
				// comunicazioni
				ComunicazioneAziendaGaraId id = new ComunicazioneAziendaGaraId(
						azienda.getId(), gara.getId());
				ComunicazioneAziendaGara cag = new ComunicazioneAziendaGara();
				cag.setId(id);
				cag.setEmail(false);
				cag.setWeb(false);
				this.storeOnDatabase(cag, entityManager);
			}
		}
		if (gara.getSOAAsList().size() > 0) {
			List<Azienda> resultList = entityManager
					.createQuery(
							" select distinct  a from Gara g join g.SOA s join s.aziende a where g.id = :g")
					.setParameter("g", gara.getId()).getResultList();
			for (Azienda azienda : resultList) {
				// per ogni azienda devo andare a inserire un riga nelle
				// comunicazioni
				ComunicazioneAziendaGaraId id = new ComunicazioneAziendaGaraId(
						azienda.getId(), gara.getId());
				ComunicazioneAziendaGara cag = new ComunicazioneAziendaGara();
				cag.setId(id);
				cag.setEmail(false);
				cag.setWeb(false);
				this.storeOnDatabase(cag, entityManager);
			}
		}
	}

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
						// devo prenderemi dal link i valori della gara che sono
						// nella pagina del dettaglio
						StartFeedParser startFeed = new StartFeedParser();
						startFeed.parse(post.getLink());
						if (startFeed.isValid()) {
							Gara gara = new Gara(post.getTitle(),
									post.getLink(), TipoGara.NUOVA.getNome(),
									fonte);
							gara.setStato(StatoGara.INSERITA.toString());
							Date dataInizio = startFeed.getDataInizio();
							if (dataInizio != null) {
								gara.setDataPubblicazione(dataInizio);
							}
							Date dataFine = startFeed.getDataFine();
							if (dataInizio != null) {
								gara.setDataScadenza(dataFine);
							}
							if (startFeed.isAperta()) {
								gara.setTipoProcedura(TipoProceduraGara.APERTA
										.getNome());
							} else if (startFeed.isNegoziata()) {
								gara.setTipoProcedura(TipoProceduraGara.NEGOZIATA
										.getNome());
							}
							if (startFeed.isOffLine()) {
								gara.setTipoSvolgimento(TipoSvolgimento.OFFLINE
										.getNome());
							} else if (startFeed.isOnLine()) {
								gara.setTipoSvolgimento(TipoSvolgimento.ONLINE
										.getNome());
							}
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
										System.out
												.println("Categoria non trovata"
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
							if (this.storeOnDatabase(gara, entityManager)) {
								this.setComunicaizioneAziende(entityManager,
										gara);
							}

						}
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

	private boolean storeOnDatabase(Object o, EntityManager entityManager) {
		UserTransaction userTx = null;
		try {
			userTx = (UserTransaction) org.jboss.seam.Component
					.getInstance("org.jboss.seam.transaction.transaction");
			if (!userTx.isActive()) {
				userTx.begin();
			}
			entityManager.joinTransaction();
			entityManager.persist(o);
			entityManager.flush();
			userTx.commit();
			userTx.begin();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (userTx != null && userTx.isActive()) {
					userTx.rollback();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}
}
