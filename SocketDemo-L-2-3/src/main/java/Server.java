import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Aauthor xianyuan_li@qq.com
 * @Date: Create in 21:43 2020/3/28
 * @Description: 服务器端
 */
public class Server {
    public static void main(String[] args) throws Exception{
        ServerSocket server = new ServerSocket(2000);

        System.out.println("服务器准备就绪~");
        System.out.println("服务器信息："+server.getInetAddress()+" Port: "+server.getLocalPort());

        //等待客户端连接
        for(;;){
            //得到客户端
            Socket client = server.accept();
            //客户端构建异步线程
            ClientHander clientHander = new ClientHander(client);
            //启动线程
            clientHander.start();
        }

    }

    /**
     * 客户端消息处理
     */
    private static class ClientHander extends Thread{
        private Socket socket;
        private boolean flag = true;

        ClientHander(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            //打印客户端的地址和客户端的端口
            System.out.println("新客户端连接: " + socket.getInetAddress() + " Post:" + socket.getPort());

            try {
                //得到打印流，用于数据输出；服务器回送数据使用
                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                //得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                do {
                    //拿到客户端的一条数据
                    String str = socketInput.readLine();
                    if ("bye".equalsIgnoreCase(str)) {
                        flag = false;
                        //回送
                        socketOutput.println("bye");
                    }else{
                        //打印到屏幕。并回送数据长度
                        System.out.println(str);//将从客户端发送过来的数据打印到屏幕中
                        socketOutput.println("回送: "+str.length());//发送数据给客户端
                    }
                } while (flag);
                socketInput.close();
                socketOutput.close();
            } catch (IOException e) {
               System.out.println("连接异常断开");
            }finally {
                //连接关闭
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("客户端已退出："+socket.getInetAddress()+
                    " Post:" + socket.getPort());

        }
    }
}
