package com.playfish.service.console;

/*
Copyright (C) 2011 by Renjith Mathew

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.ExtensionConnection;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.internal.SocketLock;
import org.openqa.selenium.net.NetworkUtils;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.Response;

public class ReusableFirefoxDriver extends FirefoxDriver {

    private HttpCommandExecutor httpClient;

    public ReusableFirefoxDriver(Capabilities capabilities) {
        super(capabilities);

    }

    @Override
    protected void startSession(Capabilities desiredCapabilities) {
        if(localServerURL!=null){
            httpClient = new HttpCommandExecutor(localServerURL);
        }
        super.startSession(desiredCapabilities);
    }

    @Override
    protected ExtensionConnection connectTo(FirefoxBinary binary,
                                            FirefoxProfile profile, String host) {
        localServerURL = getURLofExistingLocalServer();
        if(localServerURL!=null){
            try {

                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setJavascriptEnabled(true);
                capabilities.setBrowserName("firefox");
                httpClient = new HttpCommandExecutor(localServerURL);
                Command command = new Command(null, "newSession", capabilities.asMap());

                httpClient.execute(command);
                return new ExtensionConnection() {
                    @Override
                    public Response execute(Command command) throws IOException {
                        return httpClient.execute(command);
                    }

                    @Override
                    public void start() throws IOException {
                        //NOOP
                    }

                    @Override
                    public void quit() {
                        //NOOP
                    }

                    @Override
                    public boolean isConnected() {
                        try {
                            httpClient.getAddressOfRemoteServer().openConnection().connect();
                            return true;
                        } catch (IOException e) {
                            return false;
                        }
                    }
                };
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return super.connectTo(binary, profile, host);
    }


    private URL localServerURL = null;

    private URL getURLofExistingLocalServer() {
        Socket socket = new Socket();
        try {
            socket.bind(new InetSocketAddress("localhost", SocketLock.DEFAULT_PORT));
            return null; //Able to connect on default port (Assuming that default FF driver is not running)
        } catch (IOException e) {
        }finally{
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        try {
            return new URL("http",new NetworkUtils().obtainLoopbackIp4Address(), SocketLock.DEFAULT_PORT,"/hub");
        } catch (MalformedURLException e) {
            throw new WebDriverException(e);
        }
    }


//    public static void main(String...a) throws Exception {
//        WebDriver driver = new ReusableFirefoxDriver();
//        driver.get("http://www.google.com");
//        System.out.println("Start second driver (Press enter)?");
//        System.in.read();
//        //The below code could be elsewhere in a different JVM
//        driver = new ReusableFirefoxDriver();
//        driver.get("http://www.bing.com");
//    }
}