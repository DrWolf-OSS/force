package it.drwolf.force.timer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;

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

		URL url = null;
		try {
			url = new URL(
					"http://start.e.toscana.it/regione-toscana/pleiade/XML/regione-toscana.xml");
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
				System.out.println(post.getTitle());
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

		return handle;
	}
}
