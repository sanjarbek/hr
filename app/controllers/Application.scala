package controllers

import java.io.{FileOutputStream, FileInputStream, File}
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util
import java.util.{Locale, Calendar}
import java.util.Collections._

import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.hwpf.HWPFDocument
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json._
import play.api.cache._

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.concurrent.Promise
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.mvc.{WebSocket, Controller}

/**
 * Security actions that should be used by all controllers that need to protect their actions.
 * Can be composed to fine-tune access control.
 */
trait Security {
  self: Controller =>

  implicit val app: play.api.Application = play.api.Play.current

  val AuthTokenHeader = "X-XSRF-TOKEN"
  val AuthTokenCookieKey = "XSRF-TOKEN"
  val AuthTokenUrlKey = "auth"

  /** Checks that a token is either in the header or in the query string */
  def HasToken[A](p: BodyParser[A] = parse.anyContent)(f: String => Long => Request[A] => Result): Action[A] =
    Action(p) { implicit request =>
      val maybeToken = request.headers.get(AuthTokenHeader).orElse(request.getQueryString(AuthTokenUrlKey))
      maybeToken flatMap { token =>
        Cache.getAs[Long](token) map { userid =>
          Logger.info(Cache.getAs[Long](token).toString)
          f(token)(userid)(request)
        }
      } getOrElse Unauthorized(Json.obj("err" -> "No Token"))
    }

}

/** General Application actions, mainly session management */
trait Application extends Controller with Security {

  lazy val CacheExpiration =
    app.configuration.getInt("cache.expiration").getOrElse(60 /*seconds*/ * 2 /* minutes */)


  case class Login(username: String, password: String)

  val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(Login.apply)(Login.unapply)
  )

  implicit class ResultWithToken(result: Result) {
    def withToken(token: (String, Long)): Result = {
      Cache.set(token._1, token._2, CacheExpiration)
      result.withCookies(Cookie(AuthTokenCookieKey, token._1, None, httpOnly = false))
    }

    def discardingToken(token: String): Result = {
      Cache.remove(token)
      result.discardingCookies(DiscardingCookie(name = AuthTokenCookieKey))
    }
  }

  /** Check credentials, generate token and serve it back as auth token in a Cookie */
  def auth = Action(parse.json) { implicit request =>
    loginForm.bind(request.body).fold(// Bind JSON body to form values
      formErrors => BadRequest(Json.obj("err" -> formErrors.errorsAsJson)),
      loginData => {
        if (loginData.username == "samatov" && loginData.password == "admin12") {
          val token = java.util.UUID.randomUUID().toString
          Ok(Json.obj(
            "authToken" -> token,
            "userId" -> 100
          )).withToken(token -> 100)
        }
        else NotFound(Json.obj("err" -> "User Not Found or Password Invalid"))
      }
    )
  }

  /** Invalidate the token in the Cache and discard the cookie */
  def logout = Action { implicit request =>
    request.headers.get(AuthTokenHeader) map { token =>
      Redirect("/").discardingToken(token)
    } getOrElse BadRequest(Json.obj("err" -> "No Token"))
  }

  def user = Action { implicit request =>
    Ok(Json.obj("name" -> "Sanzhar", "role" -> "Administrator"))
  }

  /**
   * Returns the current user's ID if a valid token is transmitted.
   * Also sets the cookie (useful in some edge cases).
   * This action can be used by the route service.
   */
  def ping() = HasToken() { token => userId => implicit request =>
    if (userId == 100) {
      Ok(Json.obj("userId" -> userId)).withToken(token -> userId)
    }
    else {
      NotFound(Json.obj("err" -> "User Not Found"))
    }
  }

}

object Application extends Application {

  def serverStatus() = WebSocket.using[String] { implicit request =>
    val in = Iteratee.ignore[String]
    val out = Enumerator.repeatM {
      Promise.timeout(this.getConnectionStatus, 1)
    }
    (in, out)
  }

  def getConnectionStatus = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss").format(Calendar.getInstance().getTime).toString

  def angular = Action { implicit request =>
    Ok(views.html.angular("Отдел по работе с сотрудниками"))
  }

  def login = Action { implicit request =>
    Ok(views.html.application.login())
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
    val fileName = "contract_stajer.doc"
    val filePath = s"./documents/$fileName"
    val fs = new POIFSFileSystem(new FileInputStream(filePath))
    var doc = new HWPFDocument(fs)

    var begin_date = Calendar.getInstance().getTime

    val keywords = Map(
      "$СОТРУДНИК.ИМЯ" -> "Санжарбек",
      "$СОТРУДНИК.ФАМИЛИЯ" -> "Аматов",
      "$СОТРУДНИК.АДРЕС.ПРОЖИВАНИЯ" -> "г. Бишкек, пр. Чуй 57/12",
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

  def uploadForm = Action { request =>
    Ok(views.html.application.index())
  }

  def upload = Action(parse.multipartFormData) { request =>
    val id = request.body.dataParts("id")(0).toString
    Logger.info(id)
    request.body.file("file").map { picture =>
      import java.io.File
      val filename = picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(new File(s"./uploaded/documents/$filename"), true)
      //      val file =
      Ok(s"File '$filename' uploaded.")
    }.getOrElse {
      NotAcceptable
    }
  }

}