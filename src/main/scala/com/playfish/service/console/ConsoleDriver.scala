package com.playfish.service.console

import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.By

/**
 * Created by IntelliJ IDEA.
 * User: coconnor
 * Date: 28/03/12
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */

class ConsoleDriver(val driver: RemoteWebDriver) {
  
  def loadConsole = {
    driver.get("http://localhost:8080/c.html");
  }

}
