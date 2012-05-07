package com.playfish.service.console

import java.net.URL
import org.openqa.selenium.remote.{RemoteWebDriver, DesiredCapabilities}
import net.liftweb.json._
import Serialization.{read, write => swrite}

import scalaj.http.Http

import org.openqa.selenium.{By, WebDriver}
import org.scalatest.{BeforeAndAfter, GivenWhenThen, FeatureSpec}


class ConsoleTests extends FeatureSpec with GivenWhenThen with BeforeAndAfter {

  val CONSOLE_URL_PREFIX = "http://localhost:8080/"
  val CAMPAIGN_ASSETS_PREFIX = CONSOLE_URL_PREFIX + "console-application/Console-Templates/campaign-assets/"
  val TEMP_SLOT_1 = CAMPAIGN_ASSETS_PREFIX + "temp_slot1.jpg"
  val TEMP_SLOT_2 = CAMPAIGN_ASSETS_PREFIX + "temp_slot2.jpg"
  val TEMP_SLOT_3 = CAMPAIGN_ASSETS_PREFIX + "temp_slot3.jpg"
  val TEMP_SLOT_4 = CAMPAIGN_ASSETS_PREFIX + "temp_slot4.jpg"


  var driver: RemoteWebDriver = _
  var consoleDriver: ConsoleDriver = _

  before {
    val remoteDriverHost = new URL("http://127.0.0.1:8080/wd")
    val capabilities = new DesiredCapabilities()
    capabilities.setJavascriptEnabled(true);
    capabilities.setBrowserName("firefox");
    driver = new ReusableRemoteWebDriver(remoteDriverHost, capabilities)
    consoleDriver = new ConsoleDriver(driver)
  }


  feature("The user can interact with the header banners") {
    scenario("A single banner appears when configured") {
      given("A Browser")

      and("Single Banner Header Configuration")

      val consoleConfiguration: ConsoleConfiguration = ConsoleConfiguration.apply
      consoleConfiguration.headerFacetBuilder.aURLSlot().withImgSrc(TEMP_SLOT_4)
        .withLinkUrl("http://playfish.com").build()
      val jsonp = consoleConfiguration.build

      Http.post("http://localhost:8080/mcs").params("data" -> jsonp).asString

      when("The Console is Loaded")
      consoleDriver.loadConsole

      then("A Single Banner should appear")
      driver.findElement(By.xpath("//img[@src='"+TEMP_SLOT_4+"']"))
    }


    scenario("Multiple banners appear when configured") {
      given("A Browser")

      and("Three Banner Header Configurations")

      val consoleConfiguration: ConsoleConfiguration = ConsoleConfiguration.apply
      consoleConfiguration.headerFacetBuilder.aURLSlot().withImgSrc(TEMP_SLOT_4)
        .withLinkUrl("http://playfish.com").build()
      consoleConfiguration.headerFacetBuilder.aURLSlot().withImgSrc(TEMP_SLOT_3)
        .withLinkUrl("http://playfish.com").build()
      consoleConfiguration.headerFacetBuilder.aURLSlot().withImgSrc(TEMP_SLOT_2)
        .withLinkUrl("http://playfish.com").build()

      val jsonp = consoleConfiguration.build

      Http.post("http://localhost:8080/mcs").params("data" -> jsonp).asString

      when("The Console is Loaded")
      consoleDriver.loadConsole

      then("Three Banner Header Configurations should appear")
      driver.findElement(By.xpath("//img[@src='"+TEMP_SLOT_4+"']"))
      driver.findElement(By.xpath("//img[@src='"+TEMP_SLOT_3+"']"))
      driver.findElement(By.xpath("//img[@src='"+TEMP_SLOT_2+"']"))
    }
  }

}