import ca.ubc.gpec.tma.deconvoluter.Deconvoluter
import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_deconvoluter_deconvoluteraskInput_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/deconvoluter/askInput.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
printHtmlPart(1)
createTagBody(1, {->
printHtmlPart(0)
invokeTag('captureMeta','sitemesh',5,['gsp_sm_xmlClosingForEmptyTag':("/"),'http-equiv':("Content-Type"),'content':("text/html; charset=UTF-8")],-1)
printHtmlPart(0)
invokeTag('captureMeta','sitemesh',6,['gsp_sm_xmlClosingForEmptyTag':("/"),'name':("layout"),'content':("main")],-1)
printHtmlPart(0)
createTagBody(2, {->
createClosureForHtmlPart(2, 3)
invokeTag('captureTitle','sitemesh',7,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',7,[:],2)
printHtmlPart(0)
invokeTag('javascript','g',8,['library':("prototype")],-1)
printHtmlPart(0)
})
invokeTag('captureHead','sitemesh',9,[:],1)
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(3)
createTagBody(2, {->
printHtmlPart(4)
for( _it203607885 in (0..20) ) {
changeItVariable(_it203607885)
printHtmlPart(5)
expressionOut.print(it)
printHtmlPart(6)
expressionOut.print(it)
printHtmlPart(7)
}
printHtmlPart(8)
for( _it627248862 in (Deconvoluter.getAvailableOutputFileFormats()) ) {
changeItVariable(_it627248862)
printHtmlPart(5)
expressionOut.print(it)
printHtmlPart(6)
expressionOut.print(it)
printHtmlPart(7)
}
printHtmlPart(9)
invokeTag('submitButton','g',110,['id':("uploadButton"),'name':("upload"),'class':("upload"),'value':("Please select score sheet(s) & lookup file."),'disabled':("true")],-1)
printHtmlPart(10)
})
invokeTag('form','g',114,['action':("uploadFiles"),'method':("post"),'enctype':("multipart/form-data")],2)
printHtmlPart(11)
createClosureForHtmlPart(12, 2)
invokeTag('javascript','g',171,[:],2)
printHtmlPart(0)
})
invokeTag('captureBody','sitemesh',172,[:],1)
printHtmlPart(13)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1317945579943L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
