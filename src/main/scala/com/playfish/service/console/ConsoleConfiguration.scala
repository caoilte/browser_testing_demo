package com.playfish.service.console

import net.liftweb.json._
import JsonDSL._
import scalaj.http.Http

class ConsoleConfiguration() {
  implicit val formats = Serialization.formats(NoTypeHints)

  val headerFacetBuilder = new HeaderFacet.HeaderFacetBuilder

  def build = {
    val headerFacetObj = headerFacetBuilder.build()

    val jv: JValue = Extraction.decompose(headerFacetObj)

    val full = ConsoleConfiguration.top transform  {
      case JString("content") => JObject(List(JField("content", jv)))
    }

    //val finalJson = baseConfig merge full
    val bootstrapJs = """bootstrapCallback(""" + compact(render(full)) + """);"""


    //Http.post("http://localhost:8080/mcs").params("data" -> bootstrapJs).asString
    bootstrapJs
  }
}

object ConsoleConfiguration {

  val top: JValue =
    ("consoleFacetConfig" ->
      ("header" ->
        ("content")
        )
      )

  def apply = {
//    val jsonText = io.Source.fromInputStream(getClass.getResourceAsStream("/bootstrap.json")).mkString
//    val json: JValue = parse(jsonText)

    new ConsoleConfiguration()
  }
}
