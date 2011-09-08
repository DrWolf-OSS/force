package it.drwolf.force.timer;

import it.drwolf.force.entity.Fonte;
import it.drwolf.force.entity.Gara;
import it.drwolf.force.enums.TipoGara;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

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
						// nuova Gara. La inserisco
						Gara gara = new Gara(post.getTitle(), post.getLink(),
								TipoGara.NUOVA.getNome(), fonte);
						entityManager.persist(gara);
						// a questo punto devo andare a prenderemi dal link i
						// valori
						// della gara che sono nella pagina del dettaglio

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
