/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logica;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sergio
 */
public class ClienteDados {

    //UDP constantes
    public static final int MAX_SIZE = 1025;
    public static final String REQUEST_SERVIDORES = "LISTASERVIDOR";
    public static final String REQUEST_CLIENTES = "LISTACLIENTES";
     public static final String REQUEST_ADDUPDATECLIENTE = "UPDATECLIENTELIST";
    public static final String REQUEST_MSG_GERAL = "MSG_GERAL"; //para um determinado grupo de utilizadores pertencentes ao mesmo servidor
    public static final String REQUEST_MSG_INDIVIDUAL = "MSG_INDIVIDUAL";
    public static final int TIMEOUT = 10; //segundos
 public final static long CLIENTESIGNAL_SEND_RATE = 30000;	//30 segs
    
    
    ArrayList<String> lista_servidores;

    //TCP constantes
    public static final String REQUEST_LOGIN = "LOGIN";
    public static final String REQUEST_REGISTAR = "REGISTAR";
    
    public static final String REQUEST_LOGOUT = "LOGOUT";
    public static final String REQUEST_CAMINHO = "CAMINHO";
    public static final String REQUEST_COPYFILE = "COPYFILE";
    public static final String REQUEST_MOVEFILE = "MOVEFILE";
    public static final String REQUEST_CHANGEWORKINGDIRECTORY = "CHANGEWORKINGDIRECTORY";
    public static final String REQUEST_GETWORKINGDIRCONTENT = "GETWORKINGDIRCONTENT";
    public static final String REQUEST_REMOVEFILE = "REMOVEFILE";
    public static final String REQUEST_MAKEDIR = "MAKEDIR";
    //add outros requests necessarios NOTA add aqui e no servidor....

    Socket TCPserv_socket;
//heabeat
    Timer manda_sinal_para_SD;
    //variaveis de dados respectivos a dirtectoria UDP e vars para a partilha de dados
    InetAddress serDirectoria_Addr = null;
    int serDirectoria_Port = -1;
    DatagramSocket socket = null;
    DatagramPacket packet = null;
    public static String NOME;
    public static int PORT;

    //variaveis de dados respectivos ao servidor TCP e vars para a partilha de dados
    public ClienteDados() { //não sei se estara bem localizado a inicializaçao desta var no construtor , ...
        lista_servidores = new ArrayList<>();

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT * 1000);
        } catch (SocketException ex) {
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t" + ex);
        }

    }

    //get's set's Sdirectoria
    public InetAddress getSerDirectoria_Addr() {
        return serDirectoria_Addr;
    }

    public void setSerDirectoria_Addr(InetAddress serDirectoria_Addr) {
        this.serDirectoria_Addr = serDirectoria_Addr;
    }

    public int getSerDirectoria_Port() {
        return serDirectoria_Port;
    }

    public void setSerDirectoria_Port(int serDirectoria_Port) {
        this.serDirectoria_Port = serDirectoria_Port;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public ArrayList<String> getLista_servidores() {
        return lista_servidores;
    }

    //get's set's servidor
    //funções para estabelecer ligação com o servidor
    public boolean estabelece_ligacao_servidor(int pos) throws IOException { //por implementar por receber argumentos , depende de como se esta a pensar passar os dados

        if (pos < lista_servidores.size() && pos >= 0) {
            //estabelece ligacao
            String[] dados_servidor = lista_servidores.get(pos).split(" ");
            //cria objeto
            String nome_grupo = dados_servidor[0];
            String ip = dados_servidor[1];
            String port = dados_servidor[2];

            //estabelece ligação
            System.out.println("[LIGACAO]: " + nome_grupo + " ip:" + ip + " port:" + port + "\n");

            TCPserv_socket = new Socket(ip, Integer.parseInt(port));
            TCPserv_socket.setSoTimeout(5000); //ms
            System.out.println("Connection established");

            
          
       
            //add ao grupo multicast(nao sei se sera aqui ou no serviço directoria)
            return true;
        }
        System.out.println("[LIGACAO]: escolha do servidor invalida \n");
        return false;
    }

    //funções de acçôes
    //para UDP 
    public String pedido_lista_servidores() throws IOException, ClassNotFoundException {
        ObjectOutputStream out = null;
        String Lista = "lista: \n";

        lista_servidores.clear();//limpa a lista 

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);
        ObjectInputStream in;

        out.writeObject(REQUEST_SERVIDORES);
        out.flush();

        packet = new DatagramPacket(bOut.toByteArray(), bOut.size(), serDirectoria_Addr, serDirectoria_Port);
        socket.send(packet); //envia pedido de lista

        packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        socket.receive(packet);

        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));

        int n_servidores = (Integer) in.readObject();

             //System.out.println("o numero de servidores e :"+ n_servidores+ "\n");
        for (int i = 0; i < n_servidores; i++) {
            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            socket.receive(packet);

            in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
            String servidor = (String) in.readObject();

            if (servidor != null) {
                //System.out.println("info :"+ servidor);         
                lista_servidores.add(servidor);
            }
        }

        for (int i = 0; i < lista_servidores.size(); i++) {
            Lista += i + ": " + lista_servidores.get(i) + "\n";
        }

        out.close();

        return Lista;
    }

    //para TCP
    //parte LOGIN_REGISTAR
    public boolean login(String nome, String pass) {

        String nomeEpass = nome + " " + pass;
        String resposta;
        BufferedReader in;
        PrintWriter out;

        if (nomeEpass == null) {
            return false;
        }

        try {

            System.out.println("[CLIENTE_login] introduzi " + nome + " " + pass);

            out = new PrintWriter(TCPserv_socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(TCPserv_socket.getInputStream()));

            out.println(REQUEST_LOGIN);
            out.flush();

            System.out.println("[CLIENTE_login] enviei request");

            out.println(nomeEpass);
            out.flush();

            // System.out.println("[CLIENTE] enviei info nome e pass"); 
            //recebe resposta
            resposta = in.readLine();

            if (resposta.equals("true")) {
                
                guarda_dados_nome_pass(nome);
                
                     //inicia heartbeat
                   //inicia heartbeat
                manda_sinal_para_SD = new Timer();
                manda_sinal_para_SD.scheduleAtFixedRate(new HeartBeatTask(), 0, CLIENTESIGNAL_SEND_RATE);
                return true;
            }

        } catch (IOException e) {
            System.out.println(" Erro na comunicação como o cliente ");
            return false;
        }

        return false;
    }

    public void  guarda_dados_nome_pass(String nome){    
        NOME = nome;
        PORT = socket.getLocalPort();             
    }
    
    public boolean registar(String nome, String pass) {

        String nomeEpass = nome + " " + pass;
        String resposta;
        BufferedReader in;
        PrintWriter out;

        if (nomeEpass == null) {
            return false;
        }

        try {

            System.out.println("[CLIENTE_registar] introduzi " + nome + " " + pass);

            out = new PrintWriter(TCPserv_socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(TCPserv_socket.getInputStream()));

            out.println(REQUEST_REGISTAR); //faz o request ao servidor para que ele saiba o que vem aseguir
            out.flush();

            System.out.println("[CLIENTE_registar] enviei request");

            out.println(nomeEpass);
            out.flush();

             //System.out.println("[CLIENTE_registar] enviei info nome e pass"); 
            //recebe resposta
            resposta = in.readLine();

            if (resposta.equals("true")) {
                return true;
            }

        } catch (IOException e) {
            System.out.println(" Erro na comunicação como o cliente ");
            return false;
        }

        return false;

    }

    public boolean logout() {
        String resposta;
        BufferedReader in;
        PrintWriter out;

        try {

            out = new PrintWriter(TCPserv_socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(TCPserv_socket.getInputStream()));

            out.println(REQUEST_LOGOUT);
            out.flush();

            System.out.println("[CLIENTE_login] enviei request");

            //recebe resposta
            resposta = in.readLine();

            if (resposta.equals("true")) {
                return true;
            }

        } catch (IOException e) {
            System.out.println(" Erro na comunicação como o cliente ");
            return false;
        }

        return false;
    }

//parte COMANDOS
    
    public String Visualizar_caminho_DirActual() {
        String resposta ="";
        BufferedReader in;
        PrintWriter out;
        
        try {

            System.out.println("[CLIENTE_registar] introduzi " + "CAMINHO");

            out = new PrintWriter(TCPserv_socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(TCPserv_socket.getInputStream()));

            out.println(REQUEST_CAMINHO); //faz o request ao servidor para que ele saiba o que vem aseguir
            out.flush();

            System.out.println("[CLIENTE_registar] enviei request");

             //System.out.println("[CLIENTE_registar] enviei info nome e pass"); 
            //recebe resposta
            resposta = in.readLine();

            if (resposta!=null) {
                return resposta;
            }

        } catch (IOException e) {
            System.out.println(" Erro na comunicação como o cliente ");
            return null;
        }

        return resposta;
    }
    
    public void Mudar_Pasta_Trabalho(){
        
    }
    
    public void Ver_Conteudo_Pasta_Trabalho(){
        
    }
    
    public void Remover_Ficheiro(String s){
        
    }
    
    public void Criar_Pasta(String s){
        
    }
    
    public boolean Criar_Copia(char ficheiro, char localizacao, String fich, String local) {  // r para remoto , l para local

        if (ficheiro == 'r') { //ficheiro remoto
            if (localizacao == 'r') {

            } else if (localizacao == 'l') {

            }

        } else if (ficheiro == 'l') { //ficheiro local

            if (localizacao == 'r') {

            } else if (localizacao == 'l') {

            }
        }

        return false; //caso nao entre em nenhum dos if's
    }

    

    public String Visualizar_cont_ficheiro(String ficheiro) {
        String caminho = "";

        return caminho;
    }

    
    
        public static void manda_info(String info_cliente, InetAddress serDirectoria_Addr, int serDirectoria_Port) throws IOException, ClassNotFoundException {

        DatagramSocket socket_udp;
        ObjectOutputStream out;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);
        ObjectInputStream in;

        try {
            socket_udp = new DatagramSocket();
            socket_udp.setSoTimeout(TIMEOUT * 1000);
        } catch (SocketException ex) {
            System.out.println("Ocorreu um erro ao nivel do socket UDP:\n\t" + ex);
            return;
        }

        //envia pedido de validação ao UDP serviço de directoria
        out.writeObject(REQUEST_ADDUPDATECLIENTE);
        out.flush();

        DatagramPacket packet_envio = new DatagramPacket(bOut.toByteArray(), bOut.size(), serDirectoria_Addr, serDirectoria_Port);
        socket_udp.send(packet_envio);

        //envia o nome que será validado
        bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);

        out.writeObject(info_cliente);
        out.flush();

        packet_envio = new DatagramPacket(bOut.toByteArray(), bOut.size(), serDirectoria_Addr, serDirectoria_Port);
        socket_udp.send(packet_envio);

    }
    
       
        
     
    public class HeartBeatTask extends TimerTask {
     /**
     * class directoria
     */
 

        @Override
        public void run() {
            String info = NOME + " " + PORT;
            try {
                //pede para guardar

                
                manda_info(info, serDirectoria_Addr, serDirectoria_Port);
            } catch (IOException ex) {
              
            } catch (ClassNotFoundException ex) {
               
            }
        }
        
    
}
    
    
    
    
    
    //implementar o resto...
//    
//    Criar uma cópia de um ficheiro (local ou remoto) numa nova localização (local ou remota);
//• Mover um ficheiro (local ou remoto) para uma nova localização (local ou remota);
//• Mudar de directoria actual;
//• Listar o conteúdo da directoria actual;
//• Visualizar o caminho da directoria actual;
//• Visualizar o conteúdo de um ficheiro;
//• Eliminar um ficheiro ou uma directoria vazia;
//• Criar uma nova directoria.
    
    
    
    
}
