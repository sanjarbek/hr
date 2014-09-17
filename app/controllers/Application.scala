package controllers

import java.io.{FileOutputStream, FileInputStream, File}
import java.nio.ByteBuffer
import java.util
import java.util.{Locale, Calendar}
import java.util.Collections._

import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.hwpf.HWPFDocument
import play.api._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.concurrent.Promise
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.mvc.{WebSocket, Controller}


object Application extends Controller {

  def serverStatus() = WebSocket.using[String] { implicit request =>
    val in = Iteratee.ignore[String]
    val out = Enumerator.repeatM {
      Promise.timeout(this.getConnectionStatus, 1)
    }
    (in, out)
  }

  def getConnectionStatus = Calendar.getInstance().getTime.toLocaleString()

  def angular = Action { implicit request =>
    Ok(views.html.angular("Отдел по работе с сотрудниками"))
  }

  //  def genereateOdf = Action {
  //
  //    var templateFile = new File("C:\\Users\\samatov\\IdeaProjects\\playbasics\\PlayBasics\\hr\\documents\\test.odt")
  //    var outFile = new File("out.odt")
  //
  //    var template = new JavaScriptFileTemplate(templateFile);
  //
  //    // Fill with sample values.
  //    template.setField("toto", "value set using setField()")
  ////    var months = new util.ArrayList[util.Map[String, String]]
  //    //    months.add(createMap("January", "-12", "3"))
  //    //    months.add(createMap("February", "-8", "5"))
  //    //    months.add(createMap("March", "-5", "12"))
  //    //    months.add(createMap("April", "-1", "15"))
  //    //    months.add(createMap("May", "3", "21"))
  //    //    template.setField("months", months)
  //
  //    template.hideParagraph("p1");
  //
  ////    var template: JavaScriptFileTemplate = new JavaScriptFileTemplate(templateFile);
  //    template.saveAs(outFile)
  //
  ////    var template = WordprocessingMLPackage.load(new FileInputStream(templateFile));
  //
  //    val fileContent: Enumerator[Array[Byte]] = Enumerator.fromFile(outFile)
  //
  //    SimpleResult(
  //      header = ResponseHeader(200),
  //      body = fileContent
  //    )
  //
  ////    Ok.sendFile(new File("C:\\Users\\samatov\\IdeaProjects\\playbasics\\PlayBasics\\hr\\documents\\contract_stajer.doc"))
  //  }

  def generatepoi = Action {
    val filePath = "C:\\Users\\samatov\\IdeaProjects\\playbasics\\PlayBasics\\hr\\documents\\contract_stajer.doc"
    val fs = new POIFSFileSystem(new FileInputStream(filePath))
    var doc = new HWPFDocument(fs)

    var begin_date = Calendar.getInstance().getTime

    val keywords = Map(
      "$СОТРУДНИК_ИМЯ" -> "Санжарбек",
      "$СОТРУДНИК_ФАМИЛИЯ" -> "Аматов",
      "$СОТРУДНИК_АДРЕС_ПРОЖИВАНИЯ" -> "г. Бишкек, пр. Чуй 57/12",
      "$ПАСПОРТ_СЕРИЯ" -> "AN233223",
      "$ПАСПОРТ_ВЫДАН" -> "12.05.2010",
      "$ПАСПОРТ_ОКОНЧАНИЯ" -> "12.05.2020",
      "$ДОГОВОР_НАЧАЛА" -> begin_date.toString(),
      "$ДОГОВОР_КОНЕЦ" -> "12.09.2014"
    )
    keywords.foreach {
      case (key, value) => doc.getRange().replaceText(key, value)
    }

    //    saveWord(filePath, doc)
    var file = new File("test.doc")
    var out = new FileOutputStream(file)
    doc.write(out)
    Ok.sendFile(file)
  }

  def saveWord(filePath: String, doc: HWPFDocument) = {
    var out = new FileOutputStream(filePath)
    doc.write(out)
    out.close()
  }
}