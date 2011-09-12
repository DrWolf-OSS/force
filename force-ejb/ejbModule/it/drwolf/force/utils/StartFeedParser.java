package it.drwolf.force.utils;

import it.drwolf.force.interfaces.GaraFeedParserIF;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class StartFeedParser implements GaraFeedParserIF {

	HtmlPage page;

	public List<String> getCategorie() {
		Pattern pCM = Pattern.compile("^[A-Z0-9\\- ]+$");
		Pattern pSOA = Pattern.compile("^O[GS]\\s\\d");
		ArrayList<String> categorie = new ArrayList<String>();
		List<HtmlTableRow> rows = (List<HtmlTableRow>) this.page
				.getByXPath("//tr[@class='cell']");
		for (HtmlTableRow row : rows) {
			for (HtmlTableCell cell : row.getCells()) {
				String[] res = cell.asText().split(":");
				if (res.length == 2) {
					Matcher mCM = pCM.matcher(res[1]);
					Matcher mSOA = pSOA.matcher(res[0]);
					if (mCM.find()) {
						// Ho individuato una categoria merceologica
						categorie.add(res[1].trim());
					} else if (mSOA.find()) {
						categorie.add(res[0].trim());
					}
				}
			}

		}
		return categorie;
	}

	public void parse(String url) {
		WebClient wc = new WebClient();
		try {
			this.page = wc.getPage(url);
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
