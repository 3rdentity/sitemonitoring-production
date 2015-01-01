package net.sf.sitemonitoring.service.check;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import net.sf.sitemonitoring.entity.Check;
import net.sf.sitemonitoring.entity.Check.CheckCondition;
import net.sf.sitemonitoring.jaxb.sitemap.Url;
import net.sf.sitemonitoring.jaxb.sitemap.Urlset;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.eventbus.EventBus;

@RunWith(MockitoJUnitRunner.class)
public class SitemapCheckServiceTest {

	private SitemapCheckThread sitemapCheckThread;
	
	@Mock
	private SinglePageCheckService singlePageCheckServiceMock;

	private Map<URI, Object> visitedPages;

	private static final int timeout = 1000;

	@Before
	public void before() throws JAXBException {
		visitedPages = new HashMap<URI, Object>();
		SinglePageCheckService singlePageCheckService = new SinglePageCheckService();
		singlePageCheckService.setEventBus(new EventBus());
		sitemapCheckThread = new SitemapCheckThread(JAXBContext.newInstance(Urlset.class, Url.class), singlePageCheckService, null);
	}

	@Test
	public void testConvertSitemap() throws Exception {
		String sitemapXml = FileUtils.readFileToString(new File("src/test/resources/sitemap.xml"));
		Urlset urlset = sitemapCheckThread.readSitemap(sitemapXml);
		Assert.assertEquals(2, urlset.getUrls().size());
		Assert.assertEquals("http://www.sqlvids.com/", urlset.getUrls().get(0).getLoc());
	}

	@Test(expected = JAXBException.class)
	public void testConvertSitemapError() throws Exception {
		String sitemapXml = FileUtils.readFileToString(new File("src/test/resources/sitemap.corrupt.xml"));
		Urlset urlset = sitemapCheckThread.readSitemap(sitemapXml);
		Assert.assertEquals(2, urlset.getUrls().size());
		Assert.assertEquals("http://www.sqlvids.com/", urlset.getUrls().get(0).getLoc());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCheckSitemapErrors() throws Exception {
		Mockito.when(singlePageCheckServiceMock.performCheck(Mockito.any(Check.class), Mockito.any(Map.class))).thenReturn("Error!");
		sitemapCheckThread.setSinglePageCheckService(singlePageCheckServiceMock);

		String sitemapXml = FileUtils.readFileToString(new File("src/test/resources/sitemap.xml"));
		Urlset urlset = sitemapCheckThread.readSitemap(sitemapXml);
		Check sitemapCheck = new Check();
		sitemapCheck.setConditionType(CheckCondition.CONTAINS);
		sitemapCheck.setCondition("</html>");
		sitemapCheck.setCheckBrokenLinks(false);
		sitemapCheck.setSocketTimeout(timeout);
		sitemapCheck.setConnectionTimeout(timeout);
		String checkResult = sitemapCheckThread.check(urlset, sitemapCheck, null);
		Assert.assertEquals("Error!<br />Error!<br />", checkResult);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCheckSitemapNoErrors() throws Exception {
		Mockito.when(singlePageCheckServiceMock.performCheck(Mockito.any(Check.class), Mockito.any(Map.class))).thenReturn(null);
		sitemapCheckThread.setSinglePageCheckService(singlePageCheckServiceMock);

		String sitemapXml = FileUtils.readFileToString(new File("src/test/resources/sitemap.xml"));
		Urlset urlset = sitemapCheckThread.readSitemap(sitemapXml);
		Check sitemapCheck = new Check();
		sitemapCheck.setConditionType(CheckCondition.CONTAINS);
		sitemapCheck.setCondition("</html>");
		sitemapCheck.setCheckBrokenLinks(false);
		sitemapCheck.setSocketTimeout(timeout);
		sitemapCheck.setConnectionTimeout(timeout);
		String checkResult = sitemapCheckThread.check(urlset, sitemapCheck, visitedPages);
		Assert.assertEquals(null, checkResult);
	}

}
