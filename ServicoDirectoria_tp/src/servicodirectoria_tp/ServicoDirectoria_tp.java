/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicodirectoria_tp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Sergio
 */
public class ServicoDirectoria_tp {

    /**
	 * A separate thread to receive all the heartbeats
	 */
	//para receber comandos
	BufferedReader br;
        //cli
	Timer clientListCheckerTimer;
      //serv
        Timer serverListCheckerTimer;
        
     //constantes do serviço de directoria
    public static final String SAIR_COMANDO = "sair";
    
    //udp cliente
    public static final int MAX_SIZE = 10000; //a discutir
    public static final String REQUEST_SERVIDORES = "LISTASERVIDOR";
    public static final String REQUEST_ADDUPDATECLIENTE = "UPDATECLIENTELIST"; //heartbeat
    public static final String REQUEST_CLIENTES = "LISTACLIENTES";
    public static final String REQUEST_MSG_GERAL = "MSG_GERAL"; //para um determinado grupo de utilizadores pertencentes ao mesmo servidor
    public static final String REQUEST_MSG_INDIVIDUAL = "MSG_INDIVIDUAL";
    //heartbeat cliente
    public final static long CLIENT_EXPIRE_TIME=40000;	//5000 milliseconds
    public final static long CLIENT_CHECK_RATE=3000;	//1000 milliseconds

    //udp servidor
    public static final String REQUEST_VALIDACAONOME = "VALIDACAO";
    public static final String REQUEST_ADDUPDATESERVIDOR = "UPDATESERVERLIST"; //heartbeat
    
    //heartbeat servidor
    public final static long SERVER_EXPIRE_TIME=40000;	//50000 milliseconds
    public final static long SERVER_CHECK_RATE=3000;	//2000 milliseconds

    private DatagramSocket socket;
    private DatagramPacket packet; //para receber os pedidos e enviar as respostas
    private boolean debug;

    Thread sairSdirectoria;
    //array que irá guardar os servidores activos
    ArrayList<servidor> lista_de_servidores = new ArrayList<>();

    //quando um cliente faz o login com sucesso , o Sdirectoria é avisado e fica com o nome do cliente e o nome do servidor
    //associar o cliente a um grupo multicast depois para por falar em grupo 
    ArrayList<cliente_d> lista_de_clientes_log = new ArrayList<>();

    public ServicoDirectoria_tp(int listeningPort, boolean debug) throws SocketException {
        socket = null;
        packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        socket = new DatagramSocket(listeningPort);
        this.debug = debug;

          //inicia tread que permite introdução de comandos no serviço de directoria
            sairSdirectoria = new Thread(new start_linha_comandos());
            sairSdirectoria.start();
            br=new BufferedReader(new InputStreamReader(System.in));
            
            
            //heartbeat
            //servidores
            serverListCheckerTimer=new Timer();
            serverListCheckerTimer.scheduleAtFixedRate(new verifica_lista_de_servidores(), 0, SERVER_CHECK_RATE);
          
            //cliente 
            clientListCheckerTimer=new Timer();
         clientListCheckerTimer.scheduleAtFixedRate(new verifica_lista_de_clientes(), 0, CLIENT_CHECK_RATE);
        
      
    }

    public String waitDatagram() throws IOException {
        String request;
        ObjectInputStream in;

        if (socket == null) {
            return null;
        }

        socket.receive(packet);
        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

        try {
            request = (String) (in.readObject());
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("Recebido objecto diferente de String "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());
            return null;
        }

        if (debug) {
            System.out.println("Recebido \"" + request + "\" de "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());
        }

        return request;

    }

    public void processRequests() {
        String receivedMsg;

        if (socket == null) {
            return;
        }

        if (debug) {
            System.out.println("UDP Serialized Server iniciado...");
        }

        while (true) {

            try {

                receivedMsg = waitDatagram();

                if (receivedMsg == null) {
                    continue;
                }

                if (receivedMsg.equalsIgnoreCase(REQUEST_SERVIDORES)) {

                    envia_lista_servidores();

                } else if (receivedMsg.equalsIgnoreCase(REQUEST_CLIENTES)) {

                    envia_lista_clientes();
                } else if (receivedMsg.equalsIgnoreCase(REQUEST_MSG_GERAL)) {

                  String info = recebe_msg();
                  new envia_msg_para_todos(info).start();
                    
                } else if (receivedMsg.equalsIgnoreCase(REQUEST_MSG_INDIVIDUAL)) {

                    //continuar...
                } else if (receivedMsg.equalsIgnoreCase(REQUEST_VALIDACAONOME)) { //valida nome do servidor

                    envia_resposta_de_validacao();

                } else if (receivedMsg.equalsIgnoreCase(REQUEST_ADDUPDATESERVIDOR)) { //aqui é que o servidor é adicionado a lista pelos heartbeats manten-se ou é apagado
                       
                     String info = recebe_nomePortServer();
                     String[] info_serv = info.split(" ");
                                    
                       
                     ActualizaServer(info_serv[0], packet.getAddress().getHostAddress(), Integer.parseInt(info_serv[1]));

                } else if (receivedMsg.equalsIgnoreCase(REQUEST_ADDUPDATECLIENTE)) {
                     String info = recebe_nomePortCliente();
                     String[] info_cli = info.split(" ");
                                         
                     
                     
                     ActualizaCliente(info_cli[0], Integer.parseInt(info_cli[1]), info_cli[2]);
                   

                } else {
                    //continue;
                }

            } catch (IOException e) {
                System.out.println(e);
            }

        }

    }

    public void closeSocket() {
        if (socket != null) {
            socket.close();
        }
    }

    public static void main(String[] args) {
        int listeningPort;
        ServicoDirectoria_tp Serv_Directoria = null;       
                
        if (args.length != 1) {
            System.out.println("Sintaxe: java ServiçoDirectoria listeningPort");
            return;
        }

        try {

            listeningPort = Integer.parseInt(args[0]);
            Serv_Directoria = new ServicoDirectoria_tp(listeningPort, true);
            Serv_Directoria.processRequests();        

        } catch (NumberFormatException e) {
            System.out.println("O porto de escuta deve ser um inteiro positivo.");
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nível do socket UDP:\n\t" + e);
        } finally {
            if (Serv_Directoria != null) {
                Serv_Directoria.closeSocket();
            }
        }
    }
    
    public class start_linha_comandos implements Runnable{
		
        @Override
        public void run() {
        System.out.println("O serviço de directorias está a online.... escreve '" + SAIR_COMANDO + "' para fechar o serviço de directoria");

            String command = "";
            while (true) {
                try {
                    command = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (command.equals(SAIR_COMANDO)) {
                    System.out.println("fechando o serviço de directoria");
                    closeSocket();
                    System.exit(0);
                }
            }
        }
    }

    //funções de iteração 
    public String recebenome() throws IOException {
        String request;
        ObjectInputStream in;
        String info_request[];

        if (socket == null) {
            return null;
        }

        socket.receive(packet);
        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

        try {
            request = (String) (in.readObject()); //recebe uma string com o nome e o port

           // info_request = request.split(" "); //o nome é a primeira string

          //  request = info_request[0];

        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("Recebido objecto diferente de String "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());
            return null;
        }

        if (debug) {
            System.out.println("Recebi o nome: \"" + request + "\" de "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());
        }

        //return info_request;
        return request;

    }

    public void envia_resposta_de_validacao() throws IOException {
        ByteArrayOutputStream bOut;
        ObjectOutputStream out;

        bOut = new ByteArrayOutputStream(MAX_SIZE);
        out = new ObjectOutputStream(bOut);

        //String[] nomeEPort = recebenome();

        //String nome = nomeEPort[0];
        String nome=recebenome();
        
        boolean existeNome = verifica_servidor_repetido(nome);

        out.writeObject(existeNome);

        packet.setData(bOut.toByteArray());
        packet.setLength(bOut.size());
        socket.send(packet);

        /*if (!existeNome) //caso nao exista ele adiciona o servidor logo 
        {
            lista_de_servidores.add(new servidor(nome, packet.getAddress().getHostAddress(), packet.getPort()));
        }*/

    }

    public boolean verifica_servidor_repetido(String nome1) {

        for (int i = 0; i < lista_de_servidores.size(); i++) {
            String info_servidor = lista_de_servidores.get(i).toString();
            //queremos o nome que é a primeira string
            String[] dados = info_servidor.split(" ");
            //cria objeto                     
            if (dados[0].equals(nome1)) {
                return true;
            }
        }

        return false;
    }
    
    public boolean verifica_cliente_repetido(String nome1, int port) {

        for (int i = 0; i < lista_de_clientes_log.size(); i++) {
            String info_servidor = lista_de_clientes_log.get(i).toString();
            //queremos o nome que é a primeira string
            String[] dados = info_servidor.split(" ");
            //cria objeto                     
            if (dados[0].equals(nome1) && Integer.parseInt(dados[1]) == port) {
                return true;
            }
        }

        return false;
    }
    
    public void envia_lista_servidores() throws IOException {
        ByteArrayOutputStream bOut;
        ObjectOutputStream out;

        bOut = new ByteArrayOutputStream(MAX_SIZE);
        out = new ObjectOutputStream(bOut);

        //envia o numero de sevidores que o cliente vai receber
        out.writeObject(lista_de_servidores.size());

        packet.setData(bOut.toByteArray());
        packet.setLength(bOut.size());
        socket.send(packet);

//O ip e porto de destino já se encontram definidos em packet
        for (int i = 0; i < lista_de_servidores.size(); i++) {
//envia os servidores 
            bOut = new ByteArrayOutputStream(MAX_SIZE);
            out = new ObjectOutputStream(bOut);

            String info = lista_de_servidores.get(i).toString();
            out.writeObject(info);
            out.flush();

            packet.setData(bOut.toByteArray());
            packet.setLength(bOut.size());
            socket.send(packet);
        }
        //System.out.println("Tamanho da resposta serializada:  "+bOut.size()+" bytes");

    }

    public void envia_lista_clientes() throws IOException {
        ByteArrayOutputStream bOut;
        ObjectOutputStream out;

        bOut = new ByteArrayOutputStream(MAX_SIZE);
        out = new ObjectOutputStream(bOut);

        //envia o numero de sevidores que o cliente vai receber
        out.writeObject(lista_de_clientes_log.size());

        packet.setData(bOut.toByteArray());
        packet.setLength(bOut.size());
        socket.send(packet);

//O ip e porto de destino já se encontram definidos em packet
        for (int i = 0; i < lista_de_clientes_log.size(); i++) {
//envia os servidores 
            bOut = new ByteArrayOutputStream(MAX_SIZE);
            out = new ObjectOutputStream(bOut);

            String info = lista_de_clientes_log.get(i).toString();
            out.writeObject(info);
            out.flush();

            packet.setData(bOut.toByteArray());
            packet.setLength(bOut.size());
            socket.send(packet);
        }
        //System.out.println("Tamanho da resposta serializada:  "+bOut.size()+" bytes");

    }
    
    
    //////////////////HHHHHHHHHHHHHHHHHHHHH   heartbeat HHHHHHHHHHHHHHHHHHHHHHHHHHH
    
    public boolean ActualizaCliente(String cliNome, int cliPort, String nome_serv) {
	
            System.out.println("[ACTUALIZA_CLI] Recebi nome:"+cliNome+" port: "+cliPort );
            
                        int encontrou = -1;
                        
                        for(int i=0;i<lista_de_clientes_log.size();i++){
                            if(verifica_cliente_repetido(cliNome, cliPort)){
                                encontrou = i;
                                break;                             
                            }
                        }
                        
                        if(encontrou != -1)
                        {
                        if(lista_de_clientes_log.get(encontrou).getNome_cliente().equals(cliNome) && (lista_de_servidores.get(encontrou).getPort() == cliPort) ) 
                        {	//if IP address matches
				lista_de_clientes_log.get(encontrou).updateHeartbeat();
				return true;
			} else	//nickname already taken by some other client(checked using IP address)
				return false;
                        
                        } else {	//caso seja novo servidor
			lista_de_clientes_log.add(new cliente_d(cliNome, cliPort, nome_serv));
			//System.out.println("Adicionei um novo cliente: "+ cliNome);
	
			return true;
		}
	}
    
    public boolean ActualizaServer(String servNome, String servIP, int servPort) {
	
            System.out.println("[ACTUALIZA_SERV] Recebi nome:"+servNome+" IP: "+servIP+" port: "+servPort );
            
                        int encontrou = -1;
                        
                        for(int i=0;i<lista_de_servidores.size();i++){
                            if(verifica_servidor_repetido(servNome)){
                                encontrou = i;
                                break;                             
                            }
                        }
                        
                        if(encontrou != -1)
                        {
                        if(lista_de_servidores.get(encontrou).getIP().equals(servIP) && (lista_de_servidores.get(encontrou).getPort() == servPort) ) 
                        {	//if IP address matches
				lista_de_servidores.get(encontrou).updateHeartbeat();
				return true;
			} else	//nickname already taken by some other client(checked using IP address)
				return false;
                        
                        } else {	//caso seja novo servidor
			lista_de_servidores.add(new servidor(servNome, servIP, servPort));
			//System.out.println("Adicionei um novo servidor: "+ servNome);
	
			return true;
		}
    
    }
    
    public String recebe_nomePortCliente() throws IOException{
    
        
    
    String request="";
        ObjectInputStream in;         
       
        
        if (socket == null) {
            return null;
        }

        socket.receive(packet);
        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

        try {
            request = (String) (in.readObject()); //recebe uma string com o nome e o port

          
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("Recebido objecto diferente de String "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());
        
        }
    
    
    return request;
    }
    
    public String recebe_nomePortServer() throws IOException{
  
    String request="";
        ObjectInputStream in;         
       
        
        if (socket == null) {
            return null;
        }

        socket.receive(packet);
        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

        try {
            request = (String) (in.readObject()); //recebe uma string com o nome e o port

          
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("Recebido objecto diferente de String "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());       
        }
 
    return request;
    }
    
    public String recebe_msg() throws IOException{
  
    String request="";
        ObjectInputStream in;         
       
        
        if (socket == null) {
            return null;
        }

        socket.receive(packet);
        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

        try {
            request = (String) (in.readObject()); //recebe uma string com o nome e o port

          
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("Recebido objecto diferente de String "
                    + packet.getAddress().getHostAddress() + ":" + packet.getPort());       
        }
 
    return request;
    }
        //verifica servidores online
    	private class verifica_lista_de_servidores extends TimerTask {

        @Override
        public void run() {
 
            //	Iterator<Map.Entry<String, ClientDetails>> it=onlineClients.entrySet().iterator();
            long tempoActual = System.currentTimeMillis(); //obtem o tempo actual 
            for (int i = 0; i < lista_de_servidores.size(); i++) {
                if (tempoActual - lista_de_servidores.get(i).getLastHeartbeat() > SERVER_EXPIRE_TIME) { //caso tenha passado o tempo ele elimina o server da lista
                    System.out.println(lista_de_servidores.get(i).getNome() + " timed out.");
                    lista_de_servidores.remove(i);
                }

            }

        }

    }
        //verifica clientes online
        private class verifica_lista_de_clientes extends TimerTask {

        @Override
        public void run() {

            //	Iterator<Map.Entry<String, ClientDetails>> it=onlineClients.entrySet().iterator();
            long tempoActual = System.currentTimeMillis(); //obtem o tempo actual 

            for (int i = 0; i < lista_de_clientes_log.size(); i++) {
                if (tempoActual - lista_de_clientes_log.get(i).getLastHeartbeat() > CLIENT_EXPIRE_TIME) { //caso tenha passado o tempo ele elimina o server da lista
                    System.out.println(lista_de_clientes_log.get(i).getNome_cliente() + " timed out.");
                    lista_de_clientes_log.remove(i);
                }

            }

        }

    }
    
        
public class envia_msg_para_todos extends Thread{

    public DatagramSocket c;
    public String msg;
    
    envia_msg_para_todos(String msg){
    
        this.msg= msg;
        
    }
    
    
    
        @Override
        public void run() {
        
            try {
          //Open a random port to send the package
          c = new DatagramSocket();
          c.setBroadcast(true);

          byte[] sendData = msg.getBytes();

          //Try the 255.255.255.255 first
          try {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
            c.send(sendPacket);
            System.out.println(">>> pedido de msg geral abordado: 255.255.255.255 (DEFAULT)");
          } catch (Exception e) {
          }

          
         /* 
          // Broadcast the message over all the network interfaces
          Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
          while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
              continue; // Don't want to broadcast to the loopback interface
            }

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
              InetAddress broadcast = interfaceAddress.getBroadcast();
              if (broadcast == null) {
                continue;
              }

              // Send the broadcast package!
              try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                c.send(sendPacket);
              } catch (Exception e) {
              }

              System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
            }
          }

          System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

          //Wait for a response
          byte[] recvBuf = new byte[15000];
          DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
          c.receive(receivePacket);

          //We have a response
          System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

          //Check if the message is correct
          String message = new String(receivePacket.getData()).trim();
          if (message.equals("DISCOVER_FUIFSERVER_RESPONSE")) {
            //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
          
          }
*/
          //Close the port!
          c.close();
        } catch (IOException ex) {
         
        }
 
        }

}
}
