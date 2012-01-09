package it.drwolf.force.timer;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.entity.CategoriaMerceologica;
import it.drwolf.force.entity.ComunicazioneAziendaGara;
import it.drwolf.force.entity.ComunicazioneAziendaGaraId;
import it.drwolf.force.entity.EntePubblico;
import it.drwolf.force.entity.Fonte;
import it.drwolf.force.entity.Gara;
import it.drwolf.force.entity.SOA;
import it.drwolf.force.enums.StatoGara;
import it.drwolf.force.enums.TipoGara;
import it.drwolf.force.enums.TipoProceduraGara;
import it.drwolf.force.enums.TipoSvolgimento;
import it.drwolf.force.utils.AvcpFeedParser;
import it.drwolf.force.utils.StartFeedParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.jboss.seam.transaction.UserTransaction;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
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
		HashMap<String, HtmlAnchor> dettagliGare = new HashMap<String, HtmlAnchor>();
		Fonte fonte = (Fonte) entityManager.createQuery(
				"from Fonte where attiva = true and tipo='AVCP'")
				.getSingleResult();

		ArrayList<EntePubblico> enti = (ArrayList<EntePubblico>) entityManager
				.createQuery("from EntePubblico where attivo = true")
				.getResultList();
		WebClient webClient = new WebClient();
		webClient.setJavaScriptEnabled(false);

		try {
			// per prima cosa devo prendere la prima pagina della ricerca
			// (quella base) in modo da ottenere la sessione corretta
			HtmlPage firstPage = webClient.getPage(fonte.getUrl());
			// In questa pagina cerco il link alla ricerca avanzata
			List<HtmlAnchor> anchors = firstPage.getAnchors();
			HtmlAnchor anchor = null;
			// Non so come mai ma non mi trova l'acora giusta se la cerco
			// per
			// testo
			// mi faccio dare tutte le ancore e mi prendo quella con il
			// testo
			// corretto
			for (HtmlAnchor htmlAnchor : anchors) {
				if (htmlAnchor.asText().equals("Ricerca avanzata")) {
					anchor = htmlAnchor;
				}
			}
			if (anchor != null) {
				// Se ho trovato l'ancora mi vado a prendere il form e il
				// bottone
				// da submittare
				HtmlPage secondPage = anchor.click();
				HtmlForm form = secondPage.getFormByName("AdvancedSearch");
				HtmlSubmitInput button = form.getInputByValue("Cerca");
				// Imposto il giusto valore di CPV e faccio submit
				HtmlTextInput input = form.getInputByName("CFAmm");
				for (EntePubblico ente : enti) {
					System.out.println("Ricerco per codice fiscale : "
							+ ente.getCodiceFiscale());
					dettagliGare.clear();
					input.setValueAttribute(ente.getCodiceFiscale());
					HtmlPage risposta = button.click();
					HtmlTable table = (HtmlTable) risposta
							.getElementById("listaGare");
					if (table != null) {
						for (HtmlTableBody body : table.getBodies()) {
							List<HtmlTableRow> rows = body.getRows();
							for (HtmlTableRow row : rows) {
								String title = "";
								HtmlAnchor link = null;
								for (HtmlTableCell cell : row.getCells()) {
									if (!cell.getAttribute("title").equals("")) {
										title = cell.getAttribute("title");
									}
									List<HtmlElement> a = cell
											.getHtmlElementsByTagName("a");
									if (a.size() == 1) {
										link = (HtmlAnchor) a.get(0);

									}
								}
								if (!title.equals("") && link != null) {
									dettagliGare.put(title, link);
								}
							}
						}

						AvcpFeedParser avcpFeed = new AvcpFeedParser();
						for (String title : dettagliGare.keySet()) {
							String oggettoLike = title.toLowerCase()
									.replaceAll("\\W+", "%");
							List results = entityManager
									.createQuery(
											"from Gara where lower(oggetto) like lower(:oggetto)")
									.setParameter("oggetto", oggettoLike)
									.getResultList();
							if (results.size() == 0) {
								System.out.println("Nuova Gara : " + title);
								HtmlAnchor dg = dettagliGare.get(title);
								HtmlPage dettaglio = dg.click();
								avcpFeed.parse(dettaglio);
								if (avcpFeed.isValid()
										&& avcpFeed.getOggetto() != null) {
									Gara gara = new Gara(avcpFeed.getOggetto(),
											ente.getLinkSezioneGare(),
											TipoGara.NUOVA.getNome(), fonte);
									gara.setStato(StatoGara.INSERITA.toString());
									Date dataInizio = avcpFeed.getDataInizio();
									if (dataInizio != null) {
										gara.setDataPubblicazione(dataInizio);
									}
									Date dataFine = avcpFeed.getDataFine();
									if (dataInizio != null) {
										gara.setDataScadenza(dataFine);
									}
									if (avcpFeed.haveSoa()) {
										System.out.println("trovate le soa : "
												+ avcpFeed.getSOA());
										HashSet<SOA> listaSOA = new HashSet<SOA>();
										for (String element : avcpFeed.getSOA()) {
											try {
												SOA soa = (SOA) entityManager
														.createQuery(
																"from SOA where codice = :codice")
														.setParameter("codice",
																element)
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
									if (this.storeOnDatabase(gara,
											entityManager)) {
										this.setComunicaizioneAziende(
												entityManager, gara);
									}
								}
							}

						}
					}
				}
			} else {
				System.out.println("Non trovate la ricerca avanzata");
			}

		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
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
				.createQuery("from Fonte where attiva = true and tipo='START'")
				.getResultList();
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
			WebClient wc = new WebClient();
			try {
				feed = new SyndFeedInput().build(new XmlReader(url));
				Iterator i = feed.getEntries().iterator();
				StartFeedParser startFeed = new StartFeedParser();
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
						try {

							startFeed.parse((HtmlPage) wc.getPage(post
									.getLink()));
							if (startFeed.isValid()) {
								Gara gara = new Gara(post.getTitle(),
										post.getLink(),
										TipoGara.NUOVA.getNome(), fonte);
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
									System.out
											.println("trovate le categorie : "
													+ startFeed.getCategorie());
									HashSet<CategoriaMerceologica> listaCategorie = new HashSet<CategoriaMerceologica>();
									for (String element : startFeed
											.getCategorie()) {
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
													.setParameter("codice",
															element)
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
									this.setComunicaizioneAziende(
											entityManager, gara);
								}

							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
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
