/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.controller.front;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.jfinal.core.Controller;

import io.jpress.Consts;
import io.jpress.core.cache.ActionCache;
import io.jpress.model.Content;
import io.jpress.model.Option;
import io.jpress.model.Taxonomy;
import io.jpress.router.RouterMapping;
import io.jpress.utils.StringUtils;

@RouterMapping(url = "/sitemap")
public class SitemapController extends Controller {
	private static final String contentType = "text/xml; charset=" + Consts.CHARTSET_UTF8;

	@ActionCache
	public void index() {
		StringBuilder xmlBuilder = new StringBuilder();
		buildSitemapHeader(xmlBuilder);
		String domain = Option.findValue("web_domain");

		buildSitemap(xmlBuilder, domain + "/sitemap/site", new Date().toString());
		List<Taxonomy> taxonomys = Taxonomy.DAO.doFind();
		if (taxonomys != null && !taxonomys.isEmpty()) {
			for (Taxonomy t : taxonomys) {
				buildSitemap(xmlBuilder, domain + "/sitemap/taxonomy/" + t.getId(), new Date().toString());
			}
		}
		buildSitemapFooter(xmlBuilder);
		renderText(xmlBuilder.toString(), contentType);
	}

	@ActionCache
	public void site() {
		StringBuilder xmlBuilder = new StringBuilder();
		buildUrlsetHeader(xmlBuilder);
		buildUrl(xmlBuilder, Option.findValue("web_domain"), new Date().toString(), "always", "1.0");
		buildUrlsetFooter(xmlBuilder);
		renderText(xmlBuilder.toString(), contentType);
	}

	@ActionCache
	public void taxonomy() {
		String idString = getPara();
		if (!StringUtils.isNotBlank(idString)) {
			renderText("", contentType);
		}

		BigInteger id = new BigInteger(idString);
		StringBuilder xmlBuilder = new StringBuilder();
		buildUrlsetHeader(xmlBuilder);
		String domain = Option.findValue("web_domain");
		List<Content> contents = Content.DAO.findListInNormal(1, 500, id, null);
		if (contents != null && !contents.isEmpty()) {
			for (Content c : contents) {
				buildUrl(xmlBuilder, domain + c.getUrl(), new Date().toString(), "daily", "1.0");
			}
		}
		buildUrlsetFooter(xmlBuilder);
		renderText(xmlBuilder.toString(), contentType);
	}

	private void buildSitemapHeader(StringBuilder xmlBuilder) {
		buildHeader(xmlBuilder);
		xmlBuilder.append("<sitemapindex ");
		xmlBuilder.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		xmlBuilder.append(" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9\" ");
		xmlBuilder.append(" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" ");
		xmlBuilder.append(" > ");
	}

	private void buildSitemapFooter(StringBuilder xmlBuilder) {
		xmlBuilder.append("</sitemapindex>");
		buildFooter(xmlBuilder);
	}

	private void buildUrlsetHeader(StringBuilder xmlBuilder) {
		buildHeader(xmlBuilder);
		xmlBuilder.append("<urlset");
		xmlBuilder.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
		xmlBuilder.append(" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9\" ");
		xmlBuilder.append(" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" ");
		xmlBuilder.append(" > ");
	}

	private void buildUrlsetFooter(StringBuilder xmlBuilder) {
		xmlBuilder.append("</urlset>");
		buildFooter(xmlBuilder);
	}

	private void buildHeader(StringBuilder xmlBuilder) {
		xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}

	private void buildFooter(StringBuilder xmlBuilder) {
		xmlBuilder.append("<!-- This sitemap was generated by JPress --> ");
	}

	private void buildSitemap(StringBuilder xmlBuilder, String loc, String lastmod) {
		xmlBuilder.append("<sitemap>");
		xmlBuilder.append("<loc>" + loc + "</loc>");
		xmlBuilder.append("<lastmod>" + lastmod + "</lastmod>");
		xmlBuilder.append("</sitemap>");
	}

	private void buildUrl(StringBuilder xmlBuilder, String loc, String lastmod, String changefreq, String priority) {
		xmlBuilder.append("<url>");
		xmlBuilder.append("<loc>" + loc + "</loc>");
		xmlBuilder.append("<lastmod>" + lastmod + "</lastmod>");
		xmlBuilder.append("<changefreq>" + changefreq + "</changefreq>");
		xmlBuilder.append("<priority>" + priority + "</priority>");
		xmlBuilder.append("</url>");
	}

}
