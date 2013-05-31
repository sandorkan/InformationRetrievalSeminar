from scrapy.contrib.spiders import CrawlSpider, Rule
from scrapy.contrib.linkextractors.sgml import SgmlLinkExtractor
from scrapy.selector import HtmlXPathSelector
import re


class TASpider(CrawlSpider):
    name = "ta"
    allowed_domains = ["google.ch"]
    start_urls = [
        "https://www.google.ch/search?q=site:tagesanzeiger.ch/schweiz/standard+-inurl:comments+-inurl:html&num=100&tbs=qdr:w"
    ]
    
    rules = [
    	Rule(SgmlLinkExtractor(allow=('/schweiz/standard/.*',)), 'parse_item', follow=True)
    ]
    
    def parse_item(self, response):
        
        hxs = HtmlXPathSelector(response)
        filename = "ta/" + hxs.select("//meta[@property='og:title']/@content").extract()[0] + ".ta"
        doc = open(filename,'wb')

        article_text = hxs.select('//p[not(@class) and not(@style) and not(label) and (string-length() > 100)]').extract()
        
        article = ""
        article += hxs.select("//p[@class='teaser']").extract()[0] + "\n"
        for p in article_text:
        	article += p + "\n"
        	
        #the purpose of this regex expression is to remove all child html tags like <p>, <br> or <a>.. quotes like <<this>> shouldn't be removed though
        #so the regex reads as follows: first sign anything but <, exactly one <, any number of symbols but not greedy, exactly one >, anything but another >
        	
		article = re.sub("[^<]<{1}.*?>{1}","",article)
        article = re.sub("^<{1}.*?>{1}","",article)
        
        doc.write(article.encode('utf8'))
        doc.close()
        
    def __del__(self):
    	self.testfile.close()