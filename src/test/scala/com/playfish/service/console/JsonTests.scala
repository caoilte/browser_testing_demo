package com.playfish.service.console

import org.scalatest.{GivenWhenThen, FeatureSpec}

import net.liftweb.json._
import JsonDSL._
import scalaj.http.Http
import java.io.InputStreamReader
import Serialization.{read, write => swrite}
/**
 *
 */

class JsonTests extends FeatureSpec with GivenWhenThen {

  val top: JValue =
    ("consoleFacetConfig" ->
      ("header" ->
        ("content")
      )
    )

  feature("The user can interact with the header banners") {
    scenario("Three banners appear when configured") {
      given("A Three Banner Header Configuration")

      val builder = new HeaderFacet.HeaderFacetBuilder
      builder.aURLSlot().withImgSrc("http://localhost:8080/console-application/Console-Templates/campaign-assets/temp_slot4.jpg")
      .withLinkUrl("http://playfish.com").build()
      builder.build()

      val json = builder.build()

      val jv: JValue = Extraction.decompose(json)

      val full = top transform  {
        case JString("content") => JObject(List(JField("content", jv)))
      }

      val out = compact(render(full))



      System.out.println(out)
    }

    scenario("foo") {

      val mergeme: JValue =
        ("foo" ->
          ("underfooo" ->
            ("bar")
            )
          )



      val full = top transform  {
        case JString("content") => JObject(List(JField("content", mergeme)))
      }


      val out = compact(render(full))
      System.out.println(out)

    }
  }


}