import canoe.api._
import canoe.models.InputFile
import canoe.models.outgoing.PhotoContent
import canoe.syntax._
import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import scalaj.http.{Http, HttpOptions}

import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream
import java.io.File


object main extends IOApp {

  val token: String = "1542848624:AAG1wyW5s3XzJVRCkniMDQ4rTjUBWI9llkk"

  def run(args: List[String]): IO[ExitCode] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client => Bot.polling[IO].follow(greetings) }
      .compile.drain.as(ExitCode.Success)

  def greetings[F[_] : TelegramClient]: Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("hi").chat)
      //      _ <- Scenario.eval(chat.send("Hello. What's your name?"))
      //      name <- Scenario.expect(text)
      //      pictures <- Scenario.expect(photo)
      //      _ <- Scenario.eval(chat.send(pictures.fileId))
      //      _ <- Scenario.eval(chat.send(s"Nice to meet you, $name. this is your meme"))
      //      _ <- Scenario.eval(chat.send(PhotoContent(InputFile.fromFileId(pictures.fileId))))
      //      _ <- Scenario.eval(Chat(chat.id), randomPicture(),
      //        caption)
      //      _ <- Scenario.eval(chat.send(mem.photo.head.fileId))
      //      mem <- Scenario.eval(randomPicture(chat.id).call)
      //      _ <- Scenario.eval(chat.send(mem.photo.head.fileId))
      _ <- Scenario.eval(chat.send(
        Http("https://localhost:8443/sbermenu/menu/categories").option(HttpOptions.allowUnsafeSSL)
          .header("AUTH-TOKEN", "cJ5jq1za2yK7av3CExLKXlaK65SnUXr3bf2PDDpC/eg=")
          .asString.body)
      )
      _ <- Scenario.eval(chat.send(randomPicture))
    } yield ()

  //object main extends App {
  //println(getMenu)
  //
  //
  //  def getMenu: String = {
  //    Http("https://localhost:8443/sbermenu/menu").option(HttpOptions.allowUnsafeSSL)
  //    .header("AUTH-TOKEN", "cJ5jq1za2yK7av3CExLKXlaK65SnUXr3bf2PDDpC/eg=")
  //    .asString.body
  //  }


  def randomPicture: PhotoContent = {
    //    val source = Source.fromFile("memes/1.png", "ISO-8859-1")
    val bImage = ImageIO.read(new File("memes/1.png"))
    val bos = new ByteArrayOutputStream
    ImageIO.write(bImage, "png", bos)
    val data = bos.toByteArray
    PhotoContent(InputFile.Upload("mem1", data))
    //        val result = Http(s"https://api.telegram.org/bot$token/sendPhoto")
    //          .header("content-Type", "multipart/form-data")
    //          .postForm(Seq("chat_id" -> chatId.toString))
    //          .postMulti(MultiPart("photo", "1.png", "image/png", data))
    //          .asString.body
    //    source.close()
  }
}
