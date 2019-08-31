package com.pc.chapter2.one;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeServerHandler implements Runnable {

    private Socket socket;

    public TimeServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
            String currentTime = null;
            String body = null;

            while (true) {
                body = in.readLine();
                if (body == null) {
                    break;
                }
                System.out.println("The time server receiver order:" + body);
//                currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)?
//                        new Date(System.currentTimeMillis()).toString():
//                        "BAD ORDER";


                if ("hello,cxk".equals(body)) {
                    currentTime = "来自server：我喜欢、唱跳、rap、篮球\n";
                } else {
                    currentTime = "成功连接服务器\n";
                }


                out.println(currentTime);
            }


        } catch (IOException e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (out != null) {
                out.close();
                out = null;
            }
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                this.socket = null;
            }


        }


    }
}
